package com.example.grapplinghook.mechanic;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks which players currently have an active "pull" toward a target point,
 * and moves them toward it a little more each server tick until they arrive.
 */
public class PullManager {

    // How fast the player accelerates toward the target, in blocks/tick^2 (rough).
    private static final double ACCELERATION = 0.10;
    // Top speed while being pulled, in blocks/tick.
    private static final double MAX_SPEED = 1.6;
    // Distance at which we consider the player "arrived" and stop the pull.
    private static final double ARRIVE_DISTANCE = 1.75;

    private static final Map<UUID, Vec3d> ACTIVE_PULLS = new HashMap<>();

    private PullManager() {
    }

    public static boolean isPulling(ServerPlayerEntity player) {
        return ACTIVE_PULLS.containsKey(player.getUuid());
    }

    public static void start(ServerPlayerEntity player, Vec3d target) {
        ACTIVE_PULLS.put(player.getUuid(), target);
        player.setSprinting(false);
        player.fallDistance = 0;
    }

    public static void stop(ServerPlayerEntity player) {
        ACTIVE_PULLS.remove(player.getUuid());
    }

    /**
     * Call this once per server tick (see GrapplingHookMod).
     */
    public static void tick(MinecraftServer server) {
        if (ACTIVE_PULLS.isEmpty()) {
            return;
        }

        ACTIVE_PULLS.entrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null || player.isRemoved() || player.isDead()) {
                return true;
            }

            Vec3d target = entry.getValue();
            Vec3d toTarget = target.subtract(player.getPos());
            double distance = toTarget.length();

            if (distance <= ARRIVE_DISTANCE) {
                player.setVelocity(player.getVelocity().multiply(0.2, 0.2, 0.2));
                player.velocityModified = true;
                return true;
            }

            Vec3d direction = toTarget.normalize();
            Vec3d currentVelocity = player.getVelocity();

            // Accelerate toward the target, capped at MAX_SPEED.
            Vec3d desired = direction.multiply(Math.min(MAX_SPEED, distance));
            Vec3d newVelocity = currentVelocity.lerp(desired, ACCELERATION * 6.0);

            player.setVelocity(newVelocity);
            player.velocityModified = true;
            player.fallDistance = 0;

            return false;
        });
    }
}

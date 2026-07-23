package com.example.grapplinghook.mechanic;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks players currently swinging from a rope anchor and constrains their
 * movement each tick to stay on the sphere of radius = rope length around the anchor,
 * canceling out the "stretching" component of velocity so it behaves like a pendulum.
 */
public class SwingManager {

    private record SwingData(Vec3d anchor, double length) {
    }

    private static final Map<UUID, SwingData> ACTIVE_SWINGS = new HashMap<>();

    // A little slack so the rope doesn't feel instantly rigid.
    private static final double SLACK = 0.15;

    private SwingManager() {
    }

    public static boolean isSwinging(ServerPlayerEntity player) {
        return ACTIVE_SWINGS.containsKey(player.getUuid());
    }

    public static void start(ServerPlayerEntity player, Vec3d anchor) {
        double length = anchor.distanceTo(player.getPos());
        ACTIVE_SWINGS.put(player.getUuid(), new SwingData(anchor, length));
        player.fallDistance = 0;
    }

    public static void stop(ServerPlayerEntity player) {
        ACTIVE_SWINGS.remove(player.getUuid());
    }

    /**
     * Call this once per server tick, after normal entity movement has happened,
     * (see GrapplingHookMod) so we can correct the player back onto the rope.
     */
    public static void tick(MinecraftServer server) {
        if (ACTIVE_SWINGS.isEmpty()) {
            return;
        }

        ACTIVE_SWINGS.entrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null || player.isRemoved() || player.isDead()) {
                return true;
            }

            SwingData data = entry.getValue();
            Vec3d anchor = data.anchor();
            double ropeLength = data.length();

            Vec3d fromAnchor = player.getPos().subtract(anchor);
            double distance = fromAnchor.length();

            if (distance < 0.001) {
                return false;
            }

            double maxDistance = ropeLength + SLACK;
            if (distance > maxDistance) {
                Vec3d radialDir = fromAnchor.multiply(1.0 / distance);

                // Snap the player back onto the sphere around the anchor.
                Vec3d corrected = anchor.add(radialDir.multiply(maxDistance));
                player.setPosition(corrected.x, corrected.y, corrected.z);

                // Remove the outward (radial) component of velocity, keep the
                // tangential component - this is what makes it swing instead of stop dead.
                Vec3d velocity = player.getVelocity();
                double radialSpeed = velocity.dotProduct(radialDir);
                if (radialSpeed > 0) {
                    Vec3d correctedVelocity = velocity.subtract(radialDir.multiply(radialSpeed));
                    player.setVelocity(correctedVelocity);
                    player.velocityModified = true;
                }

                player.fallDistance = 0;
            }

            return false;
        });
    }
}

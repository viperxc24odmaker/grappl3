package com.example.grapplinghook.item;

import com.example.grapplinghook.mechanic.PullManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.hit.HitResult;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Right click while looking at a block within range to get pulled straight toward
 * the point you hit. Right click again (or sneak + right click) to cancel early.
 */
public class PullHookItem extends Item {

    public static final double MAX_RANGE = 30.0;

    public PullHookItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(user instanceof ServerPlayerEntity player)) {
            // Client side: let the server decide, just say we handled it so the
            // arm swings and there's no double-processing.
            return ActionResult.SUCCESS;
        }

        if (PullManager.isPulling(player)) {
            PullManager.stop(player);
            return ActionResult.SUCCESS;
        }

        HitResult hit = player.raycast(MAX_RANGE, 1.0F, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3d target = hit.getPos();
            PullManager.start(player, target);
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_FISHING_BOBBER_THROW,
                    SoundCategory.PLAYERS, 0.6F, 1.2F);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}

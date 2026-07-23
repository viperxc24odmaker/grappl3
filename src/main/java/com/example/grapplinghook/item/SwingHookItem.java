package com.example.grapplinghook.item;

import com.example.grapplinghook.mechanic.SwingManager;
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
 * Right click while looking at a block within range to anchor a rope there and
 * start swinging like a pendulum. Right click again to let go.
 */
public class SwingHookItem extends Item {

    public static final double MAX_RANGE = 25.0;

    public SwingHookItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(user instanceof ServerPlayerEntity player)) {
            return ActionResult.SUCCESS;
        }

        if (SwingManager.isSwinging(player)) {
            SwingManager.stop(player);
            return ActionResult.SUCCESS;
        }

        HitResult hit = player.raycast(MAX_RANGE, 1.0F, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3d anchor = hit.getPos();
            SwingManager.start(player, anchor);
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE,
                    SoundCategory.PLAYERS, 0.6F, 0.9F);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}

package com.example.grapplinghook;

import com.example.grapplinghook.mechanic.PullManager;
import com.example.grapplinghook.mechanic.SwingManager;
import com.example.grapplinghook.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public class GrapplingHookMod implements ModInitializer {

    public static final String MOD_ID = "grapplinghook";

    @Override
    public void onInitialize() {
        ModItems.init();

        // Put both hooks in the vanilla "Tools & Utilities" creative tab.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(ModItems.PULL_HOOK);
            entries.add(ModItems.SWING_HOOK);
        });

        // Drive both mechanics off the server tick.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            PullManager.tick(server);
            SwingManager.tick(server);
        });
    }
}

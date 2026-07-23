package com.example.grapplinghook.registry;

import com.example.grapplinghook.GrapplingHookMod;
import com.example.grapplinghook.item.PullHookItem;
import com.example.grapplinghook.item.SwingHookItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

    // The Pull Hook reels you straight toward the point you're looking at.
    public static final Item PULL_HOOK = register("pull_hook",
            settings -> new PullHookItem(settings.maxCount(1)));

    // The Swing Hook anchors a rope to the point you're looking at and lets you swing like a pendulum.
    public static final Item SWING_HOOK = register("swing_hook",
            settings -> new SwingHookItem(settings.maxCount(1)));

    private static Item register(String name, Function<Item.Settings, Item> factory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(GrapplingHookMod.MOD_ID, name));
        Item item = factory.apply(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void init() {
        // Referencing the class triggers static init / registration above.
    }
}

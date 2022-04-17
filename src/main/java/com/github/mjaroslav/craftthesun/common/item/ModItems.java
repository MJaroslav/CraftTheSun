package com.github.mjaroslav.craftthesun.common.item;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;

public class ModItems {
    public static Item estusFlask;

    public static void preInit(@NotNull FMLPreInitializationEvent event) {
        estusFlask = new ItemEstusFlask();
    }
}

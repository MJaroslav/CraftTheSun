package com.github.mjaroslav.craftthesun.common.init;

import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mjaroslav.mcmods.mjutils.module.Modular;
import mjaroslav.mcmods.mjutils.module.Module;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

@Module(ModInfo.MOD_ID)
public class ModItems implements Modular {
    public static Item estusFlask;

    @Override
    public void preInit(@NotNull FMLPreInitializationEvent event) {
        estusFlask = new ItemEstusFlask();
    }
}

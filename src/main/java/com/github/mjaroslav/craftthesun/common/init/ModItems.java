package com.github.mjaroslav.craftthesun.common.init;

import com.github.mjaroslav.craftthesun.common.item.ItemDarksign;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.item.ItemHumanity;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mjaroslav.mcmods.mjutils.module.Modular;
import mjaroslav.mcmods.mjutils.module.Module;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

@Module(ModInfo.MOD_ID)
public class ModItems implements Modular {
    public static Item estusFlask;
    public static Item darkSign;
    public static Item humanity;

    @Override
    public void preInit(@NotNull FMLPreInitializationEvent event) {
        humanity = new ItemHumanity();
        estusFlask = new ItemEstusFlask();
        darkSign = new ItemDarksign();
    }
}

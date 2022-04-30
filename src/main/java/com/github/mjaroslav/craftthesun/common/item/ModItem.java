package com.github.mjaroslav.craftthesun.common.item;

import com.github.mjaroslav.craftthesun.CraftTheSunMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

import static com.github.mjaroslav.craftthesun.lib.ModInfo.prefix;

public class ModItem extends Item {
    public ModItem(@NotNull String name) {
        setTextureName(prefix(name));
        setUnlocalizedName(prefix(name));
        setCreativeTab(CraftTheSunMod.tab);
        GameRegistry.registerItem(this, name);
    }
}

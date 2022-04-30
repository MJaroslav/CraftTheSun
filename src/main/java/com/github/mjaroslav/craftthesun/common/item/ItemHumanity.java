package com.github.mjaroslav.craftthesun.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemHumanity extends ModItem {
    public ItemHumanity() {
        super("humanity");
    }

    @Override
    public EnumRarity getRarity(@NotNull ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public ItemStack onItemRightClick(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        return super.onItemRightClick(stack, world, player);
    }
}

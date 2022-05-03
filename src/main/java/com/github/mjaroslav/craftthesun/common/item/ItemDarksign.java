package com.github.mjaroslav.craftthesun.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemDarksign extends ModItem {
    public ItemDarksign() {
        super("darksign");
        setMaxStackSize(1);
    }

    @Override
    public boolean hasEffect(@NotNull ItemStack par1ItemStack, int pass) {
        return pass == 0;
    }

    @Override
    public EnumRarity getRarity(@NotNull ItemStack stack) {
        return EnumRarity.epic;
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@NotNull ItemStack stack, @NotNull EntityPlayer player, @NotNull List list,
                               boolean advanced) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.craftthesun:darksign.text"));
    }
}

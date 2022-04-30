package com.github.mjaroslav.craftthesun.common.item;

import com.github.mjaroslav.craftthesun.common.data.CraftTheSunEEP;
import com.github.mjaroslav.craftthesun.common.data.SyncData.PlayerType;
import lombok.val;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemDarkSign extends ModItem {
    public ItemDarkSign() {
        super("dark_sign");
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

    @Override
    public ItemStack onItemRightClick(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        val eep = CraftTheSunEEP.get(player).getSyncData();
        if (!eep.getType().isUndead() && eep.getType() != PlayerType.CURSED) {
            eep.setType(PlayerType.CURSED);
            return stack;
        } else return super.onItemRightClick(stack, world, player);
    }
}

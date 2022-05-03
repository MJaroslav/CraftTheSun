package com.github.mjaroslav.craftthesun.asm.reflector;

import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import lombok.val;
import lombok.var;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public class EnchantmentHelperReflector {
    // TODO: look for shears fortune getter or just replace getEnchantmentLevel for fortune
    public static int getFortuneModifier(@NotNull EntityLivingBase entity) {
        val originalModifier = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, entity.getHeldItem());
        var bonusModifier = 0;
        if (entity instanceof EntityPlayer) {
            val player = (EntityPlayer) entity;
            bonusModifier = CommonUtils.getPlayerType(player).isUndead() &&
                    CommonUtils.getPlayerHumanity(player) > 9 ? 1 : 0;

        }
        return originalModifier + bonusModifier;
    }

    public static int getLootingModifier(@NotNull EntityLivingBase entity) {
        val originalModifier = EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, entity.getHeldItem());
        var bonusModifier = 0;
        if (entity instanceof EntityPlayer) {
            val player = (EntityPlayer) entity;
            bonusModifier = CommonUtils.getPlayerType(player).isUndead() &&
                    CommonUtils.getPlayerHumanity(player) > 9 ? 1 : 0;

        }
        return originalModifier + bonusModifier;
    }
}

package com.github.mjaroslav.craftthesun.asm.reflector;

import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient.CategorySounds;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public class EntityPlayerReflector {
    public static String getHurtSound(@NotNull EntityPlayer instance) {
        return (!CategorySounds.useSoundsOnlyForUndeadPlayers || CommonUtils.getPlayerType(instance).isUndead()) &&
                CategorySounds.replaceHurtAndDeathSounds
                ? "craftthesun:ds.random.player.hurt"
                : "game.player.hurt";
    }

    public static String getDeathSound(@NotNull EntityPlayer instance) {
        return (!CategorySounds.useSoundsOnlyForUndeadPlayers || CommonUtils.getPlayerType(instance).isUndead()) &&
                CategorySounds.replaceHurtAndDeathSounds
                ? "craftthesun:ds.random.player.die"
                : "game.player.die";
    }

    public static int getExperiencePoints(EntityPlayer instance, EntityPlayer killer) {
        if (instance.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")
                || !CommonUtils.isPlayerShouldDropInventory(instance)) {
            return 0;
        } else {
            int i = instance.experienceLevel * 7;
            return Math.min(i, 100);
        }
    }
}

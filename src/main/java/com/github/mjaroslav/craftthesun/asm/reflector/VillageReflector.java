package com.github.mjaroslav.craftthesun.asm.reflector;

import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryCommon;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lombok.val;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class VillageReflector {
    public static boolean isPlayerReputationTooLow(@NotNull Village instance, @NotNull String username) {
        val world = (World) ReflectionHelper.getPrivateValue(Village.class, instance, "worldObj", "field_75586_a");
        val player = world.getPlayerEntityByName(username);
        val value = instance.getReputationForPlayer(username);
        if (player != null)
            return (CommonUtils.isPlayerHaveVillageReputationFactor(player) ? value +
                    CategoryCommon.villageReputationFactor : value) <= -15;
        else
            return value <= -15;
    }
}

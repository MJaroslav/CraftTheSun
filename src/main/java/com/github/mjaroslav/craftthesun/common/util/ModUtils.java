package com.github.mjaroslav.craftthesun.common.util;

import org.jetbrains.annotations.NotNull;

import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;

@UtilityClass
public class ModUtils {
    public void doEstusEffects(@NotNull EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, "craftthesun:other.estus", 1F,
                player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        NetworkHandler.INSTANCE.sendEstusFX(player);
    }
}

package com.github.mjaroslav.craftthesun.asm.reflector;

import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.player.EntityPlayer;

public class EntityPlayerReflector {
    public static String getHurtSound(@NotNull EntityPlayer instance) {
        return "craftthesun:ds.random.player.hurt";
    }

    public static String getDeathSound(@NotNull EntityPlayer instance) {
        return "craftthesun:ds.random.player.die";
    }
}

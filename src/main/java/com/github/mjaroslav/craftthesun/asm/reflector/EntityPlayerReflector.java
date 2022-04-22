package com.github.mjaroslav.craftthesun.asm.reflector;

import com.github.mjaroslav.craftthesun.lib.CategoryGeneral;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public class EntityPlayerReflector {
    public static String getHurtSound(@NotNull EntityPlayer instance) {
        return CategoryGeneral.CategoryClient.CategorySounds.replaceHurtAndDeathSounds
                ? "craftthesun:ds.random.player.hurt"
                : "game.player.hurt";
    }

    public static String getDeathSound(@NotNull EntityPlayer instance) {
        return CategoryGeneral.CategoryClient.CategorySounds.replaceHurtAndDeathSounds
                ? "craftthesun:ds.random.player.die"
                : "game.player.die";
    }
}

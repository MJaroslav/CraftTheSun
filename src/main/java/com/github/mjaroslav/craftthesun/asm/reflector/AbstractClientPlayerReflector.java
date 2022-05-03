package com.github.mjaroslav.craftthesun.asm.reflector;

import com.github.mjaroslav.craftthesun.common.data.SyncData;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lombok.val;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.entity.AbstractClientPlayer.locationStevePng;

public class AbstractClientPlayerReflector {
    public static final ResourceLocation locationHollowPng = new ResourceLocation("craftthesun:textures/entity/hollow.png");

    public static ResourceLocation getLocationSkin(@NotNull AbstractClientPlayer instance) {
        // TODO: Try do it with AT
        val locationSkin = (ResourceLocation) ReflectionHelper.getPrivateValue(AbstractClientPlayer.class, instance,
                "locationSkin", "field_110312_d");
        val type = CommonUtils.getPlayerType(instance);
        return CategoryClient.replaceSkinForHollow && type == SyncData.PlayerType.HOLLOW ?
                locationHollowPng : locationSkin == null ? locationStevePng : locationSkin;
    }
}

package com.github.mjaroslav.craftthesun.common.handler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LivingEventHandler {
    public static final LivingEventHandler INSTANCE = new LivingEventHandler();

    @SubscribeEvent
    public void onEntityConstructing(@NotNull EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer)
            PlayerEventHandler.INSTANCE.onPlayerConstructing(event, (EntityPlayer) event.entity);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEventLowest(@NotNull LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer)
            PlayerEventHandler.INSTANCE.onPlayerDeathEvent(event, (EntityPlayer) event.entityLiving);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDropEventLowest(@NotNull LivingDropsEvent event) {

        for(var entityItem : event.drops) {

        }
    }
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}

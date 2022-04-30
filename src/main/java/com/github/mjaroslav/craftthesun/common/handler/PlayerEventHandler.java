package com.github.mjaroslav.craftthesun.common.handler;

import com.github.mjaroslav.craftthesun.common.data.CraftTheSunEEP;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerEventHandler {
    public static final PlayerEventHandler INSTANCE = new PlayerEventHandler();

    public void onPlayerDeathEventLowest(@NotNull LivingDeathEvent event, @NotNull EntityPlayer player) {
        CraftTheSunEEP.get(player).onPlayerDeathEvent(event, player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRespawnEventHighest(@NotNull PlayerRespawnEvent event) {
        CommonUtils.tryMakePlayerUndead(event);
        CraftTheSunEEP.get(event.player).onPlayerRespawn(event);
    }

    @SubscribeEvent
    public void onPlayerWakeUpEvent(@NotNull PlayerWakeUpEvent event) {
        if (!event.entityPlayer.worldObj.isRemote && !event.wakeImmediatly && !event.updateWorld)
            CommonUtils.tryRefillEstusFlasks(event.entityPlayer);
    }

    @SubscribeEvent
    public void onPlayerTickEvent(@NotNull PlayerTickEvent event) {
        CommonUtils.tryHardSetHungerValue(event);
        CraftTheSunEEP.get(event.player).onPlayerTickEvent(event);
    }

    @SubscribeEvent
    public void onStartTracking(@NotNull StartTracking event) {
        CraftTheSunEEP.get(event.entityPlayer).onStartTrackingEvent(event);
    }

    @SubscribeEvent
    public void onPlayerUseItemFinishEvent(@NotNull PlayerUseItemEvent.Finish event) {
        CommonUtils.tryTakeEstusFromItemInUse(event);
    }

    public void onPlayerConstructing(@NotNull EntityConstructing event, @NotNull EntityPlayer player) {
        if (CraftTheSunEEP.get((EntityPlayer) event.entity) == null)
            CraftTheSunEEP.register(player);
    }

    @SubscribeEvent
    public void onPlayerJoinEvent(@NotNull PlayerLoggedInEvent event) {
        CraftTheSunEEP.get(event.player).onPlayerJoinEvent(event);
    }

    @SubscribeEvent
    public void onClone(@NotNull Clone event) {
        CraftTheSunEEP.clone(event.original, event.entityPlayer);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
}

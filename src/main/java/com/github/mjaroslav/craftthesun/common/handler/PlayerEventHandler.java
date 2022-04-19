package com.github.mjaroslav.craftthesun.common.handler;

import com.github.mjaroslav.craftthesun.client.audio.PlayerFallSound;
import com.github.mjaroslav.craftthesun.common.data.CraftTheSunEEP;
import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.data.EstusDropCache;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.item.ModItems;
import com.github.mjaroslav.craftthesun.common.util.ModUtils;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerEventHandler {
    public static final PlayerEventHandler INSTANCE = new PlayerEventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEvent(@NotNull LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) onPlayerDeathEvent(event, (EntityPlayer) event.entityLiving);
    }

    public void onPlayerDeathEvent(@NotNull LivingDeathEvent event, @NotNull EntityPlayer player) {
        CraftTheSunEEP.get(player).onPlayerDeathEvent(event, player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        CraftTheSunEEP.get(event.player).onPlayerRespawn(event);
    }

    @SubscribeEvent
    public void onPlayerWakeUpEvent(@NotNull PlayerWakeUpEvent event) {
        if (!event.entityPlayer.worldObj.isRemote && !event.wakeImmediatly && !event.updateWorld)
            for (var index = 0; index < event.entityPlayer.inventory.getSizeInventory(); index++) {
                val stack = event.entityPlayer.inventory.getStackInSlot(index);
                if (stack != null && stack.getItem() == ModItems.estusFlask) ItemEstusFlask.refillEstusFlask(stack);
            }
    }

    private final Map<String, Boolean> SOUND_PLAYED = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTickEvent(@NotNull PlayerTickEvent event) {
        if (event.phase == Phase.START) return;
        if (!event.player.worldObj.isRemote) {
            if (event.player.worldObj.getTotalWorldTime() % 20 == 0) {
                event.player.getFoodStats().setFoodLevel(18);
                event.player.getFoodStats().setFoodSaturationLevel(18);
            }
        } else {
            val falling = !event.player.onGround;
            if (falling && !SOUND_PLAYED.getOrDefault(event.player.getCommandSenderName(), false))
                if (!event.player.capabilities.isFlying && (event.player.posY
                        - event.player.worldObj.getHeightValue(event.player.serverPosX, event.player.serverPosZ) > 24f)
                        && event.player.motionY < 0)
                    playPlayerFallSound(event.player);
            if (!falling) SOUND_PLAYED.put(event.player.getCommandSenderName(), false);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playPlayerFallSound(@NotNull EntityPlayer player) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new PlayerFallSound(player));
        SOUND_PLAYED.put(player.getCommandSenderName(), true);
//        player.worldObj.playSoundAtEntity(player, "craftthesun:ds.random.player.fall", 1F,
//                player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
    }

    @SubscribeEvent
    public void onPlayerUseItemEventFinish(@NotNull PlayerUseItemEvent.Finish event) {
        if (event.item.getItem() == ModItems.estusFlask || event.entityPlayer.worldObj.isRemote) return;
        val container = EstusContainer.getFromStack(event.item);
        if (container == null) return;
        var healCount = 0f;
        if (event.result != null && event.result.stackSize > 0) {
            container.decrease(!event.entityPlayer.capabilities.isCreativeMode);
            EstusContainer.saveToStack(container, event.result);
            healCount = 8;
        } else if (container.isInfinity()) healCount = event.entityPlayer.getMaxHealth();
        else healCount = 8 * container.getCount();
        if (healCount > 0) {
            ModUtils.doEstusEffects(event.entityPlayer);
            event.entityPlayer.heal(healCount);
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(@NotNull EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && CraftTheSunEEP.get((EntityPlayer) event.entity) == null)
            CraftTheSunEEP.register((EntityPlayer) event.entity);
    }

    @SubscribeEvent
    public void onClone(@NotNull Clone event) {
        CraftTheSunEEP.clone(event.original, event.entityPlayer);
    }
}

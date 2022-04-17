package com.github.mjaroslav.craftthesun.common.handler;

import com.github.mjaroslav.craftthesun.common.data.EstusDropCache;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.item.ModItems;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerEventHandler {
    public static final PlayerEventHandler INSTANCE = new PlayerEventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEvent(@NotNull LivingDeathEvent event) {
        if (!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityPlayer) {
            val player = (EntityPlayer) event.entityLiving;
            val cache = EstusDropCache.get(player);
            for (var slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
                val stack = player.inventory.getStackInSlot(slot);
                if (stack != null && stack.getItem() == ModItems.estusFlask) {
                    cache.CACHE.put(slot, stack.copy());
                    player.inventory.setInventorySlotContents(slot, null);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (!event.player.worldObj.isRemote) {
            val cache = EstusDropCache.get(event.player);
            cache.CACHE.forEach((slot, estusStack) -> {
                        ItemEstusFlask.refillEstus(estusStack);
                        event.player.inventory.setInventorySlotContents(slot, estusStack);
                    }
            );
            cache.CACHE.clear();
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUpEvent(@NotNull PlayerWakeUpEvent event) {
        if (!event.entityPlayer.worldObj.isRemote)
            for (var index = 0; index < event.entityPlayer.inventory.getSizeInventory(); index++) {
                val stack = event.entityPlayer.inventory.getStackInSlot(index);
                if (stack != null && stack.getItem() == ModItems.estusFlask)
                    ItemEstusFlask.refillEstus(stack);
            }
    }

    @SubscribeEvent
    public void onEntityConstructing(@NotNull EntityEvent.EntityConstructing e) {
        if (e.entity instanceof EntityPlayer && EstusDropCache.get((EntityPlayer) e.entity) == null)
            EstusDropCache.register((EntityPlayer) e.entity);
    }

    @SubscribeEvent
    public void onClone(@NotNull Clone e) {
        EstusDropCache.clone(e.original, e.entityPlayer);
    }
}

package com.github.mjaroslav.craftthesun.common.util;

import com.github.mjaroslav.craftthesun.CraftTheSunMod;
import com.github.mjaroslav.craftthesun.common.data.CraftTheSunEEP;
import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.data.SyncData;
import com.github.mjaroslav.craftthesun.common.init.ModItems;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CommonUtils {
    public void doEstusEffects(@NotNull EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, "craftthesun:ds.estus", 1F,
                player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        CraftTheSunMod.proxy.spawnParticle("estus", player);
    }

    public void tryTakeEstusFromItemInUse(@NotNull PlayerUseItemEvent.Finish event) {
        if (event.item.getItem() == ModItems.estusFlask || event.entityPlayer.worldObj.isRemote)
            return;
        val container = EstusContainer.getFromStack(event.item);
        if (container == null || !container.isExtra()) return;
        if (event.result != null && event.item != event.result) {
            EstusContainer.removeEstusContainer(event.result);
        }
        doEstusEffects(event.entityPlayer);
        event.entityPlayer.heal(8);
    }

    public boolean isHungerFixed(@NotNull EntityPlayer player) {
        val type = CraftTheSunEEP.get(player).getSyncData().getType();
        var flag = false;
        switch (CategoryGeneral.CategoryCommon.CategoryHunger.fixHungerValueFor) {
            default:
                return false;
            case 0:
                return true;
            case 1:
                return type != SyncData.PlayerType.REAL_HUMAN;
            case 2:
                return type == SyncData.PlayerType.HOLLOW;
            case 3:
                return type == SyncData.PlayerType.REAL_HUMAN;
        }
    }

    public void tryHardSetHungerValue(@NotNull TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.worldObj.isRemote ||
                !CategoryGeneral.CategoryCommon.CategoryHunger.enable || !isHungerFixed(event.player))
            return;
        if (event.player.worldObj.getTotalWorldTime() % CategoryGeneral.CategoryCommon.CategoryHunger.setHungerValueDelay == 0) {
            event.player.getFoodStats().setFoodLevel(CategoryGeneral.CategoryCommon.CategoryHunger.fixHungerValue);
            event.player.getFoodStats().setFoodSaturationLevel(CategoryGeneral.CategoryCommon.CategoryHunger.fixSaturationValue);
        }
    }

    public void tryUpdateData(@NotNull TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.worldObj.isRemote)
            return;
        val data = CraftTheSunEEP.get(event.player).getSyncData();
//        if (!data.isChanged())
//            return;
        // TODO: Make tracker
        if (event.player.worldObj.getTotalWorldTime() % 20 == 0) {
            val packet = data.getSyncPacket();
            packet.setUsername(event.player.getCommandSenderName());
            NetworkHandler.INSTANCE.sendToAllAround(packet, event.player, 64);
        }
    }

    public void tryRefillEstusFlasks(@NotNull EntityPlayer player) {
        for (var index = 0; index < player.inventory.getSizeInventory(); index++) {
            val stack = player.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == ModItems.estusFlask) ItemEstusFlask.refillEstusFlask(stack);
        }
    }

    public void tryAddEstusToFoodDrop(@NotNull LivingDropsEvent event) {
        if (CategoryGeneral.CategoryCommon.estusInFoodDropChance <= 0)
            return;
        for (var entityItem : event.drops)
            if (entityItem.getEntityItem().getItem() instanceof ItemFood &&
                    event.entityLiving.worldObj.rand.nextInt(101) <= CategoryGeneral.CategoryCommon.estusInFoodDropChance)
                EstusContainer.saveToStack(EstusContainer.createExtra(), entityItem.getEntityItem());
    }
}

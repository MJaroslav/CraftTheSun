package com.github.mjaroslav.craftthesun.common.util;

import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.item.ModItems;
import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ModUtils {
    public void doEstusEffects(@NotNull EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, "craftthesun:ds.estus", 1F,
                player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        NetworkHandler.INSTANCE.sendEstusFX(player);
    }

    public void tryTakeEstusFromItemInUse(@NotNull PlayerUseItemEvent.Finish event) {
        if (event.item.getItem() == ModItems.estusFlask || event.entityPlayer.worldObj.isRemote)
            return;
        val container = EstusContainer.getFromStack(event.item);
        if (container == null || !container.isExtra()) return;
        if (event.result != null)
            EstusContainer.removeEstusContainer(event.result);
        doEstusEffects(event.entityPlayer);
        event.entityPlayer.heal(8);
    }

    public void tryHardSetHungerValue(@NotNull TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.worldObj.isRemote)
            return;
        if (event.player.worldObj.getTotalWorldTime() % 20 == 0) {
            event.player.getFoodStats().setFoodLevel(18);
            event.player.getFoodStats().setFoodSaturationLevel(18);
        }
    }

    public void tryRefillEstusFlasks(@NotNull EntityPlayer player) {
        for (var index = 0; index < player.inventory.getSizeInventory(); index++) {
            val stack = player.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == ModItems.estusFlask) ItemEstusFlask.refillEstusFlask(stack);
        }
    }
}

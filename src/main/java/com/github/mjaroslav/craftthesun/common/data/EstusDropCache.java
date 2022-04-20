package com.github.mjaroslav.craftthesun.common.data;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.init.ModItems;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import lombok.val;
import lombok.var;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class EstusDropCache {
    public static final String TAG_ESTUS_DROP_CACHE = "estus_drop_cache";
    public static final String TAG_SLOT = "slot";
    public static final String TAG_STACK = "stack";

    private final Map<Integer, ItemStack> CACHE = new HashMap<>();

    public void loadFromNBT(@NotNull NBTTagCompound compound) {
        CACHE.clear();
        val cacheTag = compound.getTagList(TAG_ESTUS_DROP_CACHE, 10);
        for (var i = 0; i < cacheTag.tagCount(); i++) {
            val entryTag = cacheTag.getCompoundTagAt(i);
            CACHE.put(entryTag.getInteger(TAG_SLOT),
                    ItemStack.loadItemStackFromNBT(entryTag.getCompoundTag(TAG_STACK)));
        }
    }

    public void saveToNBT(@NotNull NBTTagCompound compound) {
        val cacheTag = new NBTTagList();
        CACHE.forEach((slot, stack) -> {
            val entryTag = new NBTTagCompound();
            entryTag.setInteger(TAG_SLOT, slot);
            val stackTag = new NBTTagCompound();
            stack.writeToNBT(stackTag);
            entryTag.setTag(TAG_STACK, stackTag);
            cacheTag.appendTag(entryTag);
        });
        compound.setTag(TAG_ESTUS_DROP_CACHE, cacheTag);
    }

    public void onPlayerDeathEvent(@NotNull LivingDeathEvent event, @NotNull EntityPlayer player) {
        if (!player.worldObj.isRemote) for (var slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            val stack = player.inventory.getStackInSlot(slot);
            if (stack != null && stack.getItem() == ModItems.estusFlask) {
                CACHE.put(slot, stack.copy());
                player.inventory.setInventorySlotContents(slot, null);
            }
        }
    }

    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (!event.player.worldObj.isRemote) {
            CACHE.forEach((slot, estusStack) -> {
                ItemEstusFlask.refillEstusFlask(estusStack);
                event.player.inventory.setInventorySlotContents(slot, estusStack);
            });
            CACHE.clear();
        }
    }
}

package com.github.mjaroslav.craftthesun.common.data;

import com.github.mjaroslav.craftthesun.common.init.ModItems;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import lombok.val;
import lombok.var;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

// TODO: remade by death drops canceling
public final class DropCache {
    public static final String TAG_DROP_CACHE = "drop_cache";
    public static final String TAG_SLOT = "slot";
    public static final String TAG_STACK = "stack";

    private final Map<Integer, ItemStack> CACHE = new HashMap<>();

    public void loadFromNBT(@NotNull NBTTagCompound compound) {
        CACHE.clear();
        val cacheTag = compound.getTagList(TAG_DROP_CACHE, 10);
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
        compound.setTag(TAG_DROP_CACHE, cacheTag);
    }

    void onPlayerDeathEvent(@NotNull LivingDeathEvent event, @NotNull EntityPlayer player) {
        if (!player.worldObj.isRemote)
            for (var slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
                val stack = player.inventory.getStackInSlot(slot);
                if (stack != null && (stack.getItem() == ModItems.estusFlask || stack.getItem() == ModItems.darkSign)) {
                    CACHE.put(slot, stack.copy());
                    player.inventory.setInventorySlotContents(slot, null);
                }
            }
    }

    void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (!event.player.worldObj.isRemote) {
            CACHE.forEach((slot, estusStack) -> {
                if (estusStack.getItem() == ModItems.estusFlask)
                    ItemEstusFlask.refillEstusFlask(estusStack);
                event.player.inventory.setInventorySlotContents(slot, estusStack);
            });
            CACHE.clear();
        }
    }
}

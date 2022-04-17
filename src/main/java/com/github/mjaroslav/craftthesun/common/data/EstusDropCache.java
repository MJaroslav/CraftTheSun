package com.github.mjaroslav.craftthesun.common.data;

import lombok.val;
import lombok.var;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class EstusDropCache implements IExtendedEntityProperties {
    public static final String IDENTIFIER = "EstusDropCache";
    public static final String TAG_ESTUS_DROP_CACHE = "estus_drop_cache";
    public static final String TAG_SIZE = "size";
    public static final String TAG_SLOT = "slot";
    public static final String TAG_ESTUS_STACK = "estusStack";

    public final Map<Integer, ItemStack> CACHE = new HashMap<>();

    @Override
    public void saveNBTData(@NotNull NBTTagCompound compound) {
        val list = new NBTTagList();
        CACHE.forEach((slot, estusStack) -> {
            val entry = new NBTTagCompound();
            val estusStackTag = new NBTTagCompound();
            entry.setInteger(TAG_SLOT, slot);
            estusStack.writeToNBT(estusStackTag);
            entry.setTag(TAG_ESTUS_STACK, estusStackTag);
            list.appendTag(entry);
        });
        compound.setTag(TAG_ESTUS_DROP_CACHE, list);
    }

    @Override
    public void loadNBTData(@NotNull NBTTagCompound compound) {
        CACHE.clear();
        val list = compound.getTagList(TAG_ESTUS_DROP_CACHE, 10);
        for (var i = 0; i < list.tagCount(); i++) {
            val entry = list.getCompoundTagAt(i);
            val slot = entry.getInteger(TAG_SLOT);
            val estusStack = ItemStack.loadItemStackFromNBT(entry.getCompoundTag(TAG_ESTUS_STACK));
            CACHE.put(slot, estusStack);
        }
    }

    @Override
    public void init(@NotNull Entity entity, @NotNull World world) {
    }

    @UnknownNullability
    public static EstusDropCache get(@NotNull EntityPlayer player) {
        return (EstusDropCache) player.getExtendedProperties(IDENTIFIER);
    }

    public static void clone(@Nullable EntityPlayer original, @Nullable EntityPlayer current) {
        if (original != null && current != null) {
            val nbt = new NBTTagCompound();
            get(original).saveNBTData(nbt);
            get(current).loadNBTData(nbt);
        }
    }

    public static void register(@NotNull EntityPlayer player) {
        player.registerExtendedProperties(IDENTIFIER, new EstusDropCache());
    }
}

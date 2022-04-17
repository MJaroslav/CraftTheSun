package com.github.mjaroslav.craftthesun.common.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import com.github.mjaroslav.craftthesun.lib.ModInfo;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import lombok.val;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class CraftTheSunEEP implements IExtendedEntityProperties {
    private final EstusDropCache estusDropCache = new EstusDropCache();

    @Override
    public void saveNBTData(@NotNull NBTTagCompound compound) {
        val rootTag = new NBTTagCompound();
        estusDropCache.saveToNBT(rootTag);
        compound.setTag(ModInfo.MOD_ID, rootTag);
    }

    @Override
    public void loadNBTData(@NotNull NBTTagCompound compound) {
        val rootTag = compound.getCompoundTag(ModInfo.MOD_ID);
        estusDropCache.loadFromNBT(rootTag);
    }

    @Override
    public void init(@NotNull Entity entity, @NotNull World world) {}

    public void onPlayerDeathEvent(@NotNull LivingDeathEvent event, @NotNull EntityPlayer player) {
        estusDropCache.onPlayerDeathEvent(event, player);
    }

    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        estusDropCache.onPlayerRespawn(event);
    }

    @UnknownNullability
    public static CraftTheSunEEP get(@NotNull EntityPlayer player) {
        return (CraftTheSunEEP) player.getExtendedProperties(ModInfo.MOD_ID);
    }

    public static void clone(@Nullable EntityPlayer original, @Nullable EntityPlayer current) {
        if (original != null && current != null) {
            val nbt = new NBTTagCompound();
            get(original).saveNBTData(nbt);
            get(current).loadNBTData(nbt);
        }
    }

    public static void register(@NotNull EntityPlayer player) {
        player.registerExtendedProperties(ModInfo.MOD_ID, new CraftTheSunEEP());
    }
}

package com.github.mjaroslav.craftthesun.common.data;

import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import lombok.Getter;
import lombok.val;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@Getter
public final class CraftTheSunEEP implements IExtendedEntityProperties {
    private final EstusDropCache estusDropCache = new EstusDropCache();
    private final SyncData syncData = new SyncData();

    @Override
    public void saveNBTData(@NotNull NBTTagCompound compound) {
        val rootTag = new NBTTagCompound();
        estusDropCache.saveToNBT(rootTag);
        syncData.saveToNBT(rootTag);
        compound.setTag(ModInfo.MOD_ID, rootTag);
    }

    @Override
    public void loadNBTData(@NotNull NBTTagCompound compound) {
        val rootTag = compound.getCompoundTag(ModInfo.MOD_ID);
        estusDropCache.loadFromNBT(rootTag);
        syncData.loadFromNBT(rootTag);
    }

    @Override
    public void init(@NotNull Entity entity, @NotNull World world) {
    }

    public void onPlayerDeathEvent(@NotNull LivingDeathEvent event, @NotNull EntityPlayer player) {
        estusDropCache.onPlayerDeathEvent(event, player);
    }

    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        estusDropCache.onPlayerRespawn(event);
        if (event.player.worldObj.isRemote)
            return;
        val packet = syncData.getSyncPacket();
        packet.setUsername(event.player.getCommandSenderName());
        NetworkHandler.INSTANCE.sendTo(packet, event.player);
    }

    public void onStartTrackingEvent(@NotNull StartTracking event) {
        if (event.target instanceof EntityPlayer) {
            val packet = syncData.getSyncPacket();
            packet.setUsername(event.entityPlayer.getCommandSenderName());
            NetworkHandler.INSTANCE.sendTo(packet, (EntityPlayer) event.target);
        }
    }

    public void onPlayerTickEvent(@NotNull PlayerTickEvent event) {
        if (event.phase == Phase.START || event.player.worldObj.isRemote || !syncData.isChanged())
            return;
        val packet = syncData.getSyncPacket();
        packet.setUsername(event.player.getCommandSenderName());
        CommonUtils.sendPacketToTrackingPlayers(event.player, packet, true);
        syncData.setChanged(false);
    }

    public void onPlayerJoinEvent(@NotNull PlayerLoggedInEvent event) {
        if (event.player.worldObj.isRemote)
            return;
        val packet = syncData.getSyncPacket();
        packet.setUsername(event.player.getCommandSenderName());
        NetworkHandler.INSTANCE.sendTo(packet, event.player);
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

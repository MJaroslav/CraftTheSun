package com.github.mjaroslav.craftthesun.common.network.packet;

import com.github.mjaroslav.craftthesun.common.data.CraftTheSunEEP;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class S00SyncData extends AbstractPacket<S00SyncData> {
    private String username;
    private NBTTagCompound data;

    @Override
    public void fromBytes(@NotNull ByteBuf buf) {
        username = ByteBufUtils.readUTF8String(buf);
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(@NotNull ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, username);
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    public void handleClientSide(@NotNull S00SyncData message, @NotNull EntityClientPlayerMP player) {
        val entityPlayer = player.worldObj.getPlayerEntityByName(message.username);
        if (entityPlayer != null)
            CraftTheSunEEP.get(entityPlayer).getSyncData().handleSyncPacket(message);
    }
}

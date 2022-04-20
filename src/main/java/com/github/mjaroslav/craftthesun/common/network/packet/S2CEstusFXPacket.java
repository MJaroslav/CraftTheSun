package com.github.mjaroslav.craftthesun.common.network.packet;

import com.github.mjaroslav.craftthesun.CraftTheSunMod;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Deprecated
public class S2CEstusFXPacket extends AbstractPacket<S2CEstusFXPacket> {
    private String username;

    public S2CEstusFXPacket(@NotNull EntityPlayer player) {
        username = player.getCommandSenderName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleClientSide(@NotNull S2CEstusFXPacket message, @NotNull EntityClientPlayerMP player) {
        val target = player.worldObj.getPlayerEntityByName(message.username);
        CraftTheSunMod.proxy.spawnParticle("estus", 0, 0, 0, player);
    }

    @Override
    public void fromBytes(@NotNull ByteBuf buf) {
        username = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(@NotNull ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, username);
    }
}

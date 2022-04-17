package com.github.mjaroslav.craftthesun.common.network.packet;

import org.jetbrains.annotations.NotNull;

import com.github.mjaroslav.craftthesun.client.particle.EntityEstusFX;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

@NoArgsConstructor
public class S2CEstusFXPacket extends AbstractPacket<S2CEstusFXPacket> {
    private String username;

    public S2CEstusFXPacket(@NotNull EntityPlayer player) {
        username = player.getCommandSenderName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleClientSide(@NotNull S2CEstusFXPacket message, @NotNull EntityClientPlayerMP player) {
        val target = player.worldObj.getPlayerEntityByName(message.username);
        for (var i = 0; i < 360; i += 4) {
            val fx = new EntityEstusFX(player.worldObj, target, i);
            fx.setColor(0xFFFF00);
            fx.setFadeColor(0xFF3100);
            Minecraft.getMinecraft().effectRenderer.addEffect(fx);
        }
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

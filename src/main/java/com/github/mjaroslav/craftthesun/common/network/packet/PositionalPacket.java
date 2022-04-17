package com.github.mjaroslav.craftthesun.common.network.packet;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PositionalPacket<REQ extends IMessage> extends AbstractPacket<REQ> {
    protected double x, y, z;

    @Override
    public void fromBytes(@NotNull ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    @Override
    public void toBytes(@NotNull ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }
}

package com.github.mjaroslav.craftthesun.common.network.packet;

import com.github.mjaroslav.craftthesun.client.util.ClientUtils;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
public final class S01PacketBrokenItemAnimation extends AbstractPacket<S01PacketBrokenItemAnimation> {
    private int entityId;
    private ItemStack stack;

    public S01PacketBrokenItemAnimation(@NotNull EntityLivingBase entity, @NotNull ItemStack stack) {
        entityId = entity.getEntityId();
        this.stack = stack;
    }

    @Override
    public void fromBytes(@NotNull ByteBuf buf) {
        entityId = buf.readInt();
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(@NotNull ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeItemStack(buf, stack);
    }

    @Override
    public void handleClientSide(@NotNull S01PacketBrokenItemAnimation message, @NotNull EntityClientPlayerMP player) {
        val entity = player.worldObj.getEntityByID(message.getEntityId());
        if (entity instanceof EntityLivingBase) // Im careful
            ClientUtils.renderBrokenItemStackWithNoSound((EntityLivingBase) entity, message.stack);
    }
}

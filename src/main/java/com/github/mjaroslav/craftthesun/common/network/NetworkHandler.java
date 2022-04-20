package com.github.mjaroslav.craftthesun.common.network;

import com.github.mjaroslav.craftthesun.common.network.packet.S2CEstusFXPacket;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkHandler {
    public static final NetworkHandler INSTANCE = new NetworkHandler();

    private final SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_ID);

    public void init(FMLInitializationEvent event) {
        int id = 0;
        wrapper.registerMessage(S2CEstusFXPacket.class, S2CEstusFXPacket.class, id++, Side.CLIENT);
    }

    public void sendToAllAround(@NotNull IMessage message, @NotNull EntityPlayer player, double radius) {
        wrapper.sendToAllAround(message,
                new TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, radius));
    }

    public void sendToAllAround(@NotNull IMessage message, @NotNull World world, double x, double y, double z,
                                double radius) {
        wrapper.sendToAllAround(message, new TargetPoint(world.provider.dimensionId, x, y, z, radius));
    }

    public void sendToAllAround(@NotNull IMessage message, int dimId, double x, double y, double z, double radius) {
        wrapper.sendToAllAround(message, new TargetPoint(dimId, x, y, z, radius));
    }

    public void sendToAll(@NotNull IMessage message) {
        wrapper.sendToAll(message);
    }

    public void sendToServer(@NotNull IMessage message) {
        wrapper.sendToServer(message);
    }

    @Deprecated
    public void sendEstusFX(@NotNull EntityPlayer player) {
        sendToAllAround(new S2CEstusFXPacket(player), player, 64);
    }
}

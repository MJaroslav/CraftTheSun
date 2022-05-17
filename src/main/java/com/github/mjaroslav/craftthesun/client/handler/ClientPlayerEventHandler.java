package com.github.mjaroslav.craftthesun.client.handler;

import com.github.mjaroslav.craftthesun.client.util.ClientUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientPlayerEventHandler {
    public static final ClientPlayerEventHandler INSTANCE = new ClientPlayerEventHandler();

    @SubscribeEvent
    public void onPlayerTickEvent(@NotNull TickEvent.PlayerTickEvent event) {
        ClientUtils.tryTriggerFallSound(event);
    }

    @SubscribeEvent
    public void onItemTooltipEvent(@NotNull ItemTooltipEvent event) {
        ClientUtils.tryAddTooltipWithEstus(event);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
}

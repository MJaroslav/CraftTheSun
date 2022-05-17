package com.github.mjaroslav.craftthesun.client.handler;

import com.github.mjaroslav.craftthesun.client.util.ClientUtils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientGuiEventHandler {
    public static final ClientGuiEventHandler INSTANCE = new ClientGuiEventHandler();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlayEventPreHighest(@NotNull RenderGameOverlayEvent.Pre event) {
        ClientUtils.tryHideHungerBar(event);
        ClientUtils.tryReplaceBossBar(event);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}

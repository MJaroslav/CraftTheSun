package com.github.mjaroslav.craftthesun.client.handler;

import com.github.mjaroslav.craftthesun.client.entity.healthbar.HealthBarManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientWorldEventHandler {
    public static final ClientWorldEventHandler INSTANCE = new ClientWorldEventHandler();

    @SubscribeEvent
    public void onRenderWorldLastEvent(@NotNull RenderWorldLastEvent event) {
        HealthBarManager.INSTANCE.updateFrame(event.partialTicks);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}

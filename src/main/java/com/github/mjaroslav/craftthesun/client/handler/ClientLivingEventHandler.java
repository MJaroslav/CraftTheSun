package com.github.mjaroslav.craftthesun.client.handler;

import com.github.mjaroslav.craftthesun.client.entity.boss.AdvancedBossStatus;
import com.github.mjaroslav.craftthesun.client.util.ClientUtils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientLivingEventHandler extends Gui {
    public static final ClientLivingEventHandler INSTANCE = new ClientLivingEventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurtEventLowest(@NotNull LivingHurtEvent event) {
        ClientUtils.tryUpdateBossStatuses(event);
    }

    @SubscribeEvent
    public void onRenderLivingEventPre(@NotNull RenderLivingEvent.Pre event) {
        AdvancedBossStatus.INSTANCE.addBoss(event.entity);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}

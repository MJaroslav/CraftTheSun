package com.github.mjaroslav.craftthesun.client.handler;

import com.github.mjaroslav.craftthesun.client.entity.healthbar.HealthBarManager;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientLivingEventHandler extends Gui {
    public static final ClientLivingEventHandler INSTANCE = new ClientLivingEventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurtEventLowest(@NotNull LivingHurtEvent event) {
        HealthBarManager.INSTANCE.updateBar(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingUpdateEventLowest(@NotNull LivingEvent.LivingUpdateEvent event) {
        HealthBarManager.INSTANCE.syncBar(event);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}

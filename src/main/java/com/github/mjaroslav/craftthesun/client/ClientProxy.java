package com.github.mjaroslav.craftthesun.client;

import org.jetbrains.annotations.NotNull;

import com.github.mjaroslav.craftthesun.client.handler.ClientEventHandler;
import com.github.mjaroslav.craftthesun.common.CommonProxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(@NotNull FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
    }
}

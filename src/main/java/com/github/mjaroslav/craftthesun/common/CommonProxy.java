package com.github.mjaroslav.craftthesun.common;

import com.github.mjaroslav.craftthesun.common.handler.PlayerEventHandler;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.item.ModItems;
import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public class CommonProxy {
    public void constr(@NotNull FMLConstructionEvent event) {}

    public void preInit(@NotNull FMLPreInitializationEvent event) {
        ModItems.preInit(event);
    }

    public void init(@NotNull FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(PlayerEventHandler.INSTANCE);
        NetworkHandler.INSTANCE.init(event);
    }

    public void postInit(@NotNull FMLPostInitializationEvent event) {}

    public void interModComms(@NotNull FMLInterModComms.IMCEvent event) {}

    public void loadComplete(@NotNull FMLLoadCompleteEvent event) {}

    public void serverAboutToStart(@NotNull FMLServerAboutToStartEvent event) {}

    public void serverStarting(@NotNull FMLServerStartingEvent event) {}

    public void serverStarted(@NotNull FMLServerStartedEvent event) {}

    public void serverStopping(@NotNull FMLServerStoppingEvent event) {}

    public void serverStopped(@NotNull FMLServerStoppedEvent event) {}
}

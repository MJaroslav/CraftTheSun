package com.github.mjaroslav.craftthesun.common.init;

import com.github.mjaroslav.craftthesun.client.handler.ClientGuiEventHandler;
import com.github.mjaroslav.craftthesun.client.handler.ClientLivingEventHandler;
import com.github.mjaroslav.craftthesun.client.handler.ClientPlayerEventHandler;
import com.github.mjaroslav.craftthesun.client.handler.ClientWorldEventHandler;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mjaroslav.mcmods.mjutils.module.Modular;
import mjaroslav.mcmods.mjutils.module.Module;
import mjaroslav.mcmods.mjutils.module.Proxy;

@SideOnly(Side.CLIENT)
@Module(ModInfo.MOD_ID)
public class ClientModule implements Modular {
    @Override
    public void init(FMLInitializationEvent event) {
        ClientLivingEventHandler.INSTANCE.register();
        ClientPlayerEventHandler.INSTANCE.register();
        ClientGuiEventHandler.INSTANCE.register();
        ClientWorldEventHandler.INSTANCE.register();
    }

    @Override
    public boolean canLoad() {
        return Proxy.isClient();
    }
}

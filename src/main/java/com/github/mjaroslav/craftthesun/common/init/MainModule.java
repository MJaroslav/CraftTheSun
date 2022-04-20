package com.github.mjaroslav.craftthesun.common.init;

import com.github.mjaroslav.craftthesun.common.handler.LivingEventHandler;
import com.github.mjaroslav.craftthesun.common.handler.PlayerEventHandler;
import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import mjaroslav.mcmods.mjutils.module.Modular;
import mjaroslav.mcmods.mjutils.module.Module;
import org.jetbrains.annotations.NotNull;

@Module(ModInfo.MOD_ID)
public class MainModule implements Modular {
    @Override
    public void init(@NotNull FMLInitializationEvent event) {
        PlayerEventHandler.INSTANCE.register();
        LivingEventHandler.INSTANCE.register();
        NetworkHandler.INSTANCE.init(event);
    }
}

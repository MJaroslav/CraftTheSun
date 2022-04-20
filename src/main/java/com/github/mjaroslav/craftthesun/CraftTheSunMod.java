package com.github.mjaroslav.craftthesun;

import com.github.mjaroslav.craftthesun.common.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mjaroslav.mcmods.mjutils.module.AnnotationBasedConfiguration;
import mjaroslav.mcmods.mjutils.module.ModuleSystem;
import org.jetbrains.annotations.NotNull;

import static com.github.mjaroslav.craftthesun.lib.ModInfo.*;

@Mod(modid = MOD_ID, name = NAME, version = VERSION, guiFactory = GUI_FACTORY)
public class CraftTheSunMod {
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static CommonProxy proxy;

    public static AnnotationBasedConfiguration config = new AnnotationBasedConfiguration(MOD_ID, LOG);
    public static ModuleSystem system;

    @Mod.Instance(MOD_ID)
    public static CraftTheSunMod instance;

    @Mod.EventHandler
    public void constr(@NotNull FMLConstructionEvent event) {
        system = new ModuleSystem(MOD_ID, config, proxy);
        system.initSystem(event);
    }

    @Mod.EventHandler
    public void preInit(@NotNull FMLPreInitializationEvent event) {
        system.preInit(event);
    }

    @Mod.EventHandler
    public void init(@NotNull FMLInitializationEvent event) {
        system.init(event);
    }

    @Mod.EventHandler
    public void postInit(@NotNull FMLPostInitializationEvent event) {
        system.postInit(event);
    }
}

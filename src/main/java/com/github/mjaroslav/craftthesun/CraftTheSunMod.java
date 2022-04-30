package com.github.mjaroslav.craftthesun;

import com.github.mjaroslav.craftthesun.common.CommonProxy;
import com.github.mjaroslav.craftthesun.common.command.CommandCraftTheSun;
import com.github.mjaroslav.craftthesun.common.init.ModItems;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mjaroslav.mcmods.mjutils.module.AnnotationBasedConfiguration;
import mjaroslav.mcmods.mjutils.module.ModuleSystem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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

    public static final CreativeTabs tab = new CreativeTabs(MOD_ID) {
        @SideOnly(Side.CLIENT)
        @Override
        public Item getTabIconItem() {
            return ModItems.humanity;
        }

//        @Override
//        public ItemStack getIconItemStack() {
//            return new ItemStack(ModItems.humanity, 1);
//        }
    };

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

    @Mod.EventHandler
    public void serverStarting(@NotNull FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCraftTheSun());
    }
}

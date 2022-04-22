package com.github.mjaroslav.craftthesun.client.gui;

import com.github.mjaroslav.craftthesun.CraftTheSunMod;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ModGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GUIConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class GUIConfig extends GuiConfig {
        public GUIConfig(GuiScreen parentScreen) {
            super(parentScreen, CraftTheSunMod.config.generalToElementList(), ModInfo.MOD_ID, false, false,
                    ModInfo.NAME);
        }
    }
}

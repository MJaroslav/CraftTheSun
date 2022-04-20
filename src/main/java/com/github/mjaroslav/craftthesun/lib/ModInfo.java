package com.github.mjaroslav.craftthesun.lib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ModInfo {
    public static final String MOD_ID = "craftthesun";
    public static final String NAME = "CraftTheSun";
    public static final String VERSION = "@VERSION@";

    public static final String CLIENT_PROXY = "com.github.mjaroslav.craftthesun.client.ClientProxy";
    public static final String SERVER_PROXY = "com.github.mjaroslav.craftthesun.server.ServerProxy";
    public static final String GUI_FACTORY = "com.github.mjaroslav.craftthesun.client.gui.ModGuiFactory";

    public static final Logger LOG = LogManager.getLogger(NAME);

    public static String prefix(@NotNull String str) {
        return MOD_ID + ":" + str;
    }
}

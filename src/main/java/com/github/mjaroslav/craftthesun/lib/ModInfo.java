package com.github.mjaroslav.craftthesun.lib;

import org.jetbrains.annotations.NotNull;

public class ModInfo {
    public static final String MOD_ID = "craftthesun";
    public static final String NAME = "CraftTheSun";
    public static final String VERSION = "@VERSION@";

    public static final String CLIENT_PROXY = "com.github.mjaroslav.craftthesun.client.ClientProxy";
    public static final String SERVER_PROXY = "com.github.mjaroslav.craftthesun.server.ServerProxy";

    public static String prefix(@NotNull String str) {
        return MOD_ID + ":" + str;
    }
}

package com.github.mjaroslav.craftthesun.lib;

import mjaroslav.mcmods.mjutils.module.ConfigurationCategory;
import mjaroslav.mcmods.mjutils.module.ConfigurationProperty;

@ConfigurationCategory(name = ConfigurationCategory.GENERAL_NAME, modID = ModInfo.MOD_ID, comment = ConfigurationCategory.GENERAL_COMMENT)
public class CategoryGeneral {
    @ConfigurationCategory(name = "client", comment = "Cosmetic settings")
    public static class CategoryClient {
        @ConfigurationProperty(comment = "Replace skin for hollow players (craftthesun:textures/entity/hollow.png)",
                defaultBoolean = true)
        public static boolean replaceSkinForHollow;

        @ConfigurationCategory(name = "bossbar", comment = "DS like boss bar settings")
        public static class CategoryBossBar {
            @ConfigurationProperty(comment = "Replace vanilla boss bar with DS like", defaultBoolean = true)
            public static boolean enable;

            @ConfigurationProperty(comment = "Damage reset time in ticks (yellow part of boss bar), use 0 for disable damage showing", defaultInt = 15, minInt = 0, maxInt = 200)
            public static int damageViewDelay;

            @ConfigurationProperty(comment = "Max count of boss bars for render", defaultInt = 3, minInt = 1, maxInt = 3)
            public static int maxBars;
        }

        @ConfigurationCategory(name = "estus_effect", comment = "Settings for estus effect")
        public static class CategoryEstusEffect {
            @ConfigurationProperty(comment = "Enable estus particle effect", defaultBoolean = true)
            public static boolean enable;

            @ConfigurationProperty(comment = "Count of estus effect paricles", defaultInt = 90, minInt = 4, maxInt = 360)
            public static int particleCount;

            @ConfigurationProperty(comment = "Disable of estus effect in first person mode")
            public static boolean disableOwnEffectInFirstPerson;
        }

        @ConfigurationCategory(name = "sounds", comment = "Sound additionals and replacments")
        public static class CategorySounds {
            @ConfigurationProperty(comment = "Enable fall death sound from DS for player falling", defaultBoolean = true)
            public static boolean enableFallSound;

            @ConfigurationProperty(comment = "Minimal height for trigger fall sound", defaultInt = 24)
            public static int fallSoundTriggerHeight;

            @ConfigurationProperty(comment = "Use DS hurt and death sounds for player", defaultBoolean = true)
            public static boolean replaceHurtAndDeathSounds;

            @ConfigurationProperty(comment = "Use DS hurt/death/fall sounds only for undead players")
            public static boolean useSoundsOnlyForUndeadPlayers;
        }
    }

    @ConfigurationCategory(name = "common", comment = "Common mod settinhs")
    public static class CategoryCommon {
        @ConfigurationProperty(comment = "Chance to add estus to mob ItemFood drop in percent, use 0 for disable", defaultInt = 5, minInt = 0, maxInt = 100)
        public static int estusInFoodDropChance;

        @ConfigurationProperty(comment = "Disable natural regeneration: -1 for disable feature, 0 for all players, 1 for common human players, 2 for all undead players, 3 for hollow undead players, 4 for human undead players", defaultInt = 2, minInt = -1, maxInt = 4)
        public static int disableNaturalRegenerationFor;

        @ConfigurationCategory(name = "hunger", comment = "Hunger system settings")
        public static class CategoryHunger {
            @ConfigurationProperty(comment = "Enable hunger tweaks, required for all category properties", defaultBoolean = true)
            public static boolean enable;

            @ConfigurationProperty(comment = "Show hunger bar for players with fixed hunger value")
            public static boolean showHungerBar;

            @ConfigurationProperty(comment = "Hunger value for players with fixed hunger value", defaultInt = 18, minInt = 0, maxInt = 20)
            public static int fixHungerValue;

            @ConfigurationProperty(comment = "Saturation value for players with fixed hunger value, use -1 for use value from fix_hunger_value", defaultInt = -1, minInt = -1, maxInt = 20)
            public static int fixSaturationValue;

            @ConfigurationProperty(comment = "Delay in ticks for resetting hunger and saturation values for players with fixed hunger value", minInt = 0, maxInt = 200, defaultInt = 20)
            public static int setHungerValueDelay;

            @ConfigurationProperty(comment = "Fix hunger value for: -1 for disable, 0 for all players, 1 for all common human players, 2 for all undead players, 3 for hollow undead players, 4 for human undead players", defaultInt = 2, minInt = -1, maxInt = 4)
            public static int fixHungerValueFor;
        }
    }
}

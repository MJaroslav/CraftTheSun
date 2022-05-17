package com.github.mjaroslav.craftthesun.asm;

import com.github.mjaroslav.craftthesun.asm.transformer.CraftTheSunTransformer;
import com.github.mjaroslav.reflectors.v4.Reflectors;
import com.github.mjaroslav.reflectors.v4.Reflectors.FMLLoadingPluginAdapter;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import lombok.val;
import net.minecraft.launchwrapper.IClassTransformer;

@IFMLLoadingPlugin.Name("CraftTheSunPlugin")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"com.github.mjaroslav.reflectors.v0",
        "com.github.mjaroslav.craftthesun.asm"})
public class CraftTheSunPlugin extends FMLLoadingPluginAdapter implements IFMLLoadingPlugin, IClassTransformer {
    @Override
    public String[] getASMTransformerClass() {
        Reflectors.enabledLogs = "true".equals(System.getProperty("cts.logReflectors"));
        return new String[]{getClass().getName()};
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        val prefix = "com.github.mjaroslav.craftthesun.asm.reflector.";
        switch (transformedName) {
            case "net.minecraft.entity.player.EntityPlayer":
                return CraftTheSunTransformer.entityPlayer(Reflectors.reflectClass(basicClass, transformedName, prefix + "EntityPlayerReflector"));
            case "net.minecraft.util.FoodStats":
                return CraftTheSunTransformer.foodStats(basicClass);
            case "net.minecraft.client.entity.AbstractClientPlayer":
                return Reflectors.reflectClass(basicClass, transformedName, prefix + "AbstractClientPlayerReflector");
            case "net.minecraft.village.Village":
                return Reflectors.reflectClass(basicClass, transformedName, prefix + "VillageReflector");
            case "net.minecraft.enchantment.EnchantmentHelper":
                return Reflectors.reflectClass(basicClass, transformedName, prefix + "EnchantmentHelperReflector");
            case "net.minecraft.entity.player.EntityPlayerMP":
                return CraftTheSunTransformer.entityPlayerMP(basicClass);
            case "net.minecraft.entity.monster.EntityWitch":
                return Reflectors.reflectClass(basicClass, transformedName, prefix + "EntityWitchReflector");
            default:
                return basicClass;
        }
    }
}

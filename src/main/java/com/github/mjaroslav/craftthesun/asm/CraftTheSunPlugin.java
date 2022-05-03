package com.github.mjaroslav.craftthesun.asm;

import com.github.mjaroslav.craftthesun.asm.reflector.AbstractClientPlayerReflector;
import com.github.mjaroslav.craftthesun.asm.reflector.EnchantmentHelperReflector;
import com.github.mjaroslav.craftthesun.asm.reflector.EntityPlayerReflector;
import com.github.mjaroslav.craftthesun.asm.reflector.VillageReflector;
import com.github.mjaroslav.craftthesun.asm.transformer.CraftTheSunTransformer;
import com.github.mjaroslav.reflectors.v2.Reflectors;
import com.github.mjaroslav.reflectors.v2.Reflectors.FMLLoadingPluginAdapter;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;

@IFMLLoadingPlugin.Name("CraftTheSunPlugin")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1001)
public class CraftTheSunPlugin extends FMLLoadingPluginAdapter implements IFMLLoadingPlugin, IClassTransformer {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{getClass().getName()};
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            case "net.minecraft.entity.player.EntityPlayer":
                return CraftTheSunTransformer.entityPlayer(Reflectors.reflectClass(basicClass, transformedName, EntityPlayerReflector.class.getName()));
            case "net.minecraft.util.FoodStats":
                return CraftTheSunTransformer.foodStats(basicClass);
            case "net.minecraft.client.entity.AbstractClientPlayer":
                return Reflectors.reflectClass(basicClass, transformedName, AbstractClientPlayerReflector.class.getName());
            case "net.minecraft.village.Village":
                return Reflectors.reflectClass(basicClass, transformedName, VillageReflector.class.getName());
            case "net.minecraft.enchantment.EnchantmentHelper":
                return Reflectors.reflectClass(basicClass, transformedName, EnchantmentHelperReflector.class.getName());
            default:
                return basicClass;
        }
    }
}

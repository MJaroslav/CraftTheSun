package com.github.mjaroslav.craftthesun.asm;

import com.github.mjaroslav.craftthesun.asm.reflector.EntityPlayerReflector;
import com.github.mjaroslav.craftthesun.asm.transformer.EntityPlayerTransformer;
import com.github.mjaroslav.craftthesun.asm.transformer.FoodStatsTransformer;
import com.github.mjaroslav.reflectors.v2.Reflectors;
import com.github.mjaroslav.reflectors.v2.Reflectors.FMLLoadingPluginAdapter;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import lombok.val;
import net.minecraft.launchwrapper.IClassTransformer;

@IFMLLoadingPlugin.Name("CraftTheSunPlugin")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1001)
public class CraftTheSunPlugin extends FMLLoadingPluginAdapter implements IFMLLoadingPlugin, IClassTransformer {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { getClass().getName() };
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.entity.player.EntityPlayer")) {
            val temp = Reflectors.reflectClass(basicClass, transformedName, EntityPlayerReflector.class.getName());
            return EntityPlayerTransformer.transform(temp);
        } else if (transformedName.equals("net.minecraft.util.FoodStats"))
            return FoodStatsTransformer.transform(basicClass);
        else return basicClass;
    }
}

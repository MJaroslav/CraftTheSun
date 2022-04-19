package com.github.mjaroslav.craftthesun.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;

import com.github.mjaroslav.craftthesun.asm.reflector.EntityPlayerReflector;
import com.github.mjaroslav.reflectors.v2.Reflectors;
import com.github.mjaroslav.reflectors.v2.Reflectors.FMLLoadingPluginAdapter;

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
        if (transformedName.equals("net.minecraft.entity.player.EntityPlayer"))
            return Reflectors.reflectClass(basicClass, transformedName, EntityPlayerReflector.class.getName());
        return basicClass;
    }

}

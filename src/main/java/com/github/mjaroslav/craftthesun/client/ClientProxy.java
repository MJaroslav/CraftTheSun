package com.github.mjaroslav.craftthesun.client;

import com.github.mjaroslav.craftthesun.client.particle.EntityEstusFX;
import com.github.mjaroslav.craftthesun.common.CommonProxy;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient.CategoryEstusEffect;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy extends CommonProxy {
    @Override
    public void spawnParticle(String name, double x, double y, double z, Object... args) {
        val mc = Minecraft.getMinecraft();
        if (name.equals("estus") && CategoryEstusEffect.enable) {
            val target = (EntityPlayer) args[0];
            if (target.equals(mc.thePlayer) && CategoryEstusEffect.disableOwnEffectInFirstPerson &&
                    mc.gameSettings.thirdPersonView == 0)
                return;
            val step = 360 / CategoryEstusEffect.particleCount;
            for (var i = 0; i < 360; i += step) {
                val fx = new EntityEstusFX(target.worldObj, target, i);
                fx.setColor(0xFFFF00);
                fx.setFadeColor(0xFF3100);
                Minecraft.getMinecraft().effectRenderer.addEffect(fx);
            }
        }
    }

    @Override
    public EntityPlayer getEntityPlayer(MessageContext ctx) {
        return Minecraft.getMinecraft().thePlayer;
    }
}

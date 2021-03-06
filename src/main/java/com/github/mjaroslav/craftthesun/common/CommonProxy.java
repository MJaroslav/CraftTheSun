package com.github.mjaroslav.craftthesun.common;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import mjaroslav.mcmods.mjutils.module.Proxy;
import net.minecraft.entity.player.EntityPlayer;

public abstract class CommonProxy extends Proxy {
    @Override
    public EntityPlayer getEntityPlayer(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public final void spawnParticle(String name, Object... args) {
        spawnParticle(name, 0, 0, 0, args);
    }
}

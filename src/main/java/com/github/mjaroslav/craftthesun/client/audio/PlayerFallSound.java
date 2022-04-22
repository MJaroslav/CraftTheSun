package com.github.mjaroslav.craftthesun.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PlayerFallSound extends MovingSound {
    private final EntityPlayer player;

    public PlayerFallSound(@NotNull EntityPlayer player) {
        super(new ResourceLocation("craftthesun:ds.random.player.fall"));
        this.player = player;
        // Pitch
        field_147663_c = player.worldObj.rand.nextFloat() * 0.1F + 0.9F;
    }

    @Override
    public void update() {
        if (player == null || player.isDead)
            donePlaying = true;
        else {
            xPosF = (float) player.posX;
            yPosF = (float) player.posY;
            zPosF = (float) player.posZ;
        }
    }
}

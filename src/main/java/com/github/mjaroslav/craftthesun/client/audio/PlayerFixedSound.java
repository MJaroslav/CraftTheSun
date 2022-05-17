package com.github.mjaroslav.craftthesun.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public class PlayerFixedSound extends MovingSound {
    private final EntityPlayer player;

    public PlayerFixedSound(@NotNull EntityPlayer player, @NotNull String sound) {
        super(new ResourceLocation(sound));
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

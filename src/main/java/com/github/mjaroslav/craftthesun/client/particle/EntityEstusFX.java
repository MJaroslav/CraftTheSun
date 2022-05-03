package com.github.mjaroslav.craftthesun.client.particle;

import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityEstusFX extends EntityFX {
    private final Vec3 rotation;

    private final EntityPlayer target;

    private float fadeColourRed;
    private float fadeColourGreen;
    private float fadeColourBlue;
    private boolean hasFadeColour;

    private double offsetMotion = 0;

    public EntityEstusFX(@NotNull World world, @NotNull EntityPlayer target, float angle) {
        super(world, target.posX, target.posY, target.posZ);
        this.target = target;
        rotation = Vec3.createVectorHelper(1, 0, 0);
        rotation.rotateAroundY((float) Math.PI / 180f * angle);
        syncTargetPosition();
        particleScale *= 0.75F;
        particleMaxAge = (48 + rand.nextInt(12)) / 4;
        noClip = true;
    }

    public void setColor(int color) {
        val r = ((color & 16711680) >> 16) / 255.0F;
        val g = ((color & 65280) >> 8) / 255.0F;
        val b = ((color & 255)) / 255.0F;
        setRBGColorF(r, g, b);
    }

    public void setFadeColor(int color) {
        fadeColourRed = ((color & 16711680) >> 16) / 255.0F;
        fadeColourGreen = ((color & 65280) >> 8) / 255.0F;
        fadeColourBlue = ((color & 255)) / 255.0F;
        hasFadeColour = true;
    }

    private void syncTargetPosition() {
        var y = target.posY + rotation.yCoord + offsetMotion;
        // Bullshit
        if (target != Minecraft.getMinecraft().thePlayer)
            y += target.getEyeHeight();
        double radius = 0.5;
        setPosition(target.posX + rotation.xCoord * radius, y, target.posZ + rotation.zCoord * radius);
    }

    @Override
    public void onUpdate() {
        if (target == null) {
            setDead();
            return;
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        syncTargetPosition();
        // All from fireworks
        if (particleAge++ >= particleMaxAge)
            setDead();

        if (particleAge > particleMaxAge / 2) {
            setAlphaF(1.0F - ((float) particleAge - (float) (particleMaxAge / 2)) / particleMaxAge);

            if (hasFadeColour) {
                particleRed += (fadeColourRed - particleRed) * 0.2F;
                particleGreen += (fadeColourGreen - particleGreen) * 0.2F;
                particleBlue += (fadeColourBlue - particleBlue) * 0.2F;
            }
        }

        setParticleTextureIndex(160 + (7 - particleAge * 8 / particleMaxAge));
        offsetMotion -= 0.1D;
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15728880;
    }

    @Override
    public float getBrightness(float f) {
        return 1.0F;
    }

}

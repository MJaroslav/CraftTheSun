package com.github.mjaroslav.craftthesun.asm.reflector;

import lombok.val;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.NotNull;

public class EntityWitchReflector {
    public static void attackEntityWithRangedAttack(@NotNull EntityWitch instance, @NotNull EntityLivingBase target, float p_82196_2_) {
        if (!instance.getAggressive()) {
            val entitypotion = new EntityPotion(instance.worldObj, instance, target.isEntityUndead() ? 16453 : 32732);
            entitypotion.rotationPitch -= -20.0F;
            val d0 = target.posX + target.motionX - instance.posX;
            val d1 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D - instance.posY;
            val d2 = target.posZ + target.motionZ - instance.posZ;
            val f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);

            if (f1 >= 8.0F && !target.isPotionActive(Potion.moveSlowdown))
                entitypotion.setPotionDamage(32698);
            else if (target.getHealth() >= 8.0F && !target.isPotionActive(Potion.poison))
                entitypotion.setPotionDamage(target.isEntityUndead() ? 16453 : 32660);
            else if (f1 <= 3.0F && !target.isPotionActive(Potion.weakness) && instance.worldObj.rand.nextFloat() < 0.25F) {
                entitypotion.setPotionDamage(32696);

                entitypotion.setThrowableHeading(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
                instance.worldObj.spawnEntityInWorld(entitypotion);
            }
        }
    }
}

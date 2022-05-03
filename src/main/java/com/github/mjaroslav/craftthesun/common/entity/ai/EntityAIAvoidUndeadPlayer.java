package com.github.mjaroslav.craftthesun.common.entity.ai;

import com.github.mjaroslav.craftthesun.common.command.UndeadPlayerSelectorForVillage;
import lombok.val;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityAIAvoidUndeadPlayer extends EntityAIBase {
    public final IEntitySelector undeadSelector;

    public final IEntitySelector selector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(@NotNull Entity entity) {
            return entity.isEntityAlive() && EntityAIAvoidUndeadPlayer.this.theEntity.getEntitySenses().canSee(entity)
                    && undeadSelector.isEntityApplicable(entity);
        }
    };

    private final EntityCreature theEntity;
    private final double farSpeed;
    private final double nearSpeed;
    private Entity closestLivingEntity;
    private final float distanceFromEntity;
    private PathEntity entityPathEntity;
    private final PathNavigate entityPathNavigate;

    public EntityAIAvoidUndeadPlayer(EntityCreature entity, float distanceFromEntity, double farSpeed,
                                     double nearSpeed) {
        theEntity = entity;
        this.distanceFromEntity = distanceFromEntity;
        this.farSpeed = farSpeed;
        this.nearSpeed = nearSpeed;
        entityPathNavigate = entity.getNavigator();
        setMutexBits(1);
        undeadSelector = entity instanceof EntityVillager ? new UndeadPlayerSelectorForVillage((EntityVillager) entity)
                : new UndeadPlayerSelectorForVillage();
    }

    @Override
    public boolean shouldExecute() {
        val list = theEntity.worldObj.selectEntitiesWithinAABB(EntityPlayer.class, theEntity.boundingBox
                .expand(distanceFromEntity, 3.0D, distanceFromEntity), selector);

        if (list.isEmpty())
            return false;

        closestLivingEntity = (Entity) list.get(0);

        val vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(theEntity, 16, 7,
                Vec3.createVectorHelper(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));

        if (vec3 == null)
            return false;
        else if (closestLivingEntity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) <
                closestLivingEntity.getDistanceSqToEntity(theEntity)) {
            return false;
        } else {
            entityPathEntity = entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return entityPathEntity != null && entityPathEntity.isDestinationSame(vec3);
        }
    }

    @Override
    public boolean continueExecuting() {
        return !entityPathNavigate.noPath();
    }

    @Override
    public void startExecuting() {
        entityPathNavigate.setPath(entityPathEntity, farSpeed);
    }

    @Override
    public void resetTask() {
        closestLivingEntity = null;
    }

    @Override
    public void updateTask() {
        if (theEntity.getDistanceSqToEntity(closestLivingEntity) < 49.0D)
            theEntity.getNavigator().setSpeed(nearSpeed);
        else
            theEntity.getNavigator().setSpeed(farSpeed);
    }
}

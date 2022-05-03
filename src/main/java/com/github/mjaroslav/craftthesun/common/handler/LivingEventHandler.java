package com.github.mjaroslav.craftthesun.common.handler;

import com.github.mjaroslav.craftthesun.common.command.UndeadPlayerSelectorForVillage;
import com.github.mjaroslav.craftthesun.common.entity.ai.EntityAIAvoidUndeadPlayer;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LivingEventHandler {
    public static final LivingEventHandler INSTANCE = new LivingEventHandler();

    @SubscribeEvent
    public void onEntityConstructing(@NotNull EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer)
            PlayerEventHandler.INSTANCE.onPlayerConstructing(event, (EntityPlayer) event.entity);

    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(@NotNull EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityIronGolem) {
            val golem = (EntityIronGolem) event.entity;
            golem.targetTasks.addTask(4, new EntityAINearestAttackableTarget(golem, EntityPlayer.class, 0, false,
                    true, new UndeadPlayerSelectorForVillage(golem)));
        }
        if (event.entity instanceof EntityVillager) {
            val villager = (EntityVillager) event.entity;
            villager.tasks.addTask(1, new EntityAIAvoidUndeadPlayer(villager, 8.0F, 0.6D, 0.6D));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEventLowest(@NotNull LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer)
            PlayerEventHandler.INSTANCE.onPlayerDeathEventLowest(event, (EntityPlayer) event.entityLiving);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDropEventLowest(@NotNull LivingDropsEvent event) {
        CommonUtils.tryAddEstusToFoodDrop(event);
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}

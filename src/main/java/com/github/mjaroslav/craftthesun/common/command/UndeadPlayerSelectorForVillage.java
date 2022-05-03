package com.github.mjaroslav.craftthesun.common.command;

import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lombok.val;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UndeadPlayerSelectorForVillage implements IEntitySelector {
    @Nullable
    private final EntityIronGolem golem;
    @Nullable
    private final EntityVillager villager;

    public UndeadPlayerSelectorForVillage() {
        golem = null;
        villager = null;
    }

    public UndeadPlayerSelectorForVillage(@NotNull EntityIronGolem golem) {
        this.golem = golem;
        villager = null;
    }

    public UndeadPlayerSelectorForVillage(@NotNull EntityVillager villager) {
        this.villager = villager;
        golem = null;
    }

    @Nullable
    private Village getVillage() {
        if (villager != null)
            // TODO: Try do it with AT
            return ReflectionHelper.getPrivateValue(EntityVillager.class, villager, "villageObj", "field_70954_d");
        return golem != null ? golem.getVillage() : null;
    }

    @Override
    public boolean isEntityApplicable(@NotNull Entity entity) {
        val village = getVillage();
        if (!(entity instanceof EntityPlayer))
            return false;
        val player = (EntityPlayer) entity;
        if (!CommonUtils.isPlayerHaveVillageReputationFactor(player))
            return false;
        if (village == null)
            return true;
        else
            return village.isPlayerReputationTooLow(player.getCommandSenderName());
    }
}

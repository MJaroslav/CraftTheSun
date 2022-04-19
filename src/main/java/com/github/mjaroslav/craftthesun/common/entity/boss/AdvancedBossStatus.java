package com.github.mjaroslav.craftthesun.common.entity.boss;

import java.util.*;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SideOnly(Side.CLIENT)
public class AdvancedBossStatus {
    public static final AdvancedBossStatus INSTANCE = new AdvancedBossStatus();

    private final Map<Integer, Entity> BOSSES = new HashMap<>();

    private final Map<Integer, BossStatus> STATUSES = new HashMap<>();

    private int timer = 0;

    public void findBosses(@NotNull EntityPlayer player) {
        if (timer > 10) {
            timer = 0;
            BOSSES.clear();
            player.worldObj
                    .getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(64, 64, 64),
                            entity -> entity instanceof IBossDisplayData)
                    .forEach(entry -> BOSSES.put(((Entity) entry).getEntityId(), (Entity) entry));
        } else timer++;
    }

    public void tick() {
        val toRemove = new HashSet<Integer>();
        BOSSES.forEach((id, entity) -> {
            val data = (IBossDisplayData) entity;
            if (!STATUSES.containsKey(id)) STATUSES.put(id, new BossStatus(data.func_145748_c_().getFormattedText(),
                    data.getHealth() / data.getMaxHealth(), -1, -1, 100));
            else {
                val status = STATUSES.get(id);
                status.healthScale = data.getHealth() / data.getMaxHealth();
                status.bossName = data.func_145748_c_().getFormattedText();
            }
        });
        STATUSES.forEach((id, status) -> {
            if (!BOSSES.containsKey(id)) status.timer--;
            if (status.timer <= 0) toRemove.add(id);
        });
        toRemove.forEach(STATUSES::remove);
    }

    @Nullable
    public BossStatus get(@NotNull Entity entity) {
        return STATUSES.get(entity.getEntityId());
    }

    public List<BossStatus> getFirst(int count) {
        val list = new ArrayList<>(STATUSES.values());
        list.sort(Comparator.comparingDouble(status -> status.healthScale));
        val result = new ArrayList<BossStatus>();
        for (var i = 0; i < count && i < list.size(); i++) result.add(list.get(i));
        return result;
    }

    @AllArgsConstructor
    public static class BossStatus {
        @NotNull
        public String bossName;
        public float healthScale;
        public float prevHealthScale;
        public long lastAttackTick;
        public int timer;
    }
}

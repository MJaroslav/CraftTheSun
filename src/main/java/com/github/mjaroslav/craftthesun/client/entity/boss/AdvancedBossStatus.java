package com.github.mjaroslav.craftthesun.client.entity.boss;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.IBossDisplayData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

// TODO: Remade this shit
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SideOnly(Side.CLIENT)
public class AdvancedBossStatus {
    public static final AdvancedBossStatus INSTANCE = new AdvancedBossStatus();

    private final Map<Integer, Entity> BOSSES = new HashMap<>();
    private final Map<Integer, BossStatus> STATUSES = new HashMap<>();

    public void addBoss(@NotNull Entity entity) {
        if (entity instanceof IBossDisplayData) BOSSES.put(entity.getEntityId(), entity);
    }

    public void afterTick() {
        BOSSES.clear();
    }

    public void tick() {
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
        STATUSES.entrySet().removeIf(entry -> {
            entry.getValue().timer--;
            if (entry.getValue().timer <= 0 && !BOSSES.containsKey(entry.getKey()))
                return true;
            else {
                entry.getValue().timer = 100;
                return false;
            }
        });
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

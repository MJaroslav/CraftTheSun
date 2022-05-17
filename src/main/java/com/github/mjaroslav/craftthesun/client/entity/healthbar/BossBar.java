package com.github.mjaroslav.craftthesun.client.entity.healthbar;

import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient.CategoryBossBar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class BossBar extends HealthBar {
    @NotNull
    protected String bossName;

    public BossBar(double maxHealth, double health, long initialTick, @NotNull String bossName) {
        super(maxHealth, health, initialTick);
        this.bossName = bossName;
    }

    @Override
    public boolean canRender(long ticks) {
        return true;
    }

    @Override
    public void setLastUpdateTick(long currentTick) {
        lastUpdateTick = currentTick;
        if (isDealtTimeExpired(currentTick) && prevHealth > health)
            prevHealth -= maxHealth / 50d * CategoryBossBar.dealtDamageReduceSpeed;
    }
}

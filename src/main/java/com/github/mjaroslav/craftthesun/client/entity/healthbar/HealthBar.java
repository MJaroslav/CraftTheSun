package com.github.mjaroslav.craftthesun.client.entity.healthbar;

import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient.CategoryHealthBar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SideOnly(Side.CLIENT)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class HealthBar {
    protected double maxHealth;
    protected double health;
    protected double prevHealth;
    protected long lastHitTick;
    protected long lastUpdateTick;

    public HealthBar(double maxHealth, double health, long initialTick) {
        this.maxHealth = maxHealth;
        this.health = prevHealth = health;
        lastHitTick = -1;
        lastUpdateTick = initialTick;
    }

    public void setHealth(double health) {
        if (health > this.health)
            prevHealth = health;
        this.health = health;
    }

    public double getPrimaryRatio() {
        return health / maxHealth;
    }

    public boolean isDealtTimeExpired(long ticks) {
        return ticks - lastHitTick > CategoryHealthBar.dealtDamageDelay;
    }

    public boolean canRender(long ticks) {
        return CategoryHealthBar.showAlways || (ticks - lastHitTick <= CategoryHealthBar.dealtDamageDelay * 3L);
    }

    public void setLastUpdateTick(long currentTick) {
        lastUpdateTick = currentTick;
        if (isDealtTimeExpired(currentTick) && prevHealth > health)
            prevHealth -= maxHealth / 20d * CategoryHealthBar.dealtDamageReduceSpeed;
    }

    public double getSecondaryRatio() {
        return prevHealth / maxHealth;
    }

    public boolean isExpired(long currentTick) {
        // If entity is dead or unloaded from client world then update event can't update lastUpdateTick
        return currentTick - lastUpdateTick > 100;
    }
}

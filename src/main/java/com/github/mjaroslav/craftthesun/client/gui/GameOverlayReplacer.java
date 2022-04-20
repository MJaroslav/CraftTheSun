package com.github.mjaroslav.craftthesun.client.gui;

import com.github.mjaroslav.craftthesun.client.entity.boss.AdvancedBossStatus;
import com.github.mjaroslav.craftthesun.client.util.ClientUtils;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameOverlayReplacer extends Gui {
    private static final GameOverlayReplacer INSTANCE = new GameOverlayReplacer();

    public static void drawBossBar(@NotNull ScaledResolution resolution) {
        INSTANCE.$drawBossBar(resolution);
    }

    private void $drawBossBar(@NotNull ScaledResolution resolution) {
        GL11.glEnable(GL11.GL_BLEND);
        val mc = Minecraft.getMinecraft();
        AdvancedBossStatus.INSTANCE.tick();
        val barWidth = 254;
        val barX = resolution.getScaledWidth() / 2 - barWidth / 2;
        var index = 0;
        for (var bossStatus : AdvancedBossStatus.INSTANCE.getFirst(CategoryGeneral.CategoryClient.CategoryBossBar.maxBars)) {
            mc.getTextureManager().bindTexture(ClientUtils.ICONS);
            val scaleSize = (int) (bossStatus.healthScale * (barWidth + 1));
            val prevScaleSize = (int) ((bossStatus.prevHealthScale) * (barWidth + 1));
            val barY = resolution.getScaledHeight() - (60 + 20 * index);
            drawTexturedModalRect(barX, barY, 0, 0, barWidth, 7);
            drawTexturedModalRect(barX, barY, 0, 0, barWidth, 7);
            val flag = CategoryGeneral.CategoryClient.CategoryBossBar.damageViewDelay > 0 &&
                    mc.thePlayer.worldObj.getTotalWorldTime() - bossStatus.lastAttackTick <=
                            CategoryGeneral.CategoryClient.CategoryBossBar.damageViewDelay;
            if (flag && prevScaleSize > 0) drawTexturedModalRect(barX, barY, 0, 14, prevScaleSize, 7);
            if (scaleSize > 0) drawTexturedModalRect(barX, barY, 0, 7, scaleSize, 7);
            mc.fontRenderer.drawStringWithShadow(bossStatus.bossName, barX, barY - 10, 0xFFFFFF);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            index++;
        }
        mc.getTextureManager().bindTexture(icons);
        GL11.glDisable(GL11.GL_BLEND);
        AdvancedBossStatus.INSTANCE.afterTick();
    }
}

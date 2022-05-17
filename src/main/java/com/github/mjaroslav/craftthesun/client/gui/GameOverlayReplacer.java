package com.github.mjaroslav.craftthesun.client.gui;

import com.github.mjaroslav.craftthesun.client.entity.healthbar.HealthBarManager;
import com.github.mjaroslav.craftthesun.client.util.ClientUtils;
import com.github.mjaroslav.craftthesun.common.data.SyncData;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient.CategoryBossBar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameOverlayReplacer extends Gui {
    private static final GameOverlayReplacer INSTANCE = new GameOverlayReplacer();

    public static void drawBossBar(@NotNull ScaledResolution resolution) {
        INSTANCE.$drawBossBar(resolution);
    }

    public static void drawExtendedHotBar(@NotNull ScaledResolution resolution) {
        INSTANCE.$drawExtendedHotBar(resolution);
    }

    private void $drawExtendedHotBar(@NotNull ScaledResolution resolution) {
        val width = resolution.getScaledWidth();
        val height = resolution.getScaledHeight();
        val mc = Minecraft.getMinecraft();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ClientUtils.ICONS);
        val type = CommonUtils.getPlayerType(mc.thePlayer);
        if (type.isUndead()) {
            drawTexturedModalRect(width / 2 + 91 + 2, height - 31 - 2, 0, 21, 30, 30);
            drawCenteredString(mc.fontRenderer, String.valueOf(CommonUtils.getPlayerHumanity(mc.thePlayer)),
                    width / 2 + 91 + 2 + 15, height - 31 - 2 + 15 - mc.fontRenderer.FONT_HEIGHT / 2,
                    type == SyncData.PlayerType.UNDEAD_HUMAN ? 0xFFFFFF : 0xA0A0A0);
        }
    }

    private void $drawBossBar(@NotNull ScaledResolution resolution) {
        GL11.glEnable(GL11.GL_BLEND);
        val mc = Minecraft.getMinecraft();
        val barWidth = 254;
        val barX = resolution.getScaledWidth() / 2 - barWidth / 2;
        var index = 0;
        for (var bossStatus : HealthBarManager.INSTANCE
                .getFirst(CategoryBossBar.maxBars)) {
            mc.getTextureManager().bindTexture(ClientUtils.ICONS);
            val scaleSize = (int) (bossStatus.getPrimaryRatio() * (barWidth + 1));
            val prevScaleSize = (int) ((bossStatus.getSecondaryRatio()) * (barWidth + 1));
            val barY = resolution.getScaledHeight() - (60 + 20 * index);
            drawTexturedModalRect(barX, barY, 0, 0, barWidth, 7);
            drawTexturedModalRect(barX, barY, 0, 0, barWidth, 7);
            val flag = CategoryBossBar.showDealtDamage && bossStatus.getPrevHealth() > bossStatus.getHealth();
            if (flag && prevScaleSize > 0)
                drawTexturedModalRect(barX, barY, 0, 14, prevScaleSize, 7);
            if (scaleSize > 0)
                drawTexturedModalRect(barX, barY, 0, 7, scaleSize, 7);
            mc.fontRenderer.drawStringWithShadow(bossStatus.getBossName(), barX, barY - 10, 0xFFFFFF);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            index++;
        }
        mc.getTextureManager().bindTexture(icons);
        GL11.glDisable(GL11.GL_BLEND);
    }
}

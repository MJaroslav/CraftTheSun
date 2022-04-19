package com.github.mjaroslav.craftthesun.client.handler;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.entity.boss.AdvancedBossStatus;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientEventHandler extends Gui {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    public static final ResourceLocation icons = new ResourceLocation("craftthesun:textures/gui/icons.png");

    @SubscribeEvent
    public void onItemTooltipEvent(@NotNull ItemTooltipEvent event) {
        val container = EstusContainer.getFromStack(event.itemStack);
        if (container == null) return;
        if (container.isInfinity()) event.toolTip.add(EnumChatFormatting.GOLD
                + StatCollector.translateToLocalFormatted("tooltip.craftthesun:estus_flask.infinity.text"));
        else event.toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocalFormatted(
                "tooltip.craftthesun:estus_flask.count.text", container.getCount(), container.getMaxCount()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurtEvent(@NotNull LivingHurtEvent event) {
        if (event.entityLiving instanceof IBossDisplayData) {
            val status = AdvancedBossStatus.INSTANCE.get(event.entityLiving);
            val data = (IBossDisplayData) event.entityLiving;
            status.healthScale = data.getHealth() / data.getMaxHealth();
            if (event.entityLiving.worldObj.getTotalWorldTime() - status.lastAttackTick > 15)
                status.prevHealthScale = status.healthScale;
            status.lastAttackTick = event.entityLiving.worldObj.getTotalWorldTime();
        }
    }

    @SubscribeEvent
    public void onRenderLivingEventPre(@NotNull RenderLivingEvent.Pre event) {
        AdvancedBossStatus.INSTANCE.addBoss(event.entity);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlayEventPre(@NotNull RenderGameOverlayEvent.Pre event) {
        if (event.type == ElementType.FOOD) event.setCanceled(true);
        if (event.type == ElementType.BOSSHEALTH) {
            GL11.glEnable(GL11.GL_BLEND);
            val mc = Minecraft.getMinecraft();
            AdvancedBossStatus.INSTANCE.tick();
            var index = 0;
            for (var bossStatus : AdvancedBossStatus.INSTANCE.getFirst(3)) {
                mc.getTextureManager().bindTexture(icons);

                val fontrenderer = mc.fontRenderer;
                val scaledresolution = event.resolution;
                val i = scaledresolution.getScaledWidth();
                val short1 = 254;
                val j = i / 2 - short1 / 2;
                val k = (int) (bossStatus.healthScale * (short1 + 1));
                val kk = (int) ((bossStatus.prevHealthScale) * (short1 + 1));
                val b0 = scaledresolution.getScaledHeight() - (60 + 20 * index);
                drawTexturedModalRect(j, b0, 0, 0, short1, 7);
                drawTexturedModalRect(j, b0, 0, 0, short1, 7);

                val flag = mc.thePlayer.worldObj.getTotalWorldTime() - bossStatus.lastAttackTick <= 15;
                if (flag && kk > 0) drawTexturedModalRect(j, b0, 0, 14, kk, 7);
                if (k > 0) drawTexturedModalRect(j, b0, 0, 7, k, 7);

                String s = bossStatus.bossName;
                fontrenderer.drawStringWithShadow(s, j, b0 - 10, 16777215);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                index++;
            }
            mc.getTextureManager().bindTexture(Gui.icons);
            GL11.glDisable(GL11.GL_BLEND);
            event.setCanceled(true);
            AdvancedBossStatus.INSTANCE.afterTick();
        }
    }
}

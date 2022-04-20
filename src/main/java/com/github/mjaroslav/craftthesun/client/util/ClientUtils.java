package com.github.mjaroslav.craftthesun.client.util;

import com.github.mjaroslav.craftthesun.client.audio.PlayerFallSound;
import com.github.mjaroslav.craftthesun.client.entity.boss.AdvancedBossStatus;
import com.github.mjaroslav.craftthesun.client.gui.GameOverlayReplacer;
import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ClientUtils {
    public static final ResourceLocation ICONS = new ResourceLocation("craftthesun:textures/gui/icons.png");

    public void tryAddTooltipWithEstus(@NotNull ItemTooltipEvent event) {
        val container = EstusContainer.getFromStack(event.itemStack);
        if (container == null) return;
        if (container.isExtra()) event.toolTip.add(EnumChatFormatting.GOLD
                + StatCollector.translateToLocalFormatted("tooltip.craftthesun:estus_flask.extra.text"));
        else if (container.isInfinity()) event.toolTip.add(EnumChatFormatting.GOLD
                + StatCollector.translateToLocalFormatted("tooltip.craftthesun:estus_flask.infinity.text"));
        else event.toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocalFormatted(
                    "tooltip.craftthesun:estus_flask.count.text", container.getCount(), container.getMaxCount()));
    }

    public void tryUpdateBossStatuses(@NotNull LivingHurtEvent event) {
        if (event.entityLiving instanceof IBossDisplayData) {
            val status = AdvancedBossStatus.INSTANCE.get(event.entityLiving);
            if (status == null)
                return;
            val data = (IBossDisplayData) event.entityLiving;
            status.healthScale = data.getHealth() / data.getMaxHealth();
            if (event.entityLiving.worldObj.getTotalWorldTime() - status.lastAttackTick > 15)
                status.prevHealthScale = status.healthScale;
            status.lastAttackTick = event.entityLiving.worldObj.getTotalWorldTime();
        }
    }

    private final Map<String, Boolean> PLAYER_FALL_STATE = new HashMap<>();

    public void tryTriggerFallSound(@NotNull TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || !event.player.worldObj.isRemote ||
                !CategoryGeneral.CategoryClient.CategorySounds.enableFallSound)
            return;
        if (!event.player.onGround && !PLAYER_FALL_STATE.getOrDefault(event.player.getCommandSenderName(), false))
            if (!event.player.capabilities.isFlying && (event.player.posY
                    - event.player.worldObj.getHeightValue(event.player.serverPosX, event.player.serverPosZ) >
                    CategoryGeneral.CategoryClient.CategorySounds.fallSoundTriggerHeight)
                    && event.player.motionY < 0) {
                Minecraft.getMinecraft().getSoundHandler().playSound(new PlayerFallSound(event.player));
                PLAYER_FALL_STATE.put(event.player.getCommandSenderName(), true);
            }
        if (event.player.onGround) PLAYER_FALL_STATE.put(event.player.getCommandSenderName(), false);
    }

    public void tryHideHungerBar(@NotNull RenderGameOverlayEvent.Pre event) {
        // TODO: Add player type check
        if (event.type != RenderGameOverlayEvent.ElementType.FOOD)
            return;
        event.setCanceled(!CategoryGeneral.CategoryCommon.CategoryHunger.showHungerBar);
    }

    public void tryReplaceBossBar(@NotNull RenderGameOverlayEvent.Pre event) {
        if (event.type != RenderGameOverlayEvent.ElementType.BOSSHEALTH ||
                !CategoryGeneral.CategoryClient.CategoryBossBar.enable)
            return;
        GameOverlayReplacer.drawBossBar(event.resolution);
        event.setCanceled(true);
    }
}

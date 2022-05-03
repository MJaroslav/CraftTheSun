package com.github.mjaroslav.craftthesun.client.util;

import com.github.mjaroslav.craftthesun.client.audio.PlayerFixedSound;
import com.github.mjaroslav.craftthesun.client.entity.boss.AdvancedBossStatus;
import com.github.mjaroslav.craftthesun.client.gui.GameOverlayReplacer;
import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient.CategorySounds;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
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
        if (container == null)
            return;
        if (container.isExtra())
            event.toolTip.add(EnumChatFormatting.GOLD
                    + StatCollector.translateToLocalFormatted("tooltip.craftthesun:estus_flask.extra.text"));
        else if (container.isInfinity())
            event.toolTip.add(EnumChatFormatting.GOLD
                    + StatCollector.translateToLocalFormatted("tooltip.craftthesun:estus_flask.infinity.text"));
        else
            event.toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocalFormatted(
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
        if (event.phase == TickEvent.Phase.START || !event.player.worldObj.isRemote
                || !CategorySounds.enableFallSound)
            return;
        if (!event.player.onGround && !PLAYER_FALL_STATE.getOrDefault(event.player.getCommandSenderName(), false))
            if (!event.player.capabilities.isFlying && (event.player.posY - event.player.worldObj.getHeightValue(
                    (int) event.player.posX,
                    (int) event.player.posZ) > CategoryGeneral.CategoryClient.CategorySounds.fallSoundTriggerHeight)
                    && event.player.motionY < 0) {
                Minecraft.getMinecraft().getSoundHandler().playSound(new PlayerFixedSound(event.player, "craftthesun:ds.random.player.fall"));
                PLAYER_FALL_STATE.put(event.player.getCommandSenderName(), true);
            }
        if (event.player.onGround)
            PLAYER_FALL_STATE.put(event.player.getCommandSenderName(), false);
    }

    public void tryHideHungerBar(@NotNull RenderGameOverlayEvent.Pre event) {
        if (event.type != RenderGameOverlayEvent.ElementType.FOOD)
            return;
        event.setCanceled(CommonUtils.isHungerFixed(Minecraft.getMinecraft().thePlayer)
                && !CategoryGeneral.CategoryCommon.CategoryHunger.showHungerBar);
    }

    public void tryReplaceBossBar(@NotNull RenderGameOverlayEvent.Pre event) {
        if (event.type != RenderGameOverlayEvent.ElementType.BOSSHEALTH
                || !CategoryGeneral.CategoryClient.CategoryBossBar.enable)
            return;
        GameOverlayReplacer.drawBossBar(event.resolution);
        event.setCanceled(true);
    }

    public void renderBrokenItemStackWithNoSound(@NotNull EntityLivingBase entity, @NotNull ItemStack stack) {
        for (var i = 0; i < 5; ++i) {
            var vec3 = Vec3.createVectorHelper((entity.worldObj.rand.nextFloat() - 0.5D) * 0.1D,
                    Math.random() * 0.1D + 0.1D, 0.0D);
            vec3.rotateAroundX(-entity.rotationPitch * (float) Math.PI / 180.0F);
            vec3.rotateAroundY(-entity.rotationYaw * (float) Math.PI / 180.0F);
            var vec31 = Vec3.createVectorHelper((entity.worldObj.rand.nextFloat() - 0.5D) * 0.3D,
                    (-entity.worldObj.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
            vec31.rotateAroundX(-entity.rotationPitch * (float) Math.PI / 180.0F);
            vec31.rotateAroundY(-entity.rotationYaw * (float) Math.PI / 180.0F);
            vec31 = vec31.addVector(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
            entity.worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(stack.getItem()), vec31.xCoord,
                    vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
        }
    }
}

package com.github.mjaroslav.craftthesun.client.entity.healthbar;

import com.github.mjaroslav.craftthesun.client.util.ClientUtils;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryClient.CategoryHealthBar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HealthBarManager {
    public static final HealthBarManager INSTANCE = new HealthBarManager();

    private final Map<EntityLivingBase, HealthBar> MOBS = new ConcurrentHashMap<>();
    private final Map<EntityLivingBase, BossBar> BOSSES = new ConcurrentHashMap<>();

    private void doRender(@NotNull EntityLivingBase entity, float partialTicks) {
        if (!CategoryHealthBar.enable)
            return;
        if (entity instanceof IBossDisplayData)
            return;
        if (!MOBS.containsKey(entity))
            return;
        val healthBar = MOBS.get(entity);
        val renderManager = RenderManager.instance;
        if (!(renderManager.livingPlayer instanceof EntityPlayer))
            return;
        val mc = Minecraft.getMinecraft();
        val player = mc.thePlayer;
        val world = mc.thePlayer.worldObj;
        if (Minecraft.isGuiEnabled() && entity != renderManager.livingPlayer &&
                !entity.isInvisibleToPlayer(player) && entity.riddenByEntity == null) {
            val distance = entity.getDistanceSqToEntity(renderManager.livingPlayer);
            if (distance > Math.pow(CategoryHealthBar.maxRenderDistance, 2)
                    || !healthBar.canRender(world.getTotalWorldTime()))
                return;
            glPushMatrix();
            glTranslated(-RenderManager.instance.viewerPosX,
                    -RenderManager.instance.viewerPosY,
                    -RenderManager.instance.viewerPosZ);
            glPushMatrix();
            val texture2D = ClientUtils.glToggle(GL_TEXTURE_2D, false);
            val lighting = ClientUtils.glToggle(GL_LIGHTING, false);
            val blend = ClientUtils.glToggle(GL_BLEND, false);
            val entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            val entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            val entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            glTranslated(entityX, entityY + entity.height + 0.6f, entityZ);
            glScalef(1f / 16f, 1f / 16f, 1f / 16f);
            glNormal3f(0F, 1F, 0F);
            glRotatef(-renderManager.playerViewY, 0F, 1F, 0F);
            glRotatef(renderManager.playerViewX, 1F, 0F, 0F);
            glPushMatrix();
            glRotatef(180F, 0F, 1F, 0F);
            val tess = Tessellator.instance;
            tess.startDrawingQuads();
            tess.setColorRGBA_I(CommonUtils.parseHex(CategoryHealthBar.backgroundColor), 0xFF);
            tess.addVertex(8, 0, 0);
            tess.addVertex(8, 1, 0);
            tess.addVertex(-8, 1, 0);
            tess.addVertex(-8, 0, 0);
            val primary = 16 * entity.getHealth() / entity.getMaxHealth() - 8;
            if (CategoryHealthBar.showDealtDamage && healthBar.prevHealth > healthBar.health) {
                val secondary = 16 * healthBar.getSecondaryRatio() - 8;
                tess.setColorRGBA_I(CommonUtils.parseHex(CategoryHealthBar.dealtDamageColor), 0xFF);
                tess.addVertex(secondary, 0, 0.01);
                tess.addVertex(secondary, 1, 0.01);
                tess.addVertex(-8, 1, 0.01);
                tess.addVertex(-8, 0, 0.01);
            }
            tess.setColorRGBA_I(CommonUtils.parseHex(CategoryHealthBar.healthPointColor), 0xFF);
            tess.addVertex(primary, 0, 0.02);
            tess.addVertex(primary, 1, 0.02);
            tess.addVertex(-8, 1, 0.02);
            tess.addVertex(-8, 0, 0.02);
            tess.draw();
            glPopMatrix();
            ClientUtils.glToggle(GL_TEXTURE_2D, texture2D);
            ClientUtils.glToggle(GL_LIGHTING, lighting);
            ClientUtils.glToggle(GL_BLEND, blend);
            glPopMatrix();
            glPopMatrix();
        }
    }

    public List<BossBar> getFirst(int count) {
        val list = new ArrayList<>(BOSSES.values());
        list.sort(Comparator.comparingDouble(status -> status.health));
        val result = new ArrayList<BossBar>();
        for (var i = 0; i < count && i < list.size(); i++)
            result.add(list.get(i));
        return result;
    }

    public void updateBar(@NotNull LivingHurtEvent event) {
        val entity = event.entityLiving;
        val lastTick = entity.worldObj.getTotalWorldTime();
        if (BOSSES.containsKey(entity)) {
            val bossData = (IBossDisplayData) entity;
            val bossBar = BOSSES.get(entity);
            if (bossBar.isDealtTimeExpired(entity.worldObj.getTotalWorldTime()))
                bossBar.setPrevHealth(bossData.getHealth());
            bossBar.setLastHitTick(lastTick);
        } else if (MOBS.containsKey(entity)) {
            val healthBar = MOBS.get(entity);
            if (healthBar.isDealtTimeExpired(entity.worldObj.getTotalWorldTime()))
                healthBar.setPrevHealth(healthBar.getHealth());
            healthBar.setLastHitTick(lastTick);
        }
    }

    public void syncBar(@NotNull LivingEvent.LivingUpdateEvent event) {
        val entity = event.entityLiving;
        val currentTick = entity.worldObj.getTotalWorldTime();
        if (entity instanceof IBossDisplayData) {
            val bossData = (IBossDisplayData) entity;
            if (BOSSES.containsKey(entity)) {
                val bossBar = BOSSES.get(entity);
                bossBar.setMaxHealth(bossData.getMaxHealth());
                bossBar.setHealth(bossData.getHealth());
                bossBar.setBossName(bossData.func_145748_c_().getFormattedText());
                bossBar.setLastUpdateTick(currentTick);
            } else
                BOSSES.put(entity, new BossBar(bossData.getMaxHealth(), bossData.getHealth(), currentTick,
                        bossData.func_145748_c_().getFormattedText()));
        } else if (MOBS.containsKey(entity)) {
            val healthBar = MOBS.get(entity);
            healthBar.setMaxHealth(entity.getMaxHealth());
            healthBar.setHealth(entity.getHealth());
            healthBar.setLastUpdateTick(currentTick);
        } else
            MOBS.put(entity, new HealthBar(entity.getMaxHealth(), entity.getHealth(), currentTick));
    }

    // I required in ideas for good alternative way to removing unloaded entities
    public void updateFrame(float partialTicks) {
        val player = Minecraft.getMinecraft().thePlayer;
        if (player == null || player.worldObj == null) {
            BOSSES.clear();
            MOBS.clear();
            return;
        }
        val world = player.worldObj;
        BOSSES.entrySet().removeIf(entry -> {
            val entity = entry.getKey();
            if (entity == null)
                return true;
            val worldEntity = world.getEntityByID(entity.getEntityId());
            return worldEntity != entry.getKey() || entry.getValue().isExpired(world.getTotalWorldTime());
        });
        MOBS.entrySet().removeIf(entry -> {
            val entity = entry.getKey();
            if (entity == null)
                return true;
            val worldEntity = world.getEntityByID(entity.getEntityId());
            val flag = worldEntity != entry.getKey() || entry.getValue().isExpired(world.getTotalWorldTime());
            if (!flag)
                doRender(entry.getKey(), partialTicks);
            return flag;
        });
    }
}

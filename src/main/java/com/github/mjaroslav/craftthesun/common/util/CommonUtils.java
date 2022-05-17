package com.github.mjaroslav.craftthesun.common.util;

import com.github.mjaroslav.craftthesun.CraftTheSunMod;
import com.github.mjaroslav.craftthesun.common.data.CraftTheSunEEP;
import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.data.SyncData.PlayerType;
import com.github.mjaroslav.craftthesun.common.init.ModItems;
import com.github.mjaroslav.craftthesun.common.item.ItemEstusFlask;
import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;
import com.github.mjaroslav.craftthesun.common.network.packet.S01PacketBrokenItemAnimation;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryCommon;
import com.github.mjaroslav.craftthesun.lib.CategoryGeneral.CategoryCommon.CategoryHunger;
import com.github.mjaroslav.craftthesun.lib.ModInfo;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import mjaroslav.mcmods.mjutils.util.UtilsInventory;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@UtilityClass
public class CommonUtils {
    public void doEstusEffects(@NotNull EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, "craftthesun:ds.estus", 1F,
                player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        CraftTheSunMod.proxy.spawnParticle("estus", player);
    }

    public void tryTakeEstusFromItemInUse(@NotNull PlayerUseItemEvent.Finish event) {
        if (event.item.getItem() == ModItems.estusFlask || event.entityPlayer.worldObj.isRemote)
            return;
        val container = EstusContainer.getFromStack(event.item);
        if (container == null || !container.isExtra())
            return;
        if (event.result != null && event.item != event.result)
            EstusContainer.removeEstusContainer(event.result);
        doEstusEffects(event.entityPlayer);
        event.entityPlayer.heal(8);
    }

    public boolean isHungerFixed(@NotNull EntityPlayer player) {
        val type = getPlayerType(player);
        switch (CategoryHunger.fixHungerValueFor) {
            default:
                return false;
            case 0:
                return true;
            case 1:
                return !type.isUndead();
            case 2:
                return type.isUndead();
            case 3:
                return type == PlayerType.HOLLOW;
            case 4:
                return type == PlayerType.UNDEAD_HUMAN;
        }
    }

    public void tryHardSetHungerValue(@NotNull TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player.worldObj.isRemote || !CategoryHunger.enable
                || !isHungerFixed(event.player))
            return;
        if (event.player.worldObj.getTotalWorldTime() % CategoryHunger.setHungerValueDelay == 0) {
            event.player.getFoodStats().setFoodLevel(CategoryHunger.fixHungerValue);
            event.player.getFoodStats().setFoodSaturationLevel(CategoryHunger.fixSaturationValue);
        }
    }

    public void tryRefillEstusFlasks(@NotNull EntityPlayer player) {
        for (var index = 0; index < player.inventory.getSizeInventory(); index++) {
            val stack = player.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == ModItems.estusFlask)
                ItemEstusFlask.refillEstusFlask(stack);
        }
    }

    public void tryAddEstusToFoodDrop(@NotNull LivingDropsEvent event) {
        if (CategoryCommon.estusInFoodDropChance <= 0)
            return;
        for (var entityItem : event.drops)
            if (entityItem.getEntityItem().getItem() instanceof ItemFood
                    && event.entityLiving.worldObj.rand.nextInt(101) <= CategoryCommon.estusInFoodDropChance)
                EstusContainer.saveToStack(EstusContainer.createExtra(), entityItem.getEntityItem());
    }

    public void sendPacketToTrackingPlayers(@NotNull EntityPlayer player, @NotNull IMessage message) {
        sendPacketToTrackingPlayers(player, message, false);
    }

    public void sendPacketToTrackingPlayers(@NotNull EntityPlayer player, @NotNull IMessage message,
                                            boolean sendToSender) {
        val tracker = ((WorldServer) player.worldObj).getEntityTracker();
        tracker.getTrackingPlayers(player)
                .forEach(trackedPlayer -> NetworkHandler.INSTANCE.sendTo(message, trackedPlayer));
        if (sendToSender)
            NetworkHandler.INSTANCE.sendTo(message, player);
    }

    public boolean isNaturalRegenerationEnabled(@NotNull EntityPlayer player) {
        val type = getPlayerType(player);
        switch (CategoryCommon.disableNaturalRegenerationFor) {
            default:
                return true;
            case 0:
                return false;
            case 1:
                return type.isUndead();
            case 2:
                return !type.isUndead();
            case 3:
                return type != PlayerType.HOLLOW;
            case 4:
                return type != PlayerType.UNDEAD_HUMAN;
        }
    }

    public int findFirstItemStackInPlayerInventory(@NotNull EntityPlayer player, @NotNull ItemStack request,
                                                   boolean withSize, boolean withDamage, boolean withNBT) {
        for (var slot = 0; slot < player.inventory.getSizeInventory(); slot++)
            if (UtilsInventory.itemStacksEquals(player.inventory.getStackInSlot(slot), request, false, withSize,
                    withDamage, withDamage))
                return slot;
        return -1;
    }

    public int findFirstItemInPlayerInventory(@NotNull EntityPlayer player, @NotNull Item item) {
        return findFirstItemStackInPlayerInventory(player, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    public int findFirstItemInPlayerInventory(@NotNull EntityPlayer player, @NotNull Item item, int damage) {
        return findFirstItemStackInPlayerInventory(player, new ItemStack(item, 1, damage));
    }

    public int findFirstItemStackInPlayerInventory(@NotNull EntityPlayer player, @NotNull ItemStack request) {
        return findFirstItemStackInPlayerInventory(player, request, false, true, false);
    }

    public boolean isPlayerHaveVillageReputationFactor(@NotNull EntityPlayer player) {
        val type = getPlayerType(player);
        switch (CategoryCommon.villageReputationFactorMode) {
            default:
                return false;
            case 0:
                return type.isUndead();
            case 1:
                return type == PlayerType.HOLLOW;
            case 2:
                return type == PlayerType.UNDEAD_HUMAN;
        }
    }

    public boolean isPlayerShouldDropInventory(@NotNull EntityPlayer player) {
        return !getPlayerType(player).isUndead();
    }

    public void tryMakePlayerUndead(@NotNull PlayerEvent.PlayerRespawnEvent event) {
        val player = event.player;
        val world = player.worldObj;
        if (world.isRemote)
            return;
        val inventory = player.inventory;
        val type = getPlayerType(player);
        if (type == PlayerType.HUMAN) {
            val signSlot = findFirstItemInPlayerInventory(player, ModItems.darkSign);
            if (signSlot != -1) {
                NetworkHandler.INSTANCE.sendToAllAround(new S01PacketBrokenItemAnimation(player,
                        inventory.getStackInSlot(signSlot)), player, 64);
                inventory.setInventorySlotContents(signSlot, null);
                setPlayerType(player, PlayerType.HOLLOW);
                world.playSoundAtEntity(player, "mob.zombie.infect", 2.0f, (world.rand.nextFloat() -
                        world.rand.nextFloat()) * 0.2F + 1.0F);
            }
        } else if (type == PlayerType.UNDEAD_HUMAN) {
            setPlayerType(player, PlayerType.HOLLOW);
            world.playSoundAtEntity(player, "mob.zombie.infect", 2.0f, (world.rand.nextFloat() -
                    world.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }

    public EnumCreatureAttribute getPlayerCreatureAttribute(@NotNull EntityPlayer player) {
        return getPlayerType(player).isUndead() ? EnumCreatureAttribute.UNDEAD
                : EnumCreatureAttribute.UNDEFINED;
    }

    @NotNull
    public PlayerType getPlayerType(@NotNull EntityPlayer player) {
        return CraftTheSunEEP.get(player).getSyncData().getType();
    }

    public void setPlayerType(@NotNull EntityPlayer player, @NotNull PlayerType type) {
        CraftTheSunEEP.get(player).getSyncData().setType(type);
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getPlayerHumanity(@NotNull EntityPlayer player) {
        return CraftTheSunEEP.get(player).getSyncData().getHumanity();
    }

    public void addPlayerHumanity(@NotNull EntityPlayer player, int value) {
        setPlayerHumanity(player, Math.max(getPlayerHumanity(player) + value, 0));
    }

    public void setPlayerHumanity(@NotNull EntityPlayer player, @Range(from = 0, to = Integer.MAX_VALUE) int humanity) {
        CraftTheSunEEP.get(player).getSyncData().setHumanity(humanity);
    }

    public int parseHex(@NotNull String value) {
        if (value.startsWith("#"))
            value = value.substring(1);
        try {
            return Integer.parseInt(value, 16);
        } catch (NumberFormatException ignored) {
            ModInfo.LOG.warn("Cant parse HEX string to int: " + value);
            return 0x000000;
        }
    }
}

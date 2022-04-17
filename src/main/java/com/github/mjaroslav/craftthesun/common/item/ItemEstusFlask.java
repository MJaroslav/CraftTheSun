package com.github.mjaroslav.craftthesun.common.item;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.val;
import lombok.var;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.mjaroslav.craftthesun.lib.ModInfo.prefix;

public class ItemEstusFlask extends Item {
    public static final String TAG_ESTUS = "estus";
    public static final String TAG_ESTUS_COUNT = "count";
    public static final String TAG_ESTUS_MAX = "max";

    public static final int MAX_ESTUS = 5;

    private IIcon iconFull;

    public ItemEstusFlask() {
        setUnlocalizedName(prefix("estus_flask"));
        setTextureName(prefix("estus_flask"));
        setMaxStackSize(1);
        setMaxDamage(0);
        setCreativeTab(CreativeTabs.tabBrewing);
        setHasSubtypes(true);
        GameRegistry.registerItem(this, "estus_flask");
    }

    @Override
    public void registerIcons(@NotNull IIconRegister register) {
        super.registerIcons(register);
        iconFull = register.registerIcon(prefix("estus_flask_full"));
    }

    @Override
    public IIcon getIconIndex(@NotNull ItemStack stack) {
        return hasEstus(stack) ? iconFull : itemIcon;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return hasEstus(stack) ? iconFull : itemIcon;
    }

    @Override
    public EnumRarity getRarity(@NotNull ItemStack stack) {
        return hasEstus(stack) ? EnumRarity.rare : EnumRarity.uncommon;
    }

    @Override
    public boolean showDurabilityBar(@NotNull ItemStack stack) {
        return !isEstusMax(stack);
    }

    @Override
    public double getDurabilityForDisplay(@NotNull ItemStack stack) {
        return (double) (getEstusMax(stack) - getEstusCount(stack)) / (double) getEstusMax(stack);
    }

    @Override
    public boolean hasEffect(@NotNull ItemStack stack, int pass) {
        return hasEstus(stack) && pass == 0;
    }

    @Override
    public EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return hasEstus(stack) ? EnumAction.drink : EnumAction.none;
    }

    @Override
    public ItemStack onEaten(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        if (decreaseEstusCount(stack, !player.capabilities.isCreativeMode) && !world.isRemote) {
            // TODO: Do estus shit
            val heal = new PotionEffect(Potion.heal.getId(), 1, 0);
            player.addPotionEffect(heal);
            return stack;
        } else return super.onEaten(stack, world, player);
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        return hasEstus(stack) ? 32 : 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getSubItems(@NotNull Item item, @Nullable CreativeTabs tab, @NotNull List list) {
        val baseEstus = new ItemStack(item, 1, 0);
        refillEstus(baseEstus);
        list.add(baseEstus);
    }

    @Override
    public ItemStack onItemRightClick(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        if (hasEstus(stack))
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    public static boolean isEstusMax(@NotNull ItemStack stack) {
        return getEstusMax(stack) == getEstusCount(stack);
    }

    public static boolean hasEstus(@NotNull ItemStack stack) {
        val rootNbt = stack.getTagCompound();
        return rootNbt != null && rootNbt.getCompoundTag(TAG_ESTUS).getInteger(TAG_ESTUS_COUNT) > 0;
    }

    public static int getEstusCount(@NotNull ItemStack stack) {
        val rootNbt = stack.getTagCompound();
        return rootNbt != null ? rootNbt.getCompoundTag(TAG_ESTUS).getInteger(TAG_ESTUS_COUNT) : 0;
    }

    public static int getEstusMax(@NotNull ItemStack stack) {
        val rootNbt = stack.getTagCompound();
        return rootNbt != null ? rootNbt.getCompoundTag(TAG_ESTUS).getInteger(TAG_ESTUS_MAX) : MAX_ESTUS;
    }

    public static boolean decreaseEstusCount(@NotNull ItemStack stack, boolean doReal) {
        val rootNbt = stack.getTagCompound();
        if (rootNbt == null)
            return false;
        val estusNbt = rootNbt.getCompoundTag(TAG_ESTUS);
        val count = estusNbt.getInteger(TAG_ESTUS_COUNT);
        if (count < 1)
            return false;
        if (doReal) {
            estusNbt.setInteger(TAG_ESTUS_COUNT, count - 1);
            rootNbt.setTag(TAG_ESTUS, estusNbt);
        }
        return true;
    }

    public static void refillEstus(@NotNull ItemStack stack) {
        var rootNbt = stack.getTagCompound();
        if (rootNbt == null) {
            rootNbt = new NBTTagCompound();
            stack.setTagCompound(rootNbt);
        }
        val estusNbt = rootNbt.getCompoundTag(TAG_ESTUS);
        var max = estusNbt.getInteger(TAG_ESTUS_MAX);
        if (max == 0)
            max = MAX_ESTUS;
        estusNbt.setInteger(TAG_ESTUS_COUNT, max);
        estusNbt.setInteger(TAG_ESTUS_MAX, max);
        rootNbt.setTag(TAG_ESTUS, estusNbt);
    }
}

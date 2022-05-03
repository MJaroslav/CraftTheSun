package com.github.mjaroslav.craftthesun.common.item;

import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.val;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemEstusFlask extends ModItem {
    @SideOnly(Side.CLIENT)
    private IIcon iconFull, iconBaltica;

    public ItemEstusFlask() {
        super("estus_flask");
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(@NotNull IIconRegister register) {
        super.registerIcons(register);
        iconFull = register.registerIcon(getIconString() + "_full");
        iconBaltica = register.registerIcon(getIconString() + "_baltica");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return damage == 0 ? itemIcon : damage == 3 ? iconBaltica : iconFull;
    }

    @Override
    public EnumRarity getRarity(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return container.isInfinity() ? EnumRarity.epic : container.hasEstus() ? EnumRarity.rare : EnumRarity.uncommon;
    }

    @Override
    public boolean showDurabilityBar(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return !container.isInfinity() && container.hasEstus() && !container.isMax();
    }

    @Override
    public double getDurabilityForDisplay(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return (double) (container.getMaxCount() - container.getCount()) / (double) container.getMaxCount();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(@NotNull ItemStack stack, int pass) {
        return stack.getItemDamage() != 3 && EstusContainer.getFromStackOrDefault(stack).hasEstus() && pass == 0;
    }

    @Override
    public EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return EstusContainer.getFromStackOrDefault(stack).hasEstus() ? EnumAction.drink : EnumAction.none;
    }

    @Override
    public ItemStack onEaten(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        if (EstusContainer.decreaseFromStack(stack, !player.capabilities.isCreativeMode) && !world.isRemote) {
            CommonUtils.doEstusEffects(player);
            // TODO: Do estus shit
            player.heal(8);
            if (stack.getItemDamage() == 3) {
                val confusion = player.getActivePotionEffect(Potion.confusion);
                player.addPotionEffect(new PotionEffect(Potion.confusion.id, 600 +
                        (confusion != null ? confusion.getDuration() : 0)));
                val damageBoost = player.getActivePotionEffect(Potion.damageBoost);
                player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 300 +
                        (damageBoost != null ? damageBoost.getDuration() : 0), 1));
            }
            if (!EstusContainer.hasEstus(stack))
                stack.setItemDamage(0);
            return stack;
        } else
            return super.onEaten(stack, world, player);
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return container.isInfinity() ? 32 : container.hasEstus() ? 16 : 0;
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(@NotNull Item item, @Nullable CreativeTabs tab, @NotNull List list) {
        val emptyEstus = new ItemStack(item, 1, 0);
        EstusContainer.saveToStack(EstusContainer.createCustom(0, 5), emptyEstus);
        list.add(emptyEstus);

        val baseEstus = new ItemStack(item, 1, 1);
        EstusContainer.saveToStack(EstusContainer.createDefault(), baseEstus);
        list.add(baseEstus);

        val infinityEstus = new ItemStack(item, 1, 2);
        EstusContainer.saveToStack(EstusContainer.createInfinity(), infinityEstus);
        list.add(infinityEstus);
    }

    @Override
    public ItemStack onItemRightClick(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        if (EstusContainer.getFromStackOrDefault(stack).hasEstus())
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public String getUnlocalizedName(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return stack.getItemDamage() == 3 ? getUnlocalizedName() + ".baltica" : container.isInfinity() ?
                getUnlocalizedName() + ".infinity" : container.hasEstus() ? getUnlocalizedName() :
                getUnlocalizedName() + ".empty";
    }

    public static void refillEstusFlask(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        container.refill();
        EstusContainer.saveToStack(container, stack);
        stack.setItemDamage(container.isInfinity() ? stack.getItemDamage() == 3 ? 3 : 2 : container.hasEstus() ? 1 : 0);
    }
}

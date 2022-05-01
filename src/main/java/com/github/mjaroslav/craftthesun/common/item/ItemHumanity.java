package com.github.mjaroslav.craftthesun.common.item;

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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;

public class ItemHumanity extends ModItem {
    @SideOnly(Side.CLIENT)
    private IIcon iconTwin;

    public ItemHumanity() {
        super("humanity");
        setHasSubtypes(true);
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(@NotNull Item item, @Nullable CreativeTabs tab, @NotNull List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(@NotNull IIconRegister register) {
        super.registerIcons(register);
        iconTwin = register.registerIcon(getIconString() + "_twin");
    }

    @Override
    public IIcon getIconFromDamage(@Range(from = 0, to = Integer.MAX_VALUE) int damage) {
        return damage == 1 ? iconTwin : super.getIconFromDamage(damage);
    }

    @Override
    public EnumRarity getRarity(@NotNull ItemStack stack) {
        return stack.getItemDamage() == 1 ? EnumRarity.rare : EnumRarity.uncommon;
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        return stack.getItemDamage() == 1 ? 30 : 15;
    }

    @Override
    public ItemStack onItemRightClick(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        val type = CommonUtils.getPlayerType(player);
        if (type.isUndead())
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public String getUnlocalizedName(@NotNull ItemStack stack) {
        return stack.getItemDamage() == 1 ? getUnlocalizedName() + ".twin" : getUnlocalizedName();
    }

    @Override
    public ItemStack onEaten(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        if (world.isRemote)
            return stack;
        if (!player.capabilities.isCreativeMode)
            --stack.stackSize;
        CommonUtils.addPlayerHumanity(player, stack.getItemDamage() == 1 ? 2 : 1);
        world.playSoundAtEntity(player, "craftthesun:ds.item.humanity_eaten", 0.6F, world.rand.nextFloat() * 0.1F + 0.9F);
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return EnumAction.bow;
    }
}

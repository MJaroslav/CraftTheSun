package com.github.mjaroslav.craftthesun.common.item;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.val;
import lombok.var;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.mjaroslav.craftthesun.common.data.EstusContainer;
import com.github.mjaroslav.craftthesun.common.network.NetworkHandler;
import com.github.mjaroslav.craftthesun.common.network.packet.S2CEstusFXPacket;
import com.github.mjaroslav.craftthesun.common.util.ModUtils;

import java.util.List;

import static com.github.mjaroslav.craftthesun.lib.ModInfo.prefix;

public class ItemEstusFlask extends Item {
    @SideOnly(Side.CLIENT)
    private IIcon iconFull;

    public ItemEstusFlask() {
        setUnlocalizedName(prefix("estus_flask"));
        setTextureName(prefix("estus_flask"));
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabBrewing);
        setHasSubtypes(true);
        GameRegistry.registerItem(this, "estus_flask");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(@NotNull IIconRegister register) {
        super.registerIcons(register);
        iconFull = register.registerIcon(prefix("estus_flask_full"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return damage == 0 ? itemIcon : iconFull;
    }

    @Override
    public EnumRarity getRarity(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return container.isInfinity() ? EnumRarity.epic : container.hasEstus() ? EnumRarity.rare : EnumRarity.uncommon;
    }

    @Override
    public boolean showDurabilityBar(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return !container.isInfinity() && !container.hasEstus();
    }

    @Override
    public double getDurabilityForDisplay(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return (double) (container.getMaxCount() - container.getCount()) / (double) container.getMaxCount();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(@NotNull ItemStack stack, int pass) {
        return EstusContainer.getFromStackOrDefault(stack).hasEstus() && pass == 0;
    }

    @Override
    public EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return EstusContainer.getFromStackOrDefault(stack).hasEstus() ? EnumAction.drink : EnumAction.none;
    }

    @Override
    public ItemStack onEaten(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityPlayer player) {
        if (EstusContainer.decreaseFromStack(stack, !player.capabilities.isCreativeMode) && !world.isRemote) {
            ModUtils.doEstusEffects(player);
            // TODO: Do estus shit
            player.heal(8);
            if (!EstusContainer.hasEstus(stack)) stack.setItemDamage(0);
            return stack;
        } else return super.onEaten(stack, world, player);
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

//    @SideOnly(Side.CLIENT)
//    @Override
//    public void addInformation(@NotNull ItemStack stack, @NotNull EntityPlayer player, @NotNull List list,
//            boolean isAdvanced) {
//        val container = EstusContainer.getFromStackOrDefault(stack);
//        if (container.isInfinity()) list.add(EnumChatFormatting.GOLD
//                + StatCollector.translateToLocalFormatted("tooltip.craftthesun:estus_flask.infinity.text"));
//        else list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocalFormatted(
//                "tooltip.craftthesun:estus_flask.count.text", container.getCount(), container.getMaxCount()));
//    }

    @Override
    public String getUnlocalizedName(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        return container.isInfinity() ? getUnlocalizedName() + ".infinity"
                : container.hasEstus() ? getUnlocalizedName() : getUnlocalizedName() + ".empty";
    }

    public static void refillEstusFlask(@NotNull ItemStack stack) {
        val container = EstusContainer.getFromStackOrDefault(stack);
        container.refill();
        EstusContainer.saveToStack(container, stack);
        stack.setItemDamage(container.isInfinity() ? 2 : container.hasEstus() ? 1 : 0);
    }
}

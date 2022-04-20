package com.github.mjaroslav.craftthesun.common.data;

import lombok.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnknownNullability;

@Getter
@Setter
@ToString
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstusContainer {
    public static final String TAG_ESTUS_CONTAINER = "estus_container";
    public static final String TAG_COUNT = "count";
    public static final String TAG_MAX_COUNT = "max_count";
    public static final String TAG_INFINITY = "infinity";
    public static final String TAG_EXTRA = "extra";

    @Range(from = 0, to = Integer.MAX_VALUE)
    private int count;
    @Range(from = 1, to = Integer.MAX_VALUE)
    private int maxCount;
    private boolean infinity = false;
    private boolean extra = false;

    public boolean decrease(boolean doReal) {
        val flag = count - 1 > -1 || infinity;
        if (!infinity && doReal && count > 0) count--;
        return flag;
    }

    public void refill() {
        count = maxCount;
    }

    public boolean hasExtraCount() {
        return !infinity && count > maxCount;
    }

    public int getExtraCount() {
        return infinity ? 0 : hasExtraCount() ? count - maxCount : 0;
    }

    public boolean hasEstus() {
        return infinity || count > 0;
    }

    public boolean isMax() {
        return count >= maxCount;
    }

    public void saveToNBT(@NotNull NBTTagCompound compound) {
        val containerTag = new NBTTagCompound();
        containerTag.setInteger(TAG_COUNT, count);
        containerTag.setInteger(TAG_MAX_COUNT, maxCount);
        containerTag.setBoolean(TAG_INFINITY, infinity);
        containerTag.setBoolean(TAG_EXTRA, extra);
        compound.setTag(TAG_ESTUS_CONTAINER, containerTag);
    }

    public void loadFromNBT(@NotNull NBTTagCompound compound) {
        val containerTag = compound.getCompoundTag(TAG_ESTUS_CONTAINER);
        count = containerTag.getInteger(TAG_COUNT);
        maxCount = containerTag.getInteger(TAG_MAX_COUNT);
        infinity = containerTag.getBoolean(TAG_INFINITY);
        extra = containerTag.getBoolean(TAG_EXTRA);
    }

    public static boolean hasEstus(@NotNull ItemStack stack) {
        return hasEstusContainer(stack) && getFromStack(stack).hasEstus();
    }

    /**
     * Do check for container before!
     *
     * @param stack  stack with EstusConrainer. Container MUST be in stack.
     * @param doReal save new value, use false for creative count infinity.
     * @return {@link EstusContainer#decrease(boolean)} for container in stack.
     * @see EstusContainer#decrease(boolean)
     */
    public static boolean decreaseFromStack(@NotNull ItemStack stack, boolean doReal) {
        val conrainer = getFromStack(stack);
        val flag = conrainer.decrease(doReal);
        saveToStack(conrainer, stack);
        return flag;
    }

    @UnknownNullability
    public static EstusContainer getFromStack(@NotNull ItemStack stack) {
        if (hasEstusContainer(stack)) {
            val result = new EstusContainer();
            result.loadFromNBT(stack.getTagCompound());
            return result;
        } else return null;
    }

    public static EstusContainer getFromStackOrDefault(@NotNull ItemStack stack) {
        var result = getFromStack(stack);
        if (result == null) {
            result = createDefault();
            saveToStack(result, stack);
        }
        return result;
    }

    public static void saveToStack(@NotNull EstusContainer container, @NotNull ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        container.saveToNBT(stack.getTagCompound());
    }

    public static boolean hasEstusContainer(@NotNull ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_ESTUS_CONTAINER);
    }

    public static EstusContainer createDefault() {
        return new EstusContainer(5, 5, false, false);
    }

    public static EstusContainer createInfinity() {
        return new EstusContainer(1, 1, true, false);
    }

    public static EstusContainer createExtra() {
        return new EstusContainer(1, 1, false, true);
    }

    public static EstusContainer createCustomFull(@Range(from = 1, to = Integer.MAX_VALUE) int maxCount) {
        return new EstusContainer(maxCount, maxCount, false, false);
    }

    public static EstusContainer createCustom(@Range(from = 0, to = Integer.MAX_VALUE) int count,
                                              @Range(from = 1, to = Integer.MAX_VALUE) int maxCount) {
        return new EstusContainer(count, maxCount, false, false);
    }

    public static void removeEstusContainer(@NotNull ItemStack stack) {
        if(hasEstusContainer(stack))
            stack.getTagCompound().removeTag(TAG_ESTUS_CONTAINER);
    }
}

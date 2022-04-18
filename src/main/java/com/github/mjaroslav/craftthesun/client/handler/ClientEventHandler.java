package com.github.mjaroslav.craftthesun.client.handler;

import org.jetbrains.annotations.NotNull;

import com.github.mjaroslav.craftthesun.common.data.EstusContainer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    @SubscribeEvent
    public void onItemTooltipEvent(@NotNull ItemTooltipEvent event) {
        val container = EstusContainer.getFromStack(event.itemStack);
        if (container == null) return;
        if (container.isInfinity()) event.toolTip.add(EnumChatFormatting.GOLD
                + StatCollector.translateToLocalFormatted("tooltip.craftthesun:estus_flask.infinity.text"));
        else event.toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocalFormatted(
                "tooltip.craftthesun:estus_flask.count.text", container.getCount(), container.getMaxCount()));
    }
}

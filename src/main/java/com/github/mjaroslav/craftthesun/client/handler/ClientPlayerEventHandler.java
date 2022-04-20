package com.github.mjaroslav.craftthesun.client.handler;

import com.github.mjaroslav.craftthesun.client.audio.PlayerFallSound;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientPlayerEventHandler {
    public static final ClientPlayerEventHandler INSTANCE = new ClientPlayerEventHandler();

    private final Map<String, Boolean> PLAYER_FALL_STATE = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTickEvent(@NotNull TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || !event.player.worldObj.isRemote)
            return;
        if (!event.player.onGround && !PLAYER_FALL_STATE.getOrDefault(event.player.getCommandSenderName(), false))
            if (!event.player.capabilities.isFlying && (event.player.posY
                    - event.player.worldObj.getHeightValue(event.player.serverPosX, event.player.serverPosZ) > 24f)
                    && event.player.motionY < 0) {
                Minecraft.getMinecraft().getSoundHandler().playSound(new PlayerFallSound(event.player));
                PLAYER_FALL_STATE.put(event.player.getCommandSenderName(), true);
            }
        if (event.player.onGround) PLAYER_FALL_STATE.put(event.player.getCommandSenderName(), false);
    }
}

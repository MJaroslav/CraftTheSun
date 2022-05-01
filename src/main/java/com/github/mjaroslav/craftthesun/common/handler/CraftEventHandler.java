package com.github.mjaroslav.craftthesun.common.handler;

import com.github.mjaroslav.craftthesun.common.init.ModItems;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CraftEventHandler {
    public static final CraftEventHandler INSTANCE = new CraftEventHandler();

    @SubscribeEvent
    public void onAnvilUpdateEvent(@NotNull AnvilUpdateEvent event) {
        if (event.left.getItem() == ModItems.estusFlask && event.right.getItem() == Items.poisonous_potato) {
            event.output = event.left.copy();
            event.output.setItemDamage(3);
            event.output.func_135074_t();
            event.cost = 1;
        }
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}

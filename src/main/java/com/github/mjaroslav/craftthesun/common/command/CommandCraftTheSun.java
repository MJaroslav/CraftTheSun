package com.github.mjaroslav.craftthesun.common.command;

import com.github.mjaroslav.craftthesun.common.data.CraftTheSunEEP;
import com.github.mjaroslav.craftthesun.common.data.SyncData;
import lombok.val;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCraftTheSun extends CommandBase {
    @Override
    public String getCommandName() {
        return "craftthesun";
    }

    @SuppressWarnings("rawtypes")
    @Nullable
    @Override
    public List getCommandAliases() {
        return Collections.singletonList("cts");
    }

    @Override
    public String getCommandUsage(@NotNull ICommandSender sender) {
        return "commands.craftthesun.usage";
    }

    @Override
    public void processCommand(@NotNull ICommandSender sender, @NotNull String[] args) {
        if (args.length == 3) {
            if (args[0].equals("type")) {
                val player = getPlayer(sender, args[2]);
                val type = getType(args[1]);
                if (player != null && type != null)
                    setType(sender, player, type);
                else throw new WrongUsageException(getCommandUsage(sender));
            } else throw new WrongUsageException(getCommandUsage(sender));
        } else if (args.length == 2) {
            if (args[0].equals("type")) {
                val type = getType(args[1]);
                val player = getCommandSenderAsPlayer(sender);
                if (type != null)
                    setType(sender, player, type);
                else throw new WrongUsageException(getCommandUsage(sender));
            } else throw new WrongUsageException(getCommandUsage(sender));
        } else if (args.length == 1) {
            if (args[0].equals("type")) {
                val player = getCommandSenderAsPlayer(sender);
                val type = CraftTheSunEEP.get(player).getSyncData().getType();
                sender.addChatMessage(new ChatComponentTranslation("commands.craftthesun.type.get", type));
            } else throw new WrongUsageException(getCommandUsage(sender));
        } else throw new WrongUsageException(getCommandUsage(sender));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(@NotNull ICommandSender sender, @NotNull String[] args) {
        if (args.length == 1)
            return Collections.singletonList("type");
        else if (args.length == 2 && args[0].equals("type"))
            return Arrays.stream(SyncData.PlayerType.values()).map(Enum::name).collect(Collectors.toList());
        else return null;
    }

    @Override
    public boolean isUsernameIndex(@NotNull String[] args, int index) {
        return index == 2 && args[0].equals("type");
    }

    private static void setType(@NotNull ICommandSender sender, @NotNull EntityPlayerMP player, @NotNull SyncData.PlayerType type) {
        CraftTheSunEEP.get(player).getSyncData().setType(type);
        sender.addChatMessage(new ChatComponentTranslation("commands.craftthesun.type.set", type, player.getCommandSenderName()));
    }

    private static SyncData.PlayerType getType(@NotNull String string) {
        try {
            return SyncData.PlayerType.valueOf(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

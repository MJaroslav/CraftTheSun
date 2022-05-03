package com.github.mjaroslav.craftthesun.common.command;

import com.github.mjaroslav.craftthesun.common.data.SyncData.PlayerType;
import com.github.mjaroslav.craftthesun.common.util.CommonUtils;
import lombok.val;
import lombok.var;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
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
        return getCommandUsage() + ".usage";
    }

    @NotNull
    private String getCommandUsage() {
        return "commands.craftthesun";
    }

    //
    // HELL
    //
    @Override
    public void processCommand(@NotNull ICommandSender sender, @NotNull String[] args) {
        val len = args.length;
        if (len > 0) {
            if (args[0].equals("type")) {
                if (len > 1) {
                    switch (args[1]) {
                        case "list":
                            func_152373_a(sender, this, "commands.craftthesun.type.list.success",
                                    Arrays.stream(PlayerType.values()).map(Enum::name).collect(Collectors.joining(", ")));
                            break;
                        case "show":
                            if (len == 2)
                                func_152373_a(sender, this, "commands.craftthesun.type.show.success",
                                        CommonUtils.getPlayerType(getCommandSenderAsPlayer(sender)));
                            else if (len == 3) {
                                val player = getPlayer(sender, args[2]);
                                func_152373_a(sender, this, "commands.craftthesun.type.show.another.success",
                                        player.getCommandSenderName(), CommonUtils.getPlayerType(player));
                            } else showUsage("type.show");
                            break;
                        case "set":
                            if (len == 3) {
                                val type = parseType(args[2]);
                                CommonUtils.setPlayerType(getCommandSenderAsPlayer(sender), type);
                                func_152373_a(sender, this, "commands.craftthesun.type.set.success", type);
                            } else if (len == 4) {
                                val type = parseType(args[2]);
                                val player = getPlayer(sender, args[3]);
                                CommonUtils.setPlayerType(player, type);
                                func_152373_a(sender, this, "commands.craftthesun.type.set.another.success",
                                        player.getCommandSenderName(), type);
                            } else showUsage("type.set");
                            break;
                        default:
                            showUsage("type");
                            break;
                    }
                } else showUsage("type");
            } else if (args[0].equals("humanity")) {
                if (len > 1) {
                    switch (args[1]) {
                        case "show":
                            if (len == 2) {
                                func_152373_a(sender, this, "commands.craftthesun.humanity.show.success",
                                        CommonUtils.getPlayerHumanity(getCommandSenderAsPlayer(sender)));
                            } else if (len == 3) {
                                val player = getPlayer(sender, args[2]);
                                func_152373_a(sender, this, "commands.craftthesun.humanity.show.another.success",
                                        player.getCommandSenderName(), CommonUtils.getPlayerHumanity(player));
                            } else showUsage("humanity.show");
                            break;
                        case "set":
                            if (len == 3) {
                                val value = parseIntBounded(sender, args[2], 0, Integer.MAX_VALUE);
                                CommonUtils.setPlayerHumanity(getCommandSenderAsPlayer(sender), value);
                                func_152373_a(sender, this, "commands.craftthesun.humanity.change.success", value);
                            } else if (len == 4) {
                                val value = parseIntBounded(sender, args[2], 0, Integer.MAX_VALUE);
                                val player = getPlayer(sender, args[3]);
                                CommonUtils.setPlayerHumanity(player, value);
                                func_152373_a(sender, this, "commands.craftthesun.humanity.change.another.success",
                                        player.getCommandSenderName(), value);
                            } else showUsage("humanity.set");
                            break;
                        case "add":
                            if (len == 3) {
                                val player = getCommandSenderAsPlayer(sender);
                                var value = parseInt(sender, args[2]);
                                CommonUtils.addPlayerHumanity(getCommandSenderAsPlayer(sender), value);
                                value = CommonUtils.getPlayerHumanity(player);
                                func_152373_a(sender, this, "commands.craftthesun.humanity.change.success", value);
                            } else if (len == 4) {
                                var value = parseInt(sender, args[2]);
                                val player = getPlayer(sender, args[3]);
                                CommonUtils.addPlayerHumanity(player, value);
                                value = CommonUtils.getPlayerHumanity(player);
                                func_152373_a(sender, this, "commands.craftthesun.humanity.change.another.success",
                                        player.getCommandSenderName(), value);
                            } else showUsage("humanity.add");
                            break;
                        default:
                            showUsage("humanity");
                            break;
                    }
                } else showUsage("humanity");
            } else showUsage();
        } else showUsage();
//        if (args.length == 3) {
//            if (args[0].equals("type")) {
//                val player = getPlayer(sender, args[2]);
//                val type = getType(args[1]);
//                if (player != null && type != null)
//                    setType(sender, player, type);
//                else
//                    throw new WrongUsageException(getCommandUsage(sender));
//            } if(args[0].equals("humanity")) {
//                val player = getPlayer(sender, args[2]);
//                val value =
//            }else
//                throw new WrongUsageException(getCommandUsage(sender));
//        } else if (args.length == 2) {
//            if (args[0].equals("type")) {
//                val type = getType(args[1]);
//                val player = getCommandSenderAsPlayer(sender);
//                if (type != null)
//                    setType(sender, player, type);
//                else
//                    throw new WrongUsageException(getCommandUsage(sender));
//            } else
//                throw new WrongUsageException(getCommandUsage(sender));
//        } else if (args.length == 1) {
//            if (args[0].equals("type")) {
//                val player = getCommandSenderAsPlayer(sender);
//                val type = CraftTheSunEEP.get(player).getSyncData().getType();
//                sender.addChatMessage(new ChatComponentTranslation("commands.craftthesun.type.get", type));
//            } else
//                throw new WrongUsageException(getCommandUsage(sender));
//        } else
//            throw new WrongUsageException(getCommandUsage(sender));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(@NotNull ICommandSender sender, @NotNull String[] args) {
        if (args.length == 1)
            return Arrays.asList("type", "humanity");
        else if (args.length == 2)
            return args[0].equals("type") ? Arrays.asList("list", "show", "set") : args[0].equals("humanity") ?
                    Arrays.asList("show", "add", "set") : null;
        else if (args.length == 3)
            return (args[0].equals("type") || args[0].equals("humanity")) && args[1].equals("show") ?
                    getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) :
                    args[0].equals("type") && args[1].equals("set") ?
                            Arrays.stream(PlayerType.values()).map(PlayerType::name).collect(Collectors.toList()) : null;
        else if (args.length == 4)
            return ((args[0].equals("type") || args[0].equals("humanity")) && (args[1].equals("show") ||
                    args[1].equals("set"))) || (args[0].equals("humanity") && args[1].equals("add")) ?
                    getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    private void showUsage() {
        throw new WrongUsageException(getCommandUsage() + ".usage");
    }

    private void showUsage(@NotNull String suffix) {
        throw new WrongUsageException(getCommandUsage() + "." + suffix + ".usage");
    }

//    @Override
//    public boolean isUsernameIndex(@NotNull String[] args, int index) {
//        return args.length == 4 && ((args[0].equals("type") && (args[1].equals("show") || args[1].equals("set"))) ||
//                (args[0].equals("humanity") && (args[1].equals("show") || args[1].equals("add") ||
//                        args[1].equals("set"))));
//    }

    private static void setType(@NotNull ICommandSender sender, @NotNull EntityPlayerMP player,
                                @NotNull PlayerType type) {
        CommonUtils.setPlayerType(player, type);
        sender.addChatMessage(
                new ChatComponentTranslation("commands.craftthesun.type.set", type, player.getCommandSenderName()));
    }

    private PlayerType parseType(@NotNull String string) {
        try {
            return PlayerType.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new CommandException("commands.craftthesun.playertype.invalid", string);
        }
    }
}

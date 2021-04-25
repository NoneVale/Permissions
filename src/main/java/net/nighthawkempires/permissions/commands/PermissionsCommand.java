package net.nighthawkempires.permissions.commands;

import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.lang.PermissionsMessages;
import net.nighthawkempires.permissions.permissions.PermissionsManager;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.getCommandManager;
import static net.nighthawkempires.core.CorePlugin.getMessages;
import static net.nighthawkempires.permissions.PermissionsPlugin.getUserRegistry;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.WHITE;

public class PermissionsCommand implements CommandExecutor {

    public PermissionsCommand() {
        getCommandManager().registerCommands("permissions", new String[] {
                "ne.admin", "ne.permissions.admin"
        });
    }

    String[] helpPage1 = new String[] {
            getMessages().getMessage(Messages.CHAT_HEADER),
            ChatColor.translateAlternateColorCodes('&', "&8Command&7: Permissions   &8-   [Optional], <Required>"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
            getMessages().getCommand("perms", "help", "Show this help menu."),
            getMessages().getCommand("perms", "info <-g|-u> <group|user>", "Show info about a group or user"),
            getMessages().getCommand("perms", "add <-g|-u> <group|user> <permission> [server]", "Add a permission to a group or user"),
            //getMessages().getCommand("perms", "addtemp <-g|-u> <group|user> <permission>", "Add a temporary permission to a group or user"),
            getMessages().getCommand("perms", "remove <-g|-u> <group|user> <permission> [server]", "Remove a permission from a group or user"),
            //getMessages().getCommand("perms", "remtemp <-g|-u> <group|user> <permission>", "Remove a temporary permission from a group or user"),
            getMessages().getMessage(Messages.CHAT_FOOTER)
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

            if (!player.hasPermission("ne.admin") && !player.hasPermission("ne.permissions.admin")) {
                player.sendMessage(getMessages().getChatTag(Messages.NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 0:
                    player.sendMessage(helpPage1);
                    return true;
                case 1:
                    switch (args[0].toLowerCase()) {
                        case "help":
                            player.sendMessage(helpPage1);
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                case 3:
                    switch (args[0].toLowerCase()) {
                        case "info":
                            switch (args[1].toLowerCase()) {
                                case "-g":
                                    Bukkit.dispatchCommand(sender, "group info " + args[2]);
                                    return true;
                                case "-u":
                                    Bukkit.dispatchCommand(sender, "group view " + args[2]);
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                                    return true;
                            }
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                case 4:
                    switch (args[0].toLowerCase()) {
                        case "add":
                            switch (args[1].toLowerCase()) {
                                case "-g":
                                    Bukkit.dispatchCommand(sender, "group addperm " + args[2] + " " + args[3]);
                                    return true;
                                case "-u":
                                    String targetName = args[2];
                                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                                    if (!PermissionsPlugin.getUserRegistry().userExists(offlineTarget.getUniqueId())) {
                                        player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                        return true;
                                    }

                                    UserModel targetUserModel = PermissionsPlugin.getUserRegistry().getUser(offlineTarget.getUniqueId());

                                    String permission = args[3];
                                    if (targetUserModel.hasPermission(permission)) {
                                        player.sendMessage(getMessages().getChatTag(PermissionsMessages.USER_HAS_PERMISSION));
                                        return true;
                                    }

                                    targetUserModel.addPermission(permission);
                                    player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have added permission " + ChatColor.WHITE + permission
                                            + ChatColor.GRAY + " to user " + ChatColor.GREEN + offlineTarget.getName() + ChatColor.GRAY + "."));
                                    if (offlineTarget.isOnline()) {
                                        Player targetPlayer = offlineTarget.getPlayer();
                                        PermissionsPlugin.getPermissionsManager().addPermission(targetPlayer, permission);
                                        targetPlayer.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Permission " + ChatColor.WHITE + permission
                                                + ChatColor.GRAY + " has been added to you."));
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                                    return true;
                            }
                        case "remove":
                            switch (args[1].toLowerCase()) {
                                case "-g":
                                    Bukkit.dispatchCommand(sender, "group remperm " + args[2] + " " + args[3]);
                                    return true;
                                case "-u":
                                    String targetName = args[2];
                                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                                    if (!PermissionsPlugin.getUserRegistry().userExists(offlineTarget.getUniqueId())) {
                                        player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                        return true;
                                    }

                                    UserModel targetUserModel = PermissionsPlugin.getUserRegistry().getUser(offlineTarget.getUniqueId());

                                    String permission = args[3];
                                    if (!targetUserModel.hasPermission(permission)) {
                                        player.sendMessage(getMessages().getChatTag(PermissionsMessages.USER_DOESNT_HAVE_PERMISSION));
                                        return true;
                                    }

                                    targetUserModel.removePermission(permission);
                                    player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have removed permission " + ChatColor.WHITE + permission
                                            + ChatColor.GRAY + " from user " + ChatColor.GREEN + offlineTarget.getName() + ChatColor.GRAY + "."));
                                    if (offlineTarget.isOnline()) {
                                        Player targetPlayer = offlineTarget.getPlayer();
                                        PermissionsPlugin.getPermissionsManager().removePermission(targetPlayer, permission);
                                        targetPlayer.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Permission " + ChatColor.WHITE + permission
                                                + ChatColor.GRAY + " has been removed from you."));
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                                    return true;
                            }
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                case 5:
                    switch (args[0].toLowerCase()) {
                        case "add":
                            switch (args[1].toLowerCase()) {
                                case "-g":
                                    Bukkit.dispatchCommand(sender, "group addperm " + args[2] + " " + args[3] + " " + args[4]);
                                    return true;
                                case "-u":
                                    String targetName = args[2];
                                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                                    if (!PermissionsPlugin.getUserRegistry().userExists(offlineTarget.getUniqueId())) {
                                        player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                        return true;
                                    }

                                    UserModel targetUserModel = PermissionsPlugin.getUserRegistry().getUser(offlineTarget.getUniqueId());

                                    String permission = args[3];

                                    String type = args[4];
                                    ServerType serverType = null;
                                    for (ServerType types : ServerType.values()) {
                                        if (types.name().toLowerCase().equals(type.toLowerCase())) {
                                            serverType = types;
                                            break;
                                        }
                                    }
                                    if (serverType == null) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "That server type does not exist," +
                                                " please make sure you spelled it correctly."));
                                        return true;
                                    }

                                    if (targetUserModel.hasPermission(serverType, permission)) {
                                        player.sendMessage(getMessages().getChatTag(PermissionsMessages.USER_HAS_PERMISSION));
                                        return true;
                                    }

                                    targetUserModel.addPermission(serverType, permission);
                                    player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have added server permission " + ChatColor.WHITE + permission
                                            + ChatColor.GRAY + " to user " + ChatColor.GREEN + offlineTarget.getName() + ChatColor.GRAY + " on " + WHITE + serverType.name() + GRAY + "."));
                                    if (offlineTarget.isOnline()) {
                                        Player targetPlayer = offlineTarget.getPlayer();
                                        PermissionsPlugin.getPermissionsManager().setupPermissions(targetPlayer);
                                        targetPlayer.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Server permission " + ChatColor.WHITE + permission
                                                + ChatColor.GRAY + " has been added to you on " + WHITE + serverType.name() + GRAY + "."));
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                                    return true;
                            }
                        case "remove":
                            switch (args[1].toLowerCase()) {
                                case "-g":
                                    Bukkit.dispatchCommand(sender, "group remperm " + args[2] + " " + args[3] + " " + args[4]);
                                    return true;
                                case "-u":
                                    String targetName = args[2];
                                    OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
                                    if (!PermissionsPlugin.getUserRegistry().userExists(offlineTarget.getUniqueId())) {
                                        player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                        return true;
                                    }

                                    UserModel targetUserModel = PermissionsPlugin.getUserRegistry().getUser(offlineTarget.getUniqueId());

                                    String permission = args[3].toLowerCase();

                                    String type = args[4];
                                    ServerType serverType = null;
                                    for (ServerType types : ServerType.values()) {
                                        if (types.name().toLowerCase().equals(type.toLowerCase())) {
                                            serverType = types;
                                            break;
                                        }
                                    }
                                    if (serverType == null) {
                                        player.sendMessage(getMessages().getChatMessage(GRAY + "That server type does not exist," +
                                                " please make sure you spelled it correctly."));
                                        return true;
                                    }

                                    if (!targetUserModel.hasPermission(serverType, permission)) {
                                        player.sendMessage(getMessages().getChatTag(PermissionsMessages.USER_DOESNT_HAVE_PERMISSION));
                                        return true;
                                    }

                                    targetUserModel.removePermission(serverType, permission);
                                    player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have removed server permission " + ChatColor.WHITE + permission
                                            + ChatColor.GRAY + " from user " + ChatColor.GREEN + offlineTarget.getName() + ChatColor.GRAY + " on "
                                            + WHITE + serverType.name() + GRAY +"."));
                                    if (offlineTarget.isOnline()) {
                                        Player targetPlayer = offlineTarget.getPlayer();
                                        PermissionsPlugin.getPermissionsManager().setupPermissions(targetPlayer);
                                        targetPlayer.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Server permission " + ChatColor.WHITE + permission
                                                + ChatColor.GRAY + " has been removed from you on " + WHITE + serverType.name() + GRAY + "."));
                                    }
                                    return true;
                                default:
                                    player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                                    return true;
                            }
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                default:
                    player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                    return true;
            }
        }
        return false;
    }
}
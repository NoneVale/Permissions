package net.nighthawkempires.permissions.commands;

import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.permissions.lang.PermissionsMessages;
import net.nighthawkempires.permissions.status.StatusModel;
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
import static net.nighthawkempires.permissions.PermissionsPlugin.*;

public class StatusCommand implements CommandExecutor {

    public StatusCommand() {
        getCommandManager().registerCommands("status", new String[] {
                "ne.admin", "ne.permissions.admin"
        });
    }

    private String[] helpPage1 = new String[] {
            getMessages().getMessage(Messages.CHAT_HEADER),
            ChatColor.translateAlternateColorCodes('&', "&8Command&7: Status   &8-   [Optional], <Required>"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
            getMessages().getCommand("status", "help", "Show this help menu"),
            getMessages().getCommand("status", "list", "Show a list of all statuses"),
            getMessages().getCommand("status", "delete <status>", "Delete a status"),
            getMessages().getCommand("status", "info <status>", "Show info about a status"),
            getMessages().getCommand("status", "remove [user]", "Remove a user's status"),
            getMessages().getCommand("status", "view [user]", "View a user's status"),
            getMessages().getCommand("status", "create <name> <prefix>", "Create a new status"),
            getMessages().getCommand("status", "set <status> [user]", "Set a user's status"),
            getMessages().getCommand("status", "setprefix <status> <prefix>", "Set a status' prefix"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
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
                        case "list":
                            StringBuilder statusBuilder = new StringBuilder();
                            statusBuilder.append(ChatColor.translateAlternateColorCodes('&', "&8 - "));
                            for (StatusModel statusModel : getStatusRegistry().getStatuses()) {
                                statusBuilder.append(statusModel.getPrefix(), 0, 2).append(statusModel.getName())
                                        .append(ChatColor.translateAlternateColorCodes('&', "&7, "));
                            }

                            if (getStatusRegistry().getStatuses().isEmpty()) {
                                statusBuilder.append(ChatColor.translateAlternateColorCodes('&', "&8Noneee"));
                            }
                            String[] list = new String[] {
                                    getMessages().getMessage(Messages.CHAT_HEADER),
                                    ChatColor.translateAlternateColorCodes('&', "&8List&7: Statuses"),
                                    getMessages().getMessage(Messages.CHAT_FOOTER),
                                    ChatColor.translateAlternateColorCodes('&', "&8Groups&7: "),
                                    ChatColor.translateAlternateColorCodes('&',
                                            statusBuilder.toString().substring(0, statusBuilder.toString().length() - 2)),
                                    getMessages().getMessage(Messages.CHAT_FOOTER)
                            };
                            player.sendMessage(list);
                            return true;
                        case "remove":
                            if (userModel.getStatus() == null) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You do not have a status to remove."));
                                return true;
                            }

                            userModel.setStatus(null);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have removed your status."));
                            return true;
                        case "view":
                            if (userModel.getStatus() == null) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You do not have a set status."));
                            } else {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Your current status is: "
                                        + userModel.getStatus().getColoredName() + ChatColor.GRAY + "."));
                            }
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "delete":
                            String name = args[1];
                            if (!getStatusRegistry().statusExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.STATUS_DOES_NOT_EXIST));
                                return true;
                            }

                            StatusModel statusModel = getStatusRegistry().getStatus(name);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + statusModel.getName() + " has been successfully deleted."));
                            getStatusRegistry().deleteStatus(statusModel);
                            return true;
                        case "info":
                            name = args[1];
                            if (!getStatusRegistry().statusExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.STATUS_DOES_NOT_EXIST));
                                return true;
                            }

                            statusModel = getStatusRegistry().getStatus(name);

                            String[] info = new String[] {
                                    getMessages().getMessage(Messages.CHAT_HEADER),
                                    ChatColor.translateAlternateColorCodes('&', "&8Status Info&7: &b" + statusModel.getName()),
                                    getMessages().getMessage(Messages.CHAT_FOOTER),
                                    ChatColor.translateAlternateColorCodes('&', "&8Prefix&7: " + statusModel.getPrefix()),
                                    ChatColor.translateAlternateColorCodes('&', "&8Users In Group&7: &6" + statusModel.getUsersWithStatus()),
                                    getMessages().getMessage(Messages.CHAT_FOOTER)
                            };
                            player.sendMessage(info);
                            return true;
                        case "view":
                            String targetName = args[1];
                            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                            if (!getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            UserModel targetUserModel = getUserRegistry().getUser(target.getUniqueId());

                            if (targetUserModel.getStatus() == null)
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GREEN + target.getName() + ChatColor.GRAY + " does not have a set status."));
                            else
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GREEN + target.getName() + "'s" + ChatColor.GRAY + " current status is "
                                        + targetUserModel.getStatus().getColoredName() + ChatColor.GRAY + "."));
                            return true;
                        case "remove":
                            targetName = args[1];
                            target = Bukkit.getOfflinePlayer(targetName);
                            if (!getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            targetUserModel = getUserRegistry().getUser(target.getUniqueId());

                            if (targetUserModel.getStatus() == null) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GREEN + target.getName()
                                        + ChatColor.GRAY + " does not have a status to remove."));
                                return true;
                            }

                            targetUserModel.setStatus(null);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have removed " + ChatColor.GREEN + target.getName() + "'s "
                                    + ChatColor.GRAY + "status."));
                            if (target.isOnline())
                                target.getPlayer().sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Your status has been removed."));
                            return true;
                        case "set":
                            name = args[1];
                            if (!getStatusRegistry().statusExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.STATUS_DOES_NOT_EXIST));
                                return true;
                            }

                            statusModel = getStatusRegistry().getStatus(name);
                            userModel.setStatus(statusModel);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have set your status to "
                                    + statusModel.getColoredName() + ChatColor.GRAY + "."));
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                case 3:
                    switch (args[0].toLowerCase()) {
                        case "create":
                            String name = args[1];
                            if (getStatusRegistry().statusExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.STATUS_ALREADY_EXISTS));
                                return true;
                            }

                            getStatusRegistry().createStatus(name, args[2]);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Status " + getStatusRegistry().getStatus(name).getColoredName()
                                    + ChatColor.GRAY + " has been successfully created."));
                            return true;
                        case "set":
                            name = args[1];
                            if (!getStatusRegistry().statusExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.STATUS_DOES_NOT_EXIST));
                                return true;
                            }

                            StatusModel statusModel = getStatusRegistry().getStatus(name);

                            String targetName = args[2];
                            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                            if (!getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            UserModel targetUserModel = getUserRegistry().getUser(target.getUniqueId());
                            targetUserModel.setStatus(statusModel);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have set " + ChatColor.GREEN + target.getName() + "'s "
                                    + ChatColor.GRAY + " status to " + statusModel.getColoredName() + ChatColor.GRAY + "."));
                            if (target.isOnline())
                                target.getPlayer().sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Your status has been set to "
                                        + statusModel.getColoredName() + ChatColor.GRAY + "."));
                            return true;
                        case "setprefix":
                            name = args[1];
                            if (!getStatusRegistry().statusExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.STATUS_DOES_NOT_EXIST));
                                return true;
                            }

                            String prefix = args[2];

                            statusModel = getStatusRegistry().getStatus(name);
                            statusModel.setPrefix(prefix);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have set status " + statusModel.getName() + "'s prefix to "
                                    + ChatColor.translateAlternateColorCodes('&', prefix)));
                            return true;
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

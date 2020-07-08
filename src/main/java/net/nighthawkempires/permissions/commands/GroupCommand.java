package net.nighthawkempires.permissions.commands;

import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.events.GroupChangeEvent;
import net.nighthawkempires.permissions.group.GroupModel;
import net.nighthawkempires.permissions.lang.PermissionsMessages;
import net.nighthawkempires.permissions.user.UserModel;
import org.apache.commons.lang.math.NumberUtils;
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
import static org.bukkit.ChatColor.*;

public class GroupCommand implements CommandExecutor {

    public GroupCommand() {
        getCommandManager().registerCommands("group", new String[] {
                "ne.admin", "ne.permissions.admin"
        });
    }

    private String[] helpPage1 = new String[] {
            getMessages().getMessage(Messages.CHAT_HEADER),
            translateAlternateColorCodes('&', "&8Command&7: Group   &8-   [Optional], <Required>"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
            getMessages().getCommand("group", "list", "Show a list of all groups"),
            getMessages().getCommand("group", "help [page]", "Show this help menu"),
            getMessages().getCommand("group", "info <group>", "Show info about a group"),
            getMessages().getCommand("group", "delete <group>", "Delete a group"),
            getMessages().getCommand("group", "demote [player]", "Demote a player"),
            getMessages().getCommand("group", "promote [player]", "Promote a player"),
            getMessages().getCommand("group", "setdefault <group>", "Set the default group"),
            getMessages().getCommand("group", "view [user]", "View a users group info"),
            getMessages().getCommand("group", "add <group> [user]", "Add a group to a user"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
    };

    private String[] helpPage2 = new String[] {
            getMessages().getMessage(Messages.CHAT_HEADER),
            translateAlternateColorCodes('&', "&8Command&7: Group   &8-   [Optional], <Required>"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
            getMessages().getCommand("group", "addinherit <group> <inheritGroup>", "Add an inherited group to a group"),
            getMessages().getCommand("group", "addperm <group> <permission>", "Add a permission to a group"),
            //getMessages().getCommand("group", "addtemp <group> <permission>", "Add a temporary permission to a group"),
            getMessages().getCommand("group", "create <name> <prefix>", "Create a new group"),
            getMessages().getCommand("group", "reminherit <group> <inheritGroup>", "Remove an inherited group from a group"),
            getMessages().getCommand("group", "remove <group> [user]", "Remove a group from a user"),
            getMessages().getCommand("group", "remperm <group> <permission>", "Remove a permission from a group"),
            //getMessages().getCommand("group", "remtemp <group> <permission>", "Remove a temporary permission from a group"),
            getMessages().getCommand("group", "setchain <group> <chain>", "Set a groups chain"),
            getMessages().getCommand("group", "setprefix <group> <prefix>", "Set a groups prefix"),
            getMessages().getCommand("group", "setpriority <group> <priority>", "Set a groups priority"),
            getMessages().getMessage(Messages.CHAT_FOOTER),
    };

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());

            if (!player.hasPermission("ne.admin") || !player.hasPermission("ne.permissions.admin")) {
                getMessages().getChatTag(Messages.NO_PERMS);
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
                            StringBuilder groupBuilder = new StringBuilder();
                            groupBuilder.append(translateAlternateColorCodes('&', "&8 - "));
                            for (GroupModel groupModel : getGroupRegistry().getGroups()) {
                                groupBuilder.append(groupModel.getColoredName() + GRAY).append(translateAlternateColorCodes('&',
                                        " &8[&6" + groupModel.getGroupChain() + "&8] [&6" + groupModel.getGroupPriority() + "&8]&7, "));
                            }

                            if (getGroupRegistry().getGroups().isEmpty()) {
                                groupBuilder.append(translateAlternateColorCodes('&', "&8Noneee"));
                            }

                            String[] list = new String[] {
                                    getMessages().getMessage(Messages.CHAT_HEADER),
                                    translateAlternateColorCodes('&', "&8List&7: Groups"),
                                    getMessages().getMessage(Messages.CHAT_FOOTER),
                                    translateAlternateColorCodes('&', "&8Groups&7: "),
                                    groupBuilder.toString().substring(0, groupBuilder.toString().length() - 2),
                                    getMessages().getMessage(Messages.CHAT_FOOTER)
                            };
                            player.sendMessage(list);
                            return true;
                        case "view":
                            groupBuilder = new StringBuilder();

                            if (userModel.getGroups().isEmpty())
                                groupBuilder.append(ChatColor.GRAY).append("Noneee");
                            else {
                                for (GroupModel groupModel : userModel.getGroups()) {
                                    groupBuilder.append(groupModel.getColoredName() + GRAY).append(ChatColor.GRAY).append(", ");
                                }
                            }

                            String statusString = "&7None";
                            if (userModel.getStatus() != null) {
                                statusString = userModel.getStatus().getColoredName() + GRAY;
                            }

                            StringBuilder permissionsBuilder = new StringBuilder();

                            if (userModel.getPermissions().isEmpty())
                                permissionsBuilder.append(ChatColor.GRAY).append("Noneee");
                            else {
                                for (String permission : userModel.getPermissions()) {
                                    if (permission.startsWith("-"))
                                        permissionsBuilder.append(RED).append(permission.substring(1)).append(ChatColor.GRAY).append(", ");
                                    else
                                        permissionsBuilder.append(GREEN).append(permission).append(ChatColor.GRAY).append(", ");
                                }
                            }

                            String[] info = new String[] {
                                    getMessages().getMessage(Messages.CHAT_HEADER),
                                    translateAlternateColorCodes('&', "&8User Info&7: &b" + player.getName()),
                                    getMessages().getMessage(Messages.CHAT_FOOTER),
                                    translateAlternateColorCodes('&', "&8Groups&7: "
                                            + groupBuilder.toString().substring(0, groupBuilder.toString().length() - 2)),
                                    translateAlternateColorCodes('&', "&8Status&7: " + statusString),
                                    translateAlternateColorCodes('&', "&8Permissions&7: "
                                            + permissionsBuilder.toString().substring(0, permissionsBuilder.toString().length() - 2)),
                                    getMessages().getMessage(Messages.CHAT_FOOTER)
                            };

                            player.sendMessage(info);
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "add":
                            String name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            GroupModel groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            if (userModel.getGroups().contains(groupModel)) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You already have that group."));
                                return true;
                            }

                            groupModel.setUsersInGroup(groupModel.getUsersInGroup() + 1);
                            userModel.addGroup(groupModel);
                            PermissionsPlugin.getPermissionsManager().setupPermissions(player);

                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have added the group " + groupModel.getColoredName() + GRAY
                                    + ChatColor.GRAY + " to yourself."));
                            Bukkit.getPluginManager().callEvent(new GroupChangeEvent(player));
                            return true;
                        case "delete":
                            name = args[1];
                            if (!getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = getGroupRegistry().getGroup(name);
                            for (UserModel model : getUserRegistry().loadAllFromDb().values())
                                if (model.getGroups().contains(groupModel))
                                    model.removeGroup(groupModel);

                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + groupModel.getColoredName() + GRAY + " has been successfully deleted."));
                            getGroupRegistry().deleteGroup(groupModel);
                            return true;
                        case "demote":
                            String targetName = args[1];
                            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                            if (!PermissionsPlugin.getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            UserModel targetUserModel = PermissionsPlugin.getUserRegistry().getUser(target.getUniqueId());
                            GroupModel demoteTo = PermissionsPlugin.getGroupRegistry().getNextGroupDown(targetUserModel.getHighestRankingGroup());

                            if (demoteTo == null) {
                                player.sendMessage(getMessages().getChatMessage(GREEN + target.getName() + ChatColor.GRAY
                                        + " is not able to be demoted as they're already the lowest rank in the demotion chain."));
                                return true;
                            }

                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have demoted " + GREEN + target.getName() + ChatColor.GRAY
                                    + " to group " + demoteTo.getColoredName() + GRAY + ChatColor.GRAY + "."));
                            if (target.isOnline()) {
                                target.getPlayer().sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have been demoted to " + demoteTo.getColoredName() + GRAY
                                        + ChatColor.GRAY + "."));
                            }
                            targetUserModel.getHighestRankingGroup().setUsersInGroup(targetUserModel.getHighestRankingGroup().getUsersInGroup() - 1);
                            targetUserModel.removeGroup(targetUserModel.getHighestRankingGroup());
                            demoteTo.setUsersInGroup(demoteTo.getUsersInGroup() + 1);
                            targetUserModel.addGroup(demoteTo);
                            return true;
                        case "help":
                            if (!NumberUtils.isNumber(args[1])) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "The page must be a number instead of a string."));
                                return true;
                            }

                            int page = Integer.parseInt(args[1]);
                            if (page != 1 && page != 2) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "That page does not exist, please choose between page 1 or 2."));
                                return true;
                            }

                            if (page == 1)
                                player.sendMessage(helpPage1);
                            else
                                player.sendMessage(helpPage2);
                            return true;
                        case "info":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            StringBuilder permissionsBuilder = new StringBuilder();
                            if (groupModel.getPermissions().isEmpty())
                                permissionsBuilder.append(ChatColor.GRAY).append("Noneee");
                            else {
                                for (String permission : groupModel.getPermissions())
                                    if (permission.startsWith("-"))
                                        permissionsBuilder.append(RED).append(permission.substring(1)).append(ChatColor.GRAY).append(", ");
                                    else
                                        permissionsBuilder.append(GREEN).append(permission).append(ChatColor.GRAY).append(", ");
                            }

                            StringBuilder inheritedGroupsBuilder = new StringBuilder();
                            if (groupModel.getInheritedGroups().isEmpty())
                                inheritedGroupsBuilder.append(ChatColor.GRAY).append("Noneee");
                            else {
                                for (String inheritedGroupName : groupModel.getInheritedGroups()) {
                                    GroupModel inheritedGroupModel = PermissionsPlugin.getGroupRegistry().getGroup(inheritedGroupName);
                                    inheritedGroupsBuilder.append(inheritedGroupModel.getColoredName() + GRAY).append(ChatColor.GRAY).append(", ");
                                }
                            }

                            StringBuilder inheritedPermissionsBuilder = new StringBuilder();
                            if (groupModel.getInheritedPermissions().isEmpty())
                                inheritedPermissionsBuilder.append(ChatColor.GRAY).append("Noneee");
                            else {
                                for (String permission : groupModel.getInheritedPermissions()) {
                                    if (permission.startsWith("-"))
                                        inheritedPermissionsBuilder.append(RED).append(permission.substring(1)).append(ChatColor.GRAY).append(", ");
                                    else
                                        inheritedPermissionsBuilder.append(GREEN).append(permission).append(ChatColor.GRAY).append(", ");
                                }
                            }

                            String[] info = new String[] {
                                    getMessages().getMessage(Messages.CHAT_HEADER),
                                    translateAlternateColorCodes('&', "&8Group Info&7: &b" + groupModel.getName()),
                                    getMessages().getMessage(Messages.CHAT_FOOTER),
                                    translateAlternateColorCodes('&', "&8Prefix&7: " + groupModel.getPrefix()),
                                    translateAlternateColorCodes('&', "&8Chain&7: &6" + groupModel.getGroupChain()
                                            + "    &8-    Priority&7: &6" + groupModel.getGroupPriority()),
                                    translateAlternateColorCodes('&', "&8Users In Group&7: &6" + groupModel.getUsersInGroup()),
                                    translateAlternateColorCodes('&', "&8Default&7: " + (groupModel.isDefaultGroup() ? "&aYes" : "&cNo")),
                                    translateAlternateColorCodes('&', "&8Permissions&7: "
                                            + permissionsBuilder.toString().substring(0, permissionsBuilder.toString().length() - 2)),
                                    translateAlternateColorCodes('&', "&8Inherited Groups&7: "
                                            + inheritedGroupsBuilder.toString().substring(0, inheritedGroupsBuilder.toString().length() - 2)),
                                    translateAlternateColorCodes('&', "&8Inherited Permissions&7: "
                                            + inheritedPermissionsBuilder.toString().substring(0, inheritedPermissionsBuilder.toString().length() - 2)),
                                    getMessages().getMessage(Messages.CHAT_FOOTER)
                            };
                            player.sendMessage(info);
                            return true;
                        case "promote":
                            targetName = args[1];
                            target = Bukkit.getOfflinePlayer(targetName);
                            if (!PermissionsPlugin.getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            targetUserModel = PermissionsPlugin.getUserRegistry().getUser(target.getUniqueId());
                            GroupModel promoteTo = PermissionsPlugin.getGroupRegistry().getNextGroupUp(targetUserModel.getHighestRankingGroup());

                            if (promoteTo == null) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "" + GREEN + target.getName()
                                        + ChatColor.GRAY + " is not able to be promoted as theyre already the highest rank in the promotion chain."));
                                return true;
                            }

                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have promoted " + GREEN + target.getName() + ChatColor.GRAY
                                    + " to group " + promoteTo.getName() + "."));
                            if (target.isOnline()) {
                                target.getPlayer().sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have been promoted to " + promoteTo.getName() + "."));
                            }
                            targetUserModel.getHighestRankingGroup().setUsersInGroup(targetUserModel.getHighestRankingGroup().getUsersInGroup() - 1);
                            targetUserModel.removeGroup(targetUserModel.getHighestRankingGroup());
                            promoteTo.setUsersInGroup(promoteTo.getUsersInGroup() + 1);
                            targetUserModel.addGroup(promoteTo);
                            return true;
                        case "remove":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            if (!userModel.getGroups().contains(groupModel)) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You do not have that group."));
                                return true;
                            }

                            if (userModel.getGroups().size() == 1) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You can not remove every group from yourself."));
                                return true;
                            }

                            groupModel.setUsersInGroup(groupModel.getUsersInGroup() - 1);
                            userModel.removeGroup(groupModel);
                            PermissionsPlugin.getPermissionsManager().setupPermissions(player);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have removed the group " + groupModel.getColoredName() + GRAY + " from yourself."));
                            return true;
                        case "setdefault":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            PermissionsPlugin.getGroupRegistry().getDefaultGroup().setDefaultGroup(false);
                            groupModel.setDefaultGroup(true);

                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY + " has been set as the default group."));
                            return true;
                        case "view":
                            name = args[1];
                            target = Bukkit.getOfflinePlayer(name);
                            if (!PermissionsPlugin.getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            targetUserModel = PermissionsPlugin.getUserRegistry().getUser(target.getUniqueId());

                            StringBuilder groupBuilder = new StringBuilder();
                            if (targetUserModel.getGroups().isEmpty())
                                groupBuilder.append(ChatColor.GRAY).append("Noneee");
                            else {
                                for (GroupModel groupModels : targetUserModel.getGroups()) {
                                    groupBuilder.append(groupModels.getColoredName() + GRAY).append(ChatColor.GRAY).append(", ");
                                }
                            }

                            String statusString = "&7None";
                            if (targetUserModel.getStatus() != null) {
                                statusString = targetUserModel.getStatus().getColoredName() + GRAY;
                            }

                            permissionsBuilder = new StringBuilder();

                            if (targetUserModel.getPermissions().isEmpty())
                                permissionsBuilder.append(ChatColor.GRAY).append("Noneee");
                            else {
                                for (String permission : targetUserModel.getPermissions()) {
                                    if (permission.startsWith("-"))
                                        permissionsBuilder.append(RED).append(permission.substring(1)).append(ChatColor.GRAY).append(", ");
                                    else
                                        permissionsBuilder.append(GREEN).append(permission).append(ChatColor.GRAY).append(", ");
                                }
                            }

                            info = new String[] {
                                    getMessages().getMessage(Messages.CHAT_HEADER),
                                    translateAlternateColorCodes('&', "&8User Info&7: &b" + target.getName()),
                                    getMessages().getMessage(Messages.CHAT_FOOTER),
                                    translateAlternateColorCodes('&', "&8Groups&7: "
                                            + groupBuilder.toString().substring(0, groupBuilder.toString().length() - 2)),
                                    translateAlternateColorCodes('&', "&8Status&7: " + statusString),
                                    translateAlternateColorCodes('&', "&8Permissions&7: "
                                            + permissionsBuilder.toString().substring(0, permissionsBuilder.toString().length() - 2)),
                                    getMessages().getMessage(Messages.CHAT_FOOTER)
                            };
                            player.sendMessage(info);
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
                case 3:
                    switch (args[0].toLowerCase()) {
                        case "add":
                            String name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            GroupModel groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            String targetName = args[2];
                            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                            if (!PermissionsPlugin.getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            UserModel targetUserModel =  PermissionsPlugin.getUserRegistry().getUser(target.getUniqueId());

                            if (targetUserModel.getGroups().contains(groupModel)) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "" + GREEN + target.getName() + ChatColor.GRAY
                                        + " already has that group."));
                                return true;
                            }

                            groupModel.setUsersInGroup(groupModel.getUsersInGroup() + 1);
                            targetUserModel.addGroup(groupModel);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have added group " + groupModel.getColoredName() + GRAY + ChatColor.GRAY
                                    + " to " + GREEN + target.getName() + ChatColor.GRAY + "."));
                            if (target.isOnline()) {
                                target.getPlayer().sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY + ChatColor.GRAY
                                        + " has been added to you."));
                                PermissionsPlugin.getPermissionsManager().setupPermissions(target.getPlayer());
                            }
                            return true;
                        case "addinherit":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            String inheritGroupName = args[2];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(inheritGroupName)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            GroupModel inheritGroupModel = PermissionsPlugin.getGroupRegistry().getGroup(inheritGroupName);

                            if (groupModel.getInheritedGroups().contains(inheritGroupModel.getKey())) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getName()
                                        + " already inherits group " + inheritGroupModel.getName() + "."));
                                return true;
                            }

                            if (inheritGroupModel.getInheritedGroups().contains(groupModel.getKey())) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getName()
                                        + " cannot inherit group " + inheritGroupModel.getName() + " as it is inherited by that group."));
                                return true;
                            }

                            groupModel.addInheritedGroup(inheritGroupModel);
                            PermissionsPlugin.getGroupRegistry().reloadPerms();
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getName() + " now inherits group "
                                    + inheritGroupModel.getName() + "."));

                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                UserModel onlineUserModel = PermissionsPlugin.getUserRegistry().getUser(onlinePlayer.getUniqueId());

                                if (onlineUserModel.getGroups().contains(groupModel)) {
                                    PermissionsPlugin.getPermissionsManager().setupPermissions(onlinePlayer);
                                }
                            }
                            return true;
                        case "addperm":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            String permission = args[2];

                            if (groupModel.hasPermission(permission)) {
                                player.sendMessage(getMessages().getChatMessage(groupModel.getColoredName() + GRAY + ChatColor.GRAY
                                        + " already has permission " + permission + "."));
                                return true;
                            }

                            groupModel.addPermission(permission);
                            PermissionsPlugin.getGroupRegistry().reloadPerms();
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Permission " + WHITE
                                    + permission + ChatColor.GRAY + " has been added to group " + groupModel.getColoredName() + GRAY + "."));

                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                UserModel onlineUserModel = PermissionsPlugin.getUserRegistry().getUser(onlinePlayer.getUniqueId());

                                if (onlineUserModel.getGroups().contains(groupModel)) {
                                    PermissionsPlugin.getPermissionsManager().setupPermissions(onlinePlayer);
                                }
                            }
                            return true;
                        case "create":
                            name = args[1];
                            if (getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_ALREADY_EXISTS));
                                return true;
                            }

                            getGroupRegistry().createGroup(name, args[2]);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + getGroupRegistry().getGroup(name).getColoredName() + GRAY
                                    + ChatColor.GRAY + " has been successfully created."));
                            return true;
                        case "reminherit":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            inheritGroupName = args[2];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(inheritGroupName)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            inheritGroupModel = PermissionsPlugin.getGroupRegistry().getGroup(inheritGroupName);

                            if (!groupModel.getInheritedGroups().contains(inheritGroupModel.getKey())) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY
                                        + " does not inherit group " + inheritGroupModel.getColoredName() + GRAY + "."));
                                return true;
                            }

                            groupModel.removeInheritedGroup(inheritGroupModel);
                            PermissionsPlugin.getGroupRegistry().reloadPerms();
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY + " no longer inherits group "
                                    + inheritGroupModel.getColoredName() + GRAY + "."));

                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                UserModel onlineUserModel = PermissionsPlugin.getUserRegistry().getUser(onlinePlayer.getUniqueId());

                                if (onlineUserModel.getGroups().contains(groupModel)) {
                                    PermissionsPlugin.getPermissionsManager().setupPermissions(onlinePlayer);
                                }
                            }
                            return true;
                        case "remove":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            targetName = args[2];
                            target = Bukkit.getOfflinePlayer(targetName);
                            if (!PermissionsPlugin.getUserRegistry().userExists(target.getUniqueId())) {
                                player.sendMessage(getMessages().getChatTag(Messages.PLAYER_NOT_FOUND));
                                return true;
                            }

                            targetUserModel =  PermissionsPlugin.getUserRegistry().getUser(target.getUniqueId());

                            if (!targetUserModel.getGroups().contains(groupModel)) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "" + GREEN + target.getName() + ChatColor.GRAY
                                        + " does not have that group."));
                                return true;
                            }

                            if (targetUserModel.getGroups().size() == 1) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You can not remove any more groups from that player."));
                                return true;
                            }

                            groupModel.setUsersInGroup(groupModel.getUsersInGroup() - 1);
                            targetUserModel.removeGroup(groupModel);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "You have removed group " + groupModel.getColoredName() + GRAY + " from " + GREEN
                                    + target.getName() + ChatColor.GRAY + "."));
                            if (target.isOnline()) {
                                target.getPlayer().sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY + " has been removed from you."));
                                PermissionsPlugin.getPermissionsManager().setupPermissions(target.getPlayer());
                            }
                            return true;
                        case "remperm":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            permission = args[2];

                            if (!groupModel.getPermissions().contains(permission)) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY
                                        + " does not have permission " + WHITE + permission + ChatColor.GRAY + "."));
                                return true;
                            }

                            groupModel.removePermission(permission);
                            PermissionsPlugin.getGroupRegistry().reloadPerms();
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Permission " + WHITE + permission + ChatColor.GRAY
                                    + " has been removed from group " + groupModel.getColoredName() + GRAY + "."));

                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                UserModel onlineUserModel = PermissionsPlugin.getUserRegistry().getUser(onlinePlayer.getUniqueId());

                                if (onlineUserModel.getGroups().contains(groupModel)) {
                                    PermissionsPlugin.getPermissionsManager().setupPermissions(onlinePlayer);
                                }
                            }
                            return true;
                        case "setchain":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            if (!NumberUtils.isNumber(args[2])) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "The chain must be a number instead of a string."));
                                return true;
                            }

                            int chain = Integer.parseInt(args[2]);

                            if (groupModel.getGroupChain() == chain) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY
                                        + " is already set to chain " + GOLD + chain + ChatColor.GRAY + "."));
                                return true;
                            }

                            groupModel.setGroupChain(chain);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY + " has been set to chain "
                                    + GOLD + chain + ChatColor.GRAY + "."));
                            return true;
                        case "setprefix":
                            name = args[1];
                            if (!getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            String prefix = args[2];

                            groupModel = getGroupRegistry().getGroup(name);
                            groupModel.setPrefix(prefix);
                            player.sendMessage(getMessages().getChatMessage(GRAY + "You have set group " + groupModel.getColoredName() + GRAY + "'s " + GRAY + "prefix to "
                                    + translateAlternateColorCodes('&', prefix)));
                            return true;
                        case "setpriority":
                            name = args[1];
                            if (!PermissionsPlugin.getGroupRegistry().groupExists(name)) {
                                player.sendMessage(getMessages().getChatTag(PermissionsMessages.GROUP_DOES_NOT_EXIST));
                                return true;
                            }

                            groupModel = PermissionsPlugin.getGroupRegistry().getGroup(name);

                            if (!NumberUtils.isNumber(args[2])) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "The priority must be a number instead of a string."));
                                return true;
                            }

                            int priority = Integer.parseInt(args[2]);

                            if (groupModel.getGroupPriority() == priority) {
                                player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY + GRAY
                                        + " is already set to priority " + GOLD + priority + ChatColor.GRAY + "."));
                                return true;
                            }

                            groupModel.setGroupPriority(priority);
                            player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + "Group " + groupModel.getColoredName() + GRAY + " has been set to priority "
                                    + GOLD + priority + ChatColor.GRAY + "."));
                            return true;
                        default:
                            player.sendMessage(getMessages().getChatTag(Messages.INVALID_SYNTAX));
                            return true;
                    }
            }
        }
        return false;
    }
}
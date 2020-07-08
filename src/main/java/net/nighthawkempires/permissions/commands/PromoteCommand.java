package net.nighthawkempires.permissions.commands;

import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.nighthawkempires.core.CorePlugin.getCommandManager;
import static net.nighthawkempires.core.CorePlugin.getMessages;
import static net.nighthawkempires.permissions.PermissionsPlugin.getUserRegistry;

public class PromoteCommand implements CommandExecutor {

    public PromoteCommand() {
        getCommandManager().registerCommands("promote", new String[] {
                "ne.admin", "ne.permissions.admin"
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UserModel userModel = getUserRegistry().getUser(player.getUniqueId());

            if (!player.hasPermission("ne.admin") || !player.hasPermission("ne.permissions.admin")) {
                player.sendMessage(getMessages().getChatTag(Messages.NO_PERMS));
                return true;
            }

            switch (args.length) {
                case 1:
                    Bukkit.dispatchCommand(sender, "group promote " + args[0]);
                    return true;
                default:
                    player.sendMessage(getMessages().getChatMessage(ChatColor.GRAY + " Command Usage: " + ChatColor.AQUA + "/promote " + ChatColor.DARK_AQUA + "<player>"));
                    return true;
            }
        }
        return false;
    }
}

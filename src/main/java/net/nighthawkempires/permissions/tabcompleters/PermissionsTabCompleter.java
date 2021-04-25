package net.nighthawkempires.permissions.tabcompleters;

import com.google.common.collect.Lists;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.permissions.PermissionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

import static net.nighthawkempires.core.CorePlugin.getMessages;

public class PermissionsTabCompleter implements TabCompleter {

    String[] helpPage1 = new String[] {
            getMessages().getCommand("perms", "help", "Show this help menu."),
            getMessages().getCommand("perms", "info <-g|-u> <group|user>", "Show info about a group or user"),
            getMessages().getCommand("perms", "add <-g|-u> <group|user> <permission>", "Add a permission to a group or user"),
            getMessages().getCommand("perms", "remove <-g|-u> <group|user> <permission>", "Remove a permission from a group or user"),
    };

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.admin") && !player.hasPermission("ne.permissions.admin")) {
                return completions;
            }

            switch (args.length) {
                case 1:
                    List<String> arggs = Lists.newArrayList("help", "info", "add", "remove");
                    StringUtil.copyPartialMatches(args[0], arggs, completions);
                    Collections.sort(completions);
                    return completions;
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "info":
                        case "add":
                        case "remove":
                            arggs = Lists.newArrayList("-g", "-u");
                            StringUtil.copyPartialMatches(args[1], arggs, completions);
                            Collections.sort(completions);
                            return completions;
                    }
                case 3:
                    switch (args[0].toLowerCase()) {
                        case "info":
                        case "add":
                        case "remove":
                            switch (args[1].toLowerCase()) {
                                case "-g":
                                    StringUtil.copyPartialMatches(args[2], PermissionsPlugin.getGroupRegistry().getGroupNameList(), completions);
                                    Collections.sort(completions);
                                    return completions;
                                case "-u":
                                    arggs = Lists.newArrayList();
                                    for (Player players : Bukkit.getOnlinePlayers()) {
                                        arggs.add(players.getName());
                                    }
                                    StringUtil.copyPartialMatches(args[2], arggs, completions);
                                    Collections.sort(completions);
                                    return completions;
                            }
                    }
            }
        }
        return completions;
    }
}
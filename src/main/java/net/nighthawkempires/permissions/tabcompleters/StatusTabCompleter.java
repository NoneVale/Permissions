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

public class StatusTabCompleter implements TabCompleter {

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
                    List<String> arggs = Lists.newArrayList("help", "list", "delete", "info", "remove", "view", "create", "set", "setprefix");
                    StringUtil.copyPartialMatches(args[0], arggs, completions);
                    Collections.sort(completions);
                    return completions;
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "delete":
                        case "info":
                        case "set":
                        case "setprefix":
                            StringUtil.copyPartialMatches(args[1], PermissionsPlugin.getStatusRegistry().getStatusesNameList(), completions);
                            return completions;
                        case "remove":
                        case "view":
                            arggs = Lists.newArrayList();
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                arggs.add(players.getName());
                            }
                            StringUtil.copyPartialMatches(args[1], arggs, completions);
                            Collections.sort(completions);
                            return completions;
                    }
                case 3:
                    switch (args[0].toLowerCase()) {
                        case "set":
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
        return completions;
    }
}

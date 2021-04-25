package net.nighthawkempires.permissions.tabcompleters;

import com.google.common.collect.Lists;
import net.nighthawkempires.core.lang.Messages;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.permissions.PermissionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class GroupTabCompleter implements TabCompleter {

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
                    List<String> arggs = Lists.newArrayList("list", "help", "info", "delete", "demote", "promote", "setdefault",
                            "view", "add", "addinherit", "addperm", "create", "reminherit", "remove", "remperm", "setchain", "setprefix",
                            "setpriority");
                    StringUtil.copyPartialMatches(args[0], arggs, completions);
                    Collections.sort(completions);
                    return completions;
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "info":
                        case "delete":
                        case "setdefault":
                        case "add":
                        case "addinherit":
                        case "addperm":
                        case "reminherit":
                        case "remove":
                        case "remperm":
                        case "setchain":
                        case "setprefix":
                        case "setpriority":
                            StringUtil.copyPartialMatches(args[1], PermissionsPlugin.getGroupRegistry().getGroupNameList(), completions);
                            return completions;
                        case "demote":
                        case "promote":
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
                        case "add":
                        case "remove":
                            arggs = Lists.newArrayList();
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                arggs.add(players.getName());
                            }
                            StringUtil.copyPartialMatches(args[2], arggs, completions);
                            Collections.sort(completions);
                            return completions;
                        case "addinherit":
                        case "reminherit":
                            StringUtil.copyPartialMatches(args[2], PermissionsPlugin.getGroupRegistry().getGroupNameList(), completions);
                            return completions;
                        case "addperm":
                        case "remperm":
                            arggs = Lists.newArrayList();
                            StringUtil.copyPartialMatches(args[2], arggs, completions);
                            return completions;
                    }
                case 4:
                    switch (args[0].toLowerCase()) {
                        case "addperm":
                        case "remperm":
                            arggs = Lists.newArrayList();
                            for (ServerType serverType : ServerType.values()) {
                                arggs.add(serverType.name());
                            }
                            StringUtil.copyPartialMatches(args[3], arggs, completions);
                            Collections.sort(completions);
                            return completions;
                    }
            }
        }
        return completions;
    }
}
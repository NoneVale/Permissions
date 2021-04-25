package net.nighthawkempires.permissions.tabcompleters;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class PromoteTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("ne.admin") && !player.hasPermission("ne.permissions.admin")) {
                return completions;
            }

            if (args.length == 1) {
                List<String> arggs = Lists.newArrayList();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    arggs.add(players.getName());
                }
                StringUtil.copyPartialMatches(args[0], arggs, completions);
                Collections.sort(completions);
                return completions;
            }
        }
        return completions;
    }
}

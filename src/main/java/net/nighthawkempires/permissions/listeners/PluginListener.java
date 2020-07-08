package net.nighthawkempires.permissions.listeners;

import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class PluginListener implements Listener {

    @EventHandler
    public void onEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();

        if (plugin.getName().equalsIgnoreCase(PermissionsPlugin.getPlugin().getName())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(p.getUniqueId());

                PermissionsPlugin.getPermissionsManager().setupPermissions(p);
                p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', "&8["
                        + userModel.getHighestRankingGroup().getPrefix().substring(0, 3) + "&8] &7" + p.getName()));
            }
        }
    }
}

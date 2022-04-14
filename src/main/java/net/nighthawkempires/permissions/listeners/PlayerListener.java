package net.nighthawkempires.permissions.listeners;

import net.nighthawkempires.core.util.StringUtil;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.events.GroupChangeEvent;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());

        PermissionsPlugin.getPermissionsManager().setupPermissions(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());

        player.setPlayerListName(StringUtil.colorify(userModel.getHighestRankingGroup().getPrefix()) + " " + ChatColor.GRAY + player.getName());
    }

    @EventHandler
    public void onChange(GroupChangeEvent event) {
        Player player = event.getPlayer();
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());

        PermissionsPlugin.getPermissionsManager().setupPermissions(player);
        player.setPlayerListName(StringUtil.colorify(userModel.getHighestRankingGroup().getPrefix()) + " " + ChatColor.GRAY + player.getName());
    }
}
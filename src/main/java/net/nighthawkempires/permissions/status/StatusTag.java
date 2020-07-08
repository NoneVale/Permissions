package net.nighthawkempires.permissions.status;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.nighthawkempires.core.chat.tag.PlayerTag;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.entity.Player;

public class StatusTag extends PlayerTag {

    @Override
    public TextComponent getFor(Player player) {
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());
        if (userModel.getStatus() != null) {
            TextComponent tag = new TextComponent("[");
            tag.setColor(ChatColor.DARK_GRAY);
            TextComponent mid = new TextComponent(ChatColor.translateAlternateColorCodes('&', userModel.getStatus().getPrefix()));
            tag.addExtra(mid);
            tag.addExtra("]");
            tag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                    ChatColor.translateAlternateColorCodes('&', "&8&lStatus&7&l: "
                            + userModel.getStatus().getPrefix().substring(0, 2) + "&l" + userModel.getStatus().getName()))));
            return tag;
        }
        return null;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}

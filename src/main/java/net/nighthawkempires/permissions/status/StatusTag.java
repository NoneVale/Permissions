package net.nighthawkempires.permissions.status;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.nighthawkempires.core.chat.tag.PlayerTag;
import net.nighthawkempires.core.util.StringUtil;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.group.GroupModel;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.entity.Player;

public class StatusTag extends PlayerTag {

    @Override
    public TextComponent getFor(Player player) {
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());
        if (userModel.getStatus() != null) {
            TextComponent tag = new TextComponent("[");
            tag.setColor(ChatColor.DARK_GRAY);
            TextComponent mid = new TextComponent(TextComponent.fromLegacyText(StringUtil.colorify(userModel.getStatus().getPrefix())));
            tag.addExtra(mid);
            tag.addExtra("]");

            tag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                    ChatColor.translateAlternateColorCodes('&', "&7Status: " + userModel.getStatus().getColoredName()))));

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

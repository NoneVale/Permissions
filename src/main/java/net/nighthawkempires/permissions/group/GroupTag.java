package net.nighthawkempires.permissions.group;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.nighthawkempires.core.chat.tag.PlayerTag;
import net.nighthawkempires.core.util.StringUtil;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.entity.Player;

public class GroupTag extends PlayerTag {

    @Override
    public TextComponent getFor(Player player) {
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());
        if (!userModel.getGroups().isEmpty()) {
            if (userModel.getHighestRankingGroup() != null) {
                TextComponent tag = new TextComponent("[");
                tag.setColor(ChatColor.DARK_GRAY);
                TextComponent mid = new TextComponent(TextComponent.fromLegacyText(StringUtil.colorify(userModel.getHighestRankingGroup().getPrefix())));
                tag.addExtra(mid);
                tag.addExtra("]");

                StringBuilder stringBuilder = new StringBuilder();
                for (GroupModel groupModel : userModel.getGroups()) {
                    stringBuilder.append("\n&8 ➛ ").append(groupModel.getColoredName());
                }

                tag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes('&', "&7Groups: " + stringBuilder.toString()))));

                return tag;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public int getPriority() {
        return 4;
    }
}

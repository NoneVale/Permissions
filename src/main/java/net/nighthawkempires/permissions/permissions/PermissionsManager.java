package net.nighthawkempires.permissions.permissions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.group.GroupModel;
import net.nighthawkempires.permissions.user.UserModel;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class PermissionsManager {

    private HashMap<UUID, PermissionAttachment> attachmentMap;

    public PermissionsManager() {
        this.attachmentMap = Maps.newHashMap();
    }

    public ImmutableMap<UUID, PermissionAttachment> getAttachmentMap() {
        return ImmutableMap.copyOf(this.attachmentMap);
    }

    public void setupPermissions(Player player) {
        if (this.attachmentMap.containsKey(player.getUniqueId())) {
            for (String string : getAttachmentMap().get(player.getUniqueId()).getPermissions().keySet()) {
                getAttachmentMap().get(player.getUniqueId()).unsetPermission(string);
            }
            this.attachmentMap.remove(player.getUniqueId());
        }

        PermissionAttachment attachment = player.addAttachment(PermissionsPlugin.getPlugin());
        attachment.getPermissions().clear();
        this.attachmentMap.put(player.getUniqueId(), attachment);
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());

        for (GroupModel groupModel : userModel.getGroups()) {
            if (!groupModel.getPermissions().isEmpty()) {
                for (String string : groupModel.getPermissions()) {
                    addPermission(player, string);
                }
            }

            if (!groupModel.getInheritedPermissions().isEmpty()) {
                for (String string : groupModel.getInheritedPermissions()) {
                    addPermission(player, string);
                }
            }
        }

        if (!userModel.getPermissions().isEmpty()) {
            for (String string : userModel.getPermissions()) {
                addPermission(player, string);
            }
        }
    }

    public void addPermission(Player player, String permission) {
        if (getAttachmentMap().containsKey(player.getUniqueId())) {
            boolean allowed = true;
            if (permission.startsWith("-")) {
                permission = permission.substring(1);
                allowed = false;
            }

            getAttachmentMap().get(player.getUniqueId()).setPermission(permission, allowed);
        }
    }

    public void removePermission(Player player, String permission) {
        if (getAttachmentMap().containsKey(player.getUniqueId())) {
            getAttachmentMap().get(player.getUniqueId()).unsetPermission(permission);
        }
    }
}

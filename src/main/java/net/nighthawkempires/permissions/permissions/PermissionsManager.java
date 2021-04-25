package net.nighthawkempires.permissions.permissions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.CorePlugin;
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

    public HashMap<UUID, PermissionAttachment> getAttachmentMap() {
        return this.attachmentMap;
    }

    public void setupPermissions(Player player) {
        if (this.attachmentMap.containsKey(player.getUniqueId())) {
            for (String string : this.attachmentMap.get(player.getUniqueId()).getPermissions().keySet()) {
                this.attachmentMap.get(player.getUniqueId()).unsetPermission(string);
            }
            this.attachmentMap.remove(player.getUniqueId());
        }

        PermissionAttachment attachment = player.addAttachment(PermissionsPlugin.getPlugin());
        this.attachmentMap.put(player.getUniqueId(), attachment);
        UserModel userModel = PermissionsPlugin.getUserRegistry().getUser(player.getUniqueId());

        for (GroupModel groupModel : userModel.getGroups()) {
            for (String s : PermissionsPlugin.getGroupRegistry().getAllPermissions(groupModel)) {
                addPermission(player, s);
            }
        }

        for (String string : userModel.getPermissions()) {
            addPermission(player, string);
        }

        for (String string : userModel.getPermissions(CorePlugin.getConfigg().getServerType())) {
            addPermission(player, string);
        }

        player.recalculatePermissions();
    }

    public void addPermission(Player player, String permission) {
        if (this.attachmentMap.containsKey(player.getUniqueId())) {
            boolean allowed = true;
            if (permission.startsWith("-")) {
                permission = permission.substring(1);
                allowed = false;
            }

            this.attachmentMap.get(player.getUniqueId()).setPermission(permission, allowed);
        }
    }

    public void removePermission(Player player, String permission) {
        if (this.attachmentMap.containsKey(player.getUniqueId())) {
            this.attachmentMap.get(player.getUniqueId()).unsetPermission(permission);
        }
    }
}
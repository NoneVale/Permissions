package net.nighthawkempires.permissions.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.group.GroupModel;
import net.nighthawkempires.permissions.status.StatusModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserModel implements Model {

    private String key;

    private List<String> permissions;
    private List<GroupModel> groups;

    private HashMap<String, List<String>> serverPermissions;

    private StatusModel status;

    private UUID uuid;

    public UserModel(UUID uuid) {
        this.key = uuid.toString();
        this.uuid = uuid;

        this.permissions = Lists.newArrayList();
        this.groups = Lists.newArrayList();
        addGroup(PermissionsPlugin.getGroupRegistry().getDefaultGroup());

        this.serverPermissions = Maps.newHashMap();

        this.status = null;
    }

    public UserModel(String key, DataSection data) {
        this.key = key;
        this.uuid = UUID.fromString(key);

        this.permissions = data.getStringList("permissions");
        this.groups = Lists.newArrayList();
        for (String s : data.getStringList("groups")) {
            if (PermissionsPlugin.getGroupRegistry().groupExists(s)) {
                this.groups.add(PermissionsPlugin.getGroupRegistry().getGroup(s));
            }
        }

        if (this.groups.isEmpty()) this.groups.add(PermissionsPlugin.getGroupRegistry().getDefaultGroup());

        this.serverPermissions = Maps.newHashMap();
        if (data.isSet("server-permissions")) {
            DataSection server = data.getSectionNullable("server-permissions");
            for (String s : server.keySet()) {
                this.serverPermissions.put(s, server.getStringList(s));
            }
        }

        if (data.isSet("status")) {
            this.status = PermissionsPlugin.getStatusRegistry().getStatus(data.getString("status"));
        }
    }

    public ImmutableList<String> getPermissions() {
        return ImmutableList.copyOf(this.permissions);
    }

    public void addPermission(String permission) {
        if (!this.permissions.contains(permission))
            this.permissions.add(permission);

        PermissionsPlugin.getUserRegistry().register(this);
    }

    public void removePermission(String permission) {
        this.permissions.remove(permission);

        PermissionsPlugin.getUserRegistry().register(this);
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    public ImmutableList<String> getPermissions(ServerType server) {
        List<String> permissions = this.serverPermissions.containsKey(server.name())
                ? this.serverPermissions.get(server.name()) : Lists.newArrayList();

        return ImmutableList.copyOf(permissions);
    }

    public void addPermission(ServerType server, String permission) {
        if (!this.serverPermissions.containsKey(server.name())) {
            this.serverPermissions.put(server.name(), Lists.newArrayList());
        }

        if (!this.serverPermissions.get(server.name()).contains(permission))
            this.serverPermissions.get(server.name()).add(permission);

        PermissionsPlugin.getUserRegistry().register(this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().toString().equals(key)) {
                PermissionsPlugin.getPermissionsManager().setupPermissions(player);
            }
        }
    }

    public void removePermission(ServerType server, String permission) {
        if (this.serverPermissions.containsKey(server.name())) {
            this.serverPermissions.get(server.name()).remove(permission);

            if (this.serverPermissions.get(server.name()).isEmpty()) {
                this.serverPermissions.remove(server.name());
            }
        }

        PermissionsPlugin.getUserRegistry().register(this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().toString().equals(key)) {
                PermissionsPlugin.getPermissionsManager().setupPermissions(player);
            }
        }
    }

    public boolean hasPermission(ServerType server, String permission) {
        return getPermissions(server).contains(permission);
    }

    public List<GroupModel> getGroups() {
        List<GroupModel> latestGroups = Lists.newArrayList();
        for (GroupModel groupModel : this.groups) {
            GroupModel group = PermissionsPlugin.getGroupRegistry().getGroup(groupModel.getName());
            latestGroups.add(group);
        }

        return ImmutableList.copyOf(latestGroups);
    }

    public boolean hasGroup(GroupModel groupModel) {
        for (GroupModel group : groups) {
            if ((group.getKey().toLowerCase().equals(groupModel.getKey().toLowerCase()))
                    || (groupModel == group)) return true;
        }
        return false;
    }

    public void addGroup(GroupModel groupModel) {
        boolean add = true;
        for (GroupModel group : groups) {
            if ((group.getKey().toLowerCase().equals(groupModel.getKey().toLowerCase()))
                    || (groupModel == group)) {
                add = false;
            };
        }

        if (add) groups.add(groupModel);

        PermissionsPlugin.getUserRegistry().register(this);
    }

    public void removeGroup(GroupModel groupModel) {
        groups.removeIf(group -> (group.getKey().toLowerCase().equals(groupModel.getKey().toLowerCase()))
                || (groupModel == group));
        PermissionsPlugin.getUserRegistry().register(this);
    }

    private int getHighestGroupChain() {
        int highestChain = -1;
        for (GroupModel groupModel : getGroups()) {
            if (highestChain == -1) highestChain = groupModel.getGroupChain();

            if (groupModel.getGroupChain() > highestChain) highestChain = groupModel.getGroupChain();
        }

        return highestChain;
    }

    private int getHighestGroupChainPriority() {
        int highestPriority = -1;
        for (GroupModel groupModel : getGroups()) {
            if (groupModel.getGroupChain() == getHighestGroupChain()) {
                if (highestPriority == -1) highestPriority = groupModel.getGroupPriority();

                if (groupModel.getGroupPriority() > highestPriority) highestPriority = groupModel.getGroupPriority();
            }
        }

        return highestPriority;
    }

    public GroupModel getHighestRankingGroup() {
        for (GroupModel groupModel : getGroups()) {
            if (groupModel.getGroupChain() == getHighestGroupChain()
                    && groupModel.getGroupPriority() == getHighestGroupChainPriority()) return groupModel;
        }
        return null;
    }

    public StatusModel getStatus() {
        if (this.status == null) return null;
        StatusModel statusModel = null;
        for (StatusModel model : PermissionsPlugin.getStatusRegistry().getStatuses()) {
            if (this.status.getKey().equals(model.getKey())) {
                statusModel = model;
                break;
            }
        }
        return statusModel;
    }

    public void setStatus(StatusModel status) {
        this.status = status;
        PermissionsPlugin.getUserRegistry().register(this);
    }

    public String getKey() {
        return this.key;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("permissions", this.permissions);
        List<String> groups = Lists.newArrayList();
        for (GroupModel groupModel : getGroups()) {
            groups.add(groupModel.getKey());
        }
        map.put("groups", groups);

        if (this.serverPermissions == null) this.serverPermissions = Maps.newHashMap();
        map.put("server-permissions", this.serverPermissions);

        if (status != null) {
            map.put("status", status.getKey());
        }
        return map;
    }
}

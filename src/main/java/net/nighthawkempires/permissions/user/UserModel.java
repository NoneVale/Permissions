package net.nighthawkempires.permissions.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.group.GroupModel;
import net.nighthawkempires.permissions.status.StatusModel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserModel implements Model {

    private String key;

    private List<String> permissions;
    private List<GroupModel> groups;

    private StatusModel status;

    private UUID uuid;

    public UserModel(UUID uuid) {
        this.key = uuid.toString();
        this.uuid = uuid;

        this.permissions = Lists.newArrayList();
        this.groups = Lists.newArrayList();
        addGroup(PermissionsPlugin.getGroupRegistry().getDefaultGroup());

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

    public ImmutableList<GroupModel> getGroups() {
        return ImmutableList.copyOf(this.groups);
    }

    public void addGroup(GroupModel group) {
        if (!this.groups.contains(group))
            this.groups.add(group);
        PermissionsPlugin.getUserRegistry().register(this);
    }

    public void removeGroup(GroupModel group) {
        this.groups.remove(group);
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
        return this.status;
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

        if (status != null) {
            map.put("status", status.getKey());
        }
        return map;
    }
}

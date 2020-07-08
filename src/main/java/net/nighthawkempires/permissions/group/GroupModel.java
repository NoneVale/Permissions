package net.nighthawkempires.permissions.group;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.permissions.PermissionsPlugin;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;

public class GroupModel implements Model {

    private String key;
    private String name;
    private String prefix;

    private boolean defaultGroup;

    private List<String> inheritedGroups;
    private List<String> inheritedPermissions;
    private List<String> permissions;
    private List<String> tempPermissions;

    private int groupChain;
    private int groupPriority;
    private int usersInGroup;

    public GroupModel(String name, String prefix) {
        this.key = name.toLowerCase();
        this.name = name;
        this.prefix = prefix;

        this.defaultGroup = false;

        this.inheritedGroups = Lists.newArrayList();
        this.inheritedPermissions = Lists.newArrayList();
        this.permissions = Lists.newArrayList();
        this.tempPermissions = Lists.newArrayList();

        this.groupChain = -1;
        this.groupPriority = -1;
        this.usersInGroup = 0;
    }

    public GroupModel(String key, DataSection data) {
        this.key = key.toLowerCase();
        this.name = data.getString("name");
        this.prefix = data.getString("prefix");

        if (data.isSet("default"))
            this.defaultGroup = data.getBoolean("default");

        this.inheritedGroups = data.getStringList("inherited-groups");
        this.inheritedPermissions = Lists.newArrayList();
        this.permissions = data.getStringList("permissions");
        this.tempPermissions = Lists.newArrayList();
        if (data.isSet("temp-permissions")) {
            this.tempPermissions = data.getStringList("temp-permissions");
        }

        this.groupChain = data.getInt("group-chain");
        this.groupPriority = data.getInt("group-priority");

        if (data.isSet("users-in-group"))
            this.usersInGroup = data.getInt("users-in-group");
        else
            this.usersInGroup = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public ImmutableList<String> getInheritedGroups() {
        return ImmutableList.copyOf(this.inheritedGroups);
    }

    public void addInheritedGroup(GroupModel groupModel) {
        if (!this.inheritedGroups.contains(groupModel.getKey())) {
            this.inheritedGroups.add(groupModel.getKey());
            PermissionsPlugin.getGroupRegistry().register(this);
        }
    }

    public void addInheritedGroup(String s) {
        if (!this.inheritedGroups.contains(s)) {
            this.inheritedGroups.add(s);
            PermissionsPlugin.getGroupRegistry().register(this);
        }
    }

    public void removeInheritedGroup(GroupModel groupModel) {
        this.inheritedGroups.remove(groupModel.getKey());
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public void removeInheritedGroup(String s) {
        this.inheritedGroups.remove(s);
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public ImmutableList<String> getInheritedPermissions() {
        return ImmutableList.copyOf(this.inheritedPermissions);
    }

    public void addInheritedPermission(String permission) {
        if (!this.inheritedPermissions.contains(permission))
            this.inheritedPermissions.add(permission);
    }

    public void removeInheritedPermission(String permission) {
        this.inheritedPermissions.remove(permission);
    }

    public void clearInheritedPermissions() {
        this.inheritedPermissions.clear();
    }

    public ImmutableList<String> getPermissions() {
        return ImmutableList.copyOf(this.permissions);
    }

    public void addPermission(String permission) {
        if (!this.permissions.contains(permission))
            this.permissions.add(permission);

        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public void removePermission(String permission) {
        this.permissions.remove(permission);

        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    public ImmutableList<String> getTempPermissions() {
        return ImmutableList.copyOf(this.tempPermissions);
    }

    public void addTempPermission(String permission) {
        if (!this.tempPermissions.contains(permission) && !this.permissions.contains(permission))
            this.tempPermissions.add(permission);

        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public void removeTempPermission(String permission) {
        this.tempPermissions.remove(permission);

        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public boolean hasTempPermission(String permission) {
        return this.permissions.contains(permission);
    }

    public int getGroupChain() {
        return groupChain;
    }

    public void setGroupChain(int groupChain) {
        this.groupChain = groupChain;
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public int getGroupPriority() {
        return groupPriority;
    }

    public void setGroupPriority(int groupPriority) {
        this.groupPriority = groupPriority;
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public int getUsersInGroup() {
        return usersInGroup;
    }

    public void setUsersInGroup(int usersInGroup) {
        this.usersInGroup = usersInGroup;
        PermissionsPlugin.getGroupRegistry().register(this);
    }

    public String getColoredName() {
        if (!getPrefix().startsWith("&")) return getName();
        return ChatColor.translateAlternateColorCodes('&', getPrefix().substring(0, 2) + getName());
    }

    public String getBoldColoredName() {
        if (!getPrefix().startsWith("&")) return getName();
        return ChatColor.translateAlternateColorCodes('&', getPrefix().substring(0, 2) + "&l" + getName());
    }

    public String getKey() {
        return this.key.toLowerCase();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", this.name);
        map.put("prefix", this.prefix);

        if (isDefaultGroup())
            map.put("default", this.defaultGroup);

        map.put("inherited-groups", this.inheritedGroups);
        map.put("permissions", this.permissions);

        map.put("group-chain", this.groupChain);
        map.put("group-priority", this.groupPriority);
        map.put("users-in-group", this.usersInGroup);
        return map;
    }
}
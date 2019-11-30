package net.nighthawkempires.permissions.group;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.permissions.PermissionsPlugin;

import java.util.List;
import java.util.Map;

public class GroupModel implements Model {

    private String name;
    private String prefix;

    private boolean defaultGroup;

    private List<String> inheritedGroups;
    private List<String> permissions;

    private int groupChain;
    private int groupPriority;

    public GroupModel(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;

        this.defaultGroup = false;

        this.inheritedGroups = Lists.newArrayList();
        this.permissions = Lists.newArrayList();

        this.groupChain = -1;
        this.groupPriority = -1;
    }

    public GroupModel(String key, DataSection data) {
        this.name = key;

        this.prefix = data.getString("prefix");

        this.defaultGroup = data.getBoolean("default");

        this.inheritedGroups = data.getStringList("inherited-groups");
        this.permissions = data.getStringList("permissions");

        this.groupChain = data.getInt("group-chain");
        this.groupPriority = data.getInt("group-priority");
    }

    public String getName() {
        return this.name;
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

    public ImmutableList<String> getPermissions() {
        return ImmutableList.copyOf(this.permissions);
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

    public String getKey() {
        return this.name;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("prefix", this.prefix);

        if (isDefaultGroup())
            map.put("default", this.defaultGroup);

        map.put("inherited-groups", this.inheritedGroups);
        map.put("permissions", this.permissions);

        map.put("group-chain", this.groupChain);
        map.put("group-priority", this.groupPriority);
        return map;
    }
}
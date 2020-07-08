package net.nighthawkempires.permissions.status;

import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.permissions.PermissionsPlugin;
import org.bukkit.ChatColor;

import java.util.Map;

public class StatusModel implements Model {

    private String key;
    private String name;
    private String prefix;

    private int usersWithStatus;

    public StatusModel(String name, String prefix) {
        this.key = name.toLowerCase();
        this.name = name;
        this.prefix = prefix;

        this.usersWithStatus = 0;
    }

    public StatusModel(String key, DataSection data) {
        this.key = key.toLowerCase();
        this.name = data.getString("name");
        this.prefix = data.getString("prefix");

        if (data.isSet("users-with-status"))
            this.usersWithStatus = data.getInt("users-with-status");
        else
            this.usersWithStatus = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        PermissionsPlugin.getStatusRegistry().register(this);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        PermissionsPlugin.getStatusRegistry().register(this);
    }

    public int getUsersWithStatus() {
        return usersWithStatus;
    }

    public void setUsersWithStatus(int usersWithStatus) {
        this.usersWithStatus = usersWithStatus;
        PermissionsPlugin.getStatusRegistry().register(this);
    }

    public String getColoredName() {
        if (!getPrefix().startsWith("&")) return getName();
        return ChatColor.translateAlternateColorCodes('&', getPrefix().substring(0, 2) + getName());
    }

    public String getBoldColoredName() {
        if (!getPrefix().startsWith("&")) return getName();
        return ChatColor.translateAlternateColorCodes('&', getPrefix().substring(0, 2) + "&l" + getName());
    }

    @Override
    public String getKey() {
        return this.key.toLowerCase();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", this.name);
        map.put("prefix", this.prefix);

        map.put("users-with-status", this.usersWithStatus);
        return map;
    }
}
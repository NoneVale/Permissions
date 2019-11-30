package net.nighthawkempires.permissions.status;

import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;

import java.util.Map;

public class StatusModel implements Model {

    private String name;
    private String prefix;

    public StatusModel(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public StatusModel(String key, DataSection data) {
        this.name = key;
        this.prefix = data.getString("prefix");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getKey() {
        return this.name;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", this.name);
        map.put("prefix", this.prefix);
        return map;
    }
}

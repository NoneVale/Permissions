package net.nighthawkempires.permissions.group.registry;

import com.google.common.collect.ImmutableList;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.permissions.group.GroupModel;

import java.util.Map;

public interface GroupRegistry extends Registry<GroupModel> {
    String NAME = "groups";

    default GroupModel fromDataSection(String stringKey, DataSection data) {
        return new GroupModel(stringKey, data);
    }

    default GroupModel getGroup(String name) {
        if (!groupExists(name)) return null;

        return fromKey(name).orElseGet(null);
    }

    default GroupModel getDefaultGroup() {
        for (GroupModel g : getRegisteredData().values()) {
            if (g.isDefaultGroup()) return g;
        }
        return createDefaultGroup();
    }

    default void createGroup(String name, String prefix) {
        register(new GroupModel(name, prefix));
    }

    default void deleteGroup(String name) {
        remove(getGroup(name));
    }

    default GroupModel createDefaultGroup() {
        createGroup("default", "&7Default");
        GroupModel groupModel = getGroup("default");
        groupModel.setDefaultGroup(true);
        return groupModel;
    }

    default ImmutableList<GroupModel> getGroups() {
        if (getRegisteredData().size() > 0) {
            return ImmutableList.copyOf(getRegisteredData().values());
        }
        return ImmutableList.of();
    }

    @Deprecated
    Map<String, GroupModel> getRegisteredData();

    default boolean groupExists(String name) {
        return fromKey(name).isPresent();
    }
}

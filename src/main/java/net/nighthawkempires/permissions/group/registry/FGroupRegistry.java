package net.nighthawkempires.permissions.group.registry;

import net.nighthawkempires.core.datasection.AbstractFileRegistry;
import net.nighthawkempires.permissions.group.GroupModel;

import java.util.Map;

public class FGroupRegistry extends AbstractFileRegistry<GroupModel> implements GroupRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FGroupRegistry(String path) {
        super(path, NAME, SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, GroupModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}

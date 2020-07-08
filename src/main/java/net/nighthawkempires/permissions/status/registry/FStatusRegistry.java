package net.nighthawkempires.permissions.status.registry;

import net.nighthawkempires.core.datasection.AbstractFileRegistry;
import net.nighthawkempires.permissions.status.StatusModel;

import java.util.Map;

public class FStatusRegistry extends AbstractFileRegistry<StatusModel> implements StatusRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FStatusRegistry(String path) {
        super(path, NAME, SAVE_PRETTY, -1);
    }

    @Override
    public Map<String, StatusModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}

package net.nighthawkempires.permissions.status.registry;

import com.google.common.collect.ImmutableList;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.permissions.status.StatusModel;

import java.util.Map;

public interface StatusRegistry extends Registry<StatusModel> {
    String NAME = "statuses";

    default StatusModel fromDataSection(String stringKey, DataSection data) {
        return new StatusModel(stringKey, data);
    }

    default StatusModel getStatus(String name) {
        if (!statusExists(name)) return null;

        return fromKey(name).orElseGet(null);
    }

    default void createStatus(String name, String prefix) {
        register(new StatusModel(name, prefix));
    }

    default void deleteStatus(String name) {
        remove(getStatus(name));
    }

    default ImmutableList<StatusModel> getStatuses() {
        if (getRegisteredData().size() > 0) {
            return ImmutableList.copyOf(getRegisteredData().values());
        }
        return ImmutableList.of();
    }

    @Deprecated
    Map<String, StatusModel> getRegisteredData();

    default boolean statusExists(String name) {
        return fromKey(name).isPresent();
    }
}
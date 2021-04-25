package net.nighthawkempires.permissions.status.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.permissions.group.GroupModel;
import net.nighthawkempires.permissions.status.StatusModel;

import java.util.List;
import java.util.Map;

public interface StatusRegistry extends Registry<StatusModel> {
    String NAME = "statuses";

    default StatusModel fromDataSection(String stringKey, DataSection data) {
        return new StatusModel(stringKey, data);
    }

    default StatusModel getStatus(String name) {
        if (!statusExists(name)) return null;

        return fromKey(name.toLowerCase()).orElseGet(null);
    }

    default void createStatus(String name, String prefix) {
        register(new StatusModel(name, prefix));
    }

    default void deleteStatus(String name) {
        remove(getStatus(name));
    }

    default void deleteStatus(StatusModel statusModel) {
        remove(statusModel);
    }

    default ImmutableList<StatusModel> getStatuses() {
        if (getRegisteredData().size() > 0) {
            return ImmutableList.copyOf(getStatusMap().values());
        }
        return ImmutableList.of();
    }

    default List<String> getStatusesNameList() {
        List<String> names = Lists.newArrayList();
        for (StatusModel statusModel : loadAllFromDb().values()) {
            names.add(statusModel.getName());
        }
        return names;
    }

    @Deprecated
    Map<String, StatusModel> getRegisteredData();

    default Map<String, StatusModel> getStatusMap() {
        return loadAllFromDb();
    }

    default boolean statusExists(String name) {
        return fromKey(name.toLowerCase()).isPresent();
    }
}
package net.nighthawkempires.permissions.group.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Registry;
import net.nighthawkempires.permissions.PermissionsPlugin;
import net.nighthawkempires.permissions.group.GroupModel;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface GroupRegistry extends Registry<GroupModel> {
    String NAME = "groups";

    default GroupModel fromDataSection(String stringKey, DataSection data) {
        return new GroupModel(stringKey, data);
    }

    default GroupModel getGroup(String name) {
        if (!groupExists(name)) return null;

        return fromKey(name.toLowerCase()).orElseGet(null);
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

    default void deleteGroup(GroupModel groupModel) {
        remove(groupModel);
    }

    default GroupModel createDefaultGroup() {
        createGroup("default", "&7Default");
        GroupModel groupModel = getGroup("default");
        groupModel.setDefaultGroup(true);
        return groupModel;
    }

    default ImmutableList<GroupModel> getGroups() {
        if (loadAllFromDb().values().size() > 0) {
            List<GroupModel> groups = Lists.newArrayList(getRegisteredData().values());
            groups.sort(Comparator.comparing(GroupModel::getGroupChain).thenComparing(GroupModel::getGroupPriority));

            return ImmutableList.copyOf(groups);
        }

        return ImmutableList.of();
    }

    default void reloadPerms() {
        for (GroupModel groupModel : getGroups()) {
            addInheritedPerms(groupModel);
        }
    }

    default void addInheritedPerms(GroupModel groupModel) {
        for (String string : groupModel.getInheritedGroups()) {
            GroupModel inheritedGroup = getGroup(string);
            if (inheritedGroup != null) {
                addInheritedPerms(inheritedGroup);
                groupModel.clearInheritedPermissions();
                for (String perm : inheritedGroup.getPermissions()) {
                    if (!groupModel.getInheritedPermissions().contains(perm))
                        groupModel.addInheritedPermission(perm);
                }
                for (String perm : inheritedGroup.getInheritedPermissions()) {
                    if (!groupModel.getInheritedPermissions().contains(perm))
                        groupModel.addInheritedPermission(perm);
                }
            }
        }
    }

    default GroupModel getNextGroupUp(GroupModel groupModel) {
        if (groupModel.getGroupChain() == getHighestChain()
                && groupModel.getGroupPriority() == getHighestPriorityInChain(groupModel.getGroupChain())) return null;

        if (groupModel.getGroupPriority() != getHighestPriorityInChain(groupModel.getGroupChain())) {
            return getGroup(groupModel.getGroupChain(), getNextPriorityUp(groupModel));
        } else {
            return getGroup(getNextChainUp(groupModel), getLowestPriorityInChain(getNextChainUp(groupModel)));
        }
    }

    default GroupModel getNextGroupDown(GroupModel groupModel) {
        if (groupModel.getGroupChain() == getLowestChain()
                && groupModel.getGroupPriority() == getLowestPriorityInChain(groupModel.getGroupChain())) return null;

        if (groupModel.getGroupPriority() != getLowestPriorityInChain(groupModel.getGroupChain())) {
            return getGroup(groupModel.getGroupChain(), getNextPriorityDown(groupModel));
        } else {
            return getGroup(getNextChainDown(groupModel), getHighestPriorityInChain(getNextChainDown(groupModel)));
        }
    }

    default int getHighestChain() {
        int highest = -1;
        for (GroupModel groupModel : getGroups()) {
            if (highest == -1) highest = groupModel.getGroupChain();

            if (groupModel.getGroupChain() > highest) highest = groupModel.getGroupChain();
        }

        return highest;
    }

    default int getLowestChain() {
        int lowest = -1;
        for (GroupModel groupModel : getGroups()) {
            if (lowest == -1) lowest = groupModel.getGroupChain();

            if (groupModel.getGroupChain() < lowest && groupModel.getGroupChain() > 0) lowest = groupModel.getGroupChain();
        }

        return lowest;
    }

    default int getHighestPriorityInChain(int chain) {
        int highest = -1;
        for (GroupModel groupModel : getGroupsInChain(chain)) {
            if (highest == -1) highest = groupModel.getGroupPriority();

            if (groupModel.getGroupPriority() > highest) highest = groupModel.getGroupPriority();
        }

        return highest;
    }

    default int getLowestPriorityInChain(int chain) {
        int lowest = -1;
        for (GroupModel groupModel : getGroupsInChain(chain)) {
            if (lowest == -1) lowest = groupModel.getGroupPriority();

            if (groupModel.getGroupPriority() < lowest
                    && groupModel.getGroupPriority() > 0) lowest = groupModel.getGroupPriority();
        }
        return lowest;
    }

    default GroupModel getGroup(int chain, int priority) {
        for (GroupModel groupModel : getGroups())
            if (groupModel.getGroupChain() == chain && groupModel.getGroupPriority() == priority) return groupModel;
        return null;
    }

    default int getNextChainUp(GroupModel groupModel) {
        int chain = groupModel.getGroupChain() + 1;
        while (getGroupsInChain(chain).isEmpty() && chain < getHighestChain()) {
            chain++;
        }

        return chain;
    }

    default int getNextChainDown(GroupModel groupModel) {
        int chain = groupModel.getGroupChain() - 1;
        while (getGroupsInChain(chain).isEmpty() && chain > getLowestChain()) {
            chain--;
        }

        return chain;
    }

    default int getNextPriorityUp(GroupModel groupModel) {
        int priority = groupModel.getGroupPriority() + 1;
        while (getGroup(groupModel.getGroupChain(), priority) == null && priority < getHighestPriorityInChain(groupModel.getGroupChain())) {
            priority++;
        }

        return priority;
    }

    default int getNextPriorityDown(GroupModel groupModel) {
        int priority = groupModel.getGroupPriority() - 1;
        while (getGroup(groupModel.getGroupChain(), priority) == null && priority > getLowestPriorityInChain(groupModel.getGroupChain())) {
            priority--;
        }

        return priority;
    }

    default ImmutableList<GroupModel> getGroupsInChain(int chain) {
        List<GroupModel> groups = Lists.newArrayList();
        for (GroupModel groupModel : getGroups()) {
            if (groupModel.getGroupChain() == chain) {
                groups.add(groupModel);
            }
        }
        return ImmutableList.copyOf(groups);
    }

    default ImmutableList<String> getGroupPermissions(GroupModel groupModel) {
        List<String> permissions = Lists.newArrayList();
        permissions.addAll(groupModel.getPermissions());

        List<String> finalPerms = permissions.stream().distinct().collect(Collectors.toList());
        return ImmutableList.copyOf(finalPerms);
    }

    default ImmutableList<String> getInheritedPermissions(GroupModel groupModel) {
        List<String> permissions = Lists.newArrayList();
        if (groupModel.getInheritedGroups().size() > 0) {
            for (String s : groupModel.getInheritedGroups()) {
                GroupModel group = getGroup(s);
                permissions.addAll(getInheritedPermissions(group));
                permissions.addAll(group.getPermissions());
            }
        }

        List<String> finalPerms = permissions.stream().distinct().collect(Collectors.toList());
        return ImmutableList.copyOf(finalPerms);
    }

    default ImmutableList<String> getAllPermissions(GroupModel groupModel) {
        List<String> permissions = Lists.newArrayList();
        if (groupModel.getInheritedGroups().size() > 0) {
            for (String s : groupModel.getInheritedGroups()) {
                GroupModel group = getGroup(s);
                permissions.addAll(getAllPermissions(group));
                permissions.addAll(group.getPermissions());
                permissions.addAll(group.getPermissions(CorePlugin.getConfigg().getServerType()));
            }
        }
        permissions.addAll(groupModel.getPermissions());
        permissions.addAll(groupModel.getPermissions(CorePlugin.getConfigg().getServerType()));

        List<String> finalPerms = permissions.stream().distinct().collect(Collectors.toList());
        return ImmutableList.copyOf(finalPerms);
    }

    default ImmutableList<String> getServerPermissions(GroupModel groupModel) {
        List<String> permissions = Lists.newArrayList();
        if (groupModel.getInheritedGroups().size() > 0) {
            for (String s : groupModel.getInheritedGroups()) {
                GroupModel group = getGroup(s);
                permissions.addAll(getServerPermissions(group));
                permissions.addAll(group.getPermissions(CorePlugin.getConfigg().getServerType()));
            }
        }
        permissions.addAll(groupModel.getPermissions(CorePlugin.getConfigg().getServerType()));

        List<String> finalPerms = permissions.stream().distinct().collect(Collectors.toList());
        return ImmutableList.copyOf(finalPerms);
    }

    default List<String> getGroupNameList() {
        List<String> names = Lists.newArrayList();
        for (GroupModel groupModel : loadAllFromDb().values()) {
            names.add(groupModel.getName());
        }
        return names;
    }

    @Deprecated
    Map<String, GroupModel> getRegisteredData();

    default boolean groupExists(String name) {
        return fromKey(name.toLowerCase()).isPresent();
    }
}
package net.nighthawkempires.permissions.group.registry;

import com.mongodb.client.MongoDatabase;
import net.nighthawkempires.core.datasection.AbstractMongoRegistry;
import net.nighthawkempires.permissions.group.GroupModel;

import java.util.Map;

public class MGroupRegistry extends AbstractMongoRegistry<GroupModel> implements GroupRegistry {

    public MGroupRegistry(MongoDatabase database) {
        super(database.getCollection(NAME), -1);
    }

    @Override
    public Map<String, GroupModel> getRegisteredData()
    {
        return m_RegisteredData.asMap();
    }
}

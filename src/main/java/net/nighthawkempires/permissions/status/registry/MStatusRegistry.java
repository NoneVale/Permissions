package net.nighthawkempires.permissions.status.registry;

import com.mongodb.client.MongoDatabase;
import net.nighthawkempires.core.datasection.AbstractMongoRegistry;
import net.nighthawkempires.permissions.status.StatusModel;

import java.util.Map;

public class MStatusRegistry extends AbstractMongoRegistry<StatusModel> implements StatusRegistry {

    public MStatusRegistry(MongoDatabase database) {
        super(database.getCollection(NAME), -1);
    }

    @Override
    public Map<String, StatusModel> getRegisteredData()
    {
        return m_RegisteredData.asMap();
    }
}

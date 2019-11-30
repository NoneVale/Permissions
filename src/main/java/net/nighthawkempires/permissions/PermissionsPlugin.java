package net.nighthawkempires.permissions;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.core.settings.ConfigModel;
import net.nighthawkempires.permissions.group.registry.FGroupRegistry;
import net.nighthawkempires.permissions.group.registry.GroupRegistry;
import net.nighthawkempires.permissions.group.registry.MGroupRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionsPlugin extends JavaPlugin {

    private static Plugin plugin;

    private static GroupRegistry groupRegistry;

    private static MongoDatabase mongoDatabase;


    public void onEnable() {
        plugin = this;
        if (getConfigg().getServerType() != ServerType.SETUP) {
            getLogger().info("Server Type has been registered as \'" + getConfigg().getServerType().name() + "\'");
            String pluginName = getPlugin().getName();
            try {
                String hostname = getConfigg().getMongoHostname();
                String database = getConfigg().getMongoDatabase().replaceAll("%PLUGIN%", pluginName);
                String username = getConfigg().getMongoUsername().replaceAll("%PLUGIN%", pluginName);
                String password = getConfigg().getMongoPassword();

                ServerAddress serverAddress = new ServerAddress(hostname, 27017);
                MongoCredential mongoCredential = MongoCredential.createCredential(username, database, password.toCharArray());
                mongoDatabase = new MongoClient(serverAddress, mongoCredential, new MongoClientOptions.Builder().build()).getDatabase(database);

                //userRegistry = new MUserRegistry(getMongoDatabase());
                groupRegistry = new MGroupRegistry(getMongoDatabase());
                getGroupRegistry().loadAllFromDb();

                getLogger().info("Successfully connected to MongoDB.");

                //registerListeners();

            } catch (Exception exception) {
                exception.printStackTrace();
                getLogger().warning("Could not connect to MongoDB, using file registry...");
                groupRegistry = new FGroupRegistry("empires/groups");
            }

        }
    }

    public void onDisable() {

    }

    public static GroupRegistry getGroupRegistry() {
        return groupRegistry;
    }

    public static MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public ConfigModel getConfigg() {
        return CorePlugin.getConfigg();
    }
}

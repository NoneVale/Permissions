package net.nighthawkempires.permissions;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import net.nighthawkempires.core.CorePlugin;
import net.nighthawkempires.core.server.ServerType;
import net.nighthawkempires.core.settings.ConfigModel;
import net.nighthawkempires.permissions.commands.*;
import net.nighthawkempires.permissions.group.GroupTag;
import net.nighthawkempires.permissions.group.registry.FGroupRegistry;
import net.nighthawkempires.permissions.group.registry.GroupRegistry;
import net.nighthawkempires.permissions.group.registry.MGroupRegistry;
import net.nighthawkempires.permissions.listeners.PlayerListener;
import net.nighthawkempires.permissions.listeners.PluginListener;
import net.nighthawkempires.permissions.permissions.PermissionsManager;
import net.nighthawkempires.permissions.scoreboard.PermissionsScoreboard;
import net.nighthawkempires.permissions.status.StatusTag;
import net.nighthawkempires.permissions.status.registry.FStatusRegistry;
import net.nighthawkempires.permissions.status.registry.MStatusRegistry;
import net.nighthawkempires.permissions.status.registry.StatusRegistry;
import net.nighthawkempires.permissions.user.registry.FUserRegistry;
import net.nighthawkempires.permissions.user.registry.MUserRegistry;
import net.nighthawkempires.permissions.user.registry.UserRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public class PermissionsPlugin extends JavaPlugin {

    private static Plugin plugin;

    private static GroupRegistry groupRegistry;
    private static StatusRegistry statusRegistry;
    private static UserRegistry userRegistry;

    private static MongoDatabase mongoDatabase;

    private static PermissionsManager permissionsManager;

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
                getGroupRegistry().reloadPerms();

                statusRegistry = new MStatusRegistry(getMongoDatabase());
                getStatusRegistry().loadAllFromDb();

                userRegistry = new MUserRegistry(getMongoDatabase());

                permissionsManager = new PermissionsManager();

                getLogger().info("Successfully connected to MongoDB.");

                registerCommands();
                registerListeners();

                CorePlugin.getScoreboardManager().addScoreboard(new PermissionsScoreboard());
                CorePlugin.getChatFormat().add(new GroupTag());
                CorePlugin.getChatFormat().add(new StatusTag());

            } catch (Exception exception) {
                exception.printStackTrace();
                getLogger().warning("Could not connect to MongoDB, using file registry...");
                groupRegistry = new FGroupRegistry("empires/groups");
                getGroupRegistry().loadAllFromDb();

                statusRegistry = new FStatusRegistry("empires/status");
                getStatusRegistry().loadAllFromDb();

                userRegistry = new FUserRegistry("empires/users");
            }
        }
    }

    public void onDisable() {

    }

    public void registerCommands() {
        this.getCommand("demote").setExecutor(new DemoteCommand());
        this.getCommand("group").setExecutor(new GroupCommand());
        this.getCommand("permissions").setExecutor(new PermissionsCommand());
        this.getCommand("promote").setExecutor(new PromoteCommand());
        this.getCommand("status").setExecutor(new StatusCommand());
    }

    public void registerListeners() {
        getPluginManager().registerEvents(new PlayerListener(), this);
        getPluginManager().registerEvents(new PluginListener(), this);
    }

    public static GroupRegistry getGroupRegistry() {
        return groupRegistry;
    }

    public static StatusRegistry getStatusRegistry() {
        return statusRegistry;
    }

    public static UserRegistry getUserRegistry() {
        return userRegistry;
    }

    public static MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public static PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public ConfigModel getConfigg() {
        return CorePlugin.getConfigg();
    }
}

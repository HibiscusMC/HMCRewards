package com.hibiscusmc.hmcrewards.user.data;

import com.hibiscusmc.hmcrewards.HMCRewardsPlugin;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.user.data.mongo.MongoUserDatastore;
import com.hibiscusmc.hmcrewards.util.YamlFileConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bukkit.configuration.ConfigurationSection;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Named;
import team.unnamed.inject.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public final class UserDatastoreProvider implements Provider<UserDatastore> {
    @Inject private HMCRewardsPlugin plugin;
    @Inject private RewardProviderRegistry rewardProviderRegistry;
    @Inject @Named("config.yml") private YamlFileConfiguration config;

    @Override
    public UserDatastore get() {
        final ConfigurationSection section = config.getConfigurationSection("data");
        if (section == null) {
            // warn and fallback to memory user datastore
            plugin.getLogger().warning("No 'data' section found in config.yml, no persistent" +
                    " storage will be used. (using an in-memory user datastore, data will be lost on restart)");
            return new MemoryUserDatastore();
        }

        final String type = section.getString("store", "memory");

        switch (type.toLowerCase()) {
            case "memory":
                plugin.getLogger().warning("Using an in-memory user datastore, data will be lost on restart");
                return new MemoryUserDatastore();
            case "mongo":
            case "mongodb": {
                final Logger logger = Logger.getLogger("org.mongodb.driver");
                logger.setLevel(Level.WARNING);

                final MongoClient client = MongoClients.create(requireNonNull(section.getString("mongodb.uri"), "'uri' not specified for mongodb datastore."));

                // defer client.close() to be called when the plugin is disabled
                plugin.deferResourceCloseOnPluginDisable(client);

                final MongoDatabase database = client.getDatabase(requireNonNull(section.getString("mongodb.database"), "'database' not specified for mongodb datastore."));
                return new MongoUserDatastore(database, rewardProviderRegistry);
            }
            default: {
                throw new IllegalArgumentException("Unsupported datastore type: " + type);
            }
        }
    }
}

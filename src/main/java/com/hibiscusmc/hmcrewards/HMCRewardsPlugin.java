package com.hibiscusmc.hmcrewards;

import com.github.retrooper.packetevents.PacketEvents;
import com.hibiscusmc.hmcrewards.command.CommandModule;
import com.hibiscusmc.hmcrewards.feedback.FeedbackModule;
import com.hibiscusmc.hmcrewards.hook.HookModule;
import com.hibiscusmc.hmcrewards.item.ItemModule;
import com.hibiscusmc.hmcrewards.reward.RewardModule;
import com.hibiscusmc.hmcrewards.user.UserModule;
import com.hibiscusmc.hmcrewards.util.ConfigurationBinder;
import com.hibiscusmc.hmcrewards.util.Service;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.lojosho.hibiscuscommons.HibiscusPlugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;
import team.unnamed.inject.Module;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class HMCRewardsPlugin extends HibiscusPlugin implements Module {

    @Inject private Set<Service> services;
    @Inject private Set<Listener> listeners;

    private final Collection<AutoCloseable> resources = new HashSet<>();

    @Override
    public void onLoad() {
        final var packetEvents = SpigotPacketEventsBuilder.build(this);
        PacketEvents.setAPI(packetEvents);
        packetEvents.load();
        packetEvents.getSettings().checkForUpdates(false);
    }

    @Override
    public void onStart() {
        PacketEvents.getAPI().init();
        Injector.create(this).injectMembers(this);

        if (services != null) {
            for (final Service service : services) {
                service.start();
            }
        }

        if (listeners != null) {
            for (final Listener listener : listeners) {
                getServer().getPluginManager().registerEvents(listener, this);
            }
            listeners = null;
        }
    }

    @Override
    public void onEnd() {
        PacketEvents.getAPI().terminate();
        if (services != null) {
            for (final Service service : services) {
                service.stop();
            }
        }

        {
            // close all resources registered for this plugin
            final Iterator<AutoCloseable> iterator = resources.iterator();
            while (iterator.hasNext()) {
                final AutoCloseable resource = iterator.next();
                try {
                    resource.close();
                } catch (final Exception exception) {
                    getLogger().log(Level.SEVERE, "Failed to close a plugin resource", exception);
                }
                iterator.remove();
            }
        }
    }

    public void deferResourceCloseOnPluginDisable(final @NotNull AutoCloseable resource) {
        resources.add(resource);
    }

    @Override
    public void configure(Binder binder) {
        // bind plugin!
        binder.bind(HMCRewardsPlugin.class).toInstance(this);
        binder.bind(JavaPlugin.class).to(HMCRewardsPlugin.class);
        binder.bind(Plugin.class).to(HMCRewardsPlugin.class);

        // bind configuration
        ConfigurationBinder.create(this, binder)
                .bind("config.yml")
                .bind("menu.yml");

        // install modules
        binder.install(new CommandModule());
        binder.install(new FeedbackModule());
        binder.install(new ItemModule());
        binder.install(new UserModule());
        binder.install(new RewardModule());
        binder.install(new HookModule());
    }
}

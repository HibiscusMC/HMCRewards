package com.hibiscusmc.hmcrewards;

import com.hibiscusmc.hmcrewards.command.CommandModule;
import com.hibiscusmc.hmcrewards.feedback.FeedbackModule;
import com.hibiscusmc.hmcrewards.util.ConfigurationManager;
import com.hibiscusmc.hmcrewards.util.Service;
import me.lojosho.hibiscuscommons.HibiscusPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;
import team.unnamed.inject.Module;

import java.util.Set;

@SuppressWarnings("unused")
public final class HMCRewardsPlugin extends HibiscusPlugin implements Module {
    public static final String GLOBAL_CONFIG = "config.yml";
    public static final String MENU_CONFIG = "menu.yml";

    @Inject private Set<Service> services;

    @Override
    public void onStart() {
        final var configurationManager = ConfigurationManager.create(this);
        configurationManager.getOrCreate(GLOBAL_CONFIG);
        configurationManager.getOrCreate(MENU_CONFIG);

        Injector.create(this).injectMembers(this);

        if (services != null) {
            for (final Service service : services) {
                service.start();
            }
        }
    }

    @Override
    public void onEnd() {
        if (services != null) {
            for (final Service service : services) {
                service.stop();
            }
        }
    }

    @Override
    public void configure(Binder binder) {
        // bind plugin!
        binder.bind(HMCRewardsPlugin.class).toInstance(this);
        binder.bind(JavaPlugin.class).to(HMCRewardsPlugin.class);
        binder.bind(Plugin.class).to(HMCRewardsPlugin.class);

        // install modules
        binder.install(new CommandModule());
        binder.install(new FeedbackModule());
    }
}

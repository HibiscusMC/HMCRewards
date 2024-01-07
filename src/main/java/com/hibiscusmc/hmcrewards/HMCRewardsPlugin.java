package com.hibiscusmc.hmcrewards;

import com.hibiscusmc.hmcrewards.feedback.FeedbackModule;
import com.hibiscusmc.hmcrewards.util.ConfigurationManager;
import me.lojosho.hibiscuscommons.HibiscusPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Module;

@SuppressWarnings("unused")
public final class HMCRewardsPlugin extends HibiscusPlugin implements Module {
    public static final String GLOBAL_CONFIG = "config.yml";
    public static final String MENU_CONFIG = "menu.yml";

    @Override
    public void onStart() {
        final var configurationManager = ConfigurationManager.create(this);
        configurationManager.getOrCreate(GLOBAL_CONFIG);
        configurationManager.getOrCreate(MENU_CONFIG);
    }

    @Override
    public void configure(Binder binder) {
        // bind plugin!
        binder.bind(HMCRewardsPlugin.class).toInstance(this);
        binder.bind(JavaPlugin.class).to(HMCRewardsPlugin.class);
        binder.bind(Plugin.class).to(HMCRewardsPlugin.class);

        // install modules
        binder.install(new FeedbackModule());
    }
}

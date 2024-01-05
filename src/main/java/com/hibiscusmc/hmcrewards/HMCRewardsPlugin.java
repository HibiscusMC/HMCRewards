package com.hibiscusmc.hmcrewards;

import com.hibiscusmc.hmcrewards.util.ConfigurationManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class HMCRewardsPlugin extends JavaPlugin {
    public static final String GLOBAL_CONFIG = "config.yml";
    public static final String MENU_CONFIG = "menu.yml";

    @Override
    public void onEnable() {
        final var configurationManager = ConfigurationManager.create(this);
        configurationManager.getOrCreate(GLOBAL_CONFIG);
        configurationManager.getOrCreate(MENU_CONFIG);
    }
}

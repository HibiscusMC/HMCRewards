package com.hibiscusmc.hmcrewards.hook.placeholderapi;

import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.util.Service;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;

public final class PlaceholderAPIHook implements Service {
    @Inject private Plugin plugin;
    @Inject private UserManager userManager;

    @Override
    public void start() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HMCRewardsPlaceholderExpansion(plugin, userManager).register();
        }
    }
}

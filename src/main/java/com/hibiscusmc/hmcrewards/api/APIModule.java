package com.hibiscusmc.hmcrewards.api;

import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;

public class APIModule extends AbstractModule {

    @Provides
    public HMCRewardsAPI provideHMCRewardsAPI(
            UserManager userManager,
            RewardProviderRegistry rewardProviderRegistry,
            ItemMatcher matcher,
            Plugin plugin
    ) {
        HMCRewardsAPI api = new HMCRewardsAPI(userManager, rewardProviderRegistry, matcher);

        Bukkit.getServicesManager().register(
                HMCRewardsAPI.class,
                api,
                plugin,
                ServicePriority.Normal
        );

        return api;
    }
}
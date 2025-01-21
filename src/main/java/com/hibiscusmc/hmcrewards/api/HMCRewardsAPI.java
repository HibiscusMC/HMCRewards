package com.hibiscusmc.hmcrewards.api;

import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.user.UserManager;

public record HMCRewardsAPI(
        UserManager userManager,
        RewardProviderRegistry rewardProviderRegistry,
        ItemMatcher matcher
) {
}
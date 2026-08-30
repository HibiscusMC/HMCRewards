package com.hibiscusmc.hmcrewards.command.arg;

import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import team.unnamed.commandflow.annotated.part.AbstractModule;
import team.unnamed.commandflow.annotated.part.Key;
import team.unnamed.inject.Inject;

public final class HMCRewardsCommandModule extends AbstractModule {
    @Inject private RewardProviderArgument rewardProviderArgument;
    @Inject private RewardArgument rewardArgument;
    @Inject private PlayerSelectorStringArgument playerSelectorStringArgument;

    @Override
    public void configure() {
        bindFactory(RewardProvider.class, rewardProviderArgument);
        bindFactory(new Key(String.class, RewardRef.class), rewardArgument);
        bindFactory(new Key(String.class, PlayerSelector.class), playerSelectorStringArgument);
    }
}

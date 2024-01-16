package com.hibiscusmc.hmcrewards.command.arg;

import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import me.fixeddev.commandflow.annotated.part.AbstractModule;
import team.unnamed.inject.Inject;

public final class HMCRewardsCommandModule extends AbstractModule {
    @Inject private RewardProviderArgument rewardProviderArgument;
    @Inject private RewardArgument rewardArgument;

    @Override
    public void configure() {
        bindFactory(RewardProvider.class, rewardProviderArgument);
        bindFactory(RewardId.class, rewardArgument);
    }
}

package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.util.BukkitAbstractModule;

public final class RewardModule extends BukkitAbstractModule {
    @Override
    protected void configure() {
        bind(RewardProviderRegistry.class).to(RewardProviderRegistryImpl.class).singleton();
    }
}

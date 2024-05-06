package com.hibiscusmc.hmcrewards.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class RewardProviderRegistryImpl implements RewardProviderRegistry {
    private final Map<String, RewardProvider<?>> providers = new HashMap<>();

    @Inject
    void registerDefaults(final @NotNull ItemRewardProvider item, final @NotNull CommandRewardProvider command) {
        register(item);
        register(command);
    }

    @Override
    public @NotNull List<? extends Reward> findByReference(final @NotNull String rewardRef) {
        for (final var provider : providers.values()) {
            final var rewards = provider.fromReference(rewardRef);
            if (!rewards.isEmpty()) {
                return rewards;
            }
        }
        return List.of();
    }

    @Override
    public void register(final @NotNull RewardProvider<?> provider) {
        requireNonNull(provider, "provider");
        providers.put(provider.id(), provider);
    }

    @Override
    public @Nullable RewardProvider<?> provider(final @NotNull String id) {
        requireNonNull(id, "id");
        return providers.get(id);
    }

    @Override
    public @NotNull Collection<RewardProvider<?>> providers() {
        return providers.values();
    }
}

package com.hibiscusmc.hmcrewards.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class RewardProviderRegistryImpl implements RewardProviderRegistry {
    private final Map<String, RewardProvider<?>> providers = new HashMap<>();

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

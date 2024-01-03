package com.hibiscusmc.hmcrewards.reward.provider;

import com.hibiscusmc.hmcrewards.reward.Reward;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a reward provider. A "type" of rewards. For example,
 * there are default reward providers that provide Minecraft items,
 * others that provide Oraxen items, and others that provide commands
 * to execute.
 */
public interface RewardProvider {
    /**
     * Returns the reward provider identifier. This
     * could be your plugin's name.
     *
     * @return the reward provider identifier
     */
    @NotNull String id();

    /**
     * Gets/deserializes a reward from the given
     * configuration section.
     *
     * @param section the configuration section
     * @return the reward
     * @throws IllegalArgumentException If the configuration
     * is invalid for this reward provider type.
     */
    @Nullable Reward deserialize(final @NotNull ConfigurationSection section) throws IllegalArgumentException;

    default @Nullable Reward deserializeFromString(final @NotNull String string) throws IllegalArgumentException {
        return null;
    }
}

package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.reward.Reward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a reward provider. A "type" of rewards. For example,
 * there are default reward providers that provide Minecraft items,
 * others that provide Oraxen items, and others that provide commands
 * to execute.
 */
public interface RewardProvider<T extends Reward> {
    /**
     * Returns the reward provider identifier. This
     * could be your plugin's name.
     *
     * @return the reward provider identifier
     */
    @NotNull String id();

    /**
     * Gives the given reward to the given player.
     *
     * @param player the player
     * @param reward the reward
     * @return the result of the give operation
     */
    @NotNull GiveResult give(final @NotNull Player player, final @NotNull T reward);

    /**
     * Gets/deserializes a reward from the given
     * configuration section.
     *
     * @param section the configuration section
     * @return the reward
     * @throws IllegalArgumentException If the configuration
     * is invalid for this reward provider type.
     */
    @Nullable T fromConfiguration(final @NotNull ConfigurationSection section) throws IllegalArgumentException;

    @NotNull List<T> fromReference(final @NotNull String reference);

    /**
     * Tries to stack the given rewards. If the rewards
     * can be stacked, the result will be the stacked reward.
     * If the rewards can't be stacked, the result will be null.
     *
     * @param a The first reward
     * @param b The second reward
     * @return The stacked reward, or null if the rewards can't be stacked
     */
    default @Nullable T stack(final @NotNull T a, final @NotNull T b) {
        return null;
    }

    enum GiveResult {
        SUCCESS,
        NO_SPACE_IN_INVENTORY,
        INVALID_REWARD,
        NO_PERMISSION,
        UNKNOWN_ERROR
    }
}

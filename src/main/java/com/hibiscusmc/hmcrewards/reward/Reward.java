package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Reward {
    /**
     * Returns the reward type id.
     *
     * @return the reward type id
     */
    @NotNull String type();

    /**
     * Returns the reward reference. This is, its optional
     * identifier. If the reward has a reference, the reference
     * will be stored instead of all the reward data.
     *
     * <p>For example, if you have a configurable command,
     * a reference should be used.</p>
     *
     * <p>This is useful for saving space in the database and
     * avoiding duplicate data.</p>
     *
     * <p>To rebuild a reward from its reference, a {@link RewardProvider}
     * or a {@link RewardProviderRegistry} instance should be used.</p>
     *
     * @return the reward reference, may be null
     */
    @Nullable String reference();

    @NotNull ItemDefinition icon();
}

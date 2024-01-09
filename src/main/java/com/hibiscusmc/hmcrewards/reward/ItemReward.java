package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public final class ItemReward implements Reward {
    private final String reference;
    private final ItemDefinition item;

    public ItemReward(final @Nullable String reference, final @NotNull ItemDefinition item) {
        this.reference = requireNonNull(reference, "reference");
        this.item = requireNonNull(item, "item");
    }

    @Override
    public @NotNull String type() {
        return ItemRewardProvider.ID;
    }

    @Override
    public @Nullable String reference() {
        return reference;
    }

    public @NotNull ItemDefinition item() {
        return item;
    }

    @Override
    public @NotNull ItemDefinition icon() {
        return item;
    }
}

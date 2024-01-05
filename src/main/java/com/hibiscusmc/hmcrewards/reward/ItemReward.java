package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class ItemReward implements Reward {
    private final ItemDefinition item;

    public ItemReward(final @NotNull ItemDefinition item) {
        this.item = requireNonNull(item, "item");
    }

    public @NotNull ItemDefinition item() {
        return item;
    }

    @Override
    public @NotNull ItemDefinition icon() {
        return item;
    }
}

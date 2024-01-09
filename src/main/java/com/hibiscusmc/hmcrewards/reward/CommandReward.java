package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class CommandReward implements Reward {
    private final String reference;
    private final int inventorySlots;
    private final ItemDefinition icon;
    private final List<String> commands;

    public CommandReward(final @Nullable String reference, final int inventorySlots, final ItemDefinition icon, final List<String> commands) {
        this.reference = reference;
        this.inventorySlots = inventorySlots;
        this.icon = requireNonNull(icon, "icon");
        this.commands = requireNonNull(commands, "commands");
    }

    @Override
    public @NotNull String type() {
        return CommandRewardProvider.ID;
    }

    @Override
    public @Nullable String reference() {
        return reference;
    }

    public int inventorySlots() {
        return inventorySlots;
    }

    @Override
    public @NotNull ItemDefinition icon() {
        return icon;
    }

    public @NotNull List<String> commands() {
        return commands;
    }
}

package com.hibiscusmc.hmcrewards.reward.command;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.reward.Reward;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class CommandReward implements Reward {
    private final int inventorySlots;
    private final ItemDefinition icon;
    private final List<String> commands;

    public CommandReward(final int inventorySlots, final ItemDefinition icon, final List<String> commands) {
        this.inventorySlots = inventorySlots;
        this.icon = requireNonNull(icon, "icon");
        this.commands = requireNonNull(commands, "commands");
    }

    public int inventorySlots() {
        return inventorySlots;
    }

    public @NotNull ItemDefinition icon() {
        return icon;
    }

    public @NotNull List<String> commands() {
        return commands;
    }

    @Override
    public @NotNull ItemStack buildIcon() {
        return null;
    }
}

package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

public final class CommandReward implements Reward {
    private static final String DYNAMIC_PREFIX = "dynamic-command:";
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

    public static @NotNull CommandReward dynamic(final @NotNull org.bukkit.inventory.ItemStack icon, final @NotNull java.util.Collection<String> commands) {
        String payload = ItemDefinition.ofItemStack(icon).serializedItemStack() + "." + Base64.getEncoder().encodeToString(
                String.join("\n", commands).getBytes(StandardCharsets.UTF_8)
        );
        return new CommandReward(DYNAMIC_PREFIX + payload, 0, ItemDefinition.ofItemStack(icon), List.copyOf(commands));
    }

    static @Nullable CommandReward dynamic(final @NotNull String reference) {
        if (!reference.startsWith(DYNAMIC_PREFIX)) {
            return null;
        }
        String[] values = reference.substring(DYNAMIC_PREFIX.length()).split("\\.", 2);
        String[] commands = values.length == 2 && !values[1].isEmpty()
                ? new String(Base64.getDecoder().decode(values[1]), StandardCharsets.UTF_8).split("\n", -1) : new String[0];
        return new CommandReward(reference, 0, ItemDefinition.ofSerializedItemStack(values[0]), List.of(commands));
    }
}

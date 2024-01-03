package com.hibiscusmc.hmcrewards.item;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OraxenItemMatcher implements ItemMatcher {
    @Override
    public @Nullable ItemStack find(final @NotNull String id, final @NotNull ItemMatcher context) {
        final var found = OraxenItems.getItemById(id);
        if (found == null) {
            return null;
        } else {
            return found.build();
        }
    }
}

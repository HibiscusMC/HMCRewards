package com.hibiscusmc.hmcrewards.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemMatcher {
    @Nullable ItemStack find(final @NotNull String id, final @NotNull ItemMatcher context);
}

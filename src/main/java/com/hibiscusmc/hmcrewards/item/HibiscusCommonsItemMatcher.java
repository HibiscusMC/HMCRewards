package com.hibiscusmc.hmcrewards.item;

import me.lojosho.hibiscuscommons.hooks.Hooks;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class HibiscusCommonsItemMatcher implements ItemMatcher {
    static final ItemMatcher INSTANCE = new HibiscusCommonsItemMatcher();

    private HibiscusCommonsItemMatcher() {
    }

    @Override
    public @Nullable ItemStack find(final @NotNull String id, final @NotNull ItemMatcher context) {
        return Hooks.getItem(id);
    }
}

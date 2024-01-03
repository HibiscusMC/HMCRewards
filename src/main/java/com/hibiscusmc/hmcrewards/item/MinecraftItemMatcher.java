package com.hibiscusmc.hmcrewards.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MinecraftItemMatcher implements ItemMatcher {
    @Override
    public @Nullable ItemStack find(final @NotNull String id, final @NotNull ItemMatcher context) {
        final var material = Material.matchMaterial(id);
        if (material == null) {
            return null;
        } else {
            return new ItemStack(material);
        }
    }
}

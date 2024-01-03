package com.hibiscusmc.hmcrewards.reward;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Reward {
    @NotNull ItemStack buildIcon();
}

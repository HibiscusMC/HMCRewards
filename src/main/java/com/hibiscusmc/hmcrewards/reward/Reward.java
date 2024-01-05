package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Reward {
    @NotNull ItemDefinition icon();
}

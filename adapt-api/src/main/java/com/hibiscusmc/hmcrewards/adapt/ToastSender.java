package com.hibiscusmc.hmcrewards.adapt;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ToastSender {
    void showToast(final @NotNull Player player, final @NotNull ItemStack icon, final @NotNull Component title);
}

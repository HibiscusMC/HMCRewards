package com.hibiscusmc.hmcrewards.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UserManager {
    @Nullable User getCached(final @NotNull Player player);
}

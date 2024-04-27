package com.hibiscusmc.hmcrewards.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class OptionalPlaceholderAPI {
    private OptionalPlaceholderAPI() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static boolean enabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static @NotNull String setPlaceholders(final @NotNull Player player, final @NotNull String text) {
        if (enabled()) {
            return PlaceholderAPI.setPlaceholders(player, text);
        } else {
            return text;
        }
    }
}

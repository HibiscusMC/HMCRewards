package com.hibiscusmc.hmcrewards.hook.placeholderapi;

import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public final class HMCRewardsPlaceholderExpansion extends PlaceholderExpansion {
    private final Plugin plugin;
    private final UserManager userManager;

    public HMCRewardsPlaceholderExpansion(final @NotNull Plugin plugin, final @NotNull UserManager userManager) {
        this.plugin = requireNonNull(plugin, "plugin");
        this.userManager = requireNonNull(userManager, "userManager");
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName().toLowerCase(Locale.ROOT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(final Player player, final @NotNull String params) {
        if (player == null) {
            return null;
        }

        return switch (params.toLowerCase()) {
            case "current_reward_amount" -> {
                final User user = userManager.getCached(player);
                if (user == null) {
                    yield null;
                } else {
                    yield Integer.toString(user.rewards().size());
                }
            }
            case "has_reward" -> {
                final var user = userManager.getCached(player);
                yield Boolean.toString((user != null && !user.rewards().isEmpty()));
            }
            default -> null;
        };
    }
}

package com.hibiscusmc.hmcrewards.util;

import com.nexomc.nexo.glyphs.GlyphTag;
import com.nexomc.nexo.glyphs.ShiftTag;
import io.th0rgal.oraxen.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class GlobalMiniMessage {
    private static final MiniMessage MINI_MESSAGE;

    static {
        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            // take Oraxen's MiniMessage instance, for compatibility with its placeholders
            MINI_MESSAGE = AdventureUtils.MINI_MESSAGE;
        } else {
            // use our own MiniMessage instance
            MINI_MESSAGE = MiniMessage.miniMessage();
        }
    }

    private GlobalMiniMessage() {
    }

    public static @NotNull MiniMessage get() {
        return MINI_MESSAGE;
    }

    public static @NotNull Component deserialize(final @NotNull String string) {
        return MINI_MESSAGE.deserialize(string);
    }

    public static @NotNull Component deserializeForItem(final @NotNull String string) {
        return styleItem(MINI_MESSAGE.deserialize(string));
    }

    public static @NotNull Component deserializeForMenuItem(final @NotNull Player player, final @NotNull String string) {
        final String parsed = OptionalPlaceholderAPI.setPlaceholders(player, string);
        if (Bukkit.getPluginManager().isPluginEnabled("Nexo")) {
            return styleItem(MINI_MESSAGE.deserialize(parsed, GlyphTag.INSTANCE.getRESOLVER(), ShiftTag.INSTANCE.getRESOLVER()));
        }
        return styleItem(MINI_MESSAGE.deserialize(parsed));
    }

    private static @NotNull Component styleItem(final @NotNull Component component) {
        return component
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .colorIfAbsent(NamedTextColor.WHITE);
    }
}

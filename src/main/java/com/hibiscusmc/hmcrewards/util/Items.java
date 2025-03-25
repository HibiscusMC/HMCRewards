package com.hibiscusmc.hmcrewards.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public final class Items {
    private Items() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Unwraps the component from the square brackets.
     *
     * @param component the component to unwrap
     * @return the unwrapped component
     */
    public static @NotNull Component unwrapBrackets(final @NotNull Component component) {
        if (component instanceof TranslatableComponent translatable && translatable.key().equals("chat.square_brackets")) {
            final var arguments = translatable.arguments();
            if (arguments.isEmpty()) {
                return Component.empty();
            } else {
                return arguments.getFirst().asComponent();
            }
        } else {
            return component;
        }
    }
}

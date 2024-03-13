package com.hibiscusmc.hmcrewards.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Expressions {
    private Expressions() {
    }

    public static <T> @NotNull Supplier<T> lazy(final @NotNull Supplier<T> supplier) {
        return new Supplier<>() {
            private T value;

            @Override
            public T get() {
                if (value == null) {
                    value = supplier.get();
                }
                return value;
            }
        };
    }

    public static <T> @NotNull T configure(final @NotNull T value, final @NotNull Consumer<T> configure) {
        configure.accept(value);
        return value;
    }
}

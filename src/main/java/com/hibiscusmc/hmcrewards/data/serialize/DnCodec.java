package com.hibiscusmc.hmcrewards.data.serialize;

import org.jetbrains.annotations.NotNull;

public interface DnCodec<T> {
    @NotNull Class<T> type();

    void encode(final @NotNull DnWriter writer, final @NotNull T value);

    @NotNull T decode(final @NotNull DnReader reader);
}

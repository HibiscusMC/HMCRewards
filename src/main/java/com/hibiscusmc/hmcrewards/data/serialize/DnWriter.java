package com.hibiscusmc.hmcrewards.data.serialize;

import org.jetbrains.annotations.NotNull;

public interface DnWriter {
    void writeObjectStart();

    void writeName(final @NotNull String name);

    void writeObjectEnd();

    void writeArrayStart();

    default void writeArrayStart(final @NotNull String name) {
        writeName(name);
        writeArrayStart();
    }

    void writeArrayEnd();

    void writeStringValue(final @NotNull String value);

    void writeIntValue(final int value);

    void writeBooleanValue(final boolean value);

    void writeDoubleValue(final double value);

    void writeFloatValue(final float value);

    void writeLongValue(final long value);

    default void writeStringValue(final @NotNull String name, final @NotNull String value) {
        writeName(name);
        writeStringValue(value);
    }

    default void writeBooleanValue(final @NotNull String name, final boolean value) {
        writeName(name);
        writeBooleanValue(value);
    }
}

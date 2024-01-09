package com.hibiscusmc.hmcrewards.data.serialize.json;

import com.google.gson.stream.JsonWriter;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;

import static java.util.Objects.requireNonNull;

public final class JsonDnWriter implements DnWriter {
    private final JsonWriter delegate;

    public JsonDnWriter(final @NotNull JsonWriter delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    @Override
    public void writeObjectStart() {
        try {
            delegate.beginObject();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeName(final @NotNull String name) {
        try {
            delegate.name(name);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeObjectEnd() {
        try {
            delegate.endObject();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeArrayStart() {
        try {
            delegate.beginArray();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeArrayEnd() {
        try {
            delegate.endArray();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeStringValue(final @NotNull String value) {
        try {
            delegate.value(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeIntValue(final int value) {
        try {
            delegate.value(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeBooleanValue(final boolean value) {
        try {
            delegate.value(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeDoubleValue(final double value) {
        try {
            delegate.value(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeFloatValue(final float value) {
        try {
            delegate.value(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeLongValue(final long value) {
        try {
            delegate.value(value);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

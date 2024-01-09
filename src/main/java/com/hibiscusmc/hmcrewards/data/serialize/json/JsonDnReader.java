package com.hibiscusmc.hmcrewards.data.serialize.json;

import com.google.gson.stream.JsonReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;

import static java.util.Objects.requireNonNull;

public final class JsonDnReader implements DnReader {
    private final JsonReader delegate;

    public JsonDnReader(final @NotNull JsonReader delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    @Override
    public boolean hasMoreValuesOrEntries() {
        try {
            return delegate.hasNext();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public @NotNull DnType readType() {
        try {
            return switch (delegate.peek()) {
                case BEGIN_ARRAY -> DnType.START_ARRAY;
                case END_ARRAY, END_OBJECT, END_DOCUMENT -> DnType.END;
                case BEGIN_OBJECT -> DnType.START_OBJECT;
                default -> DnType.VALUE;
            };
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void readObjectStart() {
        try {
            delegate.beginObject();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public @NotNull String readName() {
        try {
            return delegate.nextName();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void readObjectEnd() {
        try {
            delegate.endObject();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void readArrayStart() {
        try {
            delegate.beginArray();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void readArrayEnd() {
        try {
            delegate.endArray();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public @NotNull String readStringValue() {
        try {
            return delegate.nextString();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int readIntValue() {
        try {
            return delegate.nextInt();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean readBooleanValue() {
        try {
            return delegate.nextBoolean();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public double readDoubleValue() {
        try {
            return delegate.nextDouble();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public float readFloatValue() {
        try {
            return (float) delegate.nextDouble();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public long readLongValue() {
        try {
            return delegate.nextLong();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void skipValue() {
        try {
            delegate.skipValue();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

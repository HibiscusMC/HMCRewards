package com.hibiscusmc.hmcrewards.data.serialize.bson;

import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import org.bson.BsonWriter;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class BsonDnWriter implements DnWriter {
    private final BsonWriter delegate;

    public BsonDnWriter(final @NotNull BsonWriter delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    @Override
    public void writeObjectStart() {
        delegate.writeStartDocument();
    }

    @Override
    public void writeName(final @NotNull String name) {
        delegate.writeName(name);
    }

    @Override
    public void writeObjectEnd() {
        delegate.writeEndDocument();
    }

    @Override
    public void writeArrayStart() {
        delegate.writeStartArray();
    }

    @Override
    public void writeArrayEnd() {
        delegate.writeEndArray();
    }

    @Override
    public void writeStringValue(final @NotNull String value) {
        delegate.writeString(value);
    }

    @Override
    public void writeIntValue(final int value) {
        delegate.writeInt32(value);
    }

    @Override
    public void writeBooleanValue(final boolean value) {
        delegate.writeBoolean(value);
    }

    @Override
    public void writeDoubleValue(final double value) {
        delegate.writeDouble(value);
    }

    @Override
    public void writeFloatValue(final float value) {
        delegate.writeDouble(value);
    }

    @Override
    public void writeLongValue(final long value) {
        delegate.writeInt64(value);
    }
}

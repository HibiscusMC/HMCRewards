package com.hibiscusmc.hmcrewards.data.serialize.bson;

import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnType;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class BsonDnReader implements DnReader {
    private final BsonReader delegate;

    public BsonDnReader(final @NotNull BsonReader delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    @Override
    public boolean hasMoreValuesOrEntries() {
        return delegate.readBsonType() != BsonType.END_OF_DOCUMENT;
    }

    @Override
    public @NotNull DnType readType() {
        return switch (delegate.readBsonType()) {
            case DOCUMENT -> DnType.START_OBJECT;
            case END_OF_DOCUMENT -> DnType.END;
            case ARRAY -> DnType.START_ARRAY;
            default -> DnType.VALUE;
        };
    }

    @Override
    public void readObjectStart() {
        delegate.readStartDocument();
    }

    @Override
    public @NotNull String readName() {
        return delegate.readName();
    }

    @Override
    public void readObjectEnd() {
        delegate.readEndDocument();
    }

    @Override
    public void readArrayStart() {
        delegate.readStartArray();
    }

    @Override
    public void readArrayEnd() {
        delegate.readEndArray();
    }

    @Override
    public @NotNull String readStringValue() {
        return delegate.readString();
    }

    @Override
    public int readIntValue() {
        return delegate.readInt32();
    }

    @Override
    public boolean readBooleanValue() {
        return delegate.readBoolean();
    }

    @Override
    public double readDoubleValue() {
        return delegate.readDouble();
    }

    @Override
    public float readFloatValue() {
        return (float) delegate.readDouble();
    }

    @Override
    public long readLongValue() {
        return delegate.readInt64();
    }

    @Override
    public void skipValue() {
        delegate.skipValue();
    }
}

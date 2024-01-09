package com.hibiscusmc.hmcrewards.data.serialize.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class JsonCodecAdapter<T> extends TypeAdapter<T> {
    private final DnCodec<T> delegate;

    public JsonCodecAdapter(final @NotNull DnCodec<T> delegate) {
        this.delegate = requireNonNull(delegate, "delegate");
    }

    @Override
    public void write(JsonWriter jsonWriter, T t) {
        delegate.encode(new JsonDnWriter(jsonWriter), t);
    }

    @Override
    public T read(JsonReader jsonReader) {
        return delegate.decode(new JsonDnReader(jsonReader));
    }
}

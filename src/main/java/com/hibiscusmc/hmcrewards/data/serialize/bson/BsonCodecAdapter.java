package com.hibiscusmc.hmcrewards.data.serialize.bson;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class BsonCodecAdapter<T> implements Codec<T> {
    private final DnCodec<T> codec;

    public BsonCodecAdapter(final @NotNull DnCodec<T> codec) {
        this.codec = requireNonNull(codec, "codec");
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        return codec.decode(new BsonDnReader(reader));
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        codec.encode(new BsonDnWriter(writer), value);
    }

    @Override
    public Class<T> getEncoderClass() {
        return codec.type();
    }
}

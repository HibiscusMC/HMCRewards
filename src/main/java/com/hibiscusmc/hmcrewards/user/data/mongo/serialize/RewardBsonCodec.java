package com.hibiscusmc.hmcrewards.user.data.mongo.serialize;

import com.hibiscusmc.hmcrewards.reward.Reward;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public final class RewardBsonCodec implements Codec<Reward> {
    private static final Codec<Reward> INSTANCE = new RewardBsonCodec();

    private RewardBsonCodec() {
    }

    @Override
    public Reward decode(BsonReader reader, DecoderContext decoderContext) {
        return null;
    }

    @Override
    public void encode(BsonWriter writer, Reward value, EncoderContext encoderContext) {

    }

    @Override
    public Class<Reward> getEncoderClass() {
        return Reward.class;
    }

    public static @NotNull Codec<Reward> instance() {
        return INSTANCE;
    }
}

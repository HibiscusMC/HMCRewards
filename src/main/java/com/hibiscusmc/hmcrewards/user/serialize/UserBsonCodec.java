package com.hibiscusmc.hmcrewards.user.serialize;

import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.user.User;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserBsonCodec implements Codec<User> {
    @Override
    public User decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID uuid = null;
        String name = null;
        List<Reward> rewards = null;

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String prop = reader.readName();
            if (prop.equals("uuid")) {
                uuid = UUID.fromString(reader.readString());
            } else if (prop.equals("name")) {
                name = reader.readString();
            } else if (prop.equals("rewards")) {
                rewards = new ArrayList<>();
                reader.readStartArray();
                while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    reader.readStartDocument();
                    rewards.add(RewardBsonCodec.instance().decode(reader, decoderContext));
                    reader.readEndDocument();
                }
                reader.readEndArray();
            }
        }

        if (uuid == null || name == null || rewards == null) {
            throw new IllegalStateException("Missing required properties 'uuid', 'name' or 'rewards'.");
        }

        reader.readEndDocument();
        return User.user(uuid, name);
    }

    @Override
    public void encode(final BsonWriter writer, final User value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("uuid", value.uuid().toString());
        writer.writeString("name", value.name());
        writer.writeStartArray("rewards");
        for (Reward reward : value.rewards()) {
            writer.writeStartDocument();
            RewardBsonCodec.instance().encode(writer, reward, encoderContext);
            writer.writeEndDocument();
        }
        writer.writeEndArray();
        writer.writeEndDocument();
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }
}

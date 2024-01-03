package com.hibiscusmc.hmcrewards.user.serialize;

import com.hibiscusmc.hmcrewards.user.User;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

public final class UserBsonCodec implements Codec<User> {
    @Override
    public User decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();

        final int propertyCount = 2;
        UUID uuid = null;
        String name = null;

        for (int i = 0; i < propertyCount; i++) {
            String prop = reader.readName();
            if (prop.equals("uuid")) {
                uuid = UUID.fromString(reader.readString());
            } else if (prop.equals("name")) {
                name = reader.readString();
            } else {
                // unknown property, skip
                i--;
            }
        }

        if (uuid == null || name == null) {
            throw new IllegalStateException("Missing required properties 'uuid' and 'name'.");
        }

        reader.readEndDocument();
        return User.user(uuid, name);
    }

    @Override
    public void encode(final BsonWriter writer, final User value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("uuid", value.uuid().toString());
        writer.writeString("name", value.name());
        writer.writeEndDocument();
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }
}

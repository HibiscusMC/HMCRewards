package com.hibiscusmc.hmcrewards.user.data.mongo.serialize;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserCodec implements DnCodec<User> {
    @Override
    public @NotNull Class<User> type() {
        return User.class;
    }

    @Override
    public @NotNull User decode(final @NotNull DnReader reader) {
        reader.readObjectStart();

        UUID uuid = null;
        String name = null;
        List<Reward> rewards = null;

        while (reader.hasMoreValuesOrEntries()) {
            String prop = reader.readName();
            if (prop.equals("uuid")) {
                uuid = UUID.fromString(reader.readStringValue());
            } else if (prop.equals("name")) {
                name = reader.readStringValue();
            } else if (prop.equals("rewards")) {
                rewards = new ArrayList<>();
                reader.readArrayStart();
                while (reader.hasMoreValuesOrEntries()) {
                    rewards.add(RewardBsonCodec.instance().decode(reader, decoderContext));
                }
                reader.readArrayEnd();
            } else {
                reader.skipValue();
            }
        }

        if (uuid == null || name == null || rewards == null) {
            throw new IllegalStateException("Missing required properties 'uuid', 'name' or 'rewards'.");
        }

        reader.readObjectEnd();
        return User.user(uuid, name);
    }

    @Override
    public void encode(final @NotNull DnWriter writer, final @NotNull User value) {
        writer.writeObjectStart();
        writer.writeStringValue("uuid", value.uuid().toString());
        writer.writeStringValue("name", value.name());
        writer.writeArrayStart("rewards");
        for (Reward reward : value.rewards()) {
            RewardBsonCodec.instance().encode(writer, reward, encoderContext);
        }
        writer.writeArrayEnd();
        writer.writeObjectEnd();
    }
}

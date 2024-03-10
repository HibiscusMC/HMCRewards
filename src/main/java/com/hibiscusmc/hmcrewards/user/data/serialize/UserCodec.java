package com.hibiscusmc.hmcrewards.user.data.serialize;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
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
        final List<String> rewards = new ArrayList<>();
        boolean hasReceivedRewardsBefore = false;

        while (reader.hasMoreValuesOrEntries()) {
            String prop = reader.readName();
            if (prop.equals("uuid")) {
                uuid = UUID.fromString(reader.readStringValue());
            } else if (prop.equals("name")) {
                name = reader.readStringValue();
            } else if (prop.equals("rewards")) {
                reader.readArrayStart();
                while (reader.hasMoreValuesOrEntries()) {
                    rewards.add(reader.readStringValue());
                }
                reader.readArrayEnd();
            } else if (prop.equals("hasClaimedRewardsBefore")) {
                hasReceivedRewardsBefore = reader.readBooleanValue();
            } else {
                reader.skipValue();
            }
        }

        if (uuid == null || name == null) {
            throw new IllegalStateException("Missing required properties 'uuid', 'name' or 'rewards'.");
        }

        reader.readObjectEnd();
        return User.user(uuid, name, rewards, hasReceivedRewardsBefore);
    }

    @Override
    public void encode(final @NotNull DnWriter writer, final @NotNull User value) {
        writer.writeObjectStart();
        writer.writeStringValue("uuid", value.uuid().toString());
        writer.writeStringValue("name", value.name());
        writer.writeBooleanValue("hasClaimedRewardsBefore", value.hasReceivedRewardsBefore());
        writer.writeArrayStart("rewards");
        for (final String reward : value.rewards()) {
            writer.writeStringValue(reward);
        }
        writer.writeArrayEnd();
        writer.writeObjectEnd();
    }
}

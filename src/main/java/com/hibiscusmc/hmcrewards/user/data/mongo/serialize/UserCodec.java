package com.hibiscusmc.hmcrewards.user.data.mongo.serialize;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnType;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public final class UserCodec implements DnCodec<User> {
    private final RewardProviderRegistry rewardProviderRegistry;

    public UserCodec(final @NotNull RewardProviderRegistry rewardProviderRegistry) {
        this.rewardProviderRegistry = requireNonNull(rewardProviderRegistry, "rewardProviderRegistry");
    }

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
                    // id-only
                    final String id = reader.readStringValue();
                    Reward reward = null;
                    for (final RewardProvider<?> provider : rewardProviderRegistry.providers()) {
                        if ((reward = provider.fromReference(id)) != null) {
                            break;
                        }
                    }
                    if (reward == null) {
                        throw new IllegalStateException("Unknown reward reference '" + id + "'.");
                    }
                    rewards.add(reward);
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
        return User.user(uuid, name, rewards);
    }

    @Override
    public void encode(final @NotNull DnWriter writer, final @NotNull User value) {
        writer.writeObjectStart();
        writer.writeStringValue("uuid", value.uuid().toString());
        writer.writeStringValue("name", value.name());
        writer.writeArrayStart("rewards");
        for (Reward reward : value.rewards()) {
            final String reference = reward.reference();
            if (reference == null) {
                throw new UnsupportedOperationException("Object rewards are not supported yet.");
            }
            writer.writeStringValue(reference);
        }
        writer.writeArrayEnd();
        writer.writeObjectEnd();
    }
}

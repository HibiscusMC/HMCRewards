package com.hibiscusmc.hmcrewards.user.data.yaml;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.json.JsonDnReader;
import com.hibiscusmc.hmcrewards.data.serialize.json.JsonDnWriter;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.user.data.serialize.UserCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public final class JsonUserDatastore implements UserDatastore {
    private final Path folder;
    private final DnCodec<User> codec;

    public JsonUserDatastore(final @NotNull Path folder) {
        this.folder = requireNonNull(folder, "folder");
        this.codec = new UserCodec();
    }

    @Override
    public @Nullable User findByUuid(final @NotNull UUID uuid) {
        final var path = folder.resolve(uuid + ".json");
        if (!Files.exists(path)) {
            return null;
        }

        return deserializeFromPath(path);
    }

    private @NotNull User deserializeFromPath(final @NotNull Path path) {
        try (final var reader = new JsonReader(Files.newBufferedReader(path))) {
            return codec.decode(new JsonDnReader(reader));
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read user data for " + path, e);
        }
    }

    @Override
    public @Nullable User findByUsername(final @NotNull String username) {
        if (!Files.exists(folder)) {
            return null;
        }
        try (final var stream = Files.list(folder)) {
            return stream
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(this::deserializeFromPath)
                    .filter(user -> user.name().equals(username))
                    .findFirst()
                    .orElse(null);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read user data for " + username, e);
        }
    }

    @Override
    public void save(final @NotNull User user) {
        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create user data folder", e);
            }
        }
        final var path = folder.resolve(user.uuid() + ".json");
        try (final var writer = new JsonWriter(Files.newBufferedWriter(path))) {
            codec.encode(new JsonDnWriter(writer), user);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to save user data for " + user.uuid(), e);
        }
    }
}

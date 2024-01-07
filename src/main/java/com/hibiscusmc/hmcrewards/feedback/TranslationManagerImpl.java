package com.hibiscusmc.hmcrewards.feedback;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

final class TranslationManagerImpl implements TranslationManager {
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\\r?\\n");

    private final Plugin plugin;
    private final Map<String, String> formats = new HashMap<>();

    TranslationManagerImpl(final @NotNull Plugin plugin) {
        this.plugin = requireNonNull(plugin, "plugin");
        loadFormats();
        // registerLocale(Locale.ENGLISH);
    }

    @Override
    public void registerLocale(final @NotNull Locale locale) {
        requireNonNull(locale, "locale");
        final var translationRegistry = TranslationRegistry.create(Key.key(plugin.getName(), "translator_" + locale));
        translationRegistry.registerAll(
                locale,
                PropertyResourceBundle.getBundle("lang", locale, UTF8ResourceBundleControl.get()),
                false
        );
        GlobalTranslator.translator().addSource(translationRegistry);
    }

    @Override
    public void loadFormats() {
        formats.clear();

        // try to load as a file first
        final var path = plugin.getDataFolder().toPath().resolve("formats.yml");
        if (Files.exists(path)) {
            try {
                final var configuration = new YamlConfiguration();
                configuration.load(path.toFile());
                for (final var key : configuration.getKeys(true)) {
                    formats.put(key, configuration.getString(key));
                }
            } catch (final Exception e) {
                plugin.getLogger().warning("Failed to load formats.yml, falling back to default formats");
            }
            return;
        }

        // load from resource
        final byte[] data;
        try (final var resource = plugin.getResource("formats.yml")) {
            if (resource == null) {
                throw new IllegalStateException("Failed to load formats.yml from plugin resources");
            }

            // copy the resource to a byte array
            try (final var output = new ByteArrayOutputStream()) {
                final var buf = new byte[1024];
                int len;
                while ((len = resource.read(buf)) > 0) {
                    output.write(buf, 0, len);
                }
                data = output.toByteArray();
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to get formats.yml from plugin resources", e);
        }

        // copy the data to the file
        try {
            Files.write(path, data);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to write formats.yml to disk", e);
        }

        // load the formats from the byte array
        final var configuration = new YamlConfiguration();
        try (final var reader = new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8)) {
            configuration.load(reader);
        } catch (final InvalidConfigurationException | IOException e) {
            throw new IllegalStateException("Failed to read formats.yaml from memory", e);
        }

        for (final var key : configuration.getKeys(true)) {
            formats.put(key, configuration.getString(key));
        }
    }

    @Override
    public @Nullable Component get(final @NotNull String key, final @NotNull TagResolver @NotNull ... resolvers) {
        final var format = formats.get(key);
        if (format == null) {
            return null;
        }
        return parse(format, resolvers);
    }

    @Override
    public @Nullable List<Component> getMany(@NotNull String key, @NotNull TagResolver @NotNull ... resolvers) {
        final var format = formats.get(key);
        if (format == null) {
            return null;
        }

        // split by line break
        final var lines = NEW_LINE_PATTERN.split(format);
        final var components = new ArrayList<Component>(lines.length);
        for (final var line : lines) {
            components.add(parse(line, resolvers));
        }
        return components;
    }

    private static @NotNull Component parse(final @NotNull String format, final @NotNull TagResolver @NotNull ... resolvers) {
        return MiniMessage.miniMessage().deserialize(format, resolvers);
    }
}
package com.hibiscusmc.hmcrewards.feedback;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

// todo: maybe something like this could be moved to an external library?
public interface TranslationManager {

    void loadFormats();

    void registerLocale(final @NotNull Locale locale);

    @Nullable Component get(final @NotNull String key, final @NotNull TagResolver @NotNull ... resolvers);

    default @Nullable Component get(final @NotNull String key, final @NotNull UnaryOperator<Component> postProcessor, final @NotNull TagResolver @NotNull ... resolvers) {
        final var got = get(key, resolvers);
        if (got == null) {
            return null;
        } else {
            return postProcessor.apply(got);
        }
    }

    default @Nullable List<Component> getMany(final @NotNull String key, final @NotNull TagResolver @NotNull ... resolvers) {
        final var got = get(key, resolvers);
        if (got == null) {
            return null;
        } else {
            return Collections.singletonList(got);
        }
    }

    default @Nullable List<Component> getMany(final @NotNull String key, final @NotNull UnaryOperator<Component> postProcessor, final @NotNull TagResolver @NotNull ... resolvers) {
        final var got = getMany(key, resolvers);
        if (got == null) {
            return null;
        } else {
            got.replaceAll(postProcessor);
            return got;
        }
    }

    default @NotNull List<Component> getManyOrDefaultToKey(final @NotNull String key, final @NotNull TagResolver @NotNull ... resolvers) {
        final var got = getMany(key, resolvers);
        return got != null ? got : Collections.singletonList(Component.text(key));
    }

    default @NotNull List<Component> getManyOrDefaultToKey(final @NotNull String key, final @NotNull UnaryOperator<Component> postProcessor, final @NotNull TagResolver @NotNull ... resolvers) {
        final var got = getMany(key, postProcessor, resolvers);
        return got != null ? got : Collections.singletonList(Component.text(key));
    }

    default @NotNull Component getOrDefaultToKey(final @NotNull String key, final @NotNull TagResolver @NotNull ... resolvers) {
        final var got = get(key, resolvers);
        return got != null ? got : Component.text(key);
    }

    default @NotNull Component getOrDefaultToKey(final @NotNull String key, final @NotNull UnaryOperator<Component> postProcessor, final @NotNull TagResolver @NotNull ... resolvers) {
        final var got = get(key, postProcessor, resolvers);
        return got != null ? got : Component.text(key);
    }

    default void send(final @NotNull Audience audience, final @NotNull String key, final @NotNull TagResolver @NotNull ... resolvers) {
        requireNonNull(audience, "audience");
        requireNonNull(key, "key");
        final var component = get(key, resolvers);
        if (component != null) {
            audience.sendMessage(component);
        }
    }

    static @NotNull TranslationManager create(final @NotNull Plugin plugin) {
        return new TranslationManagerImpl(plugin);
    }

}
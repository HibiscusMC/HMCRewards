package com.hibiscusmc.hmcrewards.command;

import me.fixeddev.commandflow.Namespace;
import me.fixeddev.commandflow.translator.TranslationProvider;
import me.fixeddev.commandflow.translator.Translator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public final class MiniMessageTranslator implements Translator {
    public static @NotNull Translator miniMessage(final @NotNull Formatter formatter) {
        return new MiniMessageTranslator(formatter);
    }

    public static @NotNull Translator miniMessage(final @NotNull TranslationProvider translationProvider) {
        requireNonNull(translationProvider, "translationProvider");
        return new MiniMessageTranslator((namespace, key, resolvers) -> {
            final var translation = translationProvider.getTranslation(namespace, key);
            if (translation == null) {
                // no translation found, will fall back to translation key
                return null;
            } else {
                return MiniMessage.miniMessage().deserialize(translation, resolvers);
            }
        });
    }

    private final Formatter formatter;

    private MiniMessageTranslator(final @NotNull Formatter formatter) {
        this.formatter = requireNonNull(formatter, "formatter");
    }

    @Override
    public @NotNull Component translate(@NotNull Component component, final @NotNull Namespace namespace) {
        requireNonNull(component, "component");
        requireNonNull(namespace, "namespace");

        if (component instanceof TranslatableComponent translatableComponent) {
            return _translate(translatableComponent, namespace);
        }

        // translate embedded components
        if (!component.children().isEmpty()) {
            final var children = new ArrayList<Component>(component.children().size());
            for (final var child : component.children()) {
                children.add(translate(child, namespace));
            }
            component = component.children(children);
        }

        return component;
    }

    private @NotNull Component _translate(final @NotNull TranslatableComponent component, final @NotNull Namespace namespace) {
        final var translationKey = component.key();

        // recursively translate arguments
        final var translationArgs = new ArrayList<>(component.args());
        translationArgs.replaceAll(c -> translate(c, namespace));

        // create tag resolver that resolves tags like <arg>, <arg:0>, <arg:1>, <arg:2>, ...
        final var argumentTagResolver = TagResolver.resolver("arg", (args, ctx) -> {
            final var index = args.hasNext() ? args.pop().asInt().orElseThrow() : 0;
            if (index < translationArgs.size()) {
                return Tag.selfClosingInserting(translationArgs.get(index));
            } else {
                throw new IllegalArgumentException("Argument index out of bounds: " + index + ". Translation key: " + translationKey);
            }
        });

        final var result = formatter.format(namespace, translationKey, argumentTagResolver);
        if (result == null) {
            // not found, fall back to translation key
            return Component.text(translationKey);
        } else{
            return result;
        }
    }

    @Override
    public void setProvider(final @NotNull TranslationProvider provider) {
        throw new UnsupportedOperationException("This translator does not support specifying a provider!");
    }

    @Override
    public void setConverterFunction(final @NotNull Function<String, TextComponent> stringToComponent) {
        throw new UnsupportedOperationException("This translator does not support specifying a converter function!");
    }

    @FunctionalInterface
    public interface Formatter {
        @Nullable Component format(
                final @NotNull Namespace namespace,
                final @NotNull String translationkey,
                final @NotNull TagResolver @NotNull ...  tagResolvers
        );
    }
}
package com.hibiscusmc.hmcrewards.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Provider;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class ConfigurationBinder {
    private final Plugin plugin;
    private final Binder binder;
    private final Map<String, YamlFileConfiguration> configurations = new HashMap<>();

    private ConfigurationBinder(final @NotNull Plugin plugin, final @NotNull Binder binder) {
        this.plugin = requireNonNull(plugin, "plugin");
        this.binder = requireNonNull(binder, "binder");

        binder.bind(ConfigurationBinder.class).toInstance(this);
    }

    @Contract("_ -> this")
    public @NotNull ConfigurationBinder bind(final @NotNull String filename) {
        final YamlFileConfiguration configuration = YamlFileConfiguration.configuration(plugin, filename);

        binder.bind(YamlFileConfiguration.class).named(filename).toInstance(configuration);
        configurations.put(filename, configuration);
        return this;
    }

    public void loadAll() {
        this.configurations.values().forEach(YamlFileConfiguration::load);
    }

    public static @NotNull ConfigurationBinder create(final @NotNull Plugin plugin, final @NotNull Binder binder) {
        return new ConfigurationBinder(plugin, binder);
    }
}

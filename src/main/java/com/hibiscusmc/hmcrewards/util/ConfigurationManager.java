package com.hibiscusmc.hmcrewards.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class ConfigurationManager {
    private final Plugin plugin;
    private final Map<String, YamlFileConfiguration> configurations = new HashMap<>();

    private ConfigurationManager(final @NotNull Plugin plugin) {
        this.plugin = requireNonNull(plugin, "plugin");
    }

    public @NotNull YamlFileConfiguration getOrCreate(final @NotNull String name) {
        return this.configurations.computeIfAbsent(name, k -> YamlFileConfiguration.configuration(plugin, k));
    }

    public void loadAll() {
        this.configurations.values().forEach(YamlFileConfiguration::load);
    }

    public static @NotNull ConfigurationManager create(final @NotNull Plugin plugin) {
        return new ConfigurationManager(plugin);
    }
}

package com.hibiscusmc.hmcrewards.item;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A simple item definition, has a material, a name,
 * and a lore.
 */
public final class ItemDefinition {
    private final String material;
    private final String name;
    private final List<String> lore;

    private ItemDefinition(final @NotNull String material, final @Nullable String name, final @NotNull List<String> lore) {
        this.material = requireNonNull(material, "material");
        this.name = name;
        this.lore = requireNonNull(lore, "lore");
    }

    public @NotNull String material() {
        return material;
    }

    public @Nullable String name() {
        return name;
    }

    public @NotNull List<String> lore() {
        return lore;
    }

    public static @NotNull ItemDefinition of(final @NotNull String material) {
        return new ItemDefinition(material, null, Collections.emptyList());
    }

    public static @NotNull ItemDefinition of(final @NotNull String material, final @Nullable String name, final @NotNull List<String> lore) {
        return new ItemDefinition(material, name, lore);
    }

    public static @NotNull ItemDefinition deserialize(final @NotNull ConfigurationSection section) throws IllegalArgumentException {
        final String material = section.getString("material", null);
        final String name = section.getString("name", null);
        final List<String> lore = section.getStringList("lore");

        if (material == null) {
            throw new IllegalArgumentException("Missing 'material' property for item definition at " + section.getCurrentPath());
        }

        return ItemDefinition.of(material, name, lore);
    }
}

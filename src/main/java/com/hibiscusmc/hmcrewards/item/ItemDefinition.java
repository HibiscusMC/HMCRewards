package com.hibiscusmc.hmcrewards.item;

import com.hibiscusmc.hmcrewards.util.GlobalMiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A simple item definition, has a material, a name,
 * and a lore.
 */
public final class ItemDefinition {
    private final String material;
    private final String name;
    private final String modelId;
    private final int amount;
    private final List<String> lore;
    private final int customModelData;
    private final boolean glowing;

    private ItemDefinition(final @NotNull String material, final @Nullable String name, final int amount, final @NotNull List<String> lore, final int customModelData, final boolean glowing, final String modelId) {
        this.material = requireNonNull(material, "material");
        this.name = name;
        this.amount = amount;
        this.lore = requireNonNull(lore, "lore");
        this.customModelData = customModelData;
        this.glowing = glowing;
        this.modelId = modelId;
    }

    public @NotNull String material() {
        return material;
    }

    public @Nullable String name() {
        return name;
    }

    public int amount() {
        return amount;
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull ItemDefinition amount(final int amount) {
        return new ItemDefinition(material, name, amount, new ArrayList<>(lore), customModelData, glowing, modelId);
    }

    public @NotNull List<String> lore() {
        return lore;
    }

    public boolean glowing() {
        return glowing;
    }

    public @NotNull ItemStack build(final @NotNull ItemMatcher itemMatcher) {
        final ItemStack item = itemMatcher.find(material, itemMatcher);
        if (item == null) {
            // this reward contains an invalid item definition
            throw new IllegalStateException("Invalid item definition, unknown material: " + material);
        }

        // set amount
        if (amount != 1) {
            item.setAmount(amount);
        }

        // set display name
        if (name != null) {
            final Component displayName = GlobalMiniMessage.deserializeForItem(name);
            item.editMeta(meta -> meta.displayName(displayName));
        }

        // set display lore
        if (!lore.isEmpty()) {
            final List<Component> displayLore = new ArrayList<>(lore.size());
            for (final String line : lore) {
                displayLore.add(GlobalMiniMessage.deserializeForItem(line));
            }
            item.editMeta(meta -> meta.lore(displayLore));
        }

        if (customModelData > 0) {
            item.editMeta(meta -> meta.setCustomModelData(customModelData));
        }

        // TODO: Add Custom Item Model support from the Item Builder in Hibiscus Commons in 0.7
        if (glowing) {
            item.editMeta(meta -> {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            });
        }

        return item;
    }

    /**
     * Determines if this item definition is similar to the
     * other item definition. Similarity determines if the
     * definitions can be stacked together, ignoring the amount.
     *
     * @param other the other item definition
     * @return true if the definitions are similar, false otherwise
     */
    public boolean isSimilar(final @NotNull ItemDefinition other) {
        return material.equals(other.material)
                && Objects.equals(name, other.name)
                && lore.equals(other.lore)
                && customModelData == other.customModelData
                && glowing == other.glowing;
    }

    /**
     * Determines if this item definition is simple and only
     * has material and amount.
     *
     * @return true if the item definition is simple, false otherwise
     */
    public boolean isSimple() {
        return name == null && lore.isEmpty() && customModelData == -1 && !glowing;
    }

    public static @NotNull ItemDefinition of(final @NotNull String material) {
        return of(material, 1);
    }

    public static @NotNull ItemDefinition of(final @NotNull String material, final int amount) {
        return of(material, null, amount, Collections.emptyList());
    }

    public static @NotNull ItemDefinition of(final @NotNull String material, final @Nullable String name, final @NotNull List<String> lore) {
        return of(material, name, 1, lore);
    }

    public static @NotNull ItemDefinition of(final @NotNull String material, final @Nullable String name, final int amount, final @NotNull List<String> lore) {
        return of(material, name, amount, lore, -1);
    }

    public static @NotNull ItemDefinition of(final @NotNull String material, final @Nullable String name, final int amount, final @NotNull List<String> lore, final int customModelData) {
        return of(material, name, amount, lore, customModelData, false);
    }

    public static @NotNull ItemDefinition of(final @NotNull String material, final @Nullable String name, final int amount, final @NotNull List<String> lore, final int customModelData, final boolean glowing) {
        return of(material, name, amount, lore, customModelData, false, null);
    }

    public static @NotNull ItemDefinition of(final @NotNull String material, final @Nullable String name, final int amount, final @NotNull List<String> lore, final int customModelData, final boolean glowing, @Nullable final String modelId) {
        return new ItemDefinition(material, name, amount, lore, customModelData, glowing, modelId);
    }

    public static @NotNull ItemDefinition deserialize(final @NotNull ConfigurationSection section) throws IllegalArgumentException {
        final String material = section.getString("material", null);
        final String name = section.getString("name", null);
        final int amount = section.getInt("amount", 1);
        final List<String> lore = section.getStringList("lore");
        final int customModelData = section.getInt("custom-model-data", -1);
        final String modelId = section.getString("model-id", null);
        final boolean glowing = section.getBoolean("glowing", false);

        if (material == null) {
            throw new IllegalArgumentException("Missing 'material' property for item definition at " + section.getCurrentPath());
        }

        return ItemDefinition.of(material, name, amount, lore, customModelData, glowing, modelId);
    }
}

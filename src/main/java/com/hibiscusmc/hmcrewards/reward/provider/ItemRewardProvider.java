package com.hibiscusmc.hmcrewards.reward.provider;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnType;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.reward.ItemReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public final class ItemRewardProvider implements RewardProvider<ItemReward>, DnCodec<ItemReward> {
    public static final String ID = "item";

    @Inject private ItemMatcher itemMatcher;

    @Override
    public @NotNull String id() {
        return ID;
    }

    @Override
    public @NotNull GiveResult give(final @NotNull Player player, final @NotNull ItemReward reward) {
        final Inventory inventory = player.getInventory();

        final int slot;
        if ((slot = inventory.firstEmpty()) == -1) {
            return GiveResult.NO_SPACE_IN_INVENTORY;
        }

        final ItemDefinition itemDefinition = reward.item();
        final ItemStack item = itemDefinition.build(itemMatcher);

        // now actually give it
        inventory.setItem(slot, item);
        return GiveResult.SUCCESS;
    }

    @Override
    public @NotNull ItemReward fromConfiguration(final @NotNull ConfigurationSection section) throws IllegalArgumentException {
        return new ItemReward(section.getName(), ItemDefinition.deserialize(section));
    }

    @Override
    public @Nullable ItemReward fromCommandLine(final @NotNull String material) throws IllegalArgumentException {
        if (itemMatcher.find(material, itemMatcher) == null) {
            return null;
        }
        return new ItemReward(material, ItemDefinition.of(material));
    }

    @Override
    public @NotNull Class<ItemReward> type() {
        return ItemReward.class;
    }

    @Override
    public void encode(final @NotNull DnWriter writer, final @NotNull ItemReward value) {
        final String reference = value.reference();
        if (reference != null) {
            // use reference instead
            writer.writeStringValue(reference);
            return;
        }

        writer.writeObjectStart();
        writer.writeStringValue("material", value.item().material());
        if (value.item().name() != null) {
            writer.writeStringValue("name", value.item().name());
        }
        if (!value.item().lore().isEmpty()) {
            writer.writeArrayStart("lore");
            for (final String line : value.item().lore()) {
                writer.writeStringValue(line);
            }
            writer.writeArrayEnd();
        }
        writer.writeObjectEnd();
    }

    @Override
    public @NotNull ItemReward decode(final @NotNull DnReader reader) {
        // User {
        //    uuid = "..."
        //    username = "..."
        //    rewards = [
        //        'my_special_item',
        //        { type: 'item', material: '...', name: '...', lore: ['...', '...'] },
        //        'my_command_1',
        //        { type: 'command', command: '...', ... }
        //    ]
        // }
        if (reader.readType() == DnType.VALUE) {
            final String reference = reader.readStringValue();
            final ItemReward reward = fromReference(reference);
            if (reward == null) {
                throw new IllegalStateException("Unknown item reward reference: " + reference);
            }
            return reward;
        }

        reader.readObjectStart();
        String material = null;
        String displayName = null;
        final List<String> lore = new ArrayList<>();

        while (reader.hasMoreValuesOrEntries()) {
            String name = reader.readName();
            switch (name) {
                case "material" -> material = reader.readStringValue();
                case "name" -> displayName = reader.readStringValue();
                case "lore" -> {
                    reader.readArrayStart();
                    while (reader.hasMoreValuesOrEntries()) {
                        lore.add(reader.readStringValue());
                    }
                    reader.readArrayEnd();
                }
                default -> reader.skipValue();
            }
        }

        if (material == null) {
            throw new IllegalArgumentException("Missing 'material' property for item definition");
        }

        reader.readObjectEnd();
        return new ItemReward(null, ItemDefinition.of(material, displayName, lore));
    }
}

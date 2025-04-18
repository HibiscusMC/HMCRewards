package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnType;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.nexomc.nexo.api.NexoItems;
import io.tofpu.vouchers.Vouchers;
import me.fixeddev.commandflow.exception.CommandUsage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ItemRewardProvider implements RewardProvider<ItemReward>, DnCodec<ItemReward> {
    public static final String ID = "item";

        @Inject private ItemMatcher itemMatcher;

    @Override
    public @NotNull String id() {
        return ID;
    }

    public @NotNull Set<String> ids() {
        Set<String> ids = new HashSet<>();
        if (Bukkit.getPluginManager().isPluginEnabled("HMCVouchers")) {
            ids.addAll(Vouchers.get().getVoucherManager().getVoucherNames()
                    .stream()
                    .map(voucher -> "hmcvouchers:" + voucher)
                    .toList());
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Nexo")) {
            ids.addAll(NexoItems.itemNames()
                    .stream()
                    .map(item -> "nexo:" + item)
                    .toList());
        }
        return ids;
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
    public @NotNull List<ItemReward> fromReference(final @NotNull String reference) throws IllegalArgumentException {
        final String[] args = reference.split(" ", 2);
        final String material = args[0];
        final int amount;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (final NumberFormatException e) {
                throw new CommandUsage(Component.translatable("reward.give.invalid_amount", Component.text(args[1])));
            }
        } else {
            amount = 1;
        }
        final ItemStack item;
        if ((item = itemMatcher.find(material, itemMatcher)) == null) {
            return List.of();
        }
        final var maxStackSize = item.getMaxStackSize();
        if (amount <= maxStackSize) {
            return List.of(new ItemReward(reference, ItemDefinition.of(material, amount)));
        } else {
            // Split the amount into multiple rewards if it exceeds the max stack size
            final var rewards = new ArrayList<ItemReward>();
            for (int i = 0; i < amount; i += maxStackSize) {
                final var remaining = amount - i;
                final var currentAmount = Math.min(remaining, maxStackSize);
                rewards.add(new ItemReward(material + " " + currentAmount, ItemDefinition.of(material, currentAmount)));
            }
            return rewards;
        }
    }

    @Override
    public @Nullable ItemReward stack(final @NotNull ItemReward a, final @NotNull ItemReward b) {
        final var aItem = a.item();
        final var bItem = b.item();

        // Different items can't be stacked
        if (!aItem.isSimilar(bItem)) {
            return null;
        }

        final var finalAmount = aItem.amount() + bItem.amount();

        // If the item couldn't be found, or the amount exceeds the max stack size, can't stack
        final var baseItem = itemMatcher.find(aItem.material(), itemMatcher);
        if (baseItem == null || finalAmount > baseItem.getMaxStackSize()) {
            return null;
        }

        if (aItem.isSimple()) {
            // So both are simple, and both can have a reference
            return new ItemReward(aItem.material() + " " + finalAmount, aItem.amount(finalAmount));
        } else {
            // They are not simple, can't have a reference
            return new ItemReward(null, aItem.amount(finalAmount));
        }
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
        if (value.item().amount() != 1) {
            writer.writeIntValue("amount", value.item().amount());
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
            final List<ItemReward> rewards = fromReference(reference);
            if (rewards.isEmpty()) {
                throw new IllegalStateException("Unknown item reward reference: " + reference);
            } else if (rewards.size() != 1) {
                // todo: warn!!
            }
            return rewards.get(0);
        }

        reader.readObjectStart();
        String material = null;
        String displayName = null;
        int amount = 1;
        final List<String> lore = new ArrayList<>();

        while (reader.hasMoreValuesOrEntries()) {
            String name = reader.readName();
            switch (name) {
                case "material" -> material = reader.readStringValue();
                case "name" -> displayName = reader.readStringValue();
                case "amount" -> amount = reader.readIntValue();
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
        return new ItemReward(null, ItemDefinition.of(material, displayName, amount, lore));
    }
}

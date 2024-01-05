package com.hibiscusmc.hmcrewards.reward.provider;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.reward.ItemReward;
import com.hibiscusmc.hmcrewards.util.GlobalMiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public final class ItemRewardProvider implements RewardProvider<ItemReward> {
    @Inject private ItemMatcher itemMatcher;

    @Override
    public @NotNull String id() {
        return "item";
    }

    @Override
    public @NotNull GiveResult give(final @NotNull Player player, final @NotNull ItemReward reward) {
        final Inventory inventory = player.getInventory();

        final int slot;
        if ((slot = inventory.firstEmpty()) == -1) {
            return GiveResult.NO_SPACE_IN_INVENTORY;
        }

        final ItemDefinition itemDefinition = reward.item();
        final ItemStack item = itemMatcher.find(itemDefinition.material(), itemMatcher);
        if (item == null) {
            // this reward contains an invalid item definition
            return GiveResult.INVALID_REWARD;
        }

        // set display name
        final String nameStr = itemDefinition.name();
        if (nameStr != null) {
            final Component displayName = GlobalMiniMessage.deserializeForItem(nameStr);
            item.editMeta(meta -> meta.displayName(displayName));
        }

        // set display lore
        final List<String> loreStr = itemDefinition.lore();
        if (!loreStr.isEmpty()) {
            final List<Component> lore = new ArrayList<>(loreStr.size());
            for (final String line : loreStr) {
                lore.add(GlobalMiniMessage.deserializeForItem(line));
            }
            item.editMeta(meta -> meta.lore(lore));
        }

        // now actually give it
        inventory.setItem(slot, item);
        return GiveResult.SUCCESS;
    }

    @Override
    public @NotNull ItemReward fromConfiguration(final @NotNull ConfigurationSection section) throws IllegalArgumentException {
        return new ItemReward(ItemDefinition.deserialize(section));
    }

    @Override
    public @Nullable ItemReward fromCommandLine(final @NotNull String material) throws IllegalArgumentException {
        if (itemMatcher.find(material, itemMatcher) == null) {
            return null;
        }
        return new ItemReward(ItemDefinition.of(material));
    }
}

package com.hibiscusmc.hmcrewards.reward.provider;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.reward.CommandReward;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.List;

public final class CommandRewardProvider implements RewardProvider<CommandReward> {
    @Inject private Plugin plugin;

    @Override
    public @NotNull String id() {
        return "command";
    }

    @Override
    public @NotNull GiveResult give(final @NotNull Player player, final @NotNull CommandReward reward) {
        // verify required space
        {
            int requiredSlots = reward.inventorySlots();
            final ItemStack[] storageContents = player.getInventory().getStorageContents();
            for (final ItemStack item : storageContents) {
                if (item == null || item.getType().isAir()) {
                    if (--requiredSlots <= 0) {
                        break;
                    }
                }
            }

            if (requiredSlots > 0) {
                // not enough space
                return GiveResult.NO_SPACE_IN_INVENTORY;
            }
        }

        // now actually run the commands
        for (final String command : reward.commands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }
        return GiveResult.SUCCESS;
    }

    @Override
    public @NotNull CommandReward fromConfiguration(final @NotNull ConfigurationSection section) throws IllegalArgumentException {
        final int requiredSpace = section.getInt("required-space", 1);
        final List<String> commands = section.getStringList("commands");
        final ItemDefinition icon;

        // deserialize "display" section
        {
            final ConfigurationSection iconSection = section.getConfigurationSection("display");
            if (iconSection == null) {
                throw new IllegalArgumentException("Missing 'display' section for command reward at " + section.getCurrentPath());
            }
            icon = ItemDefinition.deserialize(iconSection);
        }

        return new CommandReward(requiredSpace, icon, commands);
    }

    @Override
    public @Nullable CommandReward fromCommandLine(final @NotNull String string) throws IllegalArgumentException {
        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("rewards." + string);
        if (section == null) {
            return null;
        }

        if (!"command".equals(section.getString("type"))) {
            // given a different type of reward
            return null;
        }

        return fromConfiguration(section);
    }
}

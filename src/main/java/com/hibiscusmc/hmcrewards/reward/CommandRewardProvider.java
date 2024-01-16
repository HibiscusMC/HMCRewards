package com.hibiscusmc.hmcrewards.reward;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public final class CommandRewardProvider implements RewardProvider<CommandReward>, DnCodec<CommandReward> {
    public static final String ID = "command";

    @Inject private Plugin plugin;

    @Override
    public @NotNull String id() {
        return ID;
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

        return new CommandReward(section.getName(), requiredSpace, icon, commands);
    }

    @Override
    public @Nullable CommandReward fromReference(final @NotNull String string) throws IllegalArgumentException {
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

    @Override
    public @NotNull Class<CommandReward> type() {
        return CommandReward.class;
    }

    @Override
    public void encode(final @NotNull DnWriter writer, final @NotNull CommandReward value) {
        final String reference = value.reference();
        if (reference != null) {
            // just write reference instead of all the properties
            writer.writeStringValue(reference);
            return;
        }

        writer.writeObjectStart();

        writer.writeObjectEnd();
    }

    @Override
    public @NotNull CommandReward decode(final @NotNull DnReader reader) {
        reader.readObjectStart();

        final List<String> commands = new ArrayList<>();
        int inventorySlots = 1;

        while (reader.hasMoreValuesOrEntries()) {
            String name = reader.readName();
            if (name.equals("commands")) {
                reader.readArrayStart();
                while (reader.hasMoreValuesOrEntries()) {
                    commands.add(reader.readStringValue());
                }
                reader.readArrayEnd();
            } else {
                reader.skipValue();
            }
        }
        //if (command == null) {
//            throw new IllegalArgumentException("CommandReward does not contain a command");
        // }
        reader.readObjectEnd();
        //return new CommandReward(null, inventorySlots, icon, commands);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

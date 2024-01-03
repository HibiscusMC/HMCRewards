package com.hibiscusmc.hmcrewards.reward.command;

import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.reward.provider.RewardProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CommandRewardProvider implements RewardProvider {
    @Override
    public @NotNull String id() {
        return "command";
    }

    @Override
    public @NotNull Reward deserialize(final @NotNull ConfigurationSection section) throws IllegalArgumentException {
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
}

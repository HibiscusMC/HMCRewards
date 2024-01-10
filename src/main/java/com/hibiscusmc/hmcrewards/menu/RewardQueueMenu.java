package com.hibiscusmc.hmcrewards.menu;

import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.util.GlobalMiniMessage;
import com.hibiscusmc.hmcrewards.util.YamlFileConfiguration;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Named;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RewardQueueMenu {
    @Inject private Plugin plugin;
    @Inject private UserManager userManager;
    @Inject private TranslationManager translationManager;
    @Inject private ItemMatcher itemMatcher;
    @Inject private RewardProviderRegistry rewardProviderRegistry;
    @Inject @Named("menu.yml") private YamlFileConfiguration config;

    public void open(final @NotNull Player player, final int page) {
        final Gui gui = Gui.gui()
                .title(GlobalMiniMessage.deserialize(config.getString("title", "")))
                .rows(config.getInt("rows", 6))
                .disableAllInteractions()
                .create();

        if (updateRewardIcons(player, gui, page, false)) {
            gui.open(player);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean updateRewardIcons(final @NotNull Player player, final @NotNull Gui gui, final int requestedPage, final boolean update) {
        final User user = userManager.getCached(player);
        if (user == null) {
            translationManager.send(player, "user.self_not_found");
            return false;
        }

        // copy current reward list
        final List<Reward> rewards = new ArrayList<>(user.rewards());

        final int listFrom = config.getInt("list.from", 0);
        final int listTo = config.getInt("list.to", 44);

        if (listFrom >= listTo) {
            throw new IllegalStateException("list.from cannot be greater or equal than list.to");
        }

        final int maxItemCountPerPage = listTo - listFrom + 1;

        final int maxPage = (int) Math.ceil((double) rewards.size() / maxItemCountPerPage);
        final int currentPage = Math.max(1, Math.min(requestedPage, maxPage));

        final int startIndex = (currentPage - 1) * maxItemCountPerPage;
        final int endIndex = Math.min(startIndex + maxItemCountPerPage, rewards.size());
        final List<Reward> pageRewards = startIndex == endIndex ? Collections.emptyList() : rewards.subList(startIndex, endIndex);

        // fill in reward icons
        int slot = listFrom;
        for (final Reward reward : pageRewards) {
            final ItemStack icon = reward.icon().build(itemMatcher);
            final GuiItem button = ItemBuilder.from(icon)
                    .asGuiItem(event -> {
                        event.setCancelled(true);

                        // find current rewards again (up-to-date)
                        final List<Reward> currentRewards = user.rewards();
                        final int index;

                        // if current reward is present, give it to the player and remove it from the list
                        if ((index = currentRewards.indexOf(reward)) != -1) {
                            final RewardProvider provider = rewardProviderRegistry.provider(reward.type());
                            if (provider != null) {
                                // give the reward and remove
                                provider.give(player, reward);
                                currentRewards.remove(index);
                            } else {
                                // can't give reward, provider not found
                                plugin.getLogger().warning("Provider for reward type " + reward.type() + " not found.");
                            }
                        }

                        // update in current page
                        updateRewardIcons(player, gui, currentPage, true);
                    });

            if (update) {
                gui.updateItem(slot++, button);
            } else {
                gui.setItem(slot++, button);
            }
        }

        if (update) {
            // update empty spaces
            for (; slot <= listTo; slot++) {
                gui.removeItem(slot);
            }
        }


        // Fill in other buttons
        final ConfigurationSection iconsSection = config.getConfigurationSection("icons");
        if (iconsSection != null) {
            for (final String key : iconsSection.getKeys(false)) {
                if (key.equalsIgnoreCase("next-page") && currentPage >= maxPage) {
                    // skip if no next page
                    continue;
                } else if (key.equalsIgnoreCase("previous-page") && currentPage <= 1) {
                    // skip if no previous page
                    continue;
                }

                final ItemStack icon = ItemDefinition.deserialize(iconsSection).build(itemMatcher);
                final GuiItem button = ItemBuilder.from(icon)
                        .asGuiItem(switch (key.toLowerCase()) {
                            case "next-page" -> (GuiAction<InventoryClickEvent>) (event -> {
                                if (!updateRewardIcons(player, gui, currentPage + 1, true)) {
                                    player.closeInventory();
                                }
                                event.setCancelled(true);
                            });
                            case "previous-page" -> (GuiAction<InventoryClickEvent>) (event -> {
                                if (!updateRewardIcons(player, gui, currentPage - 1, true)) {
                                    player.closeInventory();
                                }
                                event.setCancelled(true);
                            });
                            default -> (GuiAction<InventoryClickEvent>) (event -> {
                                event.setCancelled(true);
                            });
                        });

                for (final int iconSlot : iconsSection.getIntegerList("slots")) {
                    if (update) {
                        gui.updateItem(iconSlot, button);
                    } else {
                        gui.setItem(iconSlot, button);
                    }
                }
            }
        }
        return true;
    }
}

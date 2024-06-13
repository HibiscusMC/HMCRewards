package com.hibiscusmc.hmcrewards.menu;

import com.hibiscusmc.hmcrewards.feedback.SoundManager;
import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.util.GlobalMiniMessage;
import com.hibiscusmc.hmcrewards.util.OptionalPlaceholderAPI;
import com.hibiscusmc.hmcrewards.util.YamlFileConfiguration;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Named;

import java.util.*;

public final class RewardQueueMenu {
    @Inject private Plugin plugin;
    @Inject private UserManager userManager;
    @Inject private UserDatastore userDatastore;
    @Inject private TranslationManager translationManager;
    @Inject private SoundManager soundManager;
    @Inject private ItemMatcher itemMatcher;
    @Inject private RewardProviderRegistry rewardProviderRegistry;
    @Inject @Named("menu.yml") private YamlFileConfiguration config;

    public void open(final @NotNull Player player, final int page) {
        open(player, null, page);
    }

    public void open(final @NotNull Player player, final @Nullable String queueOwnerName, final int page) {
        final Gui gui = Gui.gui()
                .title(GlobalMiniMessage.deserialize(OptionalPlaceholderAPI.setPlaceholders(player, config.getString("title", ""))))
                .rows(config.getInt("rows", 6))
                .disableAllInteractions()
                .create();

        if (updateRewardIcons(player, queueOwnerName, gui, page, false)) {
            gui.open(player);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean updateRewardIcons(final @NotNull Player player, final @Nullable String queueOwnerName, final @NotNull Gui gui, final int requestedPage, final boolean update) {
        final User user;
        if (queueOwnerName == null) {
            // Then it's the player's own queue
            user = userManager.getCached(player);
            if (user == null) {
                translationManager.send(player, "user.self_not_found");
                return false;
            }
        } else {
            // Then it's another player's queue
            final var owner = Bukkit.getPlayerExact(queueOwnerName);
            if (owner != null) {
                // Player is online
                user = userManager.getCached(owner);
            } else {
                // Player is offline
                user = userDatastore.findByUsername(queueOwnerName);
            }
            if (user == null) {
                translationManager.send(player, "user.not_found", Placeholder.unparsed("arg", queueOwnerName));
                return false;
            }
        }

        // copy current reward list
        final List<Reward> rewards = new ArrayList<>(user.rewards());
        final List<Integer> listSlots;

        if (config.isList("list")) {
            // list of slots to fill in
            listSlots = config.getIntegerList("list");
        } else {
            // Range of slots to fill in
            final int listFrom = config.getInt("list.from", 0);
            final int listTo = config.getInt("list.to", 44);

            if (listFrom > listTo) {
                throw new IllegalStateException("list.from cannot be greater than list.to");
            }

            listSlots = new ArrayList<>(listTo - listFrom + 1);
            for (int slot = listFrom; slot <= listTo; slot++) {
                listSlots.add(slot);
            }
        }

        final int maxItemCountPerPage = listSlots.size();

        final int maxPage = (int) Math.ceil((double) rewards.size() / maxItemCountPerPage);
        final int currentPage = Math.max(1, Math.min(requestedPage, maxPage));

        final int startIndex = (currentPage - 1) * maxItemCountPerPage;
        final int endIndex = Math.min(startIndex + maxItemCountPerPage, rewards.size());
        final List<Reward> pageRewards = startIndex == endIndex ? Collections.emptyList() : rewards.subList(startIndex, endIndex);

        // fill in reward icons
        Iterator<Integer> slotIterator = listSlots.iterator();
        int currentIndex = 0;
        for (final Reward reward : pageRewards) {
            final int rewardOriginalIndex = currentIndex;
            final ItemStack icon = reward.icon().build(itemMatcher);
            final GuiItem button = ItemBuilder.from(icon)
                    .asGuiItem(event -> {
                        event.setCancelled(true);

                        if (queueOwnerName != null) {
                            // Then it's not the player's own queue
                            return;
                        }

                        // find current rewards again (up-to-date)
                        final List<Reward> currentRewards = user.rewards();
                        final int index;
                        
                        if (currentRewards.equals(rewards)) {
                            // no changes
                            index = startIndex + rewardOriginalIndex;
                        } else {
                            // something changed, find the index
                            // (This may cause inconsistencies, by giving the first
                            //  reward with the same reference, but the behavior is
                            //  the same and the reward won't change)
                            index = currentRewards.indexOf(reward);
                        }

                        // if current reward is present, give it to the player and remove it from the list
                        if (index != -1) {
                            final RewardProvider provider = rewardProviderRegistry.provider(reward.type());
                            if (provider != null) {
                                // give the reward and remove
                                final RewardProvider.GiveResult result = provider.give(player, reward);
                                if (result == RewardProvider.GiveResult.SUCCESS) {
                                    // success giving it
                                    userManager.saveAsync(user);
                                    currentRewards.remove(index);
                                    soundManager.play(player, "reward-give");
                                } else {
                                    translationManager.send(player, "reward.give." + result.name().toLowerCase());
                                    soundManager.play(player, "reward-give-error");
                                    return;
                                }
                            } else {
                                // can't give reward, provider not found
                                plugin.getLogger().warning("Provider for reward type " + reward.type() + " not found.");
                            }
                        }

                        // update in current page
                        updateRewardIcons(player, queueOwnerName, gui, currentPage, true);
                    });

            if (update) {
                gui.updateItem(slotIterator.next(), button);
            } else {
                gui.setItem(slotIterator.next(), button);
            }
            currentIndex++;
        }

        if (update) {
            // update empty spaces
            while (slotIterator.hasNext()) {
                gui.removeItem(slotIterator.next());
            }
        }


        // Fill in other buttons
        final ConfigurationSection iconsSection = config.getConfigurationSection("icons");
        if (iconsSection != null) {
            for (final String key : iconsSection.getKeys(false)) {
                if (key.equalsIgnoreCase("bulk-claim") && queueOwnerName != null) {
                    // bulk-claim is only available for the player's own queue
                    continue;
                }

                final ConfigurationSection iconSection = iconsSection.getConfigurationSection(key);

                // should never be null
                assert iconSection != null;

                final var iconSlots = iconSection.getIntegerList("slots");

                final ItemStack icon = ItemDefinition.deserialize(iconSection).build(itemMatcher);
                final GuiItem button = ItemBuilder.from(icon)
                        .asGuiItem(switch (key.toLowerCase()) {
                            case "bulk-claim" -> (GuiAction<InventoryClickEvent>) (event -> {
                                final List<Reward> currentRewards = user.rewards();
                                if (currentRewards.isEmpty()) {
                                    translationManager.send(player, "reward.bulk_claim.no_rewards");
                                    soundManager.play(player, "reward-bulk-claim-error");
                                    return;
                                }

                                // give all rewards
                                boolean anyGiven = false;
                                final ListIterator<Reward> rewardIterator = currentRewards.listIterator(currentRewards.size());
                                while (rewardIterator.hasPrevious()) {
                                    final Reward reward = rewardIterator.previous();
                                    final RewardProvider provider = rewardProviderRegistry.provider(reward.type());
                                    if (provider == null) {
                                        // provider not found, invalid?
                                        plugin.getLogger().warning("Provider for reward type " + reward.type() + " not found.");
                                        continue;
                                    }

                                    // give the reward and remove
                                    final RewardProvider.GiveResult result = provider.give(player, reward);
                                    if (result == RewardProvider.GiveResult.SUCCESS) {
                                        // success giving it
                                        soundManager.play(player, "reward-give");
                                        rewardIterator.remove();
                                        anyGiven = true;
                                    }
                                }

                                if (anyGiven) {
                                    userManager.saveAsync(user);
                                    translationManager.send(player, "reward.bulk_claim.claimed");
                                    soundManager.play(player, "reward-bulk-claim");
                                    player.closeInventory();
                                } else {
                                    translationManager.send(player, "reward.bulk_claim.cant_claim");
                                    soundManager.play(player, "reward-bulk-claim-error");
                                    return;
                                }

                                // update gui
                                if (!updateRewardIcons(player, queueOwnerName, gui, currentPage, true)) {
                                    player.closeInventory();
                                }
                                event.setCancelled(true);
                            });
                            case "next-page" -> (GuiAction<InventoryClickEvent>) (event -> {
                                if (currentPage + 1 > maxPage) {
                                    event.setCancelled(true);
                                    return;
                                }
                                if (!updateRewardIcons(player, queueOwnerName, gui, currentPage + 1, true)) {
                                    player.closeInventory();
                                }
                                event.setCancelled(true);
                            });
                            case "previous-page" -> (GuiAction<InventoryClickEvent>) (event -> {
                                if (currentPage - 1 < 1) {
                                    event.setCancelled(true);
                                    return;
                                }
                                if (!updateRewardIcons(player, queueOwnerName, gui, currentPage - 1, true)) {
                                    player.closeInventory();
                                }
                                event.setCancelled(true);
                            });
                            default -> (GuiAction<InventoryClickEvent>) (event -> {
                                event.setCancelled(true);
                            });
                        });

                for (final int iconSlot : iconSlots) {
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

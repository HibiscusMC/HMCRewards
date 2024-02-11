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
import com.hibiscusmc.hmcrewards.util.GlobalMiniMessage;
import com.hibiscusmc.hmcrewards.util.YamlFileConfiguration;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Named;

import java.util.*;

public final class RewardQueueMenu {
    @Inject private Plugin plugin;
    @Inject private UserManager userManager;
    @Inject private TranslationManager translationManager;
    @Inject private SoundManager soundManager;
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

    private @NotNull ItemStack buildUnavailableRewardItem(final @NotNull String ref) {
        // should never happen
        final ItemStack item = new ItemStack(Material.BARRIER);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Unavailable Reward: " + ref).color(NamedTextColor.RED));
            meta.lore(List.of(
                    Component.text("This reward is unavailable."),
                    Component.text("Contact the server administrator to fix this.")
            ));
        });
        return item;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean updateRewardIcons(final @NotNull Player player, final @NotNull Gui gui, final int requestedPage, final boolean update) {
        final User user = userManager.getCached(player);
        if (user == null) {
            translationManager.send(player, "user.self_not_found");
            return false;
        }

        // copy current reward list
        final List<String> rewards = new ArrayList<>(user.rewards());

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
        final List<String> pageRewards = startIndex == endIndex ? Collections.emptyList() : rewards.subList(startIndex, endIndex);

        // fill in reward icons
        int slot = listFrom;
        int currentIndex = 0;
        for (final String rewardReference : pageRewards) {
            final int rewardOriginalIndex = currentIndex;
            final Reward reward = rewardProviderRegistry.findByReference(rewardReference);
            final ItemStack icon = reward == null ? buildUnavailableRewardItem(rewardReference) : reward.icon().build(itemMatcher);
            final GuiItem button = ItemBuilder.from(icon)
                    .asGuiItem(event -> {
                        event.setCancelled(true);

                        if (reward == null) {
                            return;
                        }

                        // find current rewards again (up-to-date)
                        final List<String> currentRewards = user.rewards();
                        final int index;
                        
                        if (currentRewards.equals(rewards)) {
                            // no changes
                            index = startIndex + rewardOriginalIndex;
                        } else {
                            // something changed, find the index
                            // (This may cause inconsistencies, by giving the first
                            //  reward with the same reference, but the behavior is
                            //  the same and the reward won't change)
                            index = currentRewards.indexOf(rewardReference);
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
                        updateRewardIcons(player, gui, currentPage, true);
                    });

            if (update) {
                gui.updateItem(slot++, button);
            } else {
                gui.setItem(slot++, button);
            }
            currentIndex++;
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
                final ConfigurationSection iconSection = iconsSection.getConfigurationSection(key);

                // should never be null
                assert iconSection != null;

                final var iconSlots = iconSection.getIntegerList("slots");

                final ItemStack icon = ItemDefinition.deserialize(iconSection).build(itemMatcher);
                final GuiItem button = ItemBuilder.from(icon)
                        .asGuiItem(switch (key.toLowerCase()) {
                            case "bulk-claim" -> (GuiAction<InventoryClickEvent>) (event -> {
                                final List<String> currentRewards = user.rewards();
                                if (currentRewards.isEmpty()) {
                                    translationManager.send(player, "reward.bulk_claim.no_rewards");
                                    soundManager.play(player, "reward-bulk-claim-error");
                                    return;
                                }

                                // give all rewards
                                boolean anyGiven = false;
                                final ListIterator<String> rewardIterator = currentRewards.listIterator(currentRewards.size());
                                while (rewardIterator.hasPrevious()) {
                                    final String rewardReference = rewardIterator.previous();
                                    final Reward reward = rewardProviderRegistry.findByReference(rewardReference);

                                    if (reward == null) {
                                        // reward not found, invalid?
                                        plugin.getLogger().warning("Reward not found: " + rewardReference + " was tried to be given to " + player.getName() + ".");
                                        continue;
                                    }

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
                                if (!updateRewardIcons(player, gui, currentPage, true)) {
                                    player.closeInventory();
                                }
                                event.setCancelled(true);
                            });
                            case "next-page" -> (GuiAction<InventoryClickEvent>) (event -> {
                                if (currentPage + 1 > maxPage) {
                                    event.setCancelled(true);
                                    return;
                                }
                                if (!updateRewardIcons(player, gui, currentPage + 1, true)) {
                                    player.closeInventory();
                                }
                                event.setCancelled(true);
                            });
                            case "previous-page" -> (GuiAction<InventoryClickEvent>) (event -> {
                                if (currentPage - 1 < 1) {
                                    event.setCancelled(true);
                                    return;
                                }
                                if (!updateRewardIcons(player, gui, currentPage - 1, true)) {
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

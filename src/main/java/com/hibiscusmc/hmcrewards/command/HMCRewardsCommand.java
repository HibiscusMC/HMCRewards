package com.hibiscusmc.hmcrewards.command;

import com.hibiscusmc.hmcrewards.adapt.ToastSender;
import com.hibiscusmc.hmcrewards.command.arg.PlayerSelector;
import com.hibiscusmc.hmcrewards.command.arg.RewardRef;
import com.hibiscusmc.hmcrewards.feedback.SoundManager;
import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.menu.RewardQueueMenu;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.util.ConfigurationBinder;
import com.hibiscusmc.hmcrewards.util.YamlFileConfiguration;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Switch;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Named;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Command(names = { "hmcrewards", "rewards" }, permission = "hmcrewards.command.hmcrewards")
public final class HMCRewardsCommand implements CommandClass {
    @Inject private Plugin plugin;
    @Inject private UserManager userManager;
    @Inject private UserDatastore userDatastore;
    @Inject private TranslationManager translationManager;
    @Inject private SoundManager soundManager;
    @Inject private ConfigurationBinder configurationBinder;
    @Inject private RewardQueueMenu rewardQueueMenu;
    @Inject private ItemMatcher itemMatcher;
    @Inject private ToastSender toastSender;
    @Inject @Named("config.yml") private YamlFileConfiguration config;

    @Command(names = "queue", permission = "hmcrewards.command.queue")
    @SuppressWarnings("rawtypes")
    public void queue(final @NotNull CommandSender sender, final @Switch(value = "f") boolean tryForceGive, final @PlayerSelector String targetName, final @NotNull RewardProvider provider, final @NotNull @RewardRef String arg) {
        final List<Reward> rewards;

        if ((rewards = provider.fromReference(arg)).isEmpty()) {
            translationManager.send(sender, "reward.invalid", Placeholder.component("arg", Component.text(arg)));
            return;
        }

        final var icon = rewards.get(0).icon().build(itemMatcher);
        final Component rewardDisplayName;
        if (icon.hasItemMeta()) {
            final var meta = icon.getItemMeta();
            if (meta.hasDisplayName()) {
                rewardDisplayName = meta.displayName();
                assert rewardDisplayName != null;
            } else {
                rewardDisplayName = Component.translatable(icon);
            }
        } else {
            rewardDisplayName = Component.translatable(icon);
        }
        final var showToast = config.getBoolean("toasts.enabled");

        final var targets = new HashSet<Player>();
        if (targetName.equalsIgnoreCase("@a") || targetName.equalsIgnoreCase("@e")) {
            // For all online players
            targets.addAll(Bukkit.getOnlinePlayers());
        } else {
            final Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                // queue offline
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    final User user = userDatastore.findByUsername(targetName);
                    if (user == null) {
                        translationManager.send(sender, "user.not_found", Placeholder.component("arg", Component.text(targetName)));
                        return;
                    }
                    addStacking(user.rewards(), rewards, provider);
                    userDatastore.save(user);
                    translationManager.send(sender, "reward.queued", Placeholder.component("arg", Component.text(targetName)));
                });
                return;
            } else {
                // Single target
                targets.add(target);
            }
        }

        for (final var target : targets) {
            final User user = userManager.getCached(target);
            if (user == null) {
                translationManager.send(sender, "user.not_found", Placeholder.component("arg", target.displayName()));
                continue;
            }

            List<Reward> rewardsToQueue = rewards;

            if (tryForceGive) {
                // Try give if '-f' flag specified, if not, queue as always
                // Make mutable:
                rewardsToQueue = new ArrayList<>(rewardsToQueue);

                final var it = rewardsToQueue.iterator();
                while (it.hasNext()) {
                    //noinspection unchecked
                    final var result = provider.give(target, it.next());

                    if (result != RewardProvider.GiveResult.SUCCESS) {
                        // Couldn't give this one!
                        break;
                    }

                    // Given, remove from rewards to queue
                    it.remove();
                }
                if (!it.hasNext()) {
                    // All rewards were given
                    continue;
                }
            }

            if (!user.hasReceivedRewardsBefore()) {
                translationManager.send(target, "notification.on_first_reward");
                user.hasReceivedRewardsBefore(true);
            }

            if (showToast) {
                toastSender.showToast(target, icon, translationManager.getOrDefaultToKey("notification.toast",
                        Placeholder.component("reward_display_name", rewardDisplayName)));
            }

            addStacking(user.rewards(), rewardsToQueue, provider);
            userManager.saveAsync(user);
        }

        if (targets.size() == 1) {
            final var target = targets.iterator().next();
            translationManager.send(sender, "reward.queued", Placeholder.component("arg", target.displayName()));
        } else {
            translationManager.send(sender, "reward.queued_multiple",
                    Placeholder.component("players", Component.text(targets.size())));
        }
    }

    private void addStacking(final @NotNull List<Reward> rewards, final @NotNull List<Reward> additions, final @NotNull RewardProvider provider) {
        for (final var addition : additions) {
            boolean stacked = false;
            for (int i = 0; i < rewards.size(); i++) {
                final var existing = rewards.get(i);

                if (!existing.type().equals(provider.id())) {
                    // Can't be stacked, different types!
                    continue;
                }


                final var combined = provider.stack(existing, addition);
                if (combined != null) {
                    rewards.set(i, combined);
                    stacked = true;
                    break;
                }
            }

            if (!stacked) {
                rewards.add(addition);
            }
        }
    }

    @Command(names = { "", "menu" }, permission = "hmcrewards.command.menu")
    public void menu(final @NotNull CommandSender sender, final @OptArg @Nullable Player target) {
        if (target != null && target != sender && !sender.hasPermission("hmcrewards.command.menu.others")) {
            translationManager.send(sender, "menu.no_permission_others");
            return;
        }

        if (target != null) {
            rewardQueueMenu.open(target, 1);
        } else {
            if (!(sender instanceof Player)) {
                translationManager.send(sender, "menu.self_console");
                return;
            }

            rewardQueueMenu.open((Player) sender, 1);
        }
    }

    @Command(names = "reload", permission = "hmcrewards.command.reload")
    public void reload(final @NotNull CommandSender sender) {
        final long start = System.currentTimeMillis();
        translationManager.loadFormats();
        soundManager.loadSounds();
        configurationBinder.loadAll();
        final long took = System.currentTimeMillis() - start;
        translationManager.send(sender, "reload.success", Placeholder.component("arg", Component.text(took)));
    }
}

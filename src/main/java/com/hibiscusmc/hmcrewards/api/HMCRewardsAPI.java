package com.hibiscusmc.hmcrewards.api;

import com.hibiscusmc.hmcrewards.item.ItemMatcher;
import com.hibiscusmc.hmcrewards.item.ItemDefinition;
import com.hibiscusmc.hmcrewards.reward.ItemReward;
import com.hibiscusmc.hmcrewards.reward.ItemRewardProvider;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.reward.RewardStacker;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public final class HMCRewardsAPI {
    private static HMCRewardsAPI instance;

    private final UserManager userManager;
    private final RewardProviderRegistry rewardProviderRegistry;
    private final UserDatastore userDatastore;
    private final ItemMatcher matcher;
    private final Plugin plugin;

    public HMCRewardsAPI(
            final @NotNull UserManager userManager,
            final @NotNull RewardProviderRegistry rewardProviderRegistry,
            final @NotNull UserDatastore userDatastore,
            final @NotNull ItemMatcher matcher,
            final @NotNull Plugin plugin
    ) {
        HMCRewardsAPI.instance = this;
        this.userManager = requireNonNull(userManager, "userManager");
        this.rewardProviderRegistry = requireNonNull(rewardProviderRegistry, "rewardProviderRegistry");
        this.userDatastore = requireNonNull(userDatastore, "userDatastore");
        this.matcher = requireNonNull(matcher, "matcher");
        this.plugin = requireNonNull(plugin, "plugin");
    }

    public static HMCRewardsAPI getInstance() {
        return instance;
    }

    public @NotNull UserManager userManager() {
        return userManager;
    }

    public @NotNull RewardProviderRegistry rewardProviderRegistry() {
        return rewardProviderRegistry;
    }

    public @NotNull ItemMatcher matcher() {
        return matcher;
    }

    /**
     * @deprecated use {@link #queueRewards(UUID, String, Collection)}.
     */
    @Deprecated(forRemoval = false)
    public @NotNull CompletableFuture<Void> giveRewards(
            final @NotNull UUID uuid,
            final @NotNull String name,
            final @NotNull Collection<? extends Reward> rewards
    ) {
        return queueRewards(uuid, name, rewards);
    }

    public @NotNull CompletableFuture<Void> queueRewards(
            final @NotNull UUID uuid,
            final @NotNull String name,
            final @NotNull Collection<? extends Reward> rewards
    ) {
        requireNonNull(uuid, "uuid");
        requireNonNull(name, "name");
        requireNonNull(rewards, "rewards");

        final ArrayList<Reward> additions = new ArrayList<>(rewards.size());
        try {
            for (final Reward reward : rewards) {
                requireNonNull(reward, "reward");
                validatePersistable(reward);
                additions.add(reward);
            }
        } catch (final RuntimeException exception) {
            return CompletableFuture.failedFuture(exception);
        }

        final CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(plugin, () -> queueOnMainThread(uuid, name, additions, future));
        return future;
    }

    public @NotNull CompletableFuture<Void> queueItemStacks(
            final @NotNull UUID uuid,
            final @NotNull String name,
            final @NotNull Collection<? extends ItemStack> items
    ) {
        requireNonNull(uuid, "uuid");
        requireNonNull(name, "name");
        requireNonNull(items, "items");

        final ArrayList<ItemReward> rewards = new ArrayList<>(items.size());
        try {
            for (final ItemStack item : items) {
                requireNonNull(item, "item");
                rewards.add(new ItemReward(null, ItemDefinition.ofItemStack(item)));
            }
        } catch (final RuntimeException exception) {
            return CompletableFuture.failedFuture(exception);
        }

        return queueRewards(uuid, name, rewards);
    }

    public @NotNull CompletableFuture<Void> queueItemStacks(
            final @NotNull Player player,
            final @NotNull Collection<? extends ItemStack> items
    ) {
        requireNonNull(player, "player");
        return queueItemStacks(player.getUniqueId(), player.getName(), items);
    }

    private void queueOnMainThread(
            final @NotNull UUID uuid,
            final @NotNull String name,
            final @NotNull Collection<? extends Reward> additions,
            final @NotNull CompletableFuture<Void> future
    ) {
        try {
            final User cached = userManager.getCached(uuid);
            if (cached != null) {
                cached.name(name);
                RewardStacker.addStacking(cached.rewards(), additions, rewardProviderRegistry);
                userManager.update(cached);
                userManager.saveAsync(cached).whenComplete((ignored, throwable) -> {
                    if (throwable != null) {
                        future.completeExceptionally(throwable);
                    } else {
                        future.complete(null);
                    }
                });
                return;
            }
        } catch (final Exception exception) {
            future.completeExceptionally(exception);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> queueOffline(uuid, name, additions, future));
    }

    private void queueOffline(
            final @NotNull UUID uuid,
            final @NotNull String name,
            final @NotNull Collection<? extends Reward> additions,
            final @NotNull CompletableFuture<Void> future
    ) {
        try {
            User user = userDatastore.findByUuid(uuid);
            if (user == null) {
                user = User.user(uuid, name);
            } else {
                user.name(name);
            }

            RewardStacker.addStacking(user.rewards(), additions, rewardProviderRegistry);
            userDatastore.save(user);

            final User queuedUser = user;
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (Bukkit.getPlayer(uuid) != null || userManager.getCached(uuid) != null) {
                    userManager.update(queuedUser);
                }
                future.complete(null);
            });
        } catch (final Exception exception) {
            future.completeExceptionally(exception);
        }
    }

    private void validatePersistable(final @NotNull Reward reward) {
        final RewardProvider<?> provider = rewardProviderRegistry.provider(reward.type());
        if (provider == null) {
            throw new IllegalArgumentException("Reward provider not found: " + reward.type());
        }

        if (reward.reference() != null) {
            return;
        }

        if (provider instanceof ItemRewardProvider itemRewardProvider && itemRewardProvider.type().isInstance(reward)) {
            return;
        }

        throw new IllegalArgumentException("Reward type '" + reward.type() + "' does not support non-reference persistence.");
    }
}

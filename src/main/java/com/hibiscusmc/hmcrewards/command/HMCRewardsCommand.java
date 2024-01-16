package com.hibiscusmc.hmcrewards.command;

import com.hibiscusmc.hmcrewards.feedback.SoundManager;
import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.menu.RewardQueueMenu;
import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.util.ConfigurationBinder;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

@Command(names = "hmcrewards", permission = "hmcrewards.command.hmcrewards")
public final class HMCRewardsCommand implements CommandClass {
    @Inject private Plugin plugin;
    @Inject private UserManager userManager;
    @Inject private UserDatastore userDatastore;
    @Inject private TranslationManager translationManager;
    @Inject private SoundManager soundManager;
    @Inject private ConfigurationBinder configurationBinder;
    @Inject private RewardQueueMenu rewardQueueMenu;

    @Command(names = "queue", permission = "hmcrewards.command.queue")
    @SuppressWarnings("rawtypes")
    public void queue(final @NotNull CommandSender sender, final @NotNull String targetName, final @NotNull RewardProvider provider, final @NotNull @Text String arg) {
        final Player target = Bukkit.getPlayerExact(targetName);

        if (provider.fromReference(arg) == null) {
            translationManager.send(sender, "reward.invalid", Placeholder.component("arg", Component.text(arg)));
            return;
        }

        if (target == null) {
            // queue offline?
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                final User user = userDatastore.findByUsername(targetName);
                if (user == null) {
                    translationManager.send(sender, "user.not_found", Placeholder.component("arg", Component.text(targetName)));
                    return;
                }
                user.rewards().add(arg);
                userDatastore.save(user);
                translationManager.send(sender, "reward.queued", Placeholder.component("arg", Component.text(targetName)));
            });
            return;
        }

        final User user = userManager.getCached(target);
        if (user == null) {
            translationManager.send(sender, "user.not_found", Placeholder.component("arg", target.displayName()));
            return;
        }

        if (!user.hasReceivedRewardsBefore()) {
            translationManager.send(sender, "notification.on_first_reward");
            user.hasReceivedRewardsBefore(true);
        }

        user.rewards().add(arg);
        userManager.saveAsync(user);
        translationManager.send(sender, "reward.queued", Placeholder.component("arg", target.displayName()));
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

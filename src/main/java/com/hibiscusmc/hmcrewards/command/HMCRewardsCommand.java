package com.hibiscusmc.hmcrewards.command;

import com.hibiscusmc.hmcrewards.feedback.SoundManager;
import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.reward.Reward;
import com.hibiscusmc.hmcrewards.reward.provider.RewardProvider;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.exception.CommandUsage;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;

@Command(names = "hmcrewards", permission = "hmcrewards.command.hmcrewards")
public final class HMCRewardsCommand implements CommandClass {
    @Inject private Plugin plugin;
    @Inject private UserManager userManager;
    @Inject private TranslationManager translationManager;
    @Inject private SoundManager soundManager;

    @Command(names = "queue", permission = "hmcrewards.command.queue")
    public <T extends Reward> void queue(final @NotNull CommandSender sender, final @NotNull Player target, final @NotNull RewardProvider<T> provider, final @NotNull String arg) {
        final User user = userManager.getCached(target);
        if (user == null) {
            throw new CommandUsage(Component.translatable("user.not_found", target.displayName()));
        }

        final Reward reward = provider.fromCommandLine(arg);
        if (reward == null) {
            throw new CommandUsage(Component.translatable("reward.invalid", Component.text(arg)));
        }

        user.rewards().add(reward);
        throw new CommandUsage(Component.translatable("reward.queued", target.displayName()));
    }

    @Command(names = "reload", permission = "hmcrewards.command.reload")
    public void reload() {
        final long start = System.currentTimeMillis();
        translationManager.loadFormats();
        soundManager.loadSounds();
        final long took = System.currentTimeMillis() - start;
        throw new CommandUsage(Component.translatable("reload.success", Component.text(took)));
    }
}

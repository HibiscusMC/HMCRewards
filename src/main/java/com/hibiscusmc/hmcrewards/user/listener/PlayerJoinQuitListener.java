package com.hibiscusmc.hmcrewards.user.listener;

import com.hibiscusmc.hmcrewards.api.event.UserLoadEvent;
import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;

public final class PlayerJoinQuitListener implements Listener {
    @Inject private Plugin plugin;
    @Inject private UserDatastore userDatastore;
    @Inject private UserManager userManager;
    @Inject private TranslationManager translationManager;

    @EventHandler
    public void onJoin(final @NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // execute datastore query asynchronously
            User user = userDatastore.findByUuid(player.getUniqueId());

            if (!player.isOnline()) {
                // if player quit before query finished,
                // just don't do anything
                return;
            }

            if (user == null) {
                // initial user creation
                user = User.user(player.getUniqueId(), player.getName());
            } else {
                // update username
                final var oldName = user.name();
                final var newName = player.getName();
                if (!oldName.equals(newName)) {
                    plugin.getLogger().info("Updating username for " + user.uuid() + " from " + oldName + " to " + newName);
                    user.name(newName);
                }
            }

            // cache user data
            userManager.update(user);

            UserLoadEvent load = new UserLoadEvent(player, user);
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(load));

            // send unclaimed rewards notification
            if (!user.rewards().isEmpty()) {
                translationManager.send(player, "notification.unclaimed_rewards",
                        Placeholder.component("amount", Component.text(user.rewards().size())));
            }
        });
    }

    @EventHandler
    public void onQuit(final @NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final User user = userManager.getCached(player);

        if (user != null) {
            // save user data
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                userDatastore.save(user);
                userManager.update(user);
            });

            // remove user from cache
            userManager.removeCached(user);
        }
    }
}

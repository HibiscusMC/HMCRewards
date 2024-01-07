package com.hibiscusmc.hmcrewards.user.listener;

import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.UserManager;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
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
            }

            // cache user data
            userManager.update(user);
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

package com.hibiscusmc.hmcrewards.api.event;

import com.hibiscusmc.hmcrewards.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class UserLoadEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final User user;

    public UserLoadEvent(@NotNull Player who, User user) {
        super(who);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
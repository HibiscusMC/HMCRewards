package com.hibiscusmc.hmcrewards.util;

import me.fixeddev.commandflow.annotated.CommandClass;
import org.bukkit.event.Listener;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Binder;

public abstract class BukkitAbstractModule extends AbstractModule {
    protected final Binder.CollectionMultiBindingBuilder<Service> bindServices() {
        return multibind(Service.class).asSet();
    }

    protected final Binder.CollectionMultiBindingBuilder<Listener> bindListeners() {
        return multibind(Listener.class).asSet();
    }

    protected final Binder.CollectionMultiBindingBuilder<CommandClass> bindCommands() {
        return multibind(CommandClass.class).asSet();
    }
}
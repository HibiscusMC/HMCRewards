package com.hibiscusmc.hmcrewards.command;

import com.hibiscusmc.hmcrewards.util.BukkitAbstractModule;

public final class CommandModule extends BukkitAbstractModule {
    @Override
    protected void configure() {
        bindServices().to(CommandService.class);
    }
}

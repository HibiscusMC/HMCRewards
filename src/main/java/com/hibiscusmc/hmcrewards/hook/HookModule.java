package com.hibiscusmc.hmcrewards.hook;

import com.hibiscusmc.hmcrewards.hook.placeholderapi.PlaceholderAPIHook;
import com.hibiscusmc.hmcrewards.util.BukkitAbstractModule;

public final class HookModule extends BukkitAbstractModule {
    @Override
    protected void configure() {
        bindServices().to(PlaceholderAPIHook.class);
    }
}

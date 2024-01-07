package com.hibiscusmc.hmcrewards.item;

import com.hibiscusmc.hmcrewards.util.BukkitAbstractModule;

public final class ItemModule extends BukkitAbstractModule {
    @Override
    protected void configure() {
        bind(ItemMatcher.class).toInstance(ItemMatcher.hibiscusCommons());
    }
}

package com.hibiscusmc.hmcrewards.adapt;

import team.unnamed.inject.AbstractModule;

public final class AdaptModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ToastSender.class).toProvider(ToastSenderProvider.class).singleton();
    }
}

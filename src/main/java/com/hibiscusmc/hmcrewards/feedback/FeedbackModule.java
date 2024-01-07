package com.hibiscusmc.hmcrewards.feedback;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;
import team.unnamed.inject.Singleton;

public final class FeedbackModule extends AbstractModule {
    @Provides
    @Singleton
    public TranslationManager translationManager(final @NotNull Plugin plugin) {
        return new TranslationManagerImpl(plugin);
    }

    @Provides
    @Singleton
    public SoundManager soundManager(final @NotNull Plugin plugin) {
        return SoundManager.create(plugin);
    }
}
package com.hibiscusmc.hmcrewards.feedback;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A sound manager. Eases the work of playing configurable
 * sounds to players and other audiences.
 */
public interface SoundManager {
    /**
     * Loads all the sounds from the internal configuration
     * source. (Or re-loads if already loaded)
     */
    void loadSounds();

    /**
     * Plays the specified sound to the given audience.
     *
     * @param audience The audience to play the sound to
     * @param soundId The sound id to play
     */
    void play(final @NotNull Audience audience, final @NotNull String soundId);

    /**
     * Plays the specified sound to the given audience,
     * at the given location.
     *
     * @param audience The audience to play the sound to
     * @param soundId The sound id to play
     * @param at The location to play the sound at
     */
    void play(final @NotNull Audience audience, final @NotNull String soundId, final @NotNull Location at);

    /**
     * Creates a new {@link SoundManager} that takes the sounds
     * from the given plugin configuration.
     *
     * @param plugin The plugin to take the sounds from
     * @return The created sound manager
     */
    static @NotNull SoundManager create(final @NotNull Plugin plugin) {
        return new PluginSoundManager(plugin);
    }
}
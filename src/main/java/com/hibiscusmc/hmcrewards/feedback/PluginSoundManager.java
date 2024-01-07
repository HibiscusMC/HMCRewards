package com.hibiscusmc.hmcrewards.feedback;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class PluginSoundManager implements SoundManager {
    private final Plugin plugin;
    private final Map<String, Sound> sounds = new HashMap<>();

    PluginSoundManager(final @NotNull Plugin plugin) {
        this.plugin = requireNonNull(plugin, "plugin");
        loadSounds();
    }

    @Override
    public void loadSounds() {
        sounds.clear();
        final var soundSection = plugin.getConfig().getConfigurationSection("sounds");
        if (soundSection == null) {
            return;
        }
        for (final var soundId : soundSection.getKeys(false)) {
            final var soundString = soundSection.getString(soundId);
            final var sound = parseSound(soundString);
            sounds.put(soundId, sound);
        }
    }

    @Override
    public void play(final @NotNull Audience audience, final @NotNull String soundId) {
        requireNonNull(audience, "audience");
        requireNonNull(soundId, "soundId");
        final var sound = sounds.get(soundId);
        if (sound != null) {
            audience.playSound(sound);
        }
    }

    @Override
    public void play(final @NotNull Audience audience, final @NotNull String soundId, final @NotNull Location at) {
        requireNonNull(audience, "audience");
        requireNonNull(soundId, "soundId");
        requireNonNull(at, "at");
        final var sound = sounds.get(soundId);
        if (sound != null) {
            audience.playSound(sound, at.x(), at.y(), at.z());
        }
    }

    /**
     * Deserializes a sound from a string.
     *
     * @param string The string to deserialize from
     * @return The deserialized sound
     */
    private static @NotNull Sound parseSound(final @NotNull String string) {
        // deserialize from a string like <type> [volume] [pitch]
        // e.g. "minecraft:entity.experience_orb.pickup 1.0 1.0"
        // or "minecraft:entity.experience_orb.pickup"
        // or "minecraft:entity.experience_orb.pickup 1.0"
        // or "minecraft:entity.experience_orb.pickup 1.0 0.5"
        requireNonNull(string, "string");
        final var args = string.split(" ");
        @Subst("minecraft:entity.experience_orb.pickup")
        final var type = args[0];
        final var volume = args.length > 1 ? Float.parseFloat(args[1]) : 1.0F;
        final var pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1.0F;
        return Sound.sound(Key.key(type), Sound.Source.MASTER, volume, pitch);
    }
}
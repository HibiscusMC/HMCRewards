package com.hibiscusmc.hmcrewards.adapt;

import com.hibiscusmc.hmcrewards.util.ReflectiveToastSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Provider;

import java.util.logging.Level;

public final class ToastSenderProvider implements Provider<ToastSender> {
    @Inject private Plugin plugin;

    @Override
    public ToastSender get() {
        final var version = Bukkit.getMinecraftVersion();
        if (version.contains("1.21")) {
            return findForVersion("1_21_R1");
        } else if (version.contains("1.20.5") || version.contains("1.20.6")) {
            return findForVersion("1_20_R4");
        } else if (version.contains("1.20.1")) {
            return findForVersion("1_20_R1");
        } else {
            return new ReflectiveToastSender();
        }
    }

    private @NotNull ToastSender findForVersion(final @NotNull String versionId) {
        try {
            return (ToastSender) Class.forName("com.hibiscusmc.hmcrewards.v1_21_R1.ToastSender_v1_20_R1").getDeclaredConstructor(Plugin.class).newInstance(plugin);
        } catch (final Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't create Toast sender for version " + versionId + ". Falling back to no-op implementation", e);
            return (player, icon, title) -> { /* no-op */ };
        }
    }
}

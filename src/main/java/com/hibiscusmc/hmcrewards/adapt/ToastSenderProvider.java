package com.hibiscusmc.hmcrewards.adapt;

import com.hibiscusmc.hmcrewards.util.MinecraftReflect;
import com.hibiscusmc.hmcrewards.util.ReflectiveToastSender;
import com.hibiscusmc.hmcrewards.v1_20_R1.ToastSender_v1_20_R1;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Provider;

public final class ToastSenderProvider implements Provider<ToastSender> {
    @Inject private Plugin plugin;

    @Override
    public ToastSender get() {
        if (MinecraftReflect.findNullable("net.minecraft.advancements.AdvancementHolder") == null) {
            return new ToastSender_v1_20_R1(plugin);
        } else {
            return new ReflectiveToastSender();
        }
    }
}

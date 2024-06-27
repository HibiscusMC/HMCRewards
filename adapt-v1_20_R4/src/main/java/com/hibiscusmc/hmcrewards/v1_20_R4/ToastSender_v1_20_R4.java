package com.hibiscusmc.hmcrewards.v1_20_R4;

import com.google.gson.JsonObject;
import com.hibiscusmc.hmcrewards.adapt.ToastSender;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static com.hibiscusmc.hmcrewards.adapt.util.Expressions.let;
import static java.util.Objects.requireNonNull;

public final class ToastSender_v1_20_R4 implements ToastSender {
    private final Plugin plugin;

    public ToastSender_v1_20_R4(final @NotNull Plugin plugin) {
        this.plugin = requireNonNull(plugin, "plugin");
    }

    @Override
    public void showToast(final @NotNull Player player, final @NotNull ItemStack icon, final @NotNull Component title) {
        final var key = new ResourceLocation("hmcrewards", UUID.randomUUID().toString());
        final var json = let(new JsonObject(), o -> {
            o.add("criteria", let(new JsonObject(), criteria -> {
                criteria.add("impossible", let(new JsonObject(), impossible ->
                        impossible.addProperty("trigger", "minecraft:impossible")));
            }));
            o.add("display", let(new JsonObject(), display -> {
                display.add("icon", let(new JsonObject(), iconObj -> {
                    iconObj.addProperty("id", icon.getType().getKey().toString());
                    if (icon.hasItemMeta()) {
                        final var meta = icon.getItemMeta();
                        iconObj.add("components", let(new JsonObject(), components -> {
                            if (!meta.getEnchants().isEmpty()) {
                                components.addProperty("minecraft:enchantment_glint_override", true);
                            }
                            if (meta.hasCustomModelData()) {
                                components.addProperty("minecraft:custom_model_data", meta.getCustomModelData());
                            }
                        }));
                    }
                }));
                display.add("title", GsonComponentSerializer.gson().serializeToTree(title));
                display.addProperty("description", "HMCRewards Toast Description");
                display.addProperty("frame", "task");
                display.addProperty("announce_to_chat", false);
                display.addProperty("show_toast", true);
                display.addProperty("hidden", true);
            }));
        });

        final var advancement = Advancement.CODEC.parse(MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE), json);
        final var advancementHolder = new AdvancementHolder(key, advancement.result().orElseThrow());

        final var nmsPlayer = ((CraftPlayer) player).getHandle();
        final var progress = nmsPlayer.getAdvancements().getOrStartProgress(advancementHolder);
        MinecraftServer.getServer().getAdvancements().tree().addAll(Set.of(advancementHolder));
        progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().award(advancementHolder, criteria));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().revoke(advancementHolder, criteria));
            MinecraftServer.getServer().getAdvancements().tree().remove(Set.of(key));
        }, 2L);
    }
}

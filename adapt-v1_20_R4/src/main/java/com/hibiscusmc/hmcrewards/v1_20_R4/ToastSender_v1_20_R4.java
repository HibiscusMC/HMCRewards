package com.hibiscusmc.hmcrewards.v1_20_R4;

import com.google.gson.JsonObject;
import com.hibiscusmc.hmcrewards.adapt.ToastSender;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

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
                    iconObj.addProperty("item", icon.getType().getKey().toString());

                    final var binaryTag = CompoundBinaryTag.builder();
                    if (icon.hasItemMeta()) {
                        final var meta = icon.getItemMeta();
                        if (!meta.getEnchants().isEmpty()) {
                            // if it has at least one enchantment, add
                            // an enchantment to the nbt so the item is
                            // shiny in the toast
                            binaryTag.put("Enchantments", ListBinaryTag.builder(BinaryTagTypes.COMPOUND)
                                    .add(CompoundBinaryTag.builder()
                                            .putString("id", "aqua_affinity")
                                            .putInt("lvl", 1)
                                            .build())
                                    .build());
                        }

                        if (meta.hasCustomModelData()) {
                            binaryTag.putInt("CustomModelData", meta.getCustomModelData());
                        }
                    }
                    final var built = binaryTag.build();
                    if (!built.keySet().isEmpty()) {
                        String nbtString;
                        try {
                            nbtString = TagStringIO.get().asString(built);
                        } catch (final IOException e) {
                            throw new IllegalStateException("Couldn't serialize Item NBT", e);
                        }

                        iconObj.addProperty("nbt", nbtString);
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
        progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().award(advancementHolder, criteria));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().revoke(advancementHolder, criteria));
        }, 2L);
    }
}

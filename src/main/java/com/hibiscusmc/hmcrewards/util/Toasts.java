package com.hibiscusmc.hmcrewards.util;

import com.google.gson.JsonObject;
import com.hibiscusmc.hmcrewards.HMCRewardsPlugin;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.reflectionremapper.ReflectionRemapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

// Toasts 1.20.2+
public final class Toasts {
    private static final ReflectionRemapper REMAPPER = ReflectionRemapper.forReobfMappingsInPaperJar();

    // MinecraftServer
    private static final Class<?> MINECRAFT_SERVER_CLASS = MinecraftReflect.find("net.minecraft.server.MinecraftServer");

    // MinecraftServer.getServer()
    private static final Method MINECRAFT_SERVER_GET_SERVER_METHOD = MinecraftReflect.getMethod(MINECRAFT_SERVER_CLASS, "getServer");

    // ResourceLocation
    private static final Class<?> RESOURCE_LOCATION_CLASS = MinecraftReflect.find("net.minecraft.resources.MinecraftKey", "net.minecraft.resources.ResourceLocation");
    // ResourceLocation(namespace, key)
    private static final Constructor<?> RESOURCE_LOCATION_CONSTRUCTOR = MinecraftReflect.getConstructor(RESOURCE_LOCATION_CLASS, String.class, String.class);

    // Advancement
    private static final Class<?> ADVANCEMENT_CLASS = MinecraftReflect.find("net.minecraft.advancements.Advancement");

    // PredicateManager
    private static final Class<?> PREDICATE_MANAGER_CLASS = MinecraftReflect.find("net.minecraft.world.level.storage.loot.LootPredicateManager", "net.minecraft.world.level.storage.loot.PredicateManager", "net.minecraft.world.level.storage.loot.LootDataManager");

    // MinecraftServer.getPredicateManager()
    private static final Method MINECRAFT_SERVER_GET_PREDICATE_MANAGER_METHOD = MinecraftReflect.findMethod(MINECRAFT_SERVER_CLASS, PREDICATE_MANAGER_CLASS);

    // LootDeserializationContext
    private static final Class<?> LOOT_DESERIALIZATION_CONTEXT_CLASS = MinecraftReflect.find("net.minecraft.advancements.critereon.LootDeserializationContext", "net.minecraft.advancements.critereon.DeserializationContext");

    // LootDeserializationContext(ResourceLocation, PredicateManager)
    private static final Constructor<?> LOOT_DESERIALIZATION_CONTEXT_CONSTRUCTOR = MinecraftReflect.getConstructor(LOOT_DESERIALIZATION_CONTEXT_CLASS, RESOURCE_LOCATION_CLASS, PREDICATE_MANAGER_CLASS);

    // Advancement.fromJson(JsonObject, LootDeserializationContext)
    private static final Method ADVANCEMENT_FROM_JSON_METHOD = MinecraftReflect.findMethod(ADVANCEMENT_CLASS, ADVANCEMENT_CLASS, JsonObject.class,  LOOT_DESERIALIZATION_CONTEXT_CLASS);

    // AdvancementHolder
    private static final Class<?> ADVANCEMENT_HOLDER_CLASS = MinecraftReflect.find("net.minecraft.advancements.AdvancementHolder");

    // AdvancementHolder(ResourceLocation, Advancement)
    private static final Constructor<?> ADVANCEMENT_HOLDER_CONSTRUCTOR = MinecraftReflect.getConstructor(
            ADVANCEMENT_HOLDER_CLASS,
            RESOURCE_LOCATION_CLASS,
            MinecraftReflect.find("net.minecraft.advancements.Advancement") // Advancement
    );

    // ServerPlayer
    private static final Class<?> SERVER_PLAYER_CLASS = MinecraftReflect.find("net.minecraft.server.level.EntityPlayer", "net.minecraft.server.level.ServerPlayer");

    // CraftPlayer
    private static final Class<?> CRAFT_PLAYER_CLASS = MinecraftReflect.craftBukkitClass("entity.CraftPlayer");

    // CraftPlayer.getHandle()
    private static final Method CRAFT_PLAYER_GET_HANDLE_METHOD = MinecraftReflect.getMethod(CRAFT_PLAYER_CLASS, "getHandle");

    // AdvancementTree
    private static final Class<?> ADVANCEMENT_TREE_CLASS = MinecraftReflect.find("net.minecraft.advancements.AdvancementTree");

    // AdvancementTree.addAll(Collection)
    private static final Method ADVANCEMENT_TREE_ADD_ALL_METHOD = MinecraftReflect.getMethod(ADVANCEMENT_TREE_CLASS, REMAPPER.remapMethodName(ADVANCEMENT_TREE_CLASS, "addAll", Collection.class), Collection.class);

    // AdvancementTree.remove(Set)
    private static final Method ADVANCEMENT_TREE_REMOVE_METHOD = MinecraftReflect.getMethod(ADVANCEMENT_TREE_CLASS, REMAPPER.remapMethodName(ADVANCEMENT_TREE_CLASS, "remove", Set.class), Set.class);

    // ServerAdvancementManager
    private static final Class<?> SERVER_ADVANCEMENT_MANAGER_CLASS = MinecraftReflect.find("net.minecraft.server.ServerAdvancementManager", "net.minecraft.server.AdvancementDataWorld");

    // MinecraftServer.getAdvancements()
    private static final Method MINECRAFT_SERVER_GET_ADVANCEMENTS_METHOD = MinecraftReflect.findMethod(MINECRAFT_SERVER_CLASS, SERVER_ADVANCEMENT_MANAGER_CLASS);

    // ServerAdvancementManager.tree()
    private static final Method SERVER_ADVANCEMENT_MANAGER_TREE_METHOD = MinecraftReflect.findMethod(SERVER_ADVANCEMENT_MANAGER_CLASS, ADVANCEMENT_TREE_CLASS);

    private static final Class<?> ADVANCEMENT_PROGRESS_CLASS = MinecraftReflect.find("net.minecraft.advancements.AdvancementProgress");

    private static final Class<?> PLAYER_ADVANCEMENTS_CLASS = MinecraftReflect.find("net.minecraft.server.AdvancementDataPlayer", "net.minecraft.server.PlayerAdvancements");

    private static final Method SERVER_PLAYER_GET_ADVANCEMENTS_METHOD = MinecraftReflect.findMethod(SERVER_PLAYER_CLASS, PLAYER_ADVANCEMENTS_CLASS);

    private static final Method PLAYER_ADVANCEMENTS_GET_PROGRESS_METHOD = MinecraftReflect.findMethod(PLAYER_ADVANCEMENTS_CLASS, ADVANCEMENT_PROGRESS_CLASS, ADVANCEMENT_HOLDER_CLASS);

    private static final Method PLAYER_ADVANCEMENTS_AWARD_METHOD = MinecraftReflect.getMethod(PLAYER_ADVANCEMENTS_CLASS, REMAPPER.remapMethodName(PLAYER_ADVANCEMENTS_CLASS, "award", ADVANCEMENT_HOLDER_CLASS, String.class), ADVANCEMENT_HOLDER_CLASS, String.class);
    private static final Method PLAYER_ADVANCEMENTS_REVOKE_METHOD = MinecraftReflect.getMethod(PLAYER_ADVANCEMENTS_CLASS, REMAPPER.remapMethodName(PLAYER_ADVANCEMENTS_CLASS, "revoke", ADVANCEMENT_HOLDER_CLASS, String.class), ADVANCEMENT_HOLDER_CLASS, String.class);
    private static final Method ADVANCEMENT_PROGRESS_GET_REMAINING_CRITERIA_METHOD = MinecraftReflect.getMethod(ADVANCEMENT_PROGRESS_CLASS, REMAPPER.remapMethodName(ADVANCEMENT_PROGRESS_CLASS, "getRemainingCriteria"));
    private static final Method ADVANCEMENT_PROGRESS_GET_COMPLETED_CRITERIA_METHOD = MinecraftReflect.getMethod(ADVANCEMENT_PROGRESS_CLASS, REMAPPER.remapMethodName(ADVANCEMENT_PROGRESS_CLASS, "getCompletedCriteria"));

    public static void showToast(final @NotNull Player player, final @NotNull ItemStack icon, final @NotNull Component title) {
        try {
            showToast0(player, icon, title);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Could not show toast", e);
        }
    }

    private static <T> T let(final @NotNull T t, final @NotNull Consumer<T> configure) {
        configure.accept(t);
        return t;
    }

    private static void showToast0(final @NotNull Player player, final @NotNull ItemStack icon, final @NotNull Component title) throws ReflectiveOperationException {
        // Prepare
        // final var resourceLocation = new ResourceLocation("hmcrewards", ...);
        final var resourceLocation = RESOURCE_LOCATION_CONSTRUCTOR.newInstance("hmcrewards", UUID.randomUUID().toString());
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

        // MinecraftServer.getServer()
        final var minecraftServer = MINECRAFT_SERVER_GET_SERVER_METHOD.invoke(null);

        // final var advancement = new AdvancementHolder(resourceLocation, Advancement.fromJson(
        //     json,
        //     new LootDeserializationContext(resourceLocation, minecraftServer.getPredicateManager())
        // ));
        final var advancement = ADVANCEMENT_HOLDER_CONSTRUCTOR.newInstance(resourceLocation, ADVANCEMENT_FROM_JSON_METHOD.invoke(null, json, LOOT_DESERIALIZATION_CONTEXT_CONSTRUCTOR.newInstance(resourceLocation, MINECRAFT_SERVER_GET_PREDICATE_MANAGER_METHOD.invoke(minecraftServer))));

        // ----- Display ----
        // minecraftServer.getAdvancements().tree().addAll(Collections.singleton(advancement));
        ADVANCEMENT_TREE_ADD_ALL_METHOD.invoke(SERVER_ADVANCEMENT_MANAGER_TREE_METHOD.invoke(MINECRAFT_SERVER_GET_ADVANCEMENTS_METHOD.invoke(minecraftServer)), Collections.singleton(advancement));

        // Grant
        final var serverPlayer = CRAFT_PLAYER_GET_HANDLE_METHOD.invoke(player);
        final var playerAdvancements = SERVER_PLAYER_GET_ADVANCEMENTS_METHOD.invoke(serverPlayer);
        final var advancementProgress = PLAYER_ADVANCEMENTS_GET_PROGRESS_METHOD.invoke(playerAdvancements, advancement);

        for (final var it : (Iterable<?>) ADVANCEMENT_PROGRESS_GET_REMAINING_CRITERIA_METHOD.invoke(advancementProgress)) {
            PLAYER_ADVANCEMENTS_AWARD_METHOD.invoke(playerAdvancements, advancement, it);
        }

        final var plugin = HMCRewardsPlugin.getPlugin(HMCRewardsPlugin.class);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Revoke
            try {
                for (final var it : (Iterable<?>) ADVANCEMENT_PROGRESS_GET_COMPLETED_CRITERIA_METHOD.invoke(advancementProgress)) {
                    PLAYER_ADVANCEMENTS_REVOKE_METHOD.invoke(playerAdvancements, advancement, it);
                }

                // Remove
                ADVANCEMENT_TREE_REMOVE_METHOD.invoke(SERVER_ADVANCEMENT_MANAGER_TREE_METHOD.invoke(MINECRAFT_SERVER_GET_ADVANCEMENTS_METHOD.invoke(minecraftServer)), Collections.singleton(resourceLocation));
            } catch (final ReflectiveOperationException e) {
            }
            //    }
            //    AdvancementTree_remove!!(ServerAdvancementManager_tree(MinecraftServer_getAdvancements(MinecraftServer_getServer())), mutableSetOf(id))
        }, 2L);
    }
}

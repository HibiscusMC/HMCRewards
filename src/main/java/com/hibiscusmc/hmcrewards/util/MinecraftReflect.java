package com.hibiscusmc.hmcrewards.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class MinecraftReflect {
    private static final String CRAFT_BUKKIT_IMPL = Bukkit.getServer().getClass().getName().split("\\.")[3];

    public static @NotNull Class<?> find(final @NotNull String @NotNull ... names) {
        for (final var name : names) {
            try {
                return Class.forName(name);
            } catch (final ClassNotFoundException ignored) {
            }
        }
        throw new IllegalStateException("Could not find any of the classes: " + String.join(", ", names));
    }

    public static @NotNull Class<?> craftBukkitClass(final @NotNull String name) {
        return find("org.bukkit.craftbukkit." + CRAFT_BUKKIT_IMPL + "." + name);
    }

    public static @NotNull Constructor<?> getConstructor(final @NotNull Class<?> clazz, final @NotNull Class<?> @NotNull ... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException("Could not find constructor for " + clazz.getName() + " with parameter types: " + Arrays.stream(parameterTypes).map(Object::toString).collect(Collectors.joining(", ")), e);
        }
    }

    public static @NotNull Method getMethod(final @NotNull Class<?> clazz, final @NotNull String name, final @NotNull Class<?> @NotNull ... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException("Could not find method for " + clazz.getName() + " with name " + name + " and parameter types: " + Arrays.stream(parameterTypes).map(Object::toString).collect(Collectors.joining(", ")), e);
        }
    }

    public static @NotNull Method findMethod(final @NotNull Class<?> clazz, final @NotNull Class<?> returnType, final @NotNull Class<?> @NotNull ... parameterTypes) {
        for (final var method : clazz.getDeclaredMethods()) {
            if (method.getReturnType() != returnType) {
                continue;
            }
            if (method.getParameterCount() != parameterTypes.length) {
                continue;
            }
            for (int i = 0; i < parameterTypes.length; i++) {
                if (method.getParameterTypes()[i] != parameterTypes[i]) {
                    // Parameters don't match
                    continue;
                }
            }
            return method;
        }
        throw new IllegalStateException("Could not find method for " + clazz.getName() + " with return type " + returnType.getName() + " and parameter types: " + Arrays.stream(parameterTypes).map(Object::toString).collect(Collectors.joining(", ")));
    }
}

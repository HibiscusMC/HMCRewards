package com.hibiscusmc.hmcrewards.command.arg;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.Bukkit;

import java.lang.annotation.Annotation;
import java.util.*;

public final class PlayerSelectorStringArgument implements PartFactory {
    @Override
    public CommandPart createPart(String name, List<? extends Annotation> modifiers) {
        return new ArgumentPart() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public List<String> parseValue(CommandContext ctx, ArgumentStack stack, CommandPart part) throws ArgumentParseException {
                return List.of(stack.next());
            }

            @Override
            public List<String> getSuggestions(CommandContext ctx, ArgumentStack stack) {
                final var last = stack.hasNext() ? stack.next() : null;
                if (last == null) {
                    return Collections.emptyList();
                } else if (Bukkit.getPlayerExact(last) != null) {
                    return Collections.emptyList();
                } else {
                    final var names = new ArrayList<String>();
                    for (final var player : Bukkit.matchPlayer(last)) {
                        names.add(player.getName());
                    }
                    if ("@a".startsWith(last)) {
                        names.add("@a");
                    }
                    if ("@p".startsWith(last)) {
                        names.add("@p");
                    }
                    if ("@e".startsWith(last)) {
                        names.add("@e");
                    }
                    return names;
                }
            }
        };
    }
}
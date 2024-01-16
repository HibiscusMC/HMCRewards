package com.hibiscusmc.hmcrewards.command.arg;

import com.hibiscusmc.hmcrewards.reward.CommandRewardProvider;
import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

public final class RewardArgument implements PartFactory {
    @Override
    public CommandPart createPart(String name, List<? extends Annotation> modifiers) {
        // context.setObject(RewardProvider.class, "RewardProvider", provider)
        return new Part(name);
    }

    public final class Part implements ArgumentPart {
        private final String name;

        public Part(final @NotNull String name) {
            this.name = requireNonNull(name, "name");
        }

        @Override
        public List<RewardId> parseValue(CommandContext context, ArgumentStack stack, CommandPart parent) throws ArgumentParseException {
            final StringJoiner joiner = new StringJoiner(" ");
            while (stack.hasNext()) {
                joiner.add(stack.next());
            }
            return Collections.singletonList(new RewardId(joiner.toString()));
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public List<String> getSuggestions(CommandContext commandContext, ArgumentStack stack) {
            final StringJoiner joiner = new StringJoiner(" ");
            while (stack.hasNext()) {
                joiner.add(stack.next());
            }
            final String ref = joiner.toString();
            final RewardProvider provider = commandContext.getObject(RewardProvider.class, "RewardProvider");
            if (provider instanceof CommandRewardProvider commandRewardProvider) {
                final List<String> suggestions = new ArrayList<>();
                for (final String id : commandRewardProvider.ids()) {
                    if (id.startsWith(ref)) {
                        suggestions.add(id);
                    }
                }
                return suggestions;
            } else {
                return Collections.emptyList();
            }
        }
    }
}

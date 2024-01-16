package com.hibiscusmc.hmcrewards.command.arg;

import com.hibiscusmc.hmcrewards.reward.RewardProvider;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class RewardProviderArgument implements PartFactory {
    @Inject private RewardProviderRegistry rewardProviderRegistry;

    @Override
    public CommandPart createPart(String name, List<? extends Annotation> modifiers) {
        return new Part(name);
    }

    public final class Part implements ArgumentPart {
        private final String name;

        public Part(final @NotNull String name) {
            this.name = requireNonNull(name, "name");
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<RewardProvider> parseValue(final CommandContext context, final ArgumentStack stack, final CommandPart caller) throws ArgumentParseException {
            final String arg = stack.next();
            final RewardProvider provider = rewardProviderRegistry.provider(arg);
            if (provider == null) {
                return Collections.emptyList();
            } else {
                context.setObject(RewardProvider.class, "RewardProvider", provider);
                return Collections.singletonList(provider);
            }
        }

        @Override
        public List<String> getSuggestions(final CommandContext commandContext, final ArgumentStack stack) {
            if (!stack.hasNext()) {
                return Collections.emptyList();
            }

            final String arg = stack.next();
            final List<String> ids = new ArrayList<>();
            for (final RewardProvider provider : rewardProviderRegistry.providers()) {
                final String id = provider.id();
                if (id.startsWith(arg)) {
                    ids.add(id);
                    if (id.equals(arg)) {
                        commandContext.setObject(RewardProvider.class, "RewardProvider", provider);
                    }
                }
            }
            return ids;
        }
    }
}

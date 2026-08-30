package com.hibiscusmc.hmcrewards.command;

import com.hibiscusmc.hmcrewards.command.arg.HMCRewardsCommandModule;
import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.util.Service;
import team.unnamed.commandflow.CommandManager;
import team.unnamed.commandflow.annotated.AnnotatedCommandTreeBuilder;
import team.unnamed.commandflow.annotated.CommandClass;
import team.unnamed.commandflow.annotated.part.PartInjector;
import team.unnamed.commandflow.annotated.part.defaults.DefaultsModule;
import team.unnamed.commandflow.bukkit.BukkitMapCommandManager;
import team.unnamed.commandflow.bukkit.factory.BukkitModule;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.Set;

public final class CommandService implements Service {
    @Inject private Plugin plugin;
    @Inject private Set<CommandClass> commandClasses;
    @Inject private Injector injector;
    @Inject private TranslationManager translationManager;
    @Inject private HMCRewardsCommandModule commandModule;

    @Override
    public void start() {
        final CommandManager manager = new BukkitMapCommandManager(plugin);
        manager.setTranslator(MiniMessageTranslator.miniMessage((namespace, key, resolvers) -> {
            // convert from kebab-case to snake_case and add the command prefix
            key = key.replace('-', '_');
            if (!key.startsWith("command.")) {
                key = "command." + key;
            }
            return translationManager.get(key, resolvers);
        }));

        final PartInjector partInjector = PartInjector.create();
        partInjector.install(new DefaultsModule());
        partInjector.install(new BukkitModule());
        partInjector.install(commandModule);

        final AnnotatedCommandTreeBuilder builder = AnnotatedCommandTreeBuilder.create(
                partInjector,
                (clazz, parent) -> injector.getInstance(clazz)
        );

        // register!
        for (final CommandClass commandClass : commandClasses) {
            manager.registerCommands(builder.fromClass(commandClass));
        }
    }
}

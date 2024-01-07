package com.hibiscusmc.hmcrewards.command;

import com.hibiscusmc.hmcrewards.feedback.TranslationManager;
import com.hibiscusmc.hmcrewards.util.Service;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.builder.AnnotatedCommandBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.Set;

public final class CommandService implements Service {
    @Inject private Plugin plugin;
    @Inject private Set<CommandClass> commandClasses;
    @Inject private Injector injector;
    @Inject private TranslationManager translationManager;

    @Override
    public void start() {
        final CommandManager manager = new BukkitCommandManager(plugin.getName());
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

        final AnnotatedCommandTreeBuilder builder = AnnotatedCommandTreeBuilder.create(
                new AnnotatedCommandBuilderImpl(partInjector),
                (clazz, parent) -> injector.getInstance(clazz)
        );

        // register!
        for (final CommandClass commandClass : commandClasses) {
            manager.registerCommands(builder.fromClass(commandClass));
        }
    }
}

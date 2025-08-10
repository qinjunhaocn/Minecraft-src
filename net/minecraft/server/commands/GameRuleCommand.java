/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;

public class GameRuleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$0, CommandBuildContext $$1) {
        final LiteralArgumentBuilder $$2 = (LiteralArgumentBuilder)Commands.literal("gamerule").requires(Commands.hasPermission(2));
        new GameRules($$1.enabledFeatures()).visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){

            @Override
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> $$0, GameRules.Type<T> $$12) {
                LiteralArgumentBuilder<CommandSourceStack> $$22 = Commands.literal($$0.getId());
                $$2.then(((LiteralArgumentBuilder)$$22.executes($$1 -> GameRuleCommand.queryRule((CommandSourceStack)$$1.getSource(), $$0))).then($$12.createArgument("value").executes($$1 -> GameRuleCommand.setRule((CommandContext<CommandSourceStack>)$$1, $$0))));
            }
        });
        $$0.register($$2);
    }

    static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> $$0, GameRules.Key<T> $$1) {
        CommandSourceStack $$2 = (CommandSourceStack)$$0.getSource();
        Object $$3 = $$2.getServer().getGameRules().getRule($$1);
        ((GameRules.Value)$$3).setFromArgument($$0, "value");
        $$2.sendSuccess(() -> Component.a("commands.gamerule.set", new Object[]{$$1.getId(), $$3.toString()}), true);
        return ((GameRules.Value)$$3).getCommandResult();
    }

    static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack $$0, GameRules.Key<T> $$1) {
        Object $$2 = $$0.getServer().getGameRules().getRule($$1);
        $$0.sendSuccess(() -> Component.a("commands.gamerule.query", new Object[]{$$1.getId(), $$2.toString()}), false);
        return ((GameRules.Value)$$2).getCommandResult();
    }
}


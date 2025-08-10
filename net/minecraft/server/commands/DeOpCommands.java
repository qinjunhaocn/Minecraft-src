/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;

public class DeOpCommands {
    private static final SimpleCommandExceptionType ERROR_NOT_OP = new SimpleCommandExceptionType((Message)Component.translatable("commands.deop.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deop").requires(Commands.hasPermission(3))).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests(($$0, $$1) -> SharedSuggestionProvider.a(((CommandSourceStack)$$0.getSource()).getServer().getPlayerList().l(), $$1)).executes($$0 -> DeOpCommands.deopPlayers((CommandSourceStack)$$0.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)$$0, "targets")))));
    }

    private static int deopPlayers(CommandSourceStack $$0, Collection<GameProfile> $$1) throws CommandSyntaxException {
        PlayerList $$2 = $$0.getServer().getPlayerList();
        int $$3 = 0;
        for (GameProfile $$4 : $$1) {
            if (!$$2.isOp($$4)) continue;
            $$2.deop($$4);
            ++$$3;
            $$0.sendSuccess(() -> Component.a("commands.deop.success", ((GameProfile)$$1.iterator().next()).getName()), true);
        }
        if ($$3 == 0) {
            throw ERROR_NOT_OP.create();
        }
        $$0.getServer().kickUnlistedPlayers($$0);
        return $$3;
    }
}


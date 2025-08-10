/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.commands;

import com.google.common.net.InetAddresses;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.IpBanList;

public class PardonIpCommand {
    private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType((Message)Component.translatable("commands.pardonip.invalid"));
    private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType((Message)Component.translatable("commands.pardonip.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon-ip").requires(Commands.hasPermission(3))).then(Commands.argument("target", StringArgumentType.word()).suggests(($$0, $$1) -> SharedSuggestionProvider.a(((CommandSourceStack)$$0.getSource()).getServer().getPlayerList().getIpBans().a(), $$1)).executes($$0 -> PardonIpCommand.unban((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"target")))));
    }

    private static int unban(CommandSourceStack $$0, String $$1) throws CommandSyntaxException {
        if (!InetAddresses.isInetAddress($$1)) {
            throw ERROR_INVALID.create();
        }
        IpBanList $$2 = $$0.getServer().getPlayerList().getIpBans();
        if (!$$2.isBanned($$1)) {
            throw ERROR_NOT_BANNED.create();
        }
        $$2.remove($$1);
        $$0.sendSuccess(() -> Component.a("commands.pardonip.success", $$1), true);
        return 1;
    }
}


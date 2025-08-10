/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class SetSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires(Commands.hasPermission(2))).executes($$0 -> SetSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), Collections.singleton(((CommandSourceStack)$$0.getSource()).getPlayerOrException()), BlockPos.containing(((CommandSourceStack)$$0.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes($$0 -> SetSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), BlockPos.containing(((CommandSourceStack)$$0.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes($$0 -> SetSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), BlockPosArgument.getSpawnablePos((CommandContext<CommandSourceStack>)$$0, "pos"), 0.0f))).then(Commands.argument("angle", AngleArgument.angle()).executes($$0 -> SetSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), BlockPosArgument.getSpawnablePos((CommandContext<CommandSourceStack>)$$0, "pos"), AngleArgument.getAngle((CommandContext<CommandSourceStack>)$$0, "angle")))))));
    }

    private static int setSpawn(CommandSourceStack $$0, Collection<ServerPlayer> $$1, BlockPos $$2, float $$3) {
        ResourceKey<Level> $$4 = $$0.getLevel().dimension();
        for (ServerPlayer $$5 : $$1) {
            $$5.setRespawnPosition(new ServerPlayer.RespawnConfig($$4, $$2, $$3, true), false);
        }
        String $$6 = $$4.location().toString();
        if ($$1.size() == 1) {
            $$0.sendSuccess(() -> Component.a("commands.spawnpoint.success.single", $$2.getX(), $$2.getY(), $$2.getZ(), Float.valueOf($$3), $$6, ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(() -> Component.a("commands.spawnpoint.success.multiple", $$2.getX(), $$2.getY(), $$2.getZ(), Float.valueOf($$3), $$6, $$1.size()), true);
        }
        return $$1.size();
    }
}


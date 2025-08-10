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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires(Commands.hasPermission(2))).executes($$0 -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), BlockPos.containing(((CommandSourceStack)$$0.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes($$0 -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), BlockPosArgument.getSpawnablePos((CommandContext<CommandSourceStack>)$$0, "pos"), 0.0f))).then(Commands.argument("angle", AngleArgument.angle()).executes($$0 -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), BlockPosArgument.getSpawnablePos((CommandContext<CommandSourceStack>)$$0, "pos"), AngleArgument.getAngle((CommandContext<CommandSourceStack>)$$0, "angle"))))));
    }

    private static int setSpawn(CommandSourceStack $$0, BlockPos $$1, float $$2) {
        ServerLevel $$3 = $$0.getLevel();
        if ($$3.dimension() != Level.OVERWORLD) {
            $$0.sendFailure(Component.translatable("commands.setworldspawn.failure.not_overworld"));
            return 0;
        }
        $$3.setDefaultSpawnPos($$1, $$2);
        $$0.sendSuccess(() -> Component.a("commands.setworldspawn.success", $$1.getX(), $$1.getY(), $$1.getZ(), Float.valueOf($$2)), true);
        return 1;
    }
}


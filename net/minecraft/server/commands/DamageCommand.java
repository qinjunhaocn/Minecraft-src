/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageCommand {
    private static final SimpleCommandExceptionType ERROR_INVULNERABLE = new SimpleCommandExceptionType((Message)Component.translatable("commands.damage.invulnerable"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("damage").requires(Commands.hasPermission(2))).then(Commands.argument("target", EntityArgument.entity()).then(((RequiredArgumentBuilder)Commands.argument("amount", FloatArgumentType.floatArg((float)0.0f)).executes($$0 -> DamageCommand.damage((CommandSourceStack)$$0.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "target"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"amount"), ((CommandSourceStack)$$0.getSource()).getLevel().damageSources().generic()))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("damageType", ResourceArgument.resource($$1, Registries.DAMAGE_TYPE)).executes($$0 -> DamageCommand.damage((CommandSourceStack)$$0.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "target"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"amount"), new DamageSource(ResourceArgument.getResource((CommandContext<CommandSourceStack>)$$0, "damageType", Registries.DAMAGE_TYPE))))).then(Commands.literal("at").then(Commands.argument("location", Vec3Argument.vec3()).executes($$0 -> DamageCommand.damage((CommandSourceStack)$$0.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "target"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"amount"), new DamageSource(ResourceArgument.getResource((CommandContext<CommandSourceStack>)$$0, "damageType", Registries.DAMAGE_TYPE), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$0, "location"))))))).then(Commands.literal("by").then(((RequiredArgumentBuilder)Commands.argument("entity", EntityArgument.entity()).executes($$0 -> DamageCommand.damage((CommandSourceStack)$$0.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "target"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"amount"), new DamageSource(ResourceArgument.getResource((CommandContext<CommandSourceStack>)$$0, "damageType", Registries.DAMAGE_TYPE), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "entity"))))).then(Commands.literal("from").then(Commands.argument("cause", EntityArgument.entity()).executes($$0 -> DamageCommand.damage((CommandSourceStack)$$0.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "target"), FloatArgumentType.getFloat((CommandContext)$$0, (String)"amount"), new DamageSource(ResourceArgument.getResource((CommandContext<CommandSourceStack>)$$0, "damageType", Registries.DAMAGE_TYPE), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "entity"), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "cause"))))))))))));
    }

    private static int damage(CommandSourceStack $$0, Entity $$1, float $$2, DamageSource $$3) throws CommandSyntaxException {
        if ($$1.hurtServer($$0.getLevel(), $$3, $$2)) {
            $$0.sendSuccess(() -> Component.a("commands.damage.success", Float.valueOf($$2), $$1.getDisplayName()), true);
            return 1;
        }
        throw ERROR_INVULNERABLE.create();
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.InCommandFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.b("commands.fill.toobig", $$0, $$1));
    static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.fill.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$03, CommandBuildContext $$1) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires(Commands.hasPermission(2))).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(FillCommand.wrapWithMode($$1, Commands.argument("block", BlockStateArgument.block($$1)), $$0 -> BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), $$0 -> BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to"), $$0 -> BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), $$0 -> null).then(((LiteralArgumentBuilder)Commands.literal("replace").executes($$0 -> FillCommand.fillBlocks((CommandSourceStack)$$0.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.REPLACE, null, false))).then(FillCommand.wrapWithMode($$1, Commands.argument("filter", BlockPredicateArgument.blockPredicate($$1)), $$0 -> BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), $$0 -> BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to"), $$0 -> BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), $$0 -> BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "filter")))).then(Commands.literal("keep").executes($$02 -> FillCommand.fillBlocks((CommandSourceStack)$$02.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$02, "block"), Mode.REPLACE, $$0 -> $$0.getLevel().isEmptyBlock($$0.getPos()), false)))))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapWithMode(CommandBuildContext $$0, ArgumentBuilder<CommandSourceStack, ?> $$1, InCommandFunction<CommandContext<CommandSourceStack>, BlockPos> $$2, InCommandFunction<CommandContext<CommandSourceStack>, BlockPos> $$3, InCommandFunction<CommandContext<CommandSourceStack>, BlockInput> $$42, NullableCommandFunction<CommandContext<CommandSourceStack>, Predicate<BlockInWorld>> $$5) {
        return $$1.executes($$4 -> FillCommand.fillBlocks((CommandSourceStack)$$4.getSource(), BoundingBox.fromCorners((Vec3i)$$2.apply($$4), (Vec3i)$$3.apply($$4)), (BlockInput)$$42.apply($$4), Mode.REPLACE, (Predicate)$$5.apply($$4), false)).then(Commands.literal("outline").executes($$4 -> FillCommand.fillBlocks((CommandSourceStack)$$4.getSource(), BoundingBox.fromCorners((Vec3i)$$2.apply($$4), (Vec3i)$$3.apply($$4)), (BlockInput)$$42.apply($$4), Mode.OUTLINE, (Predicate)$$5.apply($$4), false))).then(Commands.literal("hollow").executes($$4 -> FillCommand.fillBlocks((CommandSourceStack)$$4.getSource(), BoundingBox.fromCorners((Vec3i)$$2.apply($$4), (Vec3i)$$3.apply($$4)), (BlockInput)$$42.apply($$4), Mode.HOLLOW, (Predicate)$$5.apply($$4), false))).then(Commands.literal("destroy").executes($$4 -> FillCommand.fillBlocks((CommandSourceStack)$$4.getSource(), BoundingBox.fromCorners((Vec3i)$$2.apply($$4), (Vec3i)$$3.apply($$4)), (BlockInput)$$42.apply($$4), Mode.DESTROY, (Predicate)$$5.apply($$4), false))).then(Commands.literal("strict").executes($$4 -> FillCommand.fillBlocks((CommandSourceStack)$$4.getSource(), BoundingBox.fromCorners((Vec3i)$$2.apply($$4), (Vec3i)$$3.apply($$4)), (BlockInput)$$42.apply($$4), Mode.REPLACE, (Predicate)$$5.apply($$4), true)));
    }

    private static int fillBlocks(CommandSourceStack $$0, BoundingBox $$1, BlockInput $$2, Mode $$3, @Nullable Predicate<BlockInWorld> $$4, boolean $$5) throws CommandSyntaxException {
        int $$7;
        int $$6 = $$1.getXSpan() * $$1.getYSpan() * $$1.getZSpan();
        if ($$6 > ($$7 = $$0.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT))) {
            throw ERROR_AREA_TOO_LARGE.create((Object)$$7, (Object)$$6);
        }
        final class UpdatedPosition
        extends Record {
            final BlockPos pos;
            final BlockState oldState;

            UpdatedPosition(BlockPos $$0, BlockState $$1) {
                this.pos = $$0;
                this.oldState = $$1;
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{UpdatedPosition.class, "pos;oldState", "pos", "oldState"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UpdatedPosition.class, "pos;oldState", "pos", "oldState"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UpdatedPosition.class, "pos;oldState", "pos", "oldState"}, this, $$0);
            }

            public BlockPos pos() {
                return this.pos;
            }

            public BlockState oldState() {
                return this.oldState;
            }
        }
        ArrayList<UpdatedPosition> $$8 = Lists.newArrayList();
        ServerLevel $$9 = $$0.getLevel();
        if ($$9.isDebug()) {
            throw ERROR_FAILED.create();
        }
        int $$10 = 0;
        for (BlockPos $$11 : BlockPos.betweenClosed($$1.minX(), $$1.minY(), $$1.minZ(), $$1.maxX(), $$1.maxY(), $$1.maxZ())) {
            BlockInput $$14;
            if ($$4 != null && !$$4.test(new BlockInWorld($$9, $$11, true))) continue;
            BlockState $$12 = $$9.getBlockState($$11);
            boolean $$13 = false;
            if ($$3.affector.affect($$9, $$11)) {
                $$13 = true;
            }
            if (($$14 = $$3.filter.filter($$1, $$11, $$2, $$9)) == null) {
                if (!$$13) continue;
                ++$$10;
                continue;
            }
            if (!$$14.place($$9, $$11, 2 | ($$5 ? 816 : 256))) {
                if (!$$13) continue;
                ++$$10;
                continue;
            }
            if (!$$5) {
                $$8.add(new UpdatedPosition($$11.immutable(), $$12));
            }
            ++$$10;
        }
        for (UpdatedPosition $$15 : $$8) {
            $$9.updateNeighboursOnBlockSet($$15.pos, $$15.oldState);
        }
        if ($$10 == 0) {
            throw ERROR_FAILED.create();
        }
        int $$16 = $$10;
        $$0.sendSuccess(() -> Component.a("commands.fill.success", $$16), true);
        return $$10;
    }

    @FunctionalInterface
    static interface NullableCommandFunction<T, R> {
        @Nullable
        public R apply(T var1) throws CommandSyntaxException;
    }

    static final class Mode
    extends Enum<Mode> {
        public static final /* enum */ Mode REPLACE = new Mode(Affector.NOOP, Filter.NOOP);
        public static final /* enum */ Mode OUTLINE = new Mode(Affector.NOOP, ($$0, $$1, $$2, $$3) -> {
            if ($$1.getX() == $$0.minX() || $$1.getX() == $$0.maxX() || $$1.getY() == $$0.minY() || $$1.getY() == $$0.maxY() || $$1.getZ() == $$0.minZ() || $$1.getZ() == $$0.maxZ()) {
                return $$2;
            }
            return null;
        });
        public static final /* enum */ Mode HOLLOW = new Mode(Affector.NOOP, ($$0, $$1, $$2, $$3) -> {
            if ($$1.getX() == $$0.minX() || $$1.getX() == $$0.maxX() || $$1.getY() == $$0.minY() || $$1.getY() == $$0.maxY() || $$1.getZ() == $$0.minZ() || $$1.getZ() == $$0.maxZ()) {
                return $$2;
            }
            return HOLLOW_CORE;
        });
        public static final /* enum */ Mode DESTROY = new Mode(($$0, $$1) -> $$0.destroyBlock($$1, true), Filter.NOOP);
        public final Filter filter;
        public final Affector affector;
        private static final /* synthetic */ Mode[] $VALUES;

        public static Mode[] values() {
            return (Mode[])$VALUES.clone();
        }

        public static Mode valueOf(String $$0) {
            return Enum.valueOf(Mode.class, $$0);
        }

        private Mode(Affector $$0, Filter $$1) {
            this.affector = $$0;
            this.filter = $$1;
        }

        private static /* synthetic */ Mode[] a() {
            return new Mode[]{REPLACE, OUTLINE, HOLLOW, DESTROY};
        }

        static {
            $VALUES = Mode.a();
        }
    }

    @FunctionalInterface
    public static interface Affector {
        public static final Affector NOOP = ($$0, $$1) -> false;

        public boolean affect(ServerLevel var1, BlockPos var2);
    }

    @FunctionalInterface
    public static interface Filter {
        public static final Filter NOOP = ($$0, $$1, $$2, $$3) -> $$2;

        @Nullable
        public BlockInput filter(BoundingBox var1, BlockPos var2, BlockInput var3, ServerLevel var4);
    }
}


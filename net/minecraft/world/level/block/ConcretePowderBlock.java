/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ConcretePowderBlock
extends FallingBlock {
    public static final MapCodec<ConcretePowderBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("concrete").forGetter($$0 -> $$0.concrete), ConcretePowderBlock.propertiesCodec()).apply((Applicative)$$02, ConcretePowderBlock::new));
    private final Block concrete;

    public MapCodec<ConcretePowderBlock> codec() {
        return CODEC;
    }

    public ConcretePowderBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.concrete = $$0;
    }

    @Override
    public void onLand(Level $$0, BlockPos $$1, BlockState $$2, BlockState $$3, FallingBlockEntity $$4) {
        if (ConcretePowderBlock.shouldSolidify($$0, $$1, $$3)) {
            $$0.setBlock($$1, this.concrete.defaultBlockState(), 3);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$3;
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        if (ConcretePowderBlock.shouldSolidify($$1, $$2 = $$0.getClickedPos(), $$3 = $$1.getBlockState($$2))) {
            return this.concrete.defaultBlockState();
        }
        return super.getStateForPlacement($$0);
    }

    private static boolean shouldSolidify(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return ConcretePowderBlock.canSolidify($$2) || ConcretePowderBlock.touchesLiquid($$0, $$1);
    }

    private static boolean touchesLiquid(BlockGetter $$0, BlockPos $$1) {
        boolean $$2 = false;
        BlockPos.MutableBlockPos $$3 = $$1.mutable();
        for (Direction $$4 : Direction.values()) {
            BlockState $$5 = $$0.getBlockState($$3);
            if ($$4 == Direction.DOWN && !ConcretePowderBlock.canSolidify($$5)) continue;
            $$3.setWithOffset((Vec3i)$$1, $$4);
            $$5 = $$0.getBlockState($$3);
            if (!ConcretePowderBlock.canSolidify($$5) || $$5.isFaceSturdy($$0, $$1, $$4.getOpposite())) continue;
            $$2 = true;
            break;
        }
        return $$2;
    }

    private static boolean canSolidify(BlockState $$0) {
        return $$0.getFluidState().is(FluidTags.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if (ConcretePowderBlock.touchesLiquid($$1, $$3)) {
            return this.concrete.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public int getDustColor(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getMapColor((BlockGetter)$$1, (BlockPos)$$2).col;
    }
}


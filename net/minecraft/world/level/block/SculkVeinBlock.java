/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class SculkVeinBlock
extends MultifaceSpreadeableBlock
implements SculkBehaviour {
    public static final MapCodec<SculkVeinBlock> CODEC = SculkVeinBlock.simpleCodec(SculkVeinBlock::new);
    private final MultifaceSpreader veinSpreader = new MultifaceSpreader(new SculkVeinSpreaderConfig(this, MultifaceSpreader.DEFAULT_SPREAD_ORDER));
    private final MultifaceSpreader sameSpaceSpreader = new MultifaceSpreader(new SculkVeinSpreaderConfig(this, MultifaceSpreader.SpreadType.SAME_POSITION));

    public MapCodec<SculkVeinBlock> codec() {
        return CODEC;
    }

    public SculkVeinBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.veinSpreader;
    }

    public MultifaceSpreader getSameSpaceSpreader() {
        return this.sameSpaceSpreader;
    }

    public static boolean regrow(LevelAccessor $$0, BlockPos $$1, BlockState $$2, Collection<Direction> $$3) {
        boolean $$4 = false;
        BlockState $$5 = Blocks.SCULK_VEIN.defaultBlockState();
        for (Direction $$6 : $$3) {
            if (!SculkVeinBlock.canAttachTo($$0, $$1, $$6)) continue;
            $$5 = (BlockState)$$5.setValue(SculkVeinBlock.getFaceProperty($$6), true);
            $$4 = true;
        }
        if (!$$4) {
            return false;
        }
        if (!$$2.getFluidState().isEmpty()) {
            $$5 = (BlockState)$$5.setValue(MultifaceBlock.WATERLOGGED, true);
        }
        $$0.setBlock($$1, $$5, 3);
        return true;
    }

    @Override
    public void onDischarged(LevelAccessor $$0, BlockState $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$1.is(this)) {
            return;
        }
        for (Direction $$4 : DIRECTIONS) {
            BooleanProperty $$5 = SculkVeinBlock.getFaceProperty($$4);
            if (!$$1.getValue($$5).booleanValue() || !$$0.getBlockState($$2.relative($$4)).is(Blocks.SCULK)) continue;
            $$1 = (BlockState)$$1.setValue($$5, false);
        }
        if (!SculkVeinBlock.hasAnyFace($$1)) {
            FluidState $$6 = $$0.getFluidState($$2);
            $$1 = ($$6.isEmpty() ? Blocks.AIR : Blocks.WATER).defaultBlockState();
        }
        $$0.setBlock($$2, $$1, 3);
        SculkBehaviour.super.onDischarged($$0, $$1, $$2, $$3);
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor $$0, LevelAccessor $$1, BlockPos $$2, RandomSource $$3, SculkSpreader $$4, boolean $$5) {
        if ($$5 && this.attemptPlaceSculk($$4, $$1, $$0.getPos(), $$3)) {
            return $$0.getCharge() - 1;
        }
        return $$3.nextInt($$4.chargeDecayRate()) == 0 ? Mth.floor((float)$$0.getCharge() * 0.5f) : $$0.getCharge();
    }

    private boolean attemptPlaceSculk(SculkSpreader $$0, LevelAccessor $$1, BlockPos $$2, RandomSource $$3) {
        BlockState $$4 = $$1.getBlockState($$2);
        TagKey<Block> $$5 = $$0.replaceableBlocks();
        for (Direction $$6 : Direction.allShuffled($$3)) {
            BlockPos $$7;
            BlockState $$8;
            if (!SculkVeinBlock.hasFace($$4, $$6) || !($$8 = $$1.getBlockState($$7 = $$2.relative($$6))).is($$5)) continue;
            BlockState $$9 = Blocks.SCULK.defaultBlockState();
            $$1.setBlock($$7, $$9, 3);
            Block.pushEntitiesUp($$8, $$9, $$1, $$7);
            $$1.playSound(null, $$7, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.veinSpreader.spreadAll($$9, $$1, $$7, $$0.isWorldGeneration());
            Direction $$10 = $$6.getOpposite();
            for (Direction $$11 : DIRECTIONS) {
                BlockPos $$12;
                BlockState $$13;
                if ($$11 == $$10 || !($$13 = $$1.getBlockState($$12 = $$7.relative($$11))).is(this)) continue;
                this.onDischarged($$1, $$13, $$12, $$3);
            }
            return true;
        }
        return false;
    }

    public static boolean hasSubstrateAccess(LevelAccessor $$0, BlockState $$1, BlockPos $$2) {
        if (!$$1.is(Blocks.SCULK_VEIN)) {
            return false;
        }
        for (Direction $$3 : DIRECTIONS) {
            if (!SculkVeinBlock.hasFace($$1, $$3) || !$$0.getBlockState($$2.relative($$3)).is(BlockTags.SCULK_REPLACEABLE)) continue;
            return true;
        }
        return false;
    }

    class SculkVeinSpreaderConfig
    extends MultifaceSpreader.DefaultSpreaderConfig {
        private final MultifaceSpreader.SpreadType[] spreadTypes;

        public SculkVeinSpreaderConfig(SculkVeinBlock sculkVeinBlock, MultifaceSpreader.SpreadType ... $$0) {
            super(sculkVeinBlock);
            this.spreadTypes = $$0;
        }

        @Override
        public boolean stateCanBeReplaced(BlockGetter $$0, BlockPos $$1, BlockPos $$2, Direction $$3, BlockState $$4) {
            BlockPos $$6;
            BlockState $$5 = $$0.getBlockState($$2.relative($$3));
            if ($$5.is(Blocks.SCULK) || $$5.is(Blocks.SCULK_CATALYST) || $$5.is(Blocks.MOVING_PISTON)) {
                return false;
            }
            if ($$1.distManhattan($$2) == 2 && $$0.getBlockState($$6 = $$1.relative($$3.getOpposite())).isFaceSturdy($$0, $$6, $$3)) {
                return false;
            }
            FluidState $$7 = $$4.getFluidState();
            if (!$$7.isEmpty() && !$$7.is(Fluids.WATER)) {
                return false;
            }
            if ($$4.is(BlockTags.FIRE)) {
                return false;
            }
            return $$4.canBeReplaced() || super.stateCanBeReplaced($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        public MultifaceSpreader.SpreadType[] a() {
            return this.spreadTypes;
        }

        @Override
        public boolean isOtherBlockValidAsSource(BlockState $$0) {
            return !$$0.is(Blocks.SCULK_VEIN);
        }
    }
}


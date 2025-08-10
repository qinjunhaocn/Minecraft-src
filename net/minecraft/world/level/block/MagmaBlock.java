/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaBlock
extends Block {
    public static final MapCodec<MagmaBlock> CODEC = MagmaBlock.simpleCodec(MagmaBlock::new);
    private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

    public MapCodec<MagmaBlock> codec() {
        return CODEC;
    }

    public MagmaBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public void stepOn(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3) {
        if (!$$3.isSteppingCarefully() && $$3 instanceof LivingEntity) {
            $$3.hurt($$0.damageSources().hotFloor(), 1.0f);
        }
        super.stepOn($$0, $$1, $$2, $$3);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BubbleColumnBlock.updateColumn($$1, $$2.above(), $$0);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4 == Direction.UP && $$6.is(Blocks.WATER)) {
            $$2.scheduleTick($$3, this, 20);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        $$1.scheduleTick($$2, this, 20);
    }
}


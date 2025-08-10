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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusFlowerBlock
extends VegetationBlock {
    public static final MapCodec<CactusFlowerBlock> CODEC = CactusFlowerBlock.simpleCodec(CactusFlowerBlock::new);
    private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 12.0);

    public MapCodec<? extends CactusFlowerBlock> codec() {
        return CODEC;
    }

    public CactusFlowerBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState($$2);
        return $$3.is(Blocks.CACTUS) || $$3.is(Blocks.FARMLAND) || $$3.isFaceSturdy($$1, $$2, Direction.UP, SupportType.CENTER);
    }
}


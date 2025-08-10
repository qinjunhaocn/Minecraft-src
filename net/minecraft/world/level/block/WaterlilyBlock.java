/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterlilyBlock
extends VegetationBlock {
    public static final MapCodec<WaterlilyBlock> CODEC = WaterlilyBlock.simpleCodec(WaterlilyBlock::new);
    private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 1.5);

    public MapCodec<WaterlilyBlock> codec() {
        return CODEC;
    }

    protected WaterlilyBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        super.entityInside($$0, $$1, $$2, $$3, $$4);
        if ($$1 instanceof ServerLevel && $$3 instanceof AbstractBoat) {
            $$1.destroyBlock(new BlockPos($$2), true, $$3);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        FluidState $$3 = $$1.getFluidState($$2);
        FluidState $$4 = $$1.getFluidState($$2.above());
        return ($$3.getType() == Fluids.WATER || $$0.getBlock() instanceof IceBlock) && $$4.getType() == Fluids.EMPTY;
    }
}


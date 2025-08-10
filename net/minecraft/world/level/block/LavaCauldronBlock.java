/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LavaCauldronBlock
extends AbstractCauldronBlock {
    public static final MapCodec<LavaCauldronBlock> CODEC = LavaCauldronBlock.simpleCodec(LavaCauldronBlock::new);
    private static final VoxelShape SHAPE_INSIDE = Block.column(12.0, 4.0, 15.0);
    private static final VoxelShape FILLED_SHAPE = Shapes.or(AbstractCauldronBlock.SHAPE, SHAPE_INSIDE);

    public MapCodec<LavaCauldronBlock> codec() {
        return CODEC;
    }

    public LavaCauldronBlock(BlockBehaviour.Properties $$0) {
        super($$0, CauldronInteraction.LAVA);
    }

    @Override
    protected double getContentHeight(BlockState $$0) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState $$0) {
        return true;
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, Entity $$3) {
        return FILLED_SHAPE;
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        $$4.apply(InsideBlockEffectType.LAVA_IGNITE);
        $$4.runAfter(InsideBlockEffectType.LAVA_IGNITE, Entity::lavaHurt);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return 3;
    }
}


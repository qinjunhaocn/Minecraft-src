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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaplingBlock
extends VegetationBlock
implements BonemealableBlock {
    public static final MapCodec<SaplingBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)TreeGrower.CODEC.fieldOf("tree").forGetter($$0 -> $$0.treeGrower), SaplingBlock.propertiesCodec()).apply((Applicative)$$02, SaplingBlock::new));
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 12.0);
    protected final TreeGrower treeGrower;

    public MapCodec<? extends SaplingBlock> codec() {
        return CODEC;
    }

    protected SaplingBlock(TreeGrower $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.treeGrower = $$0;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STAGE, 0));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.getMaxLocalRawBrightness($$2.above()) >= 9 && $$3.nextInt(7) == 0) {
            this.advanceTree($$1, $$2, $$0, $$3);
        }
    }

    public void advanceTree(ServerLevel $$0, BlockPos $$1, BlockState $$2, RandomSource $$3) {
        if ($$2.getValue(STAGE) == 0) {
            $$0.setBlock($$1, (BlockState)$$2.cycle(STAGE), 260);
        } else {
            this.treeGrower.growTree($$0, $$0.getChunkSource().getGenerator(), $$1, $$2, $$3);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return (double)$$0.random.nextFloat() < 0.45;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        this.advanceTree($$0, $$2, $$3, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(STAGE);
    }
}


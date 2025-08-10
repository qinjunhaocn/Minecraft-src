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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CropBlock
extends VegetationBlock
implements BonemealableBlock {
    public static final MapCodec<CropBlock> CODEC = CropBlock.simpleCodec(CropBlock::new);
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape[] SHAPES = Block.a(7, $$0 -> Block.column(16.0, 0.0, 2 + $$0 * 2));

    public MapCodec<? extends CropBlock> codec() {
        return CODEC;
    }

    protected CropBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(this.getAgeProperty(), 0));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES[this.getAge($$0)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.is(Blocks.FARMLAND);
    }

    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    public int getAge(BlockState $$0) {
        return $$0.getValue(this.getAgeProperty());
    }

    public BlockState getStateForAge(int $$0) {
        return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), $$0);
    }

    public final boolean isMaxAge(BlockState $$0) {
        return this.getAge($$0) >= this.getMaxAge();
    }

    @Override
    protected boolean isRandomlyTicking(BlockState $$0) {
        return !this.isMaxAge($$0);
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        float $$5;
        int $$4;
        if ($$1.getRawBrightness($$2, 0) >= 9 && ($$4 = this.getAge($$0)) < this.getMaxAge() && $$3.nextInt((int)(25.0f / ($$5 = CropBlock.getGrowthSpeed(this, $$1, $$2))) + 1) == 0) {
            $$1.setBlock($$2, this.getStateForAge($$4 + 1), 2);
        }
    }

    public void growCrops(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = Math.min(this.getMaxAge(), this.getAge($$2) + this.getBonemealAgeIncrease($$0));
        $$0.setBlock($$1, this.getStateForAge($$3), 2);
    }

    protected int getBonemealAgeIncrease(Level $$0) {
        return Mth.nextInt($$0.random, 2, 5);
    }

    protected static float getGrowthSpeed(Block $$0, BlockGetter $$1, BlockPos $$2) {
        boolean $$14;
        float $$3 = 1.0f;
        BlockPos $$4 = $$2.below();
        for (int $$5 = -1; $$5 <= 1; ++$$5) {
            for (int $$6 = -1; $$6 <= 1; ++$$6) {
                float $$7 = 0.0f;
                BlockState $$8 = $$1.getBlockState($$4.offset($$5, 0, $$6));
                if ($$8.is(Blocks.FARMLAND)) {
                    $$7 = 1.0f;
                    if ($$8.getValue(FarmBlock.MOISTURE) > 0) {
                        $$7 = 3.0f;
                    }
                }
                if ($$5 != 0 || $$6 != 0) {
                    $$7 /= 4.0f;
                }
                $$3 += $$7;
            }
        }
        BlockPos $$9 = $$2.north();
        BlockPos $$10 = $$2.south();
        BlockPos $$11 = $$2.west();
        BlockPos $$12 = $$2.east();
        boolean $$13 = $$1.getBlockState($$11).is($$0) || $$1.getBlockState($$12).is($$0);
        boolean bl = $$14 = $$1.getBlockState($$9).is($$0) || $$1.getBlockState($$10).is($$0);
        if ($$13 && $$14) {
            $$3 /= 2.0f;
        } else {
            boolean $$15;
            boolean bl2 = $$15 = $$1.getBlockState($$11.north()).is($$0) || $$1.getBlockState($$12.north()).is($$0) || $$1.getBlockState($$12.south()).is($$0) || $$1.getBlockState($$11.south()).is($$0);
            if ($$15) {
                $$3 /= 2.0f;
            }
        }
        return $$3;
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return CropBlock.hasSufficientLight($$1, $$2) && super.canSurvive($$0, $$1, $$2);
    }

    protected static boolean hasSufficientLight(LevelReader $$0, BlockPos $$1) {
        return $$0.getRawBrightness($$1, 0) >= 8;
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$1 instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)$$1;
            if ($$3 instanceof Ravager && $$5.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                $$5.destroyBlock($$2, true, $$3);
            }
        }
        super.entityInside($$0, $$1, $$2, $$3, $$4);
    }

    protected ItemLike getBaseSeedId() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return new ItemStack(this.getBaseSeedId());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return !this.isMaxAge($$2);
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        this.growCrops($$0, $$2, $$3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AGE);
    }
}


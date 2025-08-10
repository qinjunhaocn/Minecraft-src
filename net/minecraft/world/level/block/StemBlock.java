/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock
extends VegetationBlock
implements BonemealableBlock {
    public static final MapCodec<StemBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter($$0 -> $$0.fruit), (App)ResourceKey.codec(Registries.BLOCK).fieldOf("attached_stem").forGetter($$0 -> $$0.attachedStem), (App)ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter($$0 -> $$0.seed), StemBlock.propertiesCodec()).apply((Applicative)$$02, StemBlock::new));
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape[] SHAPES = Block.a(7, $$0 -> Block.column(2.0, 0.0, 2 + $$0 * 2));
    private final ResourceKey<Block> fruit;
    private final ResourceKey<Block> attachedStem;
    private final ResourceKey<Item> seed;

    public MapCodec<StemBlock> codec() {
        return CODEC;
    }

    protected StemBlock(ResourceKey<Block> $$0, ResourceKey<Block> $$1, ResourceKey<Item> $$2, BlockBehaviour.Properties $$3) {
        super($$3);
        this.fruit = $$0;
        this.attachedStem = $$1;
        this.seed = $$2;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES[$$0.getValue(AGE)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.is(Blocks.FARMLAND);
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.getRawBrightness($$2, 0) < 9) {
            return;
        }
        float $$4 = CropBlock.getGrowthSpeed(this, $$1, $$2);
        if ($$3.nextInt((int)(25.0f / $$4) + 1) == 0) {
            int $$5 = $$0.getValue(AGE);
            if ($$5 < 7) {
                $$0 = (BlockState)$$0.setValue(AGE, $$5 + 1);
                $$1.setBlock($$2, $$0, 2);
            } else {
                Direction $$6 = Direction.Plane.HORIZONTAL.getRandomDirection($$3);
                BlockPos $$7 = $$2.relative($$6);
                BlockState $$8 = $$1.getBlockState($$7.below());
                if ($$1.getBlockState($$7).isAir() && ($$8.is(Blocks.FARMLAND) || $$8.is(BlockTags.DIRT))) {
                    HolderLookup.RegistryLookup $$9 = $$1.registryAccess().lookupOrThrow(Registries.BLOCK);
                    Optional<Block> $$10 = $$9.getOptional(this.fruit);
                    Optional<Block> $$11 = $$9.getOptional(this.attachedStem);
                    if ($$10.isPresent() && $$11.isPresent()) {
                        $$1.setBlockAndUpdate($$7, $$10.get().defaultBlockState());
                        $$1.setBlockAndUpdate($$2, (BlockState)$$11.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, $$6));
                    }
                }
            }
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return new ItemStack((ItemLike)DataFixUtils.orElse($$0.registryAccess().lookupOrThrow(Registries.ITEM).getOptional(this.seed), (Object)this));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return $$2.getValue(AGE) != 7;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        int $$4 = Math.min(7, $$3.getValue(AGE) + Mth.nextInt($$0.random, 2, 5));
        BlockState $$5 = (BlockState)$$3.setValue(AGE, $$4);
        $$0.setBlock($$2, $$5, 2);
        if ($$4 == 7) {
            $$5.randomTick($$0, $$2, $$0.random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AGE);
    }
}


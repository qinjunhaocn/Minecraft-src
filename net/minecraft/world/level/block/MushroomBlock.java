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
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock
extends VegetationBlock
implements BonemealableBlock {
    public static final MapCodec<MushroomBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").forGetter($$0 -> $$0.feature), MushroomBlock.propertiesCodec()).apply((Applicative)$$02, MushroomBlock::new));
    private static final VoxelShape SHAPE = Block.column(6.0, 0.0, 6.0);
    private final ResourceKey<ConfiguredFeature<?, ?>> feature;

    public MapCodec<MushroomBlock> codec() {
        return CODEC;
    }

    public MushroomBlock(ResourceKey<ConfiguredFeature<?, ?>> $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.feature = $$0;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$3.nextInt(25) == 0) {
            int $$4 = 5;
            int $$5 = 4;
            for (BlockPos $$6 : BlockPos.betweenClosed($$2.offset(-4, -1, -4), $$2.offset(4, 1, 4))) {
                if (!$$1.getBlockState($$6).is(this) || --$$4 > 0) continue;
                return;
            }
            BlockPos $$7 = $$2.offset($$3.nextInt(3) - 1, $$3.nextInt(2) - $$3.nextInt(2), $$3.nextInt(3) - 1);
            for (int $$8 = 0; $$8 < 4; ++$$8) {
                if ($$1.isEmptyBlock($$7) && $$0.canSurvive($$1, $$7)) {
                    $$2 = $$7;
                }
                $$7 = $$2.offset($$3.nextInt(3) - 1, $$3.nextInt(2) - $$3.nextInt(2), $$3.nextInt(3) - 1);
            }
            if ($$1.isEmptyBlock($$7) && $$0.canSurvive($$1, $$7)) {
                $$1.setBlock($$7, $$0, 2);
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.isSolidRender();
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockPos $$3 = $$2.below();
        BlockState $$4 = $$1.getBlockState($$3);
        if ($$4.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }
        return $$1.getRawBrightness($$2, 0) < 13 && this.mayPlaceOn($$4, $$1, $$3);
    }

    public boolean growMushroom(ServerLevel $$0, BlockPos $$1, BlockState $$2, RandomSource $$3) {
        Optional $$4 = $$0.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(this.feature);
        if ($$4.isEmpty()) {
            return false;
        }
        $$0.removeBlock($$1, false);
        if (((ConfiguredFeature)((Object)((Holder)$$4.get()).value())).place($$0, $$0.getChunkSource().getGenerator(), $$3, $$1)) {
            return true;
        }
        $$0.setBlock($$1, $$2, 3);
        return false;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return (double)$$1.nextFloat() < 0.4;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        this.growMushroom($$0, $$2, $$3, $$1);
    }
}


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
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LayeredCauldronBlock
extends AbstractCauldronBlock {
    public static final MapCodec<LayeredCauldronBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter($$0 -> $$0.precipitationType), (App)CauldronInteraction.CODEC.fieldOf("interactions").forGetter($$0 -> $$0.interactions), LayeredCauldronBlock.propertiesCodec()).apply((Applicative)$$02, LayeredCauldronBlock::new));
    public static final int MIN_FILL_LEVEL = 1;
    public static final int MAX_FILL_LEVEL = 3;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    private static final int BASE_CONTENT_HEIGHT = 6;
    private static final double HEIGHT_PER_LEVEL = 3.0;
    private static final VoxelShape[] FILLED_SHAPES = Util.make(() -> Block.a(2, $$0 -> Shapes.or(AbstractCauldronBlock.SHAPE, Block.column(12.0, 4.0, LayeredCauldronBlock.getPixelContentHeight($$0 + 1)))));
    private final Biome.Precipitation precipitationType;

    public MapCodec<LayeredCauldronBlock> codec() {
        return CODEC;
    }

    public LayeredCauldronBlock(Biome.Precipitation $$0, CauldronInteraction.InteractionMap $$1, BlockBehaviour.Properties $$2) {
        super($$2, $$1);
        this.precipitationType = $$0;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 1));
    }

    @Override
    public boolean isFull(BlockState $$0) {
        return $$0.getValue(LEVEL) == 3;
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid $$0) {
        return $$0 == Fluids.WATER && this.precipitationType == Biome.Precipitation.RAIN;
    }

    @Override
    protected double getContentHeight(BlockState $$0) {
        return LayeredCauldronBlock.getPixelContentHeight($$0.getValue(LEVEL)) / 16.0;
    }

    private static double getPixelContentHeight(int $$0) {
        return 6.0 + (double)$$0 * 3.0;
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, Entity $$3) {
        return FILLED_SHAPES[$$0.getValue(LEVEL) - 1];
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$42) {
        if ($$1 instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)$$1;
            BlockPos $$6 = $$2.immutable();
            $$42.runBefore(InsideBlockEffectType.EXTINGUISH, $$4 -> {
                if ($$4.isOnFire() && $$4.mayInteract($$5, $$6)) {
                    this.handleEntityOnFireInside($$0, $$1, $$6);
                }
            });
        }
        $$42.apply(InsideBlockEffectType.EXTINGUISH);
    }

    private void handleEntityOnFireInside(BlockState $$0, Level $$1, BlockPos $$2) {
        if (this.precipitationType == Biome.Precipitation.SNOW) {
            LayeredCauldronBlock.lowerFillLevel((BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, $$0.getValue(LEVEL)), $$1, $$2);
        } else {
            LayeredCauldronBlock.lowerFillLevel($$0, $$1, $$2);
        }
    }

    public static void lowerFillLevel(BlockState $$0, Level $$1, BlockPos $$2) {
        int $$3 = $$0.getValue(LEVEL) - 1;
        BlockState $$4 = $$3 == 0 ? Blocks.CAULDRON.defaultBlockState() : (BlockState)$$0.setValue(LEVEL, $$3);
        $$1.setBlockAndUpdate($$2, $$4);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4));
    }

    @Override
    public void handlePrecipitation(BlockState $$0, Level $$1, BlockPos $$2, Biome.Precipitation $$3) {
        if (!CauldronBlock.shouldHandlePrecipitation($$1, $$3) || $$0.getValue(LEVEL) == 3 || $$3 != this.precipitationType) {
            return;
        }
        BlockState $$4 = (BlockState)$$0.cycle(LEVEL);
        $$1.setBlockAndUpdate($$2, $$4);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4));
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return $$0.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(LEVEL);
    }

    @Override
    protected void receiveStalactiteDrip(BlockState $$0, Level $$1, BlockPos $$2, Fluid $$3) {
        if (this.isFull($$0)) {
            return;
        }
        BlockState $$4 = (BlockState)$$0.setValue(LEVEL, $$0.getValue(LEVEL) + 1);
        $$1.setBlockAndUpdate($$2, $$4);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4));
        $$1.levelEvent(1047, $$2, 0);
    }
}


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
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CoralBlock
extends Block {
    public static final MapCodec<Block> DEAD_CORAL_FIELD = BuiltInRegistries.BLOCK.byNameCodec().fieldOf("dead");
    public static final MapCodec<CoralBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)DEAD_CORAL_FIELD.forGetter($$0 -> $$0.deadBlock), CoralBlock.propertiesCodec()).apply((Applicative)$$02, CoralBlock::new));
    private final Block deadBlock;

    public CoralBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.deadBlock = $$0;
    }

    public MapCodec<CoralBlock> codec() {
        return CODEC;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!this.scanForWater($$1, $$2)) {
            $$1.setBlock($$2, this.deadBlock.defaultBlockState(), 2);
        }
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if (!this.scanForWater($$1, $$3)) {
            $$2.scheduleTick($$3, this, 60 + $$7.nextInt(40));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    protected boolean scanForWater(BlockGetter $$0, BlockPos $$1) {
        for (Direction $$2 : Direction.values()) {
            FluidState $$3 = $$0.getFluidState($$1.relative($$2));
            if (!$$3.is(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        if (!this.scanForWater($$0.getLevel(), $$0.getClickedPos())) {
            $$0.getLevel().scheduleTick($$0.getClickedPos(), this, 60 + $$0.getLevel().getRandom().nextInt(40));
        }
        return this.defaultBlockState();
    }
}


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
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FallingBlock
extends Block
implements Fallable {
    public FallingBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    protected abstract MapCodec<? extends FallingBlock> codec();

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        $$1.scheduleTick($$2, this, this.getDelayAfterPlace());
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        $$2.scheduleTick($$3, this, this.getDelayAfterPlace());
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!FallingBlock.isFree($$1.getBlockState($$2.below())) || $$2.getY() < $$1.getMinY()) {
            return;
        }
        FallingBlockEntity $$4 = FallingBlockEntity.fall($$1, $$2, $$0);
        this.falling($$4);
    }

    protected void falling(FallingBlockEntity $$0) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState $$0) {
        return $$0.isAir() || $$0.is(BlockTags.FIRE) || $$0.liquid() || $$0.canBeReplaced();
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        BlockPos $$4;
        if ($$3.nextInt(16) == 0 && FallingBlock.isFree($$1.getBlockState($$4 = $$2.below()))) {
            ParticleUtils.spawnParticleBelow($$1, $$2, $$3, new BlockParticleOption(ParticleTypes.FALLING_DUST, $$0));
        }
    }

    public abstract int getDustColor(BlockState var1, BlockGetter var2, BlockPos var3);
}


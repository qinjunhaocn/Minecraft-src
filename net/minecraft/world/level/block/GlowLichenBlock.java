/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class GlowLichenBlock
extends MultifaceSpreadeableBlock
implements BonemealableBlock {
    public static final MapCodec<GlowLichenBlock> CODEC = GlowLichenBlock.simpleCodec(GlowLichenBlock::new);
    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public MapCodec<GlowLichenBlock> codec() {
        return CODEC;
    }

    public GlowLichenBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    public static ToIntFunction<BlockState> emission(int $$0) {
        return $$1 -> MultifaceBlock.hasAnyFace($$1) ? $$0 : 0;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return Direction.stream().anyMatch($$3 -> this.spreader.canSpreadInAnyDirection($$2, $$0, $$1, $$3.getOpposite()));
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        this.spreader.spreadFromRandomFaceTowardRandomDirection($$3, $$0, $$2, $$1);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState $$0) {
        return $$0.getFluidState().isEmpty();
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.spreader;
    }
}


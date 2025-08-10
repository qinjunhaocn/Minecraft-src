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
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperGrateBlock
extends WaterloggedTransparentBlock
implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperGrateBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperGrateBlock::getAge), WeatheringCopperGrateBlock.propertiesCodec()).apply((Applicative)$$0, WeatheringCopperGrateBlock::new));
    private final WeatheringCopper.WeatherState weatherState;

    protected MapCodec<WeatheringCopperGrateBlock> codec() {
        return CODEC;
    }

    protected WeatheringCopperGrateBlock(WeatheringCopper.WeatherState $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.weatherState = $$0;
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        this.changeOverTime($$0, $$1, $$2, $$3);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState $$0) {
        return WeatheringCopper.getNext($$0.getBlock()).isPresent();
    }

    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }

    @Override
    public /* synthetic */ Enum getAge() {
        return this.getAge();
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TorchBlock
extends BaseTorchBlock {
    protected static final MapCodec<SimpleParticleType> PARTICLE_OPTIONS_FIELD = BuiltInRegistries.PARTICLE_TYPE.byNameCodec().comapFlatMap($$0 -> {
        DataResult dataResult;
        if ($$0 instanceof SimpleParticleType) {
            SimpleParticleType $$1 = (SimpleParticleType)$$0;
            dataResult = DataResult.success((Object)$$1);
        } else {
            dataResult = DataResult.error(() -> "Not a SimpleParticleType: " + String.valueOf($$0));
        }
        return dataResult;
    }, $$0 -> $$0).fieldOf("particle_options");
    public static final MapCodec<TorchBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)PARTICLE_OPTIONS_FIELD.forGetter($$0 -> $$0.flameParticle), TorchBlock.propertiesCodec()).apply((Applicative)$$02, TorchBlock::new));
    protected final SimpleParticleType flameParticle;

    public MapCodec<? extends TorchBlock> codec() {
        return CODEC;
    }

    protected TorchBlock(SimpleParticleType $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.flameParticle = $$0;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        double $$4 = (double)$$2.getX() + 0.5;
        double $$5 = (double)$$2.getY() + 0.7;
        double $$6 = (double)$$2.getZ() + 0.5;
        $$1.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, 0.0, 0.0, 0.0);
        $$1.addParticle(this.flameParticle, $$4, $$5, $$6, 0.0, 0.0, 0.0);
    }
}


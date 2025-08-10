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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class UntintedParticleLeavesBlock
extends LeavesBlock {
    public static final MapCodec<UntintedParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("leaf_particle_chance").forGetter($$0 -> Float.valueOf($$0.leafParticleChance)), (App)ParticleTypes.CODEC.fieldOf("leaf_particle").forGetter($$0 -> $$0.leafParticle), UntintedParticleLeavesBlock.propertiesCodec()).apply((Applicative)$$02, UntintedParticleLeavesBlock::new));
    protected final ParticleOptions leafParticle;

    public UntintedParticleLeavesBlock(float $$0, ParticleOptions $$1, BlockBehaviour.Properties $$2) {
        super($$0, $$2);
        this.leafParticle = $$1;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level $$0, BlockPos $$1, RandomSource $$2) {
        ParticleUtils.spawnParticleBelow($$0, $$1, $$2, this.leafParticle);
    }

    public MapCodec<UntintedParticleLeavesBlock> codec() {
        return CODEC;
    }
}


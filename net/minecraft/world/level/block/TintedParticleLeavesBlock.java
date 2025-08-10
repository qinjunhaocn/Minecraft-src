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
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TintedParticleLeavesBlock
extends LeavesBlock {
    public static final MapCodec<TintedParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ExtraCodecs.floatRange(0.0f, 1.0f).fieldOf("leaf_particle_chance").forGetter($$0 -> Float.valueOf($$0.leafParticleChance)), TintedParticleLeavesBlock.propertiesCodec()).apply((Applicative)$$02, TintedParticleLeavesBlock::new));

    public TintedParticleLeavesBlock(float $$0, BlockBehaviour.Properties $$1) {
        super($$0, $$1);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level $$0, BlockPos $$1, RandomSource $$2) {
        ColorParticleOption $$3 = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, $$0.getClientLeafTintColor($$1));
        ParticleUtils.spawnParticleBelow($$0, $$1, $$2, $$3);
    }

    public MapCodec<? extends TintedParticleLeavesBlock> codec() {
        return CODEC;
    }
}


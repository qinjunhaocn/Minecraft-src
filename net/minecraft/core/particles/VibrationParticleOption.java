/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.core.particles;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;

public class VibrationParticleOption
implements ParticleOptions {
    private static final Codec<PositionSource> SAFE_POSITION_SOURCE_CODEC = PositionSource.CODEC.validate($$0 -> $$0 instanceof EntityPositionSource ? DataResult.error(() -> "Entity position sources are not allowed") : DataResult.success((Object)$$0));
    public static final MapCodec<VibrationParticleOption> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)SAFE_POSITION_SOURCE_CODEC.fieldOf("destination").forGetter(VibrationParticleOption::getDestination), (App)Codec.INT.fieldOf("arrival_in_ticks").forGetter(VibrationParticleOption::getArrivalInTicks)).apply((Applicative)$$0, VibrationParticleOption::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, VibrationParticleOption> STREAM_CODEC = StreamCodec.composite(PositionSource.STREAM_CODEC, VibrationParticleOption::getDestination, ByteBufCodecs.VAR_INT, VibrationParticleOption::getArrivalInTicks, VibrationParticleOption::new);
    private final PositionSource destination;
    private final int arrivalInTicks;

    public VibrationParticleOption(PositionSource $$0, int $$1) {
        this.destination = $$0;
        this.arrivalInTicks = $$1;
    }

    public ParticleType<VibrationParticleOption> getType() {
        return ParticleTypes.VIBRATION;
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}


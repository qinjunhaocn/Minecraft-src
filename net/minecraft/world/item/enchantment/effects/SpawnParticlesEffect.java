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
package net.minecraft.world.item.enchantment.effects;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record SpawnParticlesEffect(ParticleOptions particle, PositionSource horizontalPosition, PositionSource verticalPosition, VelocitySource horizontalVelocity, VelocitySource verticalVelocity, FloatProvider speed) implements EnchantmentEntityEffect
{
    public static final MapCodec<SpawnParticlesEffect> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ParticleTypes.CODEC.fieldOf("particle").forGetter(SpawnParticlesEffect::particle), (App)PositionSource.CODEC.fieldOf("horizontal_position").forGetter(SpawnParticlesEffect::horizontalPosition), (App)PositionSource.CODEC.fieldOf("vertical_position").forGetter(SpawnParticlesEffect::verticalPosition), (App)VelocitySource.CODEC.fieldOf("horizontal_velocity").forGetter(SpawnParticlesEffect::horizontalVelocity), (App)VelocitySource.CODEC.fieldOf("vertical_velocity").forGetter(SpawnParticlesEffect::verticalVelocity), (App)FloatProvider.CODEC.optionalFieldOf("speed", (Object)ConstantFloat.ZERO).forGetter(SpawnParticlesEffect::speed)).apply((Applicative)$$0, SpawnParticlesEffect::new));

    public static PositionSource offsetFromEntityPosition(float $$0) {
        return new PositionSource(PositionSourceType.ENTITY_POSITION, $$0, 1.0f);
    }

    public static PositionSource inBoundingBox() {
        return new PositionSource(PositionSourceType.BOUNDING_BOX, 0.0f, 1.0f);
    }

    public static VelocitySource movementScaled(float $$0) {
        return new VelocitySource($$0, ConstantFloat.ZERO);
    }

    public static VelocitySource fixedVelocity(FloatProvider $$0) {
        return new VelocitySource(0.0f, $$0);
    }

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
        RandomSource $$5 = $$3.getRandom();
        Vec3 $$6 = $$3.getKnownMovement();
        float $$7 = $$3.getBbWidth();
        float $$8 = $$3.getBbHeight();
        $$0.sendParticles(this.particle, this.horizontalPosition.getCoordinate($$4.x(), $$4.x(), $$7, $$5), this.verticalPosition.getCoordinate($$4.y(), $$4.y() + (double)($$8 / 2.0f), $$8, $$5), this.horizontalPosition.getCoordinate($$4.z(), $$4.z(), $$7, $$5), 0, this.horizontalVelocity.getVelocity($$6.x(), $$5), this.verticalVelocity.getVelocity($$6.y(), $$5), this.horizontalVelocity.getVelocity($$6.z(), $$5), this.speed.sample($$5));
    }

    public MapCodec<SpawnParticlesEffect> codec() {
        return CODEC;
    }

    public record PositionSource(PositionSourceType type, float offset, float scale) {
        public static final MapCodec<PositionSource> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)PositionSourceType.CODEC.fieldOf("type").forGetter(PositionSource::type), (App)Codec.FLOAT.optionalFieldOf("offset", (Object)Float.valueOf(0.0f)).forGetter(PositionSource::offset), (App)ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(PositionSource::scale)).apply((Applicative)$$0, PositionSource::new)).validate($$0 -> {
            if ($$0.type() == PositionSourceType.ENTITY_POSITION && $$0.scale() != 1.0f) {
                return DataResult.error(() -> "Cannot scale an entity position coordinate source");
            }
            return DataResult.success((Object)$$0);
        });

        public double getCoordinate(double $$0, double $$1, float $$2, RandomSource $$3) {
            return this.type.getCoordinate($$0, $$1, $$2 * this.scale, $$3) + (double)this.offset;
        }
    }

    public record VelocitySource(float movementScale, FloatProvider base) {
        public static final MapCodec<VelocitySource> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.FLOAT.optionalFieldOf("movement_scale", (Object)Float.valueOf(0.0f)).forGetter(VelocitySource::movementScale), (App)FloatProvider.CODEC.optionalFieldOf("base", (Object)ConstantFloat.ZERO).forGetter(VelocitySource::base)).apply((Applicative)$$0, VelocitySource::new));

        public double getVelocity(double $$0, RandomSource $$1) {
            return $$0 * (double)this.movementScale + (double)this.base.sample($$1);
        }
    }

    public static final class PositionSourceType
    extends Enum<PositionSourceType>
    implements StringRepresentable {
        public static final /* enum */ PositionSourceType ENTITY_POSITION = new PositionSourceType("entity_position", ($$0, $$1, $$2, $$3) -> $$0);
        public static final /* enum */ PositionSourceType BOUNDING_BOX = new PositionSourceType("in_bounding_box", ($$0, $$1, $$2, $$3) -> $$1 + ($$3.nextDouble() - 0.5) * (double)$$2);
        public static final Codec<PositionSourceType> CODEC;
        private final String id;
        private final CoordinateSource source;
        private static final /* synthetic */ PositionSourceType[] $VALUES;

        public static PositionSourceType[] values() {
            return (PositionSourceType[])$VALUES.clone();
        }

        public static PositionSourceType valueOf(String $$0) {
            return Enum.valueOf(PositionSourceType.class, $$0);
        }

        private PositionSourceType(String $$0, CoordinateSource $$1) {
            this.id = $$0;
            this.source = $$1;
        }

        public double getCoordinate(double $$0, double $$1, float $$2, RandomSource $$3) {
            return this.source.getCoordinate($$0, $$1, $$2, $$3);
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        private static /* synthetic */ PositionSourceType[] a() {
            return new PositionSourceType[]{ENTITY_POSITION, BOUNDING_BOX};
        }

        static {
            $VALUES = PositionSourceType.a();
            CODEC = StringRepresentable.fromEnum(PositionSourceType::values);
        }

        @FunctionalInterface
        static interface CoordinateSource {
            public double getCoordinate(double var1, double var3, float var5, RandomSource var6);
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.DryFoliageColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class Biome {
    public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ClimateSettings.CODEC.forGetter($$0 -> $$0.climateSettings), (App)BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter($$0 -> $$0.specialEffects), (App)BiomeGenerationSettings.CODEC.forGetter($$0 -> $$0.generationSettings), (App)MobSpawnSettings.CODEC.forGetter($$0 -> $$0.mobSettings)).apply((Applicative)$$02, Biome::new));
    public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ClimateSettings.CODEC.forGetter($$0 -> $$0.climateSettings), (App)BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter($$0 -> $$0.specialEffects)).apply((Applicative)$$02, ($$0, $$1) -> new Biome((ClimateSettings)((Object)((Object)$$0)), (BiomeSpecialEffects)$$1, BiomeGenerationSettings.EMPTY, MobSpawnSettings.EMPTY)));
    public static final Codec<Holder<Biome>> CODEC = RegistryFileCodec.create(Registries.BIOME, DIRECT_CODEC);
    public static final Codec<HolderSet<Biome>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.BIOME, DIRECT_CODEC);
    private static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise((RandomSource)new WorldgenRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));
    static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise((RandomSource)new WorldgenRandom(new LegacyRandomSource(3456L)), ImmutableList.of(-2, -1, 0));
    @Deprecated(forRemoval=true)
    public static final PerlinSimplexNoise BIOME_INFO_NOISE = new PerlinSimplexNoise((RandomSource)new WorldgenRandom(new LegacyRandomSource(2345L)), ImmutableList.of(0));
    private static final int TEMPERATURE_CACHE_SIZE = 1024;
    private final ClimateSettings climateSettings;
    private final BiomeGenerationSettings generationSettings;
    private final MobSpawnSettings mobSettings;
    private final BiomeSpecialEffects specialEffects;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap $$0 = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            protected void rehash(int $$0) {
            }
        };
        $$0.defaultReturnValue(Float.NaN);
        return $$0;
    }));

    Biome(ClimateSettings $$0, BiomeSpecialEffects $$1, BiomeGenerationSettings $$2, MobSpawnSettings $$3) {
        this.climateSettings = $$0;
        this.generationSettings = $$2;
        this.mobSettings = $$3;
        this.specialEffects = $$1;
    }

    public int getSkyColor() {
        return this.specialEffects.getSkyColor();
    }

    public MobSpawnSettings getMobSettings() {
        return this.mobSettings;
    }

    public boolean hasPrecipitation() {
        return this.climateSettings.hasPrecipitation();
    }

    public Precipitation getPrecipitationAt(BlockPos $$0, int $$1) {
        if (!this.hasPrecipitation()) {
            return Precipitation.NONE;
        }
        return this.coldEnoughToSnow($$0, $$1) ? Precipitation.SNOW : Precipitation.RAIN;
    }

    private float getHeightAdjustedTemperature(BlockPos $$0, int $$1) {
        float $$2 = this.climateSettings.temperatureModifier.modifyTemperature($$0, this.getBaseTemperature());
        int $$3 = $$1 + 17;
        if ($$0.getY() > $$3) {
            float $$4 = (float)(TEMPERATURE_NOISE.getValue((float)$$0.getX() / 8.0f, (float)$$0.getZ() / 8.0f, false) * 8.0);
            return $$2 - ($$4 + (float)$$0.getY() - (float)$$3) * 0.05f / 40.0f;
        }
        return $$2;
    }

    @Deprecated
    private float getTemperature(BlockPos $$0, int $$1) {
        long $$2 = $$0.asLong();
        Long2FloatLinkedOpenHashMap $$3 = this.temperatureCache.get();
        float $$4 = $$3.get($$2);
        if (!Float.isNaN($$4)) {
            return $$4;
        }
        float $$5 = this.getHeightAdjustedTemperature($$0, $$1);
        if ($$3.size() == 1024) {
            $$3.removeFirstFloat();
        }
        $$3.put($$2, $$5);
        return $$5;
    }

    public boolean shouldFreeze(LevelReader $$0, BlockPos $$1) {
        return this.shouldFreeze($$0, $$1, true);
    }

    public boolean shouldFreeze(LevelReader $$0, BlockPos $$1, boolean $$2) {
        if (this.warmEnoughToRain($$1, $$0.getSeaLevel())) {
            return false;
        }
        if ($$0.isInsideBuildHeight($$1.getY()) && $$0.getBrightness(LightLayer.BLOCK, $$1) < 10) {
            BlockState $$3 = $$0.getBlockState($$1);
            FluidState $$4 = $$0.getFluidState($$1);
            if ($$4.getType() == Fluids.WATER && $$3.getBlock() instanceof LiquidBlock) {
                boolean $$5;
                if (!$$2) {
                    return true;
                }
                boolean bl = $$5 = $$0.isWaterAt($$1.west()) && $$0.isWaterAt($$1.east()) && $$0.isWaterAt($$1.north()) && $$0.isWaterAt($$1.south());
                if (!$$5) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean coldEnoughToSnow(BlockPos $$0, int $$1) {
        return !this.warmEnoughToRain($$0, $$1);
    }

    public boolean warmEnoughToRain(BlockPos $$0, int $$1) {
        return this.getTemperature($$0, $$1) >= 0.15f;
    }

    public boolean shouldMeltFrozenOceanIcebergSlightly(BlockPos $$0, int $$1) {
        return this.getTemperature($$0, $$1) > 0.1f;
    }

    public boolean shouldSnow(LevelReader $$0, BlockPos $$1) {
        BlockState $$2;
        if (this.warmEnoughToRain($$1, $$0.getSeaLevel())) {
            return false;
        }
        return $$0.isInsideBuildHeight($$1.getY()) && $$0.getBrightness(LightLayer.BLOCK, $$1) < 10 && (($$2 = $$0.getBlockState($$1)).isAir() || $$2.is(Blocks.SNOW)) && Blocks.SNOW.defaultBlockState().canSurvive($$0, $$1);
    }

    public BiomeGenerationSettings getGenerationSettings() {
        return this.generationSettings;
    }

    public int getFogColor() {
        return this.specialEffects.getFogColor();
    }

    public int getGrassColor(double $$0, double $$1) {
        int $$2 = this.getBaseGrassColor();
        return this.specialEffects.getGrassColorModifier().modifyColor($$0, $$1, $$2);
    }

    private int getBaseGrassColor() {
        Optional<Integer> $$0 = this.specialEffects.getGrassColorOverride();
        if ($$0.isPresent()) {
            return $$0.get();
        }
        return this.getGrassColorFromTexture();
    }

    private int getGrassColorFromTexture() {
        double $$0 = Mth.clamp(this.climateSettings.temperature, 0.0f, 1.0f);
        double $$1 = Mth.clamp(this.climateSettings.downfall, 0.0f, 1.0f);
        return GrassColor.get($$0, $$1);
    }

    public int getFoliageColor() {
        return this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
    }

    private int getFoliageColorFromTexture() {
        double $$0 = Mth.clamp(this.climateSettings.temperature, 0.0f, 1.0f);
        double $$1 = Mth.clamp(this.climateSettings.downfall, 0.0f, 1.0f);
        return FoliageColor.get($$0, $$1);
    }

    public int getDryFoliageColor() {
        return this.specialEffects.getDryFoliageColorOverride().orElseGet(this::getDryFoliageColorFromTexture);
    }

    private int getDryFoliageColorFromTexture() {
        double $$0 = Mth.clamp(this.climateSettings.temperature, 0.0f, 1.0f);
        double $$1 = Mth.clamp(this.climateSettings.downfall, 0.0f, 1.0f);
        return DryFoliageColor.get($$0, $$1);
    }

    public float getBaseTemperature() {
        return this.climateSettings.temperature;
    }

    public BiomeSpecialEffects getSpecialEffects() {
        return this.specialEffects;
    }

    public int getWaterColor() {
        return this.specialEffects.getWaterColor();
    }

    public int getWaterFogColor() {
        return this.specialEffects.getWaterFogColor();
    }

    public Optional<AmbientParticleSettings> getAmbientParticle() {
        return this.specialEffects.getAmbientParticleSettings();
    }

    public Optional<Holder<SoundEvent>> getAmbientLoop() {
        return this.specialEffects.getAmbientLoopSoundEvent();
    }

    public Optional<AmbientMoodSettings> getAmbientMood() {
        return this.specialEffects.getAmbientMoodSettings();
    }

    public Optional<AmbientAdditionsSettings> getAmbientAdditions() {
        return this.specialEffects.getAmbientAdditionsSettings();
    }

    public Optional<WeightedList<Music>> getBackgroundMusic() {
        return this.specialEffects.getBackgroundMusic();
    }

    public float getBackgroundMusicVolume() {
        return this.specialEffects.getBackgroundMusicVolume();
    }

    static final class ClimateSettings
    extends Record {
        private final boolean hasPrecipitation;
        final float temperature;
        final TemperatureModifier temperatureModifier;
        final float downfall;
        public static final MapCodec<ClimateSettings> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.BOOL.fieldOf("has_precipitation").forGetter($$0 -> $$0.hasPrecipitation), (App)Codec.FLOAT.fieldOf("temperature").forGetter($$0 -> Float.valueOf($$0.temperature)), (App)TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", (Object)TemperatureModifier.NONE).forGetter($$0 -> $$0.temperatureModifier), (App)Codec.FLOAT.fieldOf("downfall").forGetter($$0 -> Float.valueOf($$0.downfall))).apply((Applicative)$$02, ClimateSettings::new));

        ClimateSettings(boolean $$0, float $$1, TemperatureModifier $$2, float $$3) {
            this.hasPrecipitation = $$0;
            this.temperature = $$1;
            this.temperatureModifier = $$2;
            this.downfall = $$3;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClimateSettings.class, "hasPrecipitation;temperature;temperatureModifier;downfall", "hasPrecipitation", "temperature", "temperatureModifier", "downfall"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClimateSettings.class, "hasPrecipitation;temperature;temperatureModifier;downfall", "hasPrecipitation", "temperature", "temperatureModifier", "downfall"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClimateSettings.class, "hasPrecipitation;temperature;temperatureModifier;downfall", "hasPrecipitation", "temperature", "temperatureModifier", "downfall"}, this, $$0);
        }

        public boolean hasPrecipitation() {
            return this.hasPrecipitation;
        }

        public float temperature() {
            return this.temperature;
        }

        public TemperatureModifier temperatureModifier() {
            return this.temperatureModifier;
        }

        public float downfall() {
            return this.downfall;
        }
    }

    public static final class Precipitation
    extends Enum<Precipitation>
    implements StringRepresentable {
        public static final /* enum */ Precipitation NONE = new Precipitation("none");
        public static final /* enum */ Precipitation RAIN = new Precipitation("rain");
        public static final /* enum */ Precipitation SNOW = new Precipitation("snow");
        public static final Codec<Precipitation> CODEC;
        private final String name;
        private static final /* synthetic */ Precipitation[] $VALUES;

        public static Precipitation[] values() {
            return (Precipitation[])$VALUES.clone();
        }

        public static Precipitation valueOf(String $$0) {
            return Enum.valueOf(Precipitation.class, $$0);
        }

        private Precipitation(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Precipitation[] a() {
            return new Precipitation[]{NONE, RAIN, SNOW};
        }

        static {
            $VALUES = Precipitation.a();
            CODEC = StringRepresentable.fromEnum(Precipitation::values);
        }
    }

    public static abstract sealed class TemperatureModifier
    extends Enum<TemperatureModifier>
    implements StringRepresentable {
        public static final /* enum */ TemperatureModifier NONE = new TemperatureModifier("none"){

            @Override
            public float modifyTemperature(BlockPos $$0, float $$1) {
                return $$1;
            }
        };
        public static final /* enum */ TemperatureModifier FROZEN = new TemperatureModifier("frozen"){

            @Override
            public float modifyTemperature(BlockPos $$0, float $$1) {
                double $$5;
                double $$3;
                double $$2 = FROZEN_TEMPERATURE_NOISE.getValue((double)$$0.getX() * 0.05, (double)$$0.getZ() * 0.05, false) * 7.0;
                double $$4 = $$2 + ($$3 = BIOME_INFO_NOISE.getValue((double)$$0.getX() * 0.2, (double)$$0.getZ() * 0.2, false));
                if ($$4 < 0.3 && ($$5 = BIOME_INFO_NOISE.getValue((double)$$0.getX() * 0.09, (double)$$0.getZ() * 0.09, false)) < 0.8) {
                    return 0.2f;
                }
                return $$1;
            }
        };
        private final String name;
        public static final Codec<TemperatureModifier> CODEC;
        private static final /* synthetic */ TemperatureModifier[] $VALUES;

        public static TemperatureModifier[] values() {
            return (TemperatureModifier[])$VALUES.clone();
        }

        public static TemperatureModifier valueOf(String $$0) {
            return Enum.valueOf(TemperatureModifier.class, $$0);
        }

        public abstract float modifyTemperature(BlockPos var1, float var2);

        TemperatureModifier(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ TemperatureModifier[] b() {
            return new TemperatureModifier[]{NONE, FROZEN};
        }

        static {
            $VALUES = TemperatureModifier.b();
            CODEC = StringRepresentable.fromEnum(TemperatureModifier::values);
        }
    }

    public static class BiomeBuilder {
        private boolean hasPrecipitation = true;
        @Nullable
        private Float temperature;
        private TemperatureModifier temperatureModifier = TemperatureModifier.NONE;
        @Nullable
        private Float downfall;
        @Nullable
        private BiomeSpecialEffects specialEffects;
        @Nullable
        private MobSpawnSettings mobSpawnSettings;
        @Nullable
        private BiomeGenerationSettings generationSettings;

        public BiomeBuilder hasPrecipitation(boolean $$0) {
            this.hasPrecipitation = $$0;
            return this;
        }

        public BiomeBuilder temperature(float $$0) {
            this.temperature = Float.valueOf($$0);
            return this;
        }

        public BiomeBuilder downfall(float $$0) {
            this.downfall = Float.valueOf($$0);
            return this;
        }

        public BiomeBuilder specialEffects(BiomeSpecialEffects $$0) {
            this.specialEffects = $$0;
            return this;
        }

        public BiomeBuilder mobSpawnSettings(MobSpawnSettings $$0) {
            this.mobSpawnSettings = $$0;
            return this;
        }

        public BiomeBuilder generationSettings(BiomeGenerationSettings $$0) {
            this.generationSettings = $$0;
            return this;
        }

        public BiomeBuilder temperatureAdjustment(TemperatureModifier $$0) {
            this.temperatureModifier = $$0;
            return this;
        }

        public Biome build() {
            if (this.temperature == null || this.downfall == null || this.specialEffects == null || this.mobSpawnSettings == null || this.generationSettings == null) {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + String.valueOf(this));
            }
            return new Biome(new ClimateSettings(this.hasPrecipitation, this.temperature.floatValue(), this.temperatureModifier, this.downfall.floatValue()), this.specialEffects, this.generationSettings, this.mobSpawnSettings);
        }

        public String toString() {
            return "BiomeBuilder{\nhasPrecipitation=" + this.hasPrecipitation + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + String.valueOf(this.temperatureModifier) + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + String.valueOf(this.specialEffects) + ",\nmobSpawnSettings=" + String.valueOf(this.mobSpawnSettings) + ",\ngenerationSettings=" + String.valueOf(this.generationSettings) + ",\n}";
        }
    }
}


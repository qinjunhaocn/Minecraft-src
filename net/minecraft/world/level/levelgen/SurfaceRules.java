/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class SurfaceRules {
    public static final ConditionSource ON_FLOOR = SurfaceRules.stoneDepthCheck(0, false, CaveSurface.FLOOR);
    public static final ConditionSource UNDER_FLOOR = SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR);
    public static final ConditionSource DEEP_UNDER_FLOOR = SurfaceRules.stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
    public static final ConditionSource VERY_DEEP_UNDER_FLOOR = SurfaceRules.stoneDepthCheck(0, true, 30, CaveSurface.FLOOR);
    public static final ConditionSource ON_CEILING = SurfaceRules.stoneDepthCheck(0, false, CaveSurface.CEILING);
    public static final ConditionSource UNDER_CEILING = SurfaceRules.stoneDepthCheck(0, true, CaveSurface.CEILING);

    public static ConditionSource stoneDepthCheck(int $$0, boolean $$1, CaveSurface $$2) {
        return new StoneDepthCheck($$0, $$1, 0, $$2);
    }

    public static ConditionSource stoneDepthCheck(int $$0, boolean $$1, int $$2, CaveSurface $$3) {
        return new StoneDepthCheck($$0, $$1, $$2, $$3);
    }

    public static ConditionSource not(ConditionSource $$0) {
        return new NotConditionSource($$0);
    }

    public static ConditionSource yBlockCheck(VerticalAnchor $$0, int $$1) {
        return new YConditionSource($$0, $$1, false);
    }

    public static ConditionSource yStartCheck(VerticalAnchor $$0, int $$1) {
        return new YConditionSource($$0, $$1, true);
    }

    public static ConditionSource waterBlockCheck(int $$0, int $$1) {
        return new WaterConditionSource($$0, $$1, false);
    }

    public static ConditionSource waterStartCheck(int $$0, int $$1) {
        return new WaterConditionSource($$0, $$1, true);
    }

    @SafeVarargs
    public static ConditionSource a(ResourceKey<Biome> ... $$0) {
        return SurfaceRules.isBiome(List.of((Object[])$$0));
    }

    private static BiomeConditionSource isBiome(List<ResourceKey<Biome>> $$0) {
        return new BiomeConditionSource($$0);
    }

    public static ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> $$0, double $$1) {
        return SurfaceRules.noiseCondition($$0, $$1, Double.MAX_VALUE);
    }

    public static ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> $$0, double $$1, double $$2) {
        return new NoiseThresholdConditionSource($$0, $$1, $$2);
    }

    public static ConditionSource verticalGradient(String $$0, VerticalAnchor $$1, VerticalAnchor $$2) {
        return new VerticalGradientConditionSource(ResourceLocation.parse($$0), $$1, $$2);
    }

    public static ConditionSource steep() {
        return Steep.INSTANCE;
    }

    public static ConditionSource hole() {
        return Hole.INSTANCE;
    }

    public static ConditionSource abovePreliminarySurface() {
        return AbovePreliminarySurface.INSTANCE;
    }

    public static ConditionSource temperature() {
        return Temperature.INSTANCE;
    }

    public static RuleSource ifTrue(ConditionSource $$0, RuleSource $$1) {
        return new TestRuleSource($$0, $$1);
    }

    public static RuleSource a(RuleSource ... $$0) {
        if ($$0.length == 0) {
            throw new IllegalArgumentException("Need at least 1 rule for a sequence");
        }
        return new SequenceRuleSource(Arrays.asList($$0));
    }

    public static RuleSource state(BlockState $$0) {
        return new BlockRuleSource($$0);
    }

    public static RuleSource bandlands() {
        return Bandlands.INSTANCE;
    }

    static <A> MapCodec<? extends A> register(Registry<MapCodec<? extends A>> $$0, String $$1, KeyDispatchDataCodec<? extends A> $$2) {
        return Registry.register($$0, $$1, $$2.codec());
    }

    static final class StoneDepthCheck
    extends Record
    implements ConditionSource {
        final int offset;
        final boolean addSurfaceDepth;
        final int secondaryDepthRange;
        private final CaveSurface surfaceType;
        static final KeyDispatchDataCodec<StoneDepthCheck> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.INT.fieldOf("offset").forGetter(StoneDepthCheck::offset), (App)Codec.BOOL.fieldOf("add_surface_depth").forGetter(StoneDepthCheck::addSurfaceDepth), (App)Codec.INT.fieldOf("secondary_depth_range").forGetter(StoneDepthCheck::secondaryDepthRange), (App)CaveSurface.CODEC.fieldOf("surface_type").forGetter(StoneDepthCheck::surfaceType)).apply((Applicative)$$0, StoneDepthCheck::new)));

        StoneDepthCheck(int $$0, boolean $$1, int $$2, CaveSurface $$3) {
            this.offset = $$0;
            this.addSurfaceDepth = $$1;
            this.secondaryDepthRange = $$2;
            this.surfaceType = $$3;
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(final Context $$0) {
            final boolean $$1 = this.surfaceType == CaveSurface.CEILING;
            class StoneDepthCondition
            extends LazyYCondition {
                StoneDepthCondition() {
                    super(context);
                }

                @Override
                protected boolean compute() {
                    int $$02 = $$1 ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
                    int $$12 = StoneDepthCheck.this.addSurfaceDepth ? this.context.surfaceDepth : 0;
                    int $$2 = StoneDepthCheck.this.secondaryDepthRange == 0 ? 0 : (int)Mth.map(this.context.getSurfaceSecondary(), -1.0, 1.0, 0.0, (double)StoneDepthCheck.this.secondaryDepthRange);
                    return $$02 <= 1 + StoneDepthCheck.this.offset + $$12 + $$2;
                }
            }
            return new StoneDepthCondition();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StoneDepthCheck.class, "offset;addSurfaceDepth;secondaryDepthRange;surfaceType", "offset", "addSurfaceDepth", "secondaryDepthRange", "surfaceType"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StoneDepthCheck.class, "offset;addSurfaceDepth;secondaryDepthRange;surfaceType", "offset", "addSurfaceDepth", "secondaryDepthRange", "surfaceType"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StoneDepthCheck.class, "offset;addSurfaceDepth;secondaryDepthRange;surfaceType", "offset", "addSurfaceDepth", "secondaryDepthRange", "surfaceType"}, this, $$0);
        }

        public int offset() {
            return this.offset;
        }

        public boolean addSurfaceDepth() {
            return this.addSurfaceDepth;
        }

        public int secondaryDepthRange() {
            return this.secondaryDepthRange;
        }

        public CaveSurface surfaceType() {
            return this.surfaceType;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    record NotConditionSource(ConditionSource target) implements ConditionSource
    {
        static final KeyDispatchDataCodec<NotConditionSource> CODEC = KeyDispatchDataCodec.of(ConditionSource.CODEC.xmap(NotConditionSource::new, NotConditionSource::target).fieldOf("invert"));

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(Context $$0) {
            return new NotCondition((Condition)this.target.apply($$0));
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    public static interface ConditionSource
    extends Function<Context, Condition> {
        public static final Codec<ConditionSource> CODEC = BuiltInRegistries.MATERIAL_CONDITION.byNameCodec().dispatch($$0 -> $$0.codec().codec(), Function.identity());

        public static MapCodec<? extends ConditionSource> bootstrap(Registry<MapCodec<? extends ConditionSource>> $$0) {
            SurfaceRules.register($$0, "biome", BiomeConditionSource.CODEC);
            SurfaceRules.register($$0, "noise_threshold", NoiseThresholdConditionSource.CODEC);
            SurfaceRules.register($$0, "vertical_gradient", VerticalGradientConditionSource.CODEC);
            SurfaceRules.register($$0, "y_above", YConditionSource.CODEC);
            SurfaceRules.register($$0, "water", WaterConditionSource.CODEC);
            SurfaceRules.register($$0, "temperature", Temperature.CODEC);
            SurfaceRules.register($$0, "steep", Steep.CODEC);
            SurfaceRules.register($$0, "not", NotConditionSource.CODEC);
            SurfaceRules.register($$0, "hole", Hole.CODEC);
            SurfaceRules.register($$0, "above_preliminary_surface", AbovePreliminarySurface.CODEC);
            return SurfaceRules.register($$0, "stone_depth", StoneDepthCheck.CODEC);
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec();
    }

    static final class YConditionSource
    extends Record
    implements ConditionSource {
        final VerticalAnchor anchor;
        final int surfaceDepthMultiplier;
        final boolean addStoneDepth;
        static final KeyDispatchDataCodec<YConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)VerticalAnchor.CODEC.fieldOf("anchor").forGetter(YConditionSource::anchor), (App)Codec.intRange((int)-20, (int)20).fieldOf("surface_depth_multiplier").forGetter(YConditionSource::surfaceDepthMultiplier), (App)Codec.BOOL.fieldOf("add_stone_depth").forGetter(YConditionSource::addStoneDepth)).apply((Applicative)$$0, YConditionSource::new)));

        YConditionSource(VerticalAnchor $$0, int $$1, boolean $$2) {
            this.anchor = $$0;
            this.surfaceDepthMultiplier = $$1;
            this.addStoneDepth = $$2;
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(final Context $$0) {
            class YCondition
            extends LazyYCondition {
                YCondition() {
                    super(context);
                }

                @Override
                protected boolean compute() {
                    return this.context.blockY + (YConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= YConditionSource.this.anchor.resolveY(this.context.context) + this.context.surfaceDepth * YConditionSource.this.surfaceDepthMultiplier;
                }
            }
            return new YCondition();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{YConditionSource.class, "anchor;surfaceDepthMultiplier;addStoneDepth", "anchor", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{YConditionSource.class, "anchor;surfaceDepthMultiplier;addStoneDepth", "anchor", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{YConditionSource.class, "anchor;surfaceDepthMultiplier;addStoneDepth", "anchor", "surfaceDepthMultiplier", "addStoneDepth"}, this, $$0);
        }

        public VerticalAnchor anchor() {
            return this.anchor;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    static final class WaterConditionSource
    extends Record
    implements ConditionSource {
        final int offset;
        final int surfaceDepthMultiplier;
        final boolean addStoneDepth;
        static final KeyDispatchDataCodec<WaterConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.INT.fieldOf("offset").forGetter(WaterConditionSource::offset), (App)Codec.intRange((int)-20, (int)20).fieldOf("surface_depth_multiplier").forGetter(WaterConditionSource::surfaceDepthMultiplier), (App)Codec.BOOL.fieldOf("add_stone_depth").forGetter(WaterConditionSource::addStoneDepth)).apply((Applicative)$$0, WaterConditionSource::new)));

        WaterConditionSource(int $$0, int $$1, boolean $$2) {
            this.offset = $$0;
            this.surfaceDepthMultiplier = $$1;
            this.addStoneDepth = $$2;
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(final Context $$0) {
            class WaterCondition
            extends LazyYCondition {
                WaterCondition() {
                    super(context);
                }

                @Override
                protected boolean compute() {
                    return this.context.waterHeight == Integer.MIN_VALUE || this.context.blockY + (WaterConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.waterHeight + WaterConditionSource.this.offset + this.context.surfaceDepth * WaterConditionSource.this.surfaceDepthMultiplier;
                }
            }
            return new WaterCondition();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{WaterConditionSource.class, "offset;surfaceDepthMultiplier;addStoneDepth", "offset", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WaterConditionSource.class, "offset;surfaceDepthMultiplier;addStoneDepth", "offset", "surfaceDepthMultiplier", "addStoneDepth"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WaterConditionSource.class, "offset;surfaceDepthMultiplier;addStoneDepth", "offset", "surfaceDepthMultiplier", "addStoneDepth"}, this, $$0);
        }

        public int offset() {
            return this.offset;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    static final class BiomeConditionSource
    implements ConditionSource {
        static final KeyDispatchDataCodec<BiomeConditionSource> CODEC = KeyDispatchDataCodec.of(ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biome_is").xmap(SurfaceRules::isBiome, $$0 -> $$0.biomes));
        private final List<ResourceKey<Biome>> biomes;
        final Predicate<ResourceKey<Biome>> biomeNameTest;

        BiomeConditionSource(List<ResourceKey<Biome>> $$0) {
            this.biomes = $$0;
            this.biomeNameTest = Set.copyOf($$0)::contains;
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(final Context $$0) {
            class BiomeCondition
            extends LazyYCondition {
                BiomeCondition() {
                    super(context);
                }

                @Override
                protected boolean compute() {
                    return this.context.biome.get().is(BiomeConditionSource.this.biomeNameTest);
                }
            }
            return new BiomeCondition();
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 instanceof BiomeConditionSource) {
                BiomeConditionSource $$1 = (BiomeConditionSource)$$0;
                return this.biomes.equals($$1.biomes);
            }
            return false;
        }

        public int hashCode() {
            return this.biomes.hashCode();
        }

        public String toString() {
            return "BiomeConditionSource[biomes=" + String.valueOf(this.biomes) + "]";
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    static final class NoiseThresholdConditionSource
    extends Record
    implements ConditionSource {
        private final ResourceKey<NormalNoise.NoiseParameters> noise;
        final double minThreshold;
        final double maxThreshold;
        static final KeyDispatchDataCodec<NoiseThresholdConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdConditionSource::noise), (App)Codec.DOUBLE.fieldOf("min_threshold").forGetter(NoiseThresholdConditionSource::minThreshold), (App)Codec.DOUBLE.fieldOf("max_threshold").forGetter(NoiseThresholdConditionSource::maxThreshold)).apply((Applicative)$$0, NoiseThresholdConditionSource::new)));

        NoiseThresholdConditionSource(ResourceKey<NormalNoise.NoiseParameters> $$0, double $$1, double $$2) {
            this.noise = $$0;
            this.minThreshold = $$1;
            this.maxThreshold = $$2;
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(final Context $$0) {
            final NormalNoise $$1 = $$0.randomState.getOrCreateNoise(this.noise);
            class NoiseThresholdCondition
            extends LazyXZCondition {
                NoiseThresholdCondition() {
                    super(context);
                }

                @Override
                protected boolean compute() {
                    double $$02 = $$1.getValue(this.context.blockX, 0.0, this.context.blockZ);
                    return $$02 >= NoiseThresholdConditionSource.this.minThreshold && $$02 <= NoiseThresholdConditionSource.this.maxThreshold;
                }
            }
            return new NoiseThresholdCondition();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{NoiseThresholdConditionSource.class, "noise;minThreshold;maxThreshold", "noise", "minThreshold", "maxThreshold"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NoiseThresholdConditionSource.class, "noise;minThreshold;maxThreshold", "noise", "minThreshold", "maxThreshold"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NoiseThresholdConditionSource.class, "noise;minThreshold;maxThreshold", "noise", "minThreshold", "maxThreshold"}, this, $$0);
        }

        public ResourceKey<NormalNoise.NoiseParameters> noise() {
            return this.noise;
        }

        public double minThreshold() {
            return this.minThreshold;
        }

        public double maxThreshold() {
            return this.maxThreshold;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    record VerticalGradientConditionSource(ResourceLocation randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements ConditionSource
    {
        static final KeyDispatchDataCodec<VerticalGradientConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("random_name").forGetter(VerticalGradientConditionSource::randomName), (App)VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(VerticalGradientConditionSource::trueAtAndBelow), (App)VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(VerticalGradientConditionSource::falseAtAndAbove)).apply((Applicative)$$0, VerticalGradientConditionSource::new)));

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(final Context $$0) {
            final int $$1 = this.trueAtAndBelow().resolveY($$0.context);
            final int $$2 = this.falseAtAndAbove().resolveY($$0.context);
            final PositionalRandomFactory $$3 = $$0.randomState.getOrCreateRandomFactory(this.randomName());
            class VerticalGradientCondition
            extends LazyYCondition {
                VerticalGradientCondition() {
                    super(context);
                }

                @Override
                protected boolean compute() {
                    int $$02 = this.context.blockY;
                    if ($$02 <= $$1) {
                        return true;
                    }
                    if ($$02 >= $$2) {
                        return false;
                    }
                    double $$12 = Mth.map((double)$$02, (double)$$1, (double)$$2, 1.0, 0.0);
                    RandomSource $$22 = $$3.at(this.context.blockX, $$02, this.context.blockZ);
                    return (double)$$22.nextFloat() < $$12;
                }
            }
            return new VerticalGradientCondition();
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    static final class Steep
    extends Enum<Steep>
    implements ConditionSource {
        public static final /* enum */ Steep INSTANCE = new Steep();
        static final KeyDispatchDataCodec<Steep> CODEC;
        private static final /* synthetic */ Steep[] $VALUES;

        public static Steep[] values() {
            return (Steep[])$VALUES.clone();
        }

        public static Steep valueOf(String $$0) {
            return Enum.valueOf(Steep.class, $$0);
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(Context $$0) {
            return $$0.steep;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }

        private static /* synthetic */ Steep[] b() {
            return new Steep[]{INSTANCE};
        }

        static {
            $VALUES = Steep.b();
            CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    static final class Hole
    extends Enum<Hole>
    implements ConditionSource {
        public static final /* enum */ Hole INSTANCE = new Hole();
        static final KeyDispatchDataCodec<Hole> CODEC;
        private static final /* synthetic */ Hole[] $VALUES;

        public static Hole[] values() {
            return (Hole[])$VALUES.clone();
        }

        public static Hole valueOf(String $$0) {
            return Enum.valueOf(Hole.class, $$0);
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(Context $$0) {
            return $$0.hole;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }

        private static /* synthetic */ Hole[] b() {
            return new Hole[]{INSTANCE};
        }

        static {
            $VALUES = Hole.b();
            CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    static final class AbovePreliminarySurface
    extends Enum<AbovePreliminarySurface>
    implements ConditionSource {
        public static final /* enum */ AbovePreliminarySurface INSTANCE = new AbovePreliminarySurface();
        static final KeyDispatchDataCodec<AbovePreliminarySurface> CODEC;
        private static final /* synthetic */ AbovePreliminarySurface[] $VALUES;

        public static AbovePreliminarySurface[] values() {
            return (AbovePreliminarySurface[])$VALUES.clone();
        }

        public static AbovePreliminarySurface valueOf(String $$0) {
            return Enum.valueOf(AbovePreliminarySurface.class, $$0);
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(Context $$0) {
            return $$0.abovePreliminarySurface;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }

        private static /* synthetic */ AbovePreliminarySurface[] b() {
            return new AbovePreliminarySurface[]{INSTANCE};
        }

        static {
            $VALUES = AbovePreliminarySurface.b();
            CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    static final class Temperature
    extends Enum<Temperature>
    implements ConditionSource {
        public static final /* enum */ Temperature INSTANCE = new Temperature();
        static final KeyDispatchDataCodec<Temperature> CODEC;
        private static final /* synthetic */ Temperature[] $VALUES;

        public static Temperature[] values() {
            return (Temperature[])$VALUES.clone();
        }

        public static Temperature valueOf(String $$0) {
            return Enum.valueOf(Temperature.class, $$0);
        }

        @Override
        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        @Override
        public Condition apply(Context $$0) {
            return $$0.temperature;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }

        private static /* synthetic */ Temperature[] b() {
            return new Temperature[]{INSTANCE};
        }

        static {
            $VALUES = Temperature.b();
            CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    record TestRuleSource(ConditionSource ifTrue, RuleSource thenRun) implements RuleSource
    {
        static final KeyDispatchDataCodec<TestRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ConditionSource.CODEC.fieldOf("if_true").forGetter(TestRuleSource::ifTrue), (App)RuleSource.CODEC.fieldOf("then_run").forGetter(TestRuleSource::thenRun)).apply((Applicative)$$0, TestRuleSource::new)));

        @Override
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRule apply(Context $$0) {
            return new TestRule((Condition)this.ifTrue.apply($$0), (SurfaceRule)this.thenRun.apply($$0));
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    public static interface RuleSource
    extends Function<Context, SurfaceRule> {
        public static final Codec<RuleSource> CODEC = BuiltInRegistries.MATERIAL_RULE.byNameCodec().dispatch($$0 -> $$0.codec().codec(), Function.identity());

        public static MapCodec<? extends RuleSource> bootstrap(Registry<MapCodec<? extends RuleSource>> $$0) {
            SurfaceRules.register($$0, "bandlands", Bandlands.CODEC);
            SurfaceRules.register($$0, "block", BlockRuleSource.CODEC);
            SurfaceRules.register($$0, "sequence", SequenceRuleSource.CODEC);
            return SurfaceRules.register($$0, "condition", TestRuleSource.CODEC);
        }

        public KeyDispatchDataCodec<? extends RuleSource> codec();
    }

    record SequenceRuleSource(List<RuleSource> sequence) implements RuleSource
    {
        static final KeyDispatchDataCodec<SequenceRuleSource> CODEC = KeyDispatchDataCodec.of(RuleSource.CODEC.listOf().xmap(SequenceRuleSource::new, SequenceRuleSource::sequence).fieldOf("sequence"));

        @Override
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRule apply(Context $$0) {
            if (this.sequence.size() == 1) {
                return (SurfaceRule)this.sequence.get(0).apply($$0);
            }
            ImmutableList.Builder $$1 = ImmutableList.builder();
            for (RuleSource $$2 : this.sequence) {
                $$1.add((SurfaceRule)$$2.apply($$0));
            }
            return new SequenceRule((List<SurfaceRule>)((Object)$$1.build()));
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    record BlockRuleSource(BlockState resultState, StateRule rule) implements RuleSource
    {
        static final KeyDispatchDataCodec<BlockRuleSource> CODEC = KeyDispatchDataCodec.of(BlockState.CODEC.xmap(BlockRuleSource::new, BlockRuleSource::resultState).fieldOf("result_state"));

        BlockRuleSource(BlockState $$0) {
            this($$0, new StateRule($$0));
        }

        @Override
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRule apply(Context $$0) {
            return this.rule;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }
    }

    static final class Bandlands
    extends Enum<Bandlands>
    implements RuleSource {
        public static final /* enum */ Bandlands INSTANCE = new Bandlands();
        static final KeyDispatchDataCodec<Bandlands> CODEC;
        private static final /* synthetic */ Bandlands[] $VALUES;

        public static Bandlands[] values() {
            return (Bandlands[])$VALUES.clone();
        }

        public static Bandlands valueOf(String $$0) {
            return Enum.valueOf(Bandlands.class, $$0);
        }

        @Override
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRule apply(Context $$0) {
            return $$0.system::getBand;
        }

        @Override
        public /* synthetic */ Object apply(Object object) {
            return this.apply((Context)object);
        }

        private static /* synthetic */ Bandlands[] b() {
            return new Bandlands[]{INSTANCE};
        }

        static {
            $VALUES = Bandlands.b();
            CODEC = KeyDispatchDataCodec.of(MapCodec.unit((Object)INSTANCE));
        }
    }

    record SequenceRule(List<SurfaceRule> rules) implements SurfaceRule
    {
        @Override
        @Nullable
        public BlockState tryApply(int $$0, int $$1, int $$2) {
            for (SurfaceRule $$3 : this.rules) {
                BlockState $$4 = $$3.tryApply($$0, $$1, $$2);
                if ($$4 == null) continue;
                return $$4;
            }
            return null;
        }
    }

    record TestRule(Condition condition, SurfaceRule followup) implements SurfaceRule
    {
        @Override
        @Nullable
        public BlockState tryApply(int $$0, int $$1, int $$2) {
            if (!this.condition.test()) {
                return null;
            }
            return this.followup.tryApply($$0, $$1, $$2);
        }
    }

    record StateRule(BlockState state) implements SurfaceRule
    {
        @Override
        public BlockState tryApply(int $$0, int $$1, int $$2) {
            return this.state;
        }
    }

    protected static interface SurfaceRule {
        @Nullable
        public BlockState tryApply(int var1, int var2, int var3);
    }

    record NotCondition(Condition target) implements Condition
    {
        @Override
        public boolean test() {
            return !this.target.test();
        }
    }

    static abstract class LazyYCondition
    extends LazyCondition {
        protected LazyYCondition(Context $$0) {
            super($$0);
        }

        @Override
        protected long getContextLastUpdate() {
            return this.context.lastUpdateY;
        }
    }

    static abstract class LazyXZCondition
    extends LazyCondition {
        protected LazyXZCondition(Context $$0) {
            super($$0);
        }

        @Override
        protected long getContextLastUpdate() {
            return this.context.lastUpdateXZ;
        }
    }

    static abstract class LazyCondition
    implements Condition {
        protected final Context context;
        private long lastUpdate;
        @Nullable
        Boolean result;

        protected LazyCondition(Context $$0) {
            this.context = $$0;
            this.lastUpdate = this.getContextLastUpdate() - 1L;
        }

        @Override
        public boolean test() {
            long $$0 = this.getContextLastUpdate();
            if ($$0 == this.lastUpdate) {
                if (this.result == null) {
                    throw new IllegalStateException("Update triggered but the result is null");
                }
                return this.result;
            }
            this.lastUpdate = $$0;
            this.result = this.compute();
            return this.result;
        }

        protected abstract long getContextLastUpdate();

        protected abstract boolean compute();
    }

    static interface Condition {
        public boolean test();
    }

    protected static final class Context {
        private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
        private static final int SURFACE_CELL_BITS = 4;
        private static final int SURFACE_CELL_SIZE = 16;
        private static final int SURFACE_CELL_MASK = 15;
        final SurfaceSystem system;
        final Condition temperature = new TemperatureHelperCondition(this);
        final Condition steep = new SteepMaterialCondition(this);
        final Condition hole = new HoleCondition(this);
        final Condition abovePreliminarySurface = new AbovePreliminarySurfaceCondition();
        final RandomState randomState;
        final ChunkAccess chunk;
        private final NoiseChunk noiseChunk;
        private final Function<BlockPos, Holder<Biome>> biomeGetter;
        final WorldGenerationContext context;
        private long lastPreliminarySurfaceCellOrigin = Long.MAX_VALUE;
        private final int[] preliminarySurfaceCache = new int[4];
        long lastUpdateXZ = -9223372036854775807L;
        int blockX;
        int blockZ;
        int surfaceDepth;
        private long lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
        private double surfaceSecondary;
        private long lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
        private int minSurfaceLevel;
        long lastUpdateY = -9223372036854775807L;
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Supplier<Holder<Biome>> biome;
        int blockY;
        int waterHeight;
        int stoneDepthBelow;
        int stoneDepthAbove;

        protected Context(SurfaceSystem $$0, RandomState $$1, ChunkAccess $$2, NoiseChunk $$3, Function<BlockPos, Holder<Biome>> $$4, Registry<Biome> $$5, WorldGenerationContext $$6) {
            this.system = $$0;
            this.randomState = $$1;
            this.chunk = $$2;
            this.noiseChunk = $$3;
            this.biomeGetter = $$4;
            this.context = $$6;
        }

        protected void updateXZ(int $$0, int $$1) {
            ++this.lastUpdateXZ;
            ++this.lastUpdateY;
            this.blockX = $$0;
            this.blockZ = $$1;
            this.surfaceDepth = this.system.getSurfaceDepth($$0, $$1);
        }

        protected void updateY(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
            ++this.lastUpdateY;
            this.biome = Suppliers.memoize(() -> this.biomeGetter.apply(this.pos.set($$3, $$4, $$5)));
            this.blockY = $$4;
            this.waterHeight = $$2;
            this.stoneDepthBelow = $$1;
            this.stoneDepthAbove = $$0;
        }

        protected double getSurfaceSecondary() {
            if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
                this.lastSurfaceDepth2Update = this.lastUpdateXZ;
                this.surfaceSecondary = this.system.getSurfaceSecondary(this.blockX, this.blockZ);
            }
            return this.surfaceSecondary;
        }

        public int getSeaLevel() {
            return this.system.getSeaLevel();
        }

        private static int blockCoordToSurfaceCell(int $$0) {
            return $$0 >> 4;
        }

        private static int surfaceCellToBlockCoord(int $$0) {
            return $$0 << 4;
        }

        protected int getMinSurfaceLevel() {
            if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
                int $$1;
                this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
                int $$0 = Context.blockCoordToSurfaceCell(this.blockX);
                long $$2 = ChunkPos.asLong($$0, $$1 = Context.blockCoordToSurfaceCell(this.blockZ));
                if (this.lastPreliminarySurfaceCellOrigin != $$2) {
                    this.lastPreliminarySurfaceCellOrigin = $$2;
                    this.preliminarySurfaceCache[0] = this.noiseChunk.preliminarySurfaceLevel(Context.surfaceCellToBlockCoord($$0), Context.surfaceCellToBlockCoord($$1));
                    this.preliminarySurfaceCache[1] = this.noiseChunk.preliminarySurfaceLevel(Context.surfaceCellToBlockCoord($$0 + 1), Context.surfaceCellToBlockCoord($$1));
                    this.preliminarySurfaceCache[2] = this.noiseChunk.preliminarySurfaceLevel(Context.surfaceCellToBlockCoord($$0), Context.surfaceCellToBlockCoord($$1 + 1));
                    this.preliminarySurfaceCache[3] = this.noiseChunk.preliminarySurfaceLevel(Context.surfaceCellToBlockCoord($$0 + 1), Context.surfaceCellToBlockCoord($$1 + 1));
                }
                int $$3 = Mth.floor(Mth.lerp2((float)(this.blockX & 0xF) / 16.0f, (float)(this.blockZ & 0xF) / 16.0f, this.preliminarySurfaceCache[0], this.preliminarySurfaceCache[1], this.preliminarySurfaceCache[2], this.preliminarySurfaceCache[3]));
                this.minSurfaceLevel = $$3 + this.surfaceDepth - 8;
            }
            return this.minSurfaceLevel;
        }

        static class TemperatureHelperCondition
        extends LazyYCondition {
            TemperatureHelperCondition(Context $$0) {
                super($$0);
            }

            @Override
            protected boolean compute() {
                return this.context.biome.get().value().coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ), this.context.getSeaLevel());
            }
        }

        static class SteepMaterialCondition
        extends LazyXZCondition {
            SteepMaterialCondition(Context $$0) {
                super($$0);
            }

            @Override
            protected boolean compute() {
                int $$10;
                int $$0 = this.context.blockX & 0xF;
                int $$1 = this.context.blockZ & 0xF;
                int $$2 = Math.max($$1 - 1, 0);
                int $$3 = Math.min($$1 + 1, 15);
                ChunkAccess $$4 = this.context.chunk;
                int $$5 = $$4.getHeight(Heightmap.Types.WORLD_SURFACE_WG, $$0, $$2);
                int $$6 = $$4.getHeight(Heightmap.Types.WORLD_SURFACE_WG, $$0, $$3);
                if ($$6 >= $$5 + 4) {
                    return true;
                }
                int $$7 = Math.max($$0 - 1, 0);
                int $$8 = Math.min($$0 + 1, 15);
                int $$9 = $$4.getHeight(Heightmap.Types.WORLD_SURFACE_WG, $$7, $$1);
                return $$9 >= ($$10 = $$4.getHeight(Heightmap.Types.WORLD_SURFACE_WG, $$8, $$1)) + 4;
            }
        }

        static final class HoleCondition
        extends LazyXZCondition {
            HoleCondition(Context $$0) {
                super($$0);
            }

            @Override
            protected boolean compute() {
                return this.context.surfaceDepth <= 0;
            }
        }

        final class AbovePreliminarySurfaceCondition
        implements Condition {
            AbovePreliminarySurfaceCondition() {
            }

            @Override
            public boolean test() {
                return Context.this.blockY >= Context.this.getMinSurfaceLevel();
            }
        }
    }
}


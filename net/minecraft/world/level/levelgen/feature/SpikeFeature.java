/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.phys.AABB;

public class SpikeFeature
extends Feature<SpikeConfiguration> {
    public static final int NUMBER_OF_SPIKES = 10;
    private static final int SPIKE_DISTANCE = 42;
    private static final LoadingCache<Long, List<EndSpike>> SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new SpikeCacheLoader());

    public SpikeFeature(Codec<SpikeConfiguration> $$0) {
        super($$0);
    }

    public static List<EndSpike> getSpikesForLevel(WorldGenLevel $$0) {
        RandomSource $$1 = RandomSource.create($$0.getSeed());
        long $$2 = $$1.nextLong() & 0xFFFFL;
        return SPIKE_CACHE.getUnchecked($$2);
    }

    @Override
    public boolean place(FeaturePlaceContext<SpikeConfiguration> $$0) {
        SpikeConfiguration $$1 = $$0.config();
        WorldGenLevel $$2 = $$0.level();
        RandomSource $$3 = $$0.random();
        BlockPos $$4 = $$0.origin();
        List<EndSpike> $$5 = $$1.getSpikes();
        if ($$5.isEmpty()) {
            $$5 = SpikeFeature.getSpikesForLevel($$2);
        }
        for (EndSpike $$6 : $$5) {
            if (!$$6.isCenterWithinChunk($$4)) continue;
            this.placeSpike($$2, $$3, $$1, $$6);
        }
        return true;
    }

    private void placeSpike(ServerLevelAccessor $$0, RandomSource $$1, SpikeConfiguration $$2, EndSpike $$3) {
        EndCrystal $$19;
        int $$4 = $$3.getRadius();
        for (BlockPos $$5 : BlockPos.betweenClosed(new BlockPos($$3.getCenterX() - $$4, $$0.getMinY(), $$3.getCenterZ() - $$4), new BlockPos($$3.getCenterX() + $$4, $$3.getHeight() + 10, $$3.getCenterZ() + $$4))) {
            if ($$5.distToLowCornerSqr($$3.getCenterX(), $$5.getY(), $$3.getCenterZ()) <= (double)($$4 * $$4 + 1) && $$5.getY() < $$3.getHeight()) {
                this.setBlock($$0, $$5, Blocks.OBSIDIAN.defaultBlockState());
                continue;
            }
            if ($$5.getY() <= 65) continue;
            this.setBlock($$0, $$5, Blocks.AIR.defaultBlockState());
        }
        if ($$3.isGuarded()) {
            int $$6 = -2;
            int $$7 = 2;
            int $$8 = 3;
            BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
            for (int $$10 = -2; $$10 <= 2; ++$$10) {
                for (int $$11 = -2; $$11 <= 2; ++$$11) {
                    for (int $$12 = 0; $$12 <= 3; ++$$12) {
                        boolean $$15;
                        boolean $$13 = Mth.abs($$10) == 2;
                        boolean $$14 = Mth.abs($$11) == 2;
                        boolean bl = $$15 = $$12 == 3;
                        if (!$$13 && !$$14 && !$$15) continue;
                        boolean $$16 = $$10 == -2 || $$10 == 2 || $$15;
                        boolean $$17 = $$11 == -2 || $$11 == 2 || $$15;
                        BlockState $$18 = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, $$16 && $$11 != -2)).setValue(IronBarsBlock.SOUTH, $$16 && $$11 != 2)).setValue(IronBarsBlock.WEST, $$17 && $$10 != -2)).setValue(IronBarsBlock.EAST, $$17 && $$10 != 2);
                        this.setBlock($$0, $$9.set($$3.getCenterX() + $$10, $$3.getHeight() + $$12, $$3.getCenterZ() + $$11), $$18);
                    }
                }
            }
        }
        if (($$19 = EntityType.END_CRYSTAL.create($$0.getLevel(), EntitySpawnReason.STRUCTURE)) != null) {
            $$19.setBeamTarget($$2.getCrystalBeamTarget());
            $$19.setInvulnerable($$2.isCrystalInvulnerable());
            $$19.snapTo((double)$$3.getCenterX() + 0.5, $$3.getHeight() + 1, (double)$$3.getCenterZ() + 0.5, $$1.nextFloat() * 360.0f, 0.0f);
            $$0.addFreshEntity($$19);
            BlockPos $$20 = $$19.blockPosition();
            this.setBlock($$0, $$20.below(), Blocks.BEDROCK.defaultBlockState());
            this.setBlock($$0, $$20, FireBlock.getState($$0, $$20));
        }
    }

    public static class EndSpike {
        public static final Codec<EndSpike> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.INT.fieldOf("centerX").orElse((Object)0).forGetter($$0 -> $$0.centerX), (App)Codec.INT.fieldOf("centerZ").orElse((Object)0).forGetter($$0 -> $$0.centerZ), (App)Codec.INT.fieldOf("radius").orElse((Object)0).forGetter($$0 -> $$0.radius), (App)Codec.INT.fieldOf("height").orElse((Object)0).forGetter($$0 -> $$0.height), (App)Codec.BOOL.fieldOf("guarded").orElse((Object)false).forGetter($$0 -> $$0.guarded)).apply((Applicative)$$02, EndSpike::new));
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final AABB topBoundingBox;

        public EndSpike(int $$0, int $$1, int $$2, int $$3, boolean $$4) {
            this.centerX = $$0;
            this.centerZ = $$1;
            this.radius = $$2;
            this.height = $$3;
            this.guarded = $$4;
            this.topBoundingBox = new AABB($$0 - $$2, DimensionType.MIN_Y, $$1 - $$2, $$0 + $$2, DimensionType.MAX_Y, $$1 + $$2);
        }

        public boolean isCenterWithinChunk(BlockPos $$0) {
            return SectionPos.blockToSectionCoord($$0.getX()) == SectionPos.blockToSectionCoord(this.centerX) && SectionPos.blockToSectionCoord($$0.getZ()) == SectionPos.blockToSectionCoord(this.centerZ);
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public AABB getTopBoundingBox() {
            return this.topBoundingBox;
        }
    }

    static class SpikeCacheLoader
    extends CacheLoader<Long, List<EndSpike>> {
        SpikeCacheLoader() {
        }

        @Override
        public List<EndSpike> load(Long $$0) {
            IntArrayList $$1 = Util.toShuffledList(IntStream.range(0, 10), RandomSource.create($$0));
            ArrayList<EndSpike> $$2 = Lists.newArrayList();
            for (int $$3 = 0; $$3 < 10; ++$$3) {
                int $$4 = Mth.floor(42.0 * Math.cos(2.0 * (-Math.PI + 0.3141592653589793 * (double)$$3)));
                int $$5 = Mth.floor(42.0 * Math.sin(2.0 * (-Math.PI + 0.3141592653589793 * (double)$$3)));
                int $$6 = $$1.get($$3);
                int $$7 = 2 + $$6 / 3;
                int $$8 = 76 + $$6 * 3;
                boolean $$9 = $$6 == 1 || $$6 == 2;
                $$2.add(new EndSpike($$4, $$5, $$7, $$8, $$9));
            }
            return $$2;
        }

        @Override
        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((Long)object);
        }
    }
}


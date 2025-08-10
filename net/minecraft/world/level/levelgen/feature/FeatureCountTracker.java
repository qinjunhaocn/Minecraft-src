/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureCountTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LoadingCache<ServerLevel, LevelData> data = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<ServerLevel, LevelData>(){

        @Override
        public LevelData load(ServerLevel $$0) {
            return new LevelData((Object2IntMap<FeatureData>)Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap()), new MutableInt(0));
        }

        @Override
        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((ServerLevel)object);
        }
    });

    public static void chunkDecorated(ServerLevel $$0) {
        try {
            data.get($$0).chunksWithFeatures().increment();
        } catch (Exception $$1) {
            LOGGER.error("Failed to increment chunk count", $$1);
        }
    }

    public static void featurePlaced(ServerLevel $$02, ConfiguredFeature<?, ?> $$12, Optional<PlacedFeature> $$2) {
        try {
            data.get($$02).featureData().computeInt((Object)new FeatureData($$12, $$2), ($$0, $$1) -> $$1 == null ? 1 : $$1 + 1);
        } catch (Exception $$3) {
            LOGGER.error("Failed to increment feature count", $$3);
        }
    }

    public static void clearCounts() {
        data.invalidateAll();
        LOGGER.debug("Cleared feature counts");
    }

    public static void logCounts() {
        LOGGER.debug("Logging feature counts:");
        data.asMap().forEach(($$0, $$1) -> {
            String $$2 = $$0.dimension().location().toString();
            boolean $$3 = $$0.getServer().isRunning();
            HolderLookup.RegistryLookup $$4 = $$0.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
            String $$5 = ($$3 ? "running" : "dead") + " " + $$2;
            Integer $$6 = $$1.chunksWithFeatures().getValue();
            LOGGER.debug($$5 + " total_chunks: " + $$6);
            $$1.featureData().forEach((arg_0, arg_1) -> FeatureCountTracker.lambda$logCounts$1($$5, $$6, (Registry)$$4, arg_0, arg_1));
        });
    }

    private static /* synthetic */ void lambda$logCounts$1(String $$0, Integer $$1, Registry $$2, FeatureData $$3, Integer $$4) {
        LOGGER.debug($$0 + " " + String.format(Locale.ROOT, "%10d ", $$4) + String.format(Locale.ROOT, "%10f ", (double)$$4.intValue() / (double)$$1.intValue()) + String.valueOf($$3.topFeature().flatMap($$2::getResourceKey).map(ResourceKey::location)) + " " + String.valueOf($$3.feature().feature()) + " " + String.valueOf($$3.feature()));
    }

    record LevelData(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
    }

    record FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
    }
}


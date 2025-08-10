/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;

public class EndFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_PLATFORM = FeatureUtils.createKey("end_platform");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_SPIKE = FeatureUtils.createKey("end_spike");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_GATEWAY_RETURN = FeatureUtils.createKey("end_gateway_return");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_GATEWAY_DELAYED = FeatureUtils.createKey("end_gateway_delayed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHORUS_PLANT = FeatureUtils.createKey("chorus_plant");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_ISLAND = FeatureUtils.createKey("end_island");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> $$0) {
        FeatureUtils.register($$0, END_PLATFORM, Feature.END_PLATFORM);
        FeatureUtils.register($$0, END_SPIKE, Feature.END_SPIKE, new SpikeConfiguration(false, (List<SpikeFeature.EndSpike>)ImmutableList.of(), null));
        FeatureUtils.register($$0, END_GATEWAY_RETURN, Feature.END_GATEWAY, EndGatewayConfiguration.knownExit(ServerLevel.END_SPAWN_POINT, true));
        FeatureUtils.register($$0, END_GATEWAY_DELAYED, Feature.END_GATEWAY, EndGatewayConfiguration.delayedExitSearch());
        FeatureUtils.register($$0, CHORUS_PLANT, Feature.CHORUS_PLANT);
        FeatureUtils.register($$0, END_ISLAND, Feature.END_ISLAND);
    }
}


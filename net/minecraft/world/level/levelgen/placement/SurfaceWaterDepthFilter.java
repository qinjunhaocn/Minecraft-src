/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class SurfaceWaterDepthFilter
extends PlacementFilter {
    public static final MapCodec<SurfaceWaterDepthFilter> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.INT.fieldOf("max_water_depth").forGetter($$0 -> $$0.maxWaterDepth)).apply((Applicative)$$02, SurfaceWaterDepthFilter::new));
    private final int maxWaterDepth;

    private SurfaceWaterDepthFilter(int $$0) {
        this.maxWaterDepth = $$0;
    }

    public static SurfaceWaterDepthFilter forMaxDepth(int $$0) {
        return new SurfaceWaterDepthFilter($$0);
    }

    @Override
    protected boolean shouldPlace(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        int $$3 = $$0.getHeight(Heightmap.Types.OCEAN_FLOOR, $$2.getX(), $$2.getZ());
        int $$4 = $$0.getHeight(Heightmap.Types.WORLD_SURFACE, $$2.getX(), $$2.getZ());
        return $$4 - $$3 <= this.maxWaterDepth;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.SURFACE_WATER_DEPTH_FILTER;
    }
}


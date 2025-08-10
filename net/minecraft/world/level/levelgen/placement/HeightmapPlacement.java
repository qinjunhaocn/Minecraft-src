/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class HeightmapPlacement
extends PlacementModifier {
    public static final MapCodec<HeightmapPlacement> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Heightmap.Types.CODEC.fieldOf("heightmap").forGetter($$0 -> $$0.heightmap)).apply((Applicative)$$02, HeightmapPlacement::new));
    private final Heightmap.Types heightmap;

    private HeightmapPlacement(Heightmap.Types $$0) {
        this.heightmap = $$0;
    }

    public static HeightmapPlacement onHeightmap(Heightmap.Types $$0) {
        return new HeightmapPlacement($$0);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        int $$4;
        int $$3 = $$2.getX();
        int $$5 = $$0.getHeight(this.heightmap, $$3, $$4 = $$2.getZ());
        if ($$5 > $$0.getMinY()) {
            return Stream.of(new BlockPos($$3, $$5, $$4));
        }
        return Stream.of(new BlockPos[0]);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.HEIGHTMAP;
    }
}


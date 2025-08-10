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
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class FixedPlacement
extends PlacementModifier {
    public static final MapCodec<FixedPlacement> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BlockPos.CODEC.listOf().fieldOf("positions").forGetter($$0 -> $$0.positions)).apply((Applicative)$$02, FixedPlacement::new));
    private final List<BlockPos> positions;

    public static FixedPlacement a(BlockPos ... $$0) {
        return new FixedPlacement(List.of((Object[])$$0));
    }

    private FixedPlacement(List<BlockPos> $$0) {
        this.positions = $$0;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext $$0, RandomSource $$1, BlockPos $$22) {
        int $$3 = SectionPos.blockToSectionCoord($$22.getX());
        int $$4 = SectionPos.blockToSectionCoord($$22.getZ());
        boolean $$5 = false;
        for (BlockPos $$6 : this.positions) {
            if (!FixedPlacement.isSameChunk($$3, $$4, $$6)) continue;
            $$5 = true;
            break;
        }
        if (!$$5) {
            return Stream.empty();
        }
        return this.positions.stream().filter($$2 -> FixedPlacement.isSameChunk($$3, $$4, $$2));
    }

    private static boolean isSameChunk(int $$0, int $$1, BlockPos $$2) {
        return $$0 == SectionPos.blockToSectionCoord($$2.getX()) && $$1 == SectionPos.blockToSectionCoord($$2.getZ());
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.FIXED_PLACEMENT;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public abstract class PlacementModifier {
    public static final Codec<PlacementModifier> CODEC = BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.byNameCodec().dispatch(PlacementModifier::type, PlacementModifierType::codec);

    public abstract Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3);

    public abstract PlacementModifierType<?> type();
}


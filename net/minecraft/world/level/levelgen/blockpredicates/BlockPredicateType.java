/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.blockpredicates.AllOfPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.AnyOfPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.HasSturdyFacePredicate;
import net.minecraft.world.level.levelgen.blockpredicates.InsideWorldBoundsPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlockTagPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingFluidsPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.NotPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.ReplaceablePredicate;
import net.minecraft.world.level.levelgen.blockpredicates.SolidPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.TrueBlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.UnobstructedPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.WouldSurvivePredicate;

public interface BlockPredicateType<P extends BlockPredicate> {
    public static final BlockPredicateType<MatchingBlocksPredicate> MATCHING_BLOCKS = BlockPredicateType.register("matching_blocks", MatchingBlocksPredicate.CODEC);
    public static final BlockPredicateType<MatchingBlockTagPredicate> MATCHING_BLOCK_TAG = BlockPredicateType.register("matching_block_tag", MatchingBlockTagPredicate.CODEC);
    public static final BlockPredicateType<MatchingFluidsPredicate> MATCHING_FLUIDS = BlockPredicateType.register("matching_fluids", MatchingFluidsPredicate.CODEC);
    public static final BlockPredicateType<HasSturdyFacePredicate> HAS_STURDY_FACE = BlockPredicateType.register("has_sturdy_face", HasSturdyFacePredicate.CODEC);
    public static final BlockPredicateType<SolidPredicate> SOLID = BlockPredicateType.register("solid", SolidPredicate.CODEC);
    public static final BlockPredicateType<ReplaceablePredicate> REPLACEABLE = BlockPredicateType.register("replaceable", ReplaceablePredicate.CODEC);
    public static final BlockPredicateType<WouldSurvivePredicate> WOULD_SURVIVE = BlockPredicateType.register("would_survive", WouldSurvivePredicate.CODEC);
    public static final BlockPredicateType<InsideWorldBoundsPredicate> INSIDE_WORLD_BOUNDS = BlockPredicateType.register("inside_world_bounds", InsideWorldBoundsPredicate.CODEC);
    public static final BlockPredicateType<AnyOfPredicate> ANY_OF = BlockPredicateType.register("any_of", AnyOfPredicate.CODEC);
    public static final BlockPredicateType<AllOfPredicate> ALL_OF = BlockPredicateType.register("all_of", AllOfPredicate.CODEC);
    public static final BlockPredicateType<NotPredicate> NOT = BlockPredicateType.register("not", NotPredicate.CODEC);
    public static final BlockPredicateType<TrueBlockPredicate> TRUE = BlockPredicateType.register("true", TrueBlockPredicate.CODEC);
    public static final BlockPredicateType<UnobstructedPredicate> UNOBSTRUCTED = BlockPredicateType.register("unobstructed", UnobstructedPredicate.CODEC);

    public MapCodec<P> codec();

    private static <P extends BlockPredicate> BlockPredicateType<P> register(String $$0, MapCodec<P> $$1) {
        return Registry.register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, $$0, () -> $$1);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

class MatchingBlocksPredicate
extends StateTestingPredicate {
    private final HolderSet<Block> blocks;
    public static final MapCodec<MatchingBlocksPredicate> CODEC = RecordCodecBuilder.mapCodec($$02 -> MatchingBlocksPredicate.stateTestingCodec($$02).and((App)RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter($$0 -> $$0.blocks)).apply((Applicative)$$02, MatchingBlocksPredicate::new));

    public MatchingBlocksPredicate(Vec3i $$0, HolderSet<Block> $$1) {
        super($$0);
        this.blocks = $$1;
    }

    @Override
    protected boolean test(BlockState $$0) {
        return $$0.is(this.blocks);
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.MATCHING_BLOCKS;
    }
}


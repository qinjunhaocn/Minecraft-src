/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

@Deprecated
public class SolidPredicate
extends StateTestingPredicate {
    public static final MapCodec<SolidPredicate> CODEC = RecordCodecBuilder.mapCodec($$0 -> SolidPredicate.stateTestingCodec($$0).apply((Applicative)$$0, SolidPredicate::new));

    public SolidPredicate(Vec3i $$0) {
        super($$0);
    }

    @Override
    protected boolean test(BlockState $$0) {
        return $$0.isSolid();
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.SOLID;
    }
}


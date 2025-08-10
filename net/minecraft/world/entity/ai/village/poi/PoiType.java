/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.village.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;

public record PoiType(Set<BlockState> matchingStates, int maxTickets, int validRange) {
    public static final Predicate<Holder<PoiType>> NONE = $$0 -> false;

    public PoiType(Set<BlockState> $$0, int $$1, int $$2) {
        this.matchingStates = $$0 = Set.copyOf($$0);
        this.maxTickets = $$1;
        this.validRange = $$2;
    }

    public boolean is(BlockState $$0) {
        return this.matchingStates.contains($$0);
    }
}


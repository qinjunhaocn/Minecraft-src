/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;

@FunctionalInterface
public interface PoolAliasLookup {
    public static final PoolAliasLookup EMPTY = $$0 -> $$0;

    public ResourceKey<StructureTemplatePool> lookup(ResourceKey<StructureTemplatePool> var1);

    public static PoolAliasLookup create(List<PoolAliasBinding> $$0, BlockPos $$12, long $$22) {
        if ($$0.isEmpty()) {
            return EMPTY;
        }
        RandomSource $$3 = RandomSource.create($$22).forkPositional().at($$12);
        ImmutableMap.Builder $$4 = ImmutableMap.builder();
        $$0.forEach($$2 -> $$2.forEachResolved($$3, $$4::put));
        ImmutableMap $$5 = $$4.build();
        return $$1 -> Objects.requireNonNull($$5.getOrDefault($$1, $$1), () -> "alias " + String.valueOf($$1.location()) + " was mapped to null value");
    }
}


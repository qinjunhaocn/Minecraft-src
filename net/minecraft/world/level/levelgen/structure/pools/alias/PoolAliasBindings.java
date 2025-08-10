/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.DirectPoolAlias;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomGroupPoolAlias;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomPoolAlias;

public class PoolAliasBindings {
    public static MapCodec<? extends PoolAliasBinding> bootstrap(Registry<MapCodec<? extends PoolAliasBinding>> $$0) {
        Registry.register($$0, "random", RandomPoolAlias.CODEC);
        Registry.register($$0, "random_group", RandomGroupPoolAlias.CODEC);
        return Registry.register($$0, "direct", DirectPoolAlias.CODEC);
    }

    public static void registerTargetsAsPools(BootstrapContext<StructureTemplatePool> $$02, Holder<StructureTemplatePool> $$1, List<PoolAliasBinding> $$22) {
        $$22.stream().flatMap(PoolAliasBinding::allTargets).map($$0 -> $$0.location().getPath()).forEach($$2 -> Pools.register($$02, $$2, new StructureTemplatePool($$1, List.of((Object)Pair.of(StructurePoolElement.single($$2), (Object)1)), StructureTemplatePool.Projection.RIGID)));
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.DirectPoolAlias;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomGroupPoolAlias;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomPoolAlias;

public interface PoolAliasBinding {
    public static final Codec<PoolAliasBinding> CODEC = BuiltInRegistries.POOL_ALIAS_BINDING_TYPE.byNameCodec().dispatch(PoolAliasBinding::codec, Function.identity());

    public void forEachResolved(RandomSource var1, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> var2);

    public Stream<ResourceKey<StructureTemplatePool>> allTargets();

    public static DirectPoolAlias direct(String $$0, String $$1) {
        return PoolAliasBinding.direct(Pools.createKey($$0), Pools.createKey($$1));
    }

    public static DirectPoolAlias direct(ResourceKey<StructureTemplatePool> $$0, ResourceKey<StructureTemplatePool> $$1) {
        return new DirectPoolAlias($$0, $$1);
    }

    public static RandomPoolAlias random(String $$0, WeightedList<String> $$12) {
        WeightedList.Builder $$2 = WeightedList.builder();
        $$12.unwrap().forEach($$1 -> $$2.add(Pools.createKey((String)$$1.value()), $$1.weight()));
        return PoolAliasBinding.random(Pools.createKey($$0), $$2.build());
    }

    public static RandomPoolAlias random(ResourceKey<StructureTemplatePool> $$0, WeightedList<ResourceKey<StructureTemplatePool>> $$1) {
        return new RandomPoolAlias($$0, $$1);
    }

    public static RandomGroupPoolAlias randomGroup(WeightedList<List<PoolAliasBinding>> $$0) {
        return new RandomGroupPoolAlias($$0);
    }

    public MapCodec<? extends PoolAliasBinding> codec();
}


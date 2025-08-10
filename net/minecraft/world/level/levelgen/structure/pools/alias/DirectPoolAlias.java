/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;

public record DirectPoolAlias(ResourceKey<StructureTemplatePool> alias, ResourceKey<StructureTemplatePool> target) implements PoolAliasBinding
{
    static MapCodec<DirectPoolAlias> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(DirectPoolAlias::alias), (App)ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("target").forGetter(DirectPoolAlias::target)).apply((Applicative)$$0, DirectPoolAlias::new));

    @Override
    public void forEachResolved(RandomSource $$0, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> $$1) {
        $$1.accept(this.alias, this.target);
    }

    @Override
    public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
        return Stream.of(this.target);
    }

    public MapCodec<DirectPoolAlias> codec() {
        return CODEC;
    }
}


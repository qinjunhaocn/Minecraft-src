/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.entity.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnContext;

public interface SpawnCondition
extends PriorityProvider.SelectorCondition<SpawnContext> {
    public static final Codec<SpawnCondition> CODEC = BuiltInRegistries.SPAWN_CONDITION_TYPE.byNameCodec().dispatch(SpawnCondition::codec, $$0 -> $$0);

    public MapCodec<? extends SpawnCondition> codec();
}


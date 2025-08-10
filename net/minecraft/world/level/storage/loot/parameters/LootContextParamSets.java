/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootContextParamSets {
    private static final BiMap<ResourceLocation, ContextKeySet> REGISTRY = HashBiMap.create();
    public static final Codec<ContextKeySet> CODEC = ResourceLocation.CODEC.comapFlatMap($$0 -> Optional.ofNullable((ContextKeySet)REGISTRY.get($$0)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No parameter set exists with id: '" + String.valueOf($$0) + "'")), REGISTRY.inverse()::get);
    public static final ContextKeySet EMPTY = LootContextParamSets.register("empty", $$0 -> {});
    public static final ContextKeySet CHEST = LootContextParamSets.register("chest", $$0 -> $$0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
    public static final ContextKeySet COMMAND = LootContextParamSets.register("command", $$0 -> $$0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
    public static final ContextKeySet SELECTOR = LootContextParamSets.register("selector", $$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
    public static final ContextKeySet FISHING = LootContextParamSets.register("fishing", $$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY));
    public static final ContextKeySet ENTITY = LootContextParamSets.register("entity", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.ATTACKING_ENTITY).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER));
    public static final ContextKeySet EQUIPMENT = LootContextParamSets.register("equipment", $$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
    public static final ContextKeySet ARCHAEOLOGY = LootContextParamSets.register("archaeology", $$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL));
    public static final ContextKeySet GIFT = LootContextParamSets.register("gift", $$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
    public static final ContextKeySet PIGLIN_BARTER = LootContextParamSets.register("barter", $$0 -> $$0.required(LootContextParams.THIS_ENTITY));
    public static final ContextKeySet VAULT = LootContextParamSets.register("vault", $$0 -> $$0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.TOOL));
    public static final ContextKeySet ADVANCEMENT_REWARD = LootContextParamSets.register("advancement_reward", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN));
    public static final ContextKeySet ADVANCEMENT_ENTITY = LootContextParamSets.register("advancement_entity", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN));
    public static final ContextKeySet ADVANCEMENT_LOCATION = LootContextParamSets.register("advancement_location", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).required(LootContextParams.BLOCK_STATE));
    public static final ContextKeySet BLOCK_USE = LootContextParamSets.register("block_use", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE));
    public static final ContextKeySet ALL_PARAMS = LootContextParamSets.register("generic", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.ATTACKING_ENTITY).required(LootContextParams.DIRECT_ATTACKING_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS));
    public static final ContextKeySet BLOCK = LootContextParamSets.register("block", $$0 -> $$0.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS));
    public static final ContextKeySet SHEARING = LootContextParamSets.register("shearing", $$0 -> $$0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(LootContextParams.TOOL));
    public static final ContextKeySet ENCHANTED_DAMAGE = LootContextParamSets.register("enchanted_damage", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.DIRECT_ATTACKING_ENTITY).optional(LootContextParams.ATTACKING_ENTITY));
    public static final ContextKeySet ENCHANTED_ITEM = LootContextParamSets.register("enchanted_item", $$0 -> $$0.required(LootContextParams.TOOL).required(LootContextParams.ENCHANTMENT_LEVEL));
    public static final ContextKeySet ENCHANTED_LOCATION = LootContextParamSets.register("enchanted_location", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.ENCHANTMENT_ACTIVE));
    public static final ContextKeySet ENCHANTED_ENTITY = LootContextParamSets.register("enchanted_entity", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN));
    public static final ContextKeySet HIT_BLOCK = LootContextParamSets.register("hit_block", $$0 -> $$0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ENCHANTMENT_LEVEL).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE));

    private static ContextKeySet register(String $$0, Consumer<ContextKeySet.Builder> $$1) {
        ContextKeySet.Builder $$2 = new ContextKeySet.Builder();
        $$1.accept($$2);
        ContextKeySet $$3 = $$2.build();
        ResourceLocation $$4 = ResourceLocation.withDefaultNamespace($$0);
        ContextKeySet $$5 = REGISTRY.put($$4, $$3);
        if ($$5 != null) {
            throw new IllegalStateException("Loot table parameter set " + String.valueOf($$4) + " is already registered");
        }
        return $$3;
    }
}


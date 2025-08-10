/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.enchantment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record TargetedConditionalEffect<T>(EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, Optional<LootItemCondition> requirements) {
    public static <S> Codec<TargetedConditionalEffect<S>> codec(Codec<S> $$0, ContextKeySet $$1) {
        return RecordCodecBuilder.create($$2 -> $$2.group((App)EnchantmentTarget.CODEC.fieldOf("enchanted").forGetter(TargetedConditionalEffect::enchanted), (App)EnchantmentTarget.CODEC.fieldOf("affected").forGetter(TargetedConditionalEffect::affected), (App)$$0.fieldOf("effect").forGetter(TargetedConditionalEffect::effect), (App)ConditionalEffect.conditionCodec($$1).optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)).apply((Applicative)$$2, TargetedConditionalEffect::new));
    }

    public static <S> Codec<TargetedConditionalEffect<S>> equipmentDropsCodec(Codec<S> $$0, ContextKeySet $$1) {
        return RecordCodecBuilder.create($$22 -> $$22.group((App)EnchantmentTarget.CODEC.validate($$0 -> $$0 != EnchantmentTarget.DAMAGING_ENTITY ? DataResult.success((Object)$$0) : DataResult.error(() -> "enchanted must be attacker or victim")).fieldOf("enchanted").forGetter(TargetedConditionalEffect::enchanted), (App)$$0.fieldOf("effect").forGetter(TargetedConditionalEffect::effect), (App)ConditionalEffect.conditionCodec($$1).optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)).apply((Applicative)$$22, ($$0, $$1, $$2) -> new TargetedConditionalEffect<Object>((EnchantmentTarget)$$0, EnchantmentTarget.VICTIM, $$1, (Optional<LootItemCondition>)$$2)));
    }

    public boolean matches(LootContext $$0) {
        if (this.requirements.isEmpty()) {
            return true;
        }
        return this.requirements.get().test($$0);
    }
}


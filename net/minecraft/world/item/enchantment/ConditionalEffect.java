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
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ConditionalEffect<T>(T effect, Optional<LootItemCondition> requirements) {
    public static Codec<LootItemCondition> conditionCodec(ContextKeySet $$0) {
        return LootItemCondition.DIRECT_CODEC.validate($$1 -> {
            ProblemReporter.Collector $$2 = new ProblemReporter.Collector();
            ValidationContext $$3 = new ValidationContext($$2, $$0);
            $$1.validate($$3);
            if (!$$2.isEmpty()) {
                return DataResult.error(() -> "Validation error in enchantment effect condition: " + $$2.getReport());
            }
            return DataResult.success((Object)$$1);
        });
    }

    public static <T> Codec<ConditionalEffect<T>> codec(Codec<T> $$0, ContextKeySet $$1) {
        return RecordCodecBuilder.create($$2 -> $$2.group((App)$$0.fieldOf("effect").forGetter(ConditionalEffect::effect), (App)ConditionalEffect.conditionCodec($$1).optionalFieldOf("requirements").forGetter(ConditionalEffect::requirements)).apply((Applicative)$$2, ConditionalEffect::new));
    }

    public boolean matches(LootContext $$0) {
        if (this.requirements.isEmpty()) {
            return true;
        }
        return this.requirements.get().test($$0);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.phys.Vec3;

public record DamageSourceCondition(Optional<DamageSourcePredicate> predicate) implements LootItemCondition
{
    public static final MapCodec<DamageSourceCondition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DamageSourcePredicate.CODEC.optionalFieldOf("predicate").forGetter(DamageSourceCondition::predicate)).apply((Applicative)$$0, DamageSourceCondition::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.DAMAGE_SOURCE_PROPERTIES;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ORIGIN, LootContextParams.DAMAGE_SOURCE);
    }

    @Override
    public boolean test(LootContext $$0) {
        DamageSource $$1 = $$0.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
        Vec3 $$2 = $$0.getOptionalParameter(LootContextParams.ORIGIN);
        if ($$2 == null || $$1 == null) {
            return false;
        }
        return this.predicate.isEmpty() || this.predicate.get().matches($$0.getLevel(), $$2, $$1);
    }

    public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder $$0) {
        return () -> new DamageSourceCondition(Optional.of($$0.build()));
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }
}


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
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public record InvertedLootItemCondition(LootItemCondition term) implements LootItemCondition
{
    public static final MapCodec<InvertedLootItemCondition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LootItemCondition.DIRECT_CODEC.fieldOf("term").forGetter(InvertedLootItemCondition::term)).apply((Applicative)$$0, InvertedLootItemCondition::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.INVERTED;
    }

    @Override
    public boolean test(LootContext $$0) {
        return !this.term.test($$0);
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.term.getReferencedContextParams();
    }

    @Override
    public void validate(ValidationContext $$0) {
        LootItemCondition.super.validate($$0);
        this.term.validate($$0);
    }

    public static LootItemCondition.Builder invert(LootItemCondition.Builder $$0) {
        InvertedLootItemCondition $$1 = new InvertedLootItemCondition($$0.build());
        return () -> $$1;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }
}


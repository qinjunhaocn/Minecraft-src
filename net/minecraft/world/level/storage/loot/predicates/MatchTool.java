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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public record MatchTool(Optional<ItemPredicate> predicate) implements LootItemCondition
{
    public static final MapCodec<MatchTool> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchTool::predicate)).apply((Applicative)$$0, MatchTool::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.MATCH_TOOL;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.TOOL);
    }

    @Override
    public boolean test(LootContext $$0) {
        ItemStack $$1 = $$0.getOptionalParameter(LootContextParams.TOOL);
        return $$1 != null && (this.predicate.isEmpty() || this.predicate.get().test($$1));
    }

    public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder $$0) {
        return () -> new MatchTool(Optional.of($$0.build()));
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }
}


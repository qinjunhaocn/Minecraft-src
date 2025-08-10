/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public record TimeCheck(Optional<Long> period, IntRange value) implements LootItemCondition
{
    public static final MapCodec<TimeCheck> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.LONG.optionalFieldOf("period").forGetter(TimeCheck::period), (App)IntRange.CODEC.fieldOf("value").forGetter(TimeCheck::value)).apply((Applicative)$$0, TimeCheck::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.TIME_CHECK;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.value.getReferencedContextParams();
    }

    @Override
    public boolean test(LootContext $$0) {
        ServerLevel $$1 = $$0.getLevel();
        long $$2 = $$1.getDayTime();
        if (this.period.isPresent()) {
            $$2 %= this.period.get().longValue();
        }
        return this.value.test($$0, (int)$$2);
    }

    public static Builder time(IntRange $$0) {
        return new Builder($$0);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Builder
    implements LootItemCondition.Builder {
        private Optional<Long> period = Optional.empty();
        private final IntRange value;

        public Builder(IntRange $$0) {
            this.value = $$0;
        }

        public Builder setPeriod(long $$0) {
            this.period = Optional.of($$0);
            return this;
        }

        @Override
        public TimeCheck build() {
            return new TimeCheck(this.period, this.value);
        }

        @Override
        public /* synthetic */ LootItemCondition build() {
            return this.build();
        }
    }
}


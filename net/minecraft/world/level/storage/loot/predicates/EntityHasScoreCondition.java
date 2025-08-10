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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

public record EntityHasScoreCondition(Map<String, IntRange> scores, LootContext.EntityTarget entityTarget) implements LootItemCondition
{
    public static final MapCodec<EntityHasScoreCondition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.unboundedMap((Codec)Codec.STRING, IntRange.CODEC).fieldOf("scores").forGetter(EntityHasScoreCondition::scores), (App)LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(EntityHasScoreCondition::entityTarget)).apply((Applicative)$$0, EntityHasScoreCondition::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ENTITY_SCORES;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Stream.concat(Stream.of(this.entityTarget.getParam()), this.scores.values().stream().flatMap($$0 -> $$0.getReferencedContextParams().stream())).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public boolean test(LootContext $$0) {
        Entity $$1 = $$0.getOptionalParameter(this.entityTarget.getParam());
        if ($$1 == null) {
            return false;
        }
        ServerScoreboard $$2 = $$0.getLevel().getScoreboard();
        for (Map.Entry<String, IntRange> $$3 : this.scores.entrySet()) {
            if (this.hasScore($$0, $$1, $$2, $$3.getKey(), $$3.getValue())) continue;
            return false;
        }
        return true;
    }

    protected boolean hasScore(LootContext $$0, Entity $$1, Scoreboard $$2, String $$3, IntRange $$4) {
        Objective $$5 = $$2.getObjective($$3);
        if ($$5 == null) {
            return false;
        }
        ReadOnlyScoreInfo $$6 = $$2.getPlayerScoreInfo($$1, $$5);
        if ($$6 == null) {
            return false;
        }
        return $$4.test($$0, $$6.value());
    }

    public static Builder hasScores(LootContext.EntityTarget $$0) {
        return new Builder($$0);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Builder
    implements LootItemCondition.Builder {
        private final ImmutableMap.Builder<String, IntRange> scores = ImmutableMap.builder();
        private final LootContext.EntityTarget entityTarget;

        public Builder(LootContext.EntityTarget $$0) {
            this.entityTarget = $$0;
        }

        public Builder withScore(String $$0, IntRange $$1) {
            this.scores.put($$0, $$1);
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new EntityHasScoreCondition(this.scores.build(), this.entityTarget);
        }
    }
}


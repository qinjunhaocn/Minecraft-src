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
package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;

public record ScoreboardValue(ScoreboardNameProvider target, String score, float scale) implements NumberProvider
{
    public static final MapCodec<ScoreboardValue> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ScoreboardNameProviders.CODEC.fieldOf("target").forGetter(ScoreboardValue::target), (App)Codec.STRING.fieldOf("score").forGetter(ScoreboardValue::score), (App)Codec.FLOAT.fieldOf("scale").orElse((Object)Float.valueOf(1.0f)).forGetter(ScoreboardValue::scale)).apply((Applicative)$$0, ScoreboardValue::new));

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.SCORE;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.target.getReferencedContextParams();
    }

    public static ScoreboardValue fromScoreboard(LootContext.EntityTarget $$0, String $$1) {
        return ScoreboardValue.fromScoreboard($$0, $$1, 1.0f);
    }

    public static ScoreboardValue fromScoreboard(LootContext.EntityTarget $$0, String $$1, float $$2) {
        return new ScoreboardValue(ContextScoreboardNameProvider.forTarget($$0), $$1, $$2);
    }

    @Override
    public float getFloat(LootContext $$0) {
        ScoreHolder $$1 = this.target.getScoreHolder($$0);
        if ($$1 == null) {
            return 0.0f;
        }
        ServerScoreboard $$2 = $$0.getLevel().getScoreboard();
        Objective $$3 = $$2.getObjective(this.score);
        if ($$3 == null) {
            return 0.0f;
        }
        ReadOnlyScoreInfo $$4 = $$2.getPlayerScoreInfo($$1, $$3);
        if ($$4 == null) {
            return 0.0f;
        }
        return (float)$$4.value() * this.scale;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot.providers.score;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import net.minecraft.world.scores.ScoreHolder;

public interface ScoreboardNameProvider {
    @Nullable
    public ScoreHolder getScoreHolder(LootContext var1);

    public LootScoreProviderType getType();

    public Set<ContextKey<?>> getReferencedContextParams();
}


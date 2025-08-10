/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 */
package net.minecraft.world.scores;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

class PlayerScores {
    private final Reference2ObjectOpenHashMap<Objective, Score> scores = new Reference2ObjectOpenHashMap(16, 0.5f);

    PlayerScores() {
    }

    @Nullable
    public Score get(Objective $$0) {
        return (Score)this.scores.get((Object)$$0);
    }

    public Score getOrCreate(Objective $$0, Consumer<Score> $$12) {
        return (Score)this.scores.computeIfAbsent((Object)$$0, $$1 -> {
            Score $$2 = new Score();
            $$12.accept($$2);
            return $$2;
        });
    }

    public boolean remove(Objective $$0) {
        return this.scores.remove((Object)$$0) != null;
    }

    public boolean hasScores() {
        return !this.scores.isEmpty();
    }

    public Object2IntMap<Objective> listScores() {
        Object2IntOpenHashMap $$0 = new Object2IntOpenHashMap();
        this.scores.forEach((arg_0, arg_1) -> PlayerScores.lambda$listScores$1((Object2IntMap)$$0, arg_0, arg_1));
        return $$0;
    }

    void setScore(Objective $$0, Score $$1) {
        this.scores.put((Object)$$0, (Object)$$1);
    }

    Map<Objective, Score> listRawScores() {
        return Collections.unmodifiableMap(this.scores);
    }

    private static /* synthetic */ void lambda$listScores$1(Object2IntMap $$0, Objective $$1, Score $$2) {
        $$0.put((Object)$$1, $$2.value());
    }
}


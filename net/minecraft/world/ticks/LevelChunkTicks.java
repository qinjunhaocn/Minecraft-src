/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet
 */
package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.SavedTick;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.SerializableTickContainer;
import net.minecraft.world.ticks.TickContainerAccess;

public class LevelChunkTicks<T>
implements SerializableTickContainer<T>,
TickContainerAccess<T> {
    private final Queue<ScheduledTick<T>> tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
    @Nullable
    private List<SavedTick<T>> pendingTicks;
    private final Set<ScheduledTick<?>> ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
    @Nullable
    private BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded;

    public LevelChunkTicks() {
    }

    public LevelChunkTicks(List<SavedTick<T>> $$0) {
        this.pendingTicks = $$0;
        for (SavedTick<T> $$1 : $$0) {
            this.ticksPerPosition.add(ScheduledTick.probe($$1.type(), $$1.pos()));
        }
    }

    public void setOnTickAdded(@Nullable BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> $$0) {
        this.onTickAdded = $$0;
    }

    @Nullable
    public ScheduledTick<T> peek() {
        return this.tickQueue.peek();
    }

    @Nullable
    public ScheduledTick<T> poll() {
        ScheduledTick<T> $$0 = this.tickQueue.poll();
        if ($$0 != null) {
            this.ticksPerPosition.remove($$0);
        }
        return $$0;
    }

    @Override
    public void schedule(ScheduledTick<T> $$0) {
        if (this.ticksPerPosition.add($$0)) {
            this.scheduleUnchecked($$0);
        }
    }

    private void scheduleUnchecked(ScheduledTick<T> $$0) {
        this.tickQueue.add($$0);
        if (this.onTickAdded != null) {
            this.onTickAdded.accept(this, $$0);
        }
    }

    @Override
    public boolean hasScheduledTick(BlockPos $$0, T $$1) {
        return this.ticksPerPosition.contains(ScheduledTick.probe($$1, $$0));
    }

    public void removeIf(Predicate<ScheduledTick<T>> $$0) {
        Iterator $$1 = this.tickQueue.iterator();
        while ($$1.hasNext()) {
            ScheduledTick $$2 = (ScheduledTick)((Object)$$1.next());
            if (!$$0.test($$2)) continue;
            $$1.remove();
            this.ticksPerPosition.remove((Object)$$2);
        }
    }

    public Stream<ScheduledTick<T>> getAll() {
        return this.tickQueue.stream();
    }

    @Override
    public int count() {
        return this.tickQueue.size() + (this.pendingTicks != null ? this.pendingTicks.size() : 0);
    }

    @Override
    public List<SavedTick<T>> pack(long $$0) {
        ArrayList<SavedTick<T>> $$1 = new ArrayList<SavedTick<T>>(this.tickQueue.size());
        if (this.pendingTicks != null) {
            $$1.addAll(this.pendingTicks);
        }
        for (ScheduledTick scheduledTick : this.tickQueue) {
            $$1.add(scheduledTick.toSavedTick($$0));
        }
        return $$1;
    }

    public void unpack(long $$0) {
        if (this.pendingTicks != null) {
            int $$1 = -this.pendingTicks.size();
            for (SavedTick<T> $$2 : this.pendingTicks) {
                this.scheduleUnchecked($$2.unpack($$0, $$1++));
            }
        }
        this.pendingTicks = null;
    }
}


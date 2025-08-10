/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2LongMaps
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet
 */
package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickAccess;

public class LevelTicks<T>
implements LevelTickAccess<T> {
    private static final Comparator<LevelChunkTicks<?>> CONTAINER_DRAIN_ORDER = ($$0, $$1) -> ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare($$0.peek(), $$1.peek());
    private final LongPredicate tickCheck;
    private final Long2ObjectMap<LevelChunkTicks<T>> allContainers = new Long2ObjectOpenHashMap();
    private final Long2LongMap nextTickForContainer = (Long2LongMap)Util.make(new Long2LongOpenHashMap(), $$0 -> $$0.defaultReturnValue(Long.MAX_VALUE));
    private final Queue<LevelChunkTicks<T>> containersToTick = new PriorityQueue(CONTAINER_DRAIN_ORDER);
    private final Queue<ScheduledTick<T>> toRunThisTick = new ArrayDeque<ScheduledTick<T>>();
    private final List<ScheduledTick<T>> alreadyRunThisTick = new ArrayList<ScheduledTick<T>>();
    private final Set<ScheduledTick<?>> toRunThisTickSet = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
    private final BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> chunkScheduleUpdater = ($$0, $$1) -> {
        if ($$1.equals((Object)$$0.peek())) {
            this.updateContainerScheduling((ScheduledTick<T>)((Object)$$1));
        }
    };

    public LevelTicks(LongPredicate $$02) {
        this.tickCheck = $$02;
    }

    public void addContainer(ChunkPos $$0, LevelChunkTicks<T> $$1) {
        long $$2 = $$0.toLong();
        this.allContainers.put($$2, $$1);
        ScheduledTick<T> $$3 = $$1.peek();
        if ($$3 != null) {
            this.nextTickForContainer.put($$2, $$3.triggerTick());
        }
        $$1.setOnTickAdded(this.chunkScheduleUpdater);
    }

    public void removeContainer(ChunkPos $$0) {
        long $$1 = $$0.toLong();
        LevelChunkTicks $$2 = (LevelChunkTicks)this.allContainers.remove($$1);
        this.nextTickForContainer.remove($$1);
        if ($$2 != null) {
            $$2.setOnTickAdded(null);
        }
    }

    @Override
    public void schedule(ScheduledTick<T> $$0) {
        long $$1 = ChunkPos.asLong($$0.pos());
        LevelChunkTicks $$2 = (LevelChunkTicks)this.allContainers.get($$1);
        if ($$2 == null) {
            Util.logAndPauseIfInIde("Trying to schedule tick in not loaded position " + String.valueOf($$0.pos()));
            return;
        }
        $$2.schedule($$0);
    }

    public void tick(long $$0, int $$1, BiConsumer<BlockPos, T> $$2) {
        ProfilerFiller $$3 = Profiler.get();
        $$3.push("collect");
        this.collectTicks($$0, $$1, $$3);
        $$3.popPush("run");
        $$3.incrementCounter("ticksToRun", this.toRunThisTick.size());
        this.runCollectedTicks($$2);
        $$3.popPush("cleanup");
        this.cleanupAfterTick();
        $$3.pop();
    }

    private void collectTicks(long $$0, int $$1, ProfilerFiller $$2) {
        this.sortContainersToTick($$0);
        $$2.incrementCounter("containersToTick", this.containersToTick.size());
        this.drainContainers($$0, $$1);
        this.rescheduleLeftoverContainers();
    }

    private void sortContainersToTick(long $$0) {
        ObjectIterator $$1 = Long2LongMaps.fastIterator((Long2LongMap)this.nextTickForContainer);
        while ($$1.hasNext()) {
            Long2LongMap.Entry $$2 = (Long2LongMap.Entry)$$1.next();
            long $$3 = $$2.getLongKey();
            long $$4 = $$2.getLongValue();
            if ($$4 > $$0) continue;
            LevelChunkTicks $$5 = (LevelChunkTicks)this.allContainers.get($$3);
            if ($$5 == null) {
                $$1.remove();
                continue;
            }
            ScheduledTick $$6 = $$5.peek();
            if ($$6 == null) {
                $$1.remove();
                continue;
            }
            if ($$6.triggerTick() > $$0) {
                $$2.setValue($$6.triggerTick());
                continue;
            }
            if (!this.tickCheck.test($$3)) continue;
            $$1.remove();
            this.containersToTick.add($$5);
        }
    }

    private void drainContainers(long $$0, int $$1) {
        LevelChunkTicks<T> $$2;
        while (this.canScheduleMoreTicks($$1) && ($$2 = this.containersToTick.poll()) != null) {
            ScheduledTick<T> $$3 = $$2.poll();
            this.scheduleForThisTick($$3);
            this.drainFromCurrentContainer(this.containersToTick, $$2, $$0, $$1);
            ScheduledTick<T> $$4 = $$2.peek();
            if ($$4 == null) continue;
            if ($$4.triggerTick() <= $$0 && this.canScheduleMoreTicks($$1)) {
                this.containersToTick.add($$2);
                continue;
            }
            this.updateContainerScheduling($$4);
        }
    }

    private void rescheduleLeftoverContainers() {
        for (LevelChunkTicks levelChunkTicks : this.containersToTick) {
            this.updateContainerScheduling(levelChunkTicks.peek());
        }
    }

    private void updateContainerScheduling(ScheduledTick<T> $$0) {
        this.nextTickForContainer.put(ChunkPos.asLong($$0.pos()), $$0.triggerTick());
    }

    private void drainFromCurrentContainer(Queue<LevelChunkTicks<T>> $$0, LevelChunkTicks<T> $$1, long $$2, int $$3) {
        ScheduledTick<T> $$6;
        ScheduledTick<T> $$5;
        if (!this.canScheduleMoreTicks($$3)) {
            return;
        }
        LevelChunkTicks<T> $$4 = $$0.peek();
        ScheduledTick<T> scheduledTick = $$5 = $$4 != null ? $$4.peek() : null;
        while (this.canScheduleMoreTicks($$3) && ($$6 = $$1.peek()) != null && $$6.triggerTick() <= $$2 && ($$5 == null || ScheduledTick.INTRA_TICK_DRAIN_ORDER.compare($$6, $$5) <= 0)) {
            $$1.poll();
            this.scheduleForThisTick($$6);
        }
    }

    private void scheduleForThisTick(ScheduledTick<T> $$0) {
        this.toRunThisTick.add($$0);
    }

    private boolean canScheduleMoreTicks(int $$0) {
        return this.toRunThisTick.size() < $$0;
    }

    private void runCollectedTicks(BiConsumer<BlockPos, T> $$0) {
        while (!this.toRunThisTick.isEmpty()) {
            ScheduledTick<T> $$1 = this.toRunThisTick.poll();
            if (!this.toRunThisTickSet.isEmpty()) {
                this.toRunThisTickSet.remove($$1);
            }
            this.alreadyRunThisTick.add($$1);
            $$0.accept($$1.pos(), (BlockPos)$$1.type());
        }
    }

    private void cleanupAfterTick() {
        this.toRunThisTick.clear();
        this.containersToTick.clear();
        this.alreadyRunThisTick.clear();
        this.toRunThisTickSet.clear();
    }

    @Override
    public boolean hasScheduledTick(BlockPos $$0, T $$1) {
        LevelChunkTicks $$2 = (LevelChunkTicks)this.allContainers.get(ChunkPos.asLong($$0));
        return $$2 != null && $$2.hasScheduledTick($$0, $$1);
    }

    @Override
    public boolean willTickThisTick(BlockPos $$0, T $$1) {
        this.calculateTickSetIfNeeded();
        return this.toRunThisTickSet.contains(ScheduledTick.probe($$1, $$0));
    }

    private void calculateTickSetIfNeeded() {
        if (this.toRunThisTickSet.isEmpty() && !this.toRunThisTick.isEmpty()) {
            this.toRunThisTickSet.addAll(this.toRunThisTick);
        }
    }

    private void forContainersInArea(BoundingBox $$0, PosAndContainerConsumer<T> $$1) {
        int $$2 = SectionPos.posToSectionCoord($$0.minX());
        int $$3 = SectionPos.posToSectionCoord($$0.minZ());
        int $$4 = SectionPos.posToSectionCoord($$0.maxX());
        int $$5 = SectionPos.posToSectionCoord($$0.maxZ());
        for (int $$6 = $$2; $$6 <= $$4; ++$$6) {
            for (int $$7 = $$3; $$7 <= $$5; ++$$7) {
                long $$8 = ChunkPos.asLong($$6, $$7);
                LevelChunkTicks $$9 = (LevelChunkTicks)this.allContainers.get($$8);
                if ($$9 == null) continue;
                $$1.accept($$8, $$9);
            }
        }
    }

    public void clearArea(BoundingBox $$0) {
        Predicate<ScheduledTick> $$12 = $$1 -> $$0.isInside($$1.pos());
        this.forContainersInArea($$0, ($$1, $$2) -> {
            ScheduledTick $$3 = $$2.peek();
            $$2.removeIf($$12);
            ScheduledTick $$4 = $$2.peek();
            if ($$4 != $$3) {
                if ($$4 != null) {
                    this.updateContainerScheduling($$4);
                } else {
                    this.nextTickForContainer.remove($$1);
                }
            }
        });
        this.alreadyRunThisTick.removeIf($$12);
        this.toRunThisTick.removeIf($$12);
    }

    public void copyArea(BoundingBox $$0, Vec3i $$1) {
        this.copyAreaFrom(this, $$0, $$1);
    }

    public void copyAreaFrom(LevelTicks<T> $$0, BoundingBox $$12, Vec3i $$22) {
        ArrayList $$32 = new ArrayList();
        Predicate<ScheduledTick> $$4 = $$1 -> $$12.isInside($$1.pos());
        $$0.alreadyRunThisTick.stream().filter($$4).forEach($$32::add);
        $$0.toRunThisTick.stream().filter($$4).forEach($$32::add);
        $$0.forContainersInArea($$12, ($$2, $$3) -> $$3.getAll().filter($$4).forEach($$32::add));
        LongSummaryStatistics $$5 = $$32.stream().mapToLong(ScheduledTick::subTickOrder).summaryStatistics();
        long $$6 = $$5.getMin();
        long $$7 = $$5.getMax();
        $$32.forEach($$3 -> this.schedule(new ScheduledTick($$3.type(), $$3.pos().offset($$22), $$3.triggerTick(), $$3.priority(), $$3.subTickOrder() - $$6 + $$7 + 1L)));
    }

    @Override
    public int count() {
        return this.allContainers.values().stream().mapToInt(TickAccess::count).sum();
    }

    @FunctionalInterface
    static interface PosAndContainerConsumer<T> {
        public void accept(long var1, LevelChunkTicks<T> var3);
    }
}


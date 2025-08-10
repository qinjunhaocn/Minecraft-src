/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerCallbacks;
import org.slf4j.Logger;

public class TimerQueue<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CALLBACK_DATA_TAG = "Callback";
    private static final String TIMER_NAME_TAG = "Name";
    private static final String TIMER_TRIGGER_TIME_TAG = "TriggerTime";
    private final TimerCallbacks<T> callbacksRegistry;
    private final Queue<Event<T>> queue = new PriorityQueue<Event<T>>(TimerQueue.createComparator());
    private UnsignedLong sequentialId = UnsignedLong.ZERO;
    private final Table<String, Long, Event<T>> events = HashBasedTable.create();

    private static <T> Comparator<Event<T>> createComparator() {
        return Comparator.comparingLong($$0 -> $$0.triggerTime).thenComparing($$0 -> $$0.sequentialId);
    }

    public TimerQueue(TimerCallbacks<T> $$02, Stream<? extends Dynamic<?>> $$1) {
        this($$02);
        this.queue.clear();
        this.events.clear();
        this.sequentialId = UnsignedLong.ZERO;
        $$1.forEach($$0 -> {
            Tag $$1 = (Tag)$$0.convert((DynamicOps)NbtOps.INSTANCE).getValue();
            if ($$1 instanceof CompoundTag) {
                CompoundTag $$2 = (CompoundTag)$$1;
                this.loadEvent($$2);
            } else {
                LOGGER.warn("Invalid format of events: {}", (Object)$$1);
            }
        });
    }

    public TimerQueue(TimerCallbacks<T> $$0) {
        this.callbacksRegistry = $$0;
    }

    public void tick(T $$0, long $$1) {
        Event<T> $$2;
        while (($$2 = this.queue.peek()) != null && $$2.triggerTime <= $$1) {
            this.queue.remove();
            this.events.remove($$2.id, $$1);
            $$2.callback.handle($$0, this, $$1);
        }
    }

    public void schedule(String $$0, long $$1, TimerCallback<T> $$2) {
        if (this.events.contains($$0, $$1)) {
            return;
        }
        this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
        Event<T> $$3 = new Event<T>($$1, this.sequentialId, $$0, $$2);
        this.events.put($$0, $$1, $$3);
        this.queue.add($$3);
    }

    public int remove(String $$0) {
        Collection<Event<Event>> $$1 = this.events.row($$0).values();
        $$1.forEach(this.queue::remove);
        int $$2 = $$1.size();
        $$1.clear();
        return $$2;
    }

    public Set<String> getEventsIds() {
        return Collections.unmodifiableSet(this.events.rowKeySet());
    }

    private void loadEvent(CompoundTag $$0) {
        TimerCallback $$1 = $$0.read(CALLBACK_DATA_TAG, this.callbacksRegistry.codec()).orElse(null);
        if ($$1 != null) {
            String $$2 = $$0.getStringOr(TIMER_NAME_TAG, "");
            long $$3 = $$0.getLongOr(TIMER_TRIGGER_TIME_TAG, 0L);
            this.schedule($$2, $$3, $$1);
        }
    }

    private CompoundTag storeEvent(Event<T> $$0) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putString(TIMER_NAME_TAG, $$0.id);
        $$1.putLong(TIMER_TRIGGER_TIME_TAG, $$0.triggerTime);
        $$1.store(CALLBACK_DATA_TAG, this.callbacksRegistry.codec(), $$0.callback);
        return $$1;
    }

    public ListTag store() {
        ListTag $$0 = new ListTag();
        this.queue.stream().sorted(TimerQueue.createComparator()).map(this::storeEvent).forEach($$0::add);
        return $$0;
    }

    public static class Event<T> {
        public final long triggerTime;
        public final UnsignedLong sequentialId;
        public final String id;
        public final TimerCallback<T> callback;

        Event(long $$0, UnsignedLong $$1, String $$2, TimerCallback<T> $$3) {
            this.triggerTime = $$0;
            this.sequentialId = $$1;
            this.id = $$2;
            this.callback = $$3;
        }
    }
}


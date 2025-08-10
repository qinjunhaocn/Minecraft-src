/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Hash$Strategy
 */
package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.SavedTick;
import net.minecraft.world.ticks.TickPriority;

public record ScheduledTick<T>(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
    public static final Comparator<ScheduledTick<?>> DRAIN_ORDER = ($$0, $$1) -> {
        int $$2 = Long.compare($$0.triggerTick, $$1.triggerTick);
        if ($$2 != 0) {
            return $$2;
        }
        $$2 = $$0.priority.compareTo($$1.priority);
        if ($$2 != 0) {
            return $$2;
        }
        return Long.compare($$0.subTickOrder, $$1.subTickOrder);
    };
    public static final Comparator<ScheduledTick<?>> INTRA_TICK_DRAIN_ORDER = ($$0, $$1) -> {
        int $$2 = $$0.priority.compareTo($$1.priority);
        if ($$2 != 0) {
            return $$2;
        }
        return Long.compare($$0.subTickOrder, $$1.subTickOrder);
    };
    public static final Hash.Strategy<ScheduledTick<?>> UNIQUE_TICK_HASH = new Hash.Strategy<ScheduledTick<?>>(){

        public int hashCode(ScheduledTick<?> $$0) {
            return 31 * $$0.pos().hashCode() + $$0.type().hashCode();
        }

        public boolean equals(@Nullable ScheduledTick<?> $$0, @Nullable ScheduledTick<?> $$1) {
            if ($$0 == $$1) {
                return true;
            }
            if ($$0 == null || $$1 == null) {
                return false;
            }
            return $$0.type() == $$1.type() && $$0.pos().equals($$1.pos());
        }

        public /* synthetic */ boolean equals(@Nullable Object object, @Nullable Object object2) {
            return this.equals((ScheduledTick)((Object)object), (ScheduledTick)((Object)object2));
        }

        public /* synthetic */ int hashCode(Object object) {
            return this.hashCode((ScheduledTick)((Object)object));
        }
    };

    public ScheduledTick(T $$0, BlockPos $$1, long $$2, long $$3) {
        this($$0, $$1, $$2, TickPriority.NORMAL, $$3);
    }

    public ScheduledTick {
        $$1 = $$1.immutable();
    }

    public static <T> ScheduledTick<T> probe(T $$0, BlockPos $$1) {
        return new ScheduledTick<T>($$0, $$1, 0L, TickPriority.NORMAL, 0L);
    }

    public SavedTick<T> toSavedTick(long $$0) {
        return new SavedTick<T>(this.type, this.pos, (int)(this.triggerTick - $$0), this.priority);
    }
}


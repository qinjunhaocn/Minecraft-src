/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GateBehavior<E extends LivingEntity>
implements BehaviorControl<E> {
    private final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
    private final Set<MemoryModuleType<?>> exitErasedMemories;
    private final OrderPolicy orderPolicy;
    private final RunningPolicy runningPolicy;
    private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList();
    private Behavior.Status status = Behavior.Status.STOPPED;

    public GateBehavior(Map<MemoryModuleType<?>, MemoryStatus> $$02, Set<MemoryModuleType<?>> $$1, OrderPolicy $$2, RunningPolicy $$3, List<Pair<? extends BehaviorControl<? super E>, Integer>> $$4) {
        this.entryCondition = $$02;
        this.exitErasedMemories = $$1;
        this.orderPolicy = $$2;
        this.runningPolicy = $$3;
        $$4.forEach($$0 -> this.behaviors.add((BehaviorControl)$$0.getFirst(), (Integer)$$0.getSecond()));
    }

    @Override
    public Behavior.Status getStatus() {
        return this.status;
    }

    private boolean hasRequiredMemories(E $$0) {
        for (Map.Entry<MemoryModuleType<?>, MemoryStatus> $$1 : this.entryCondition.entrySet()) {
            MemoryModuleType<?> $$2 = $$1.getKey();
            MemoryStatus $$3 = $$1.getValue();
            if (((LivingEntity)$$0).getBrain().checkMemory($$2, $$3)) continue;
            return false;
        }
        return true;
    }

    @Override
    public final boolean tryStart(ServerLevel $$0, E $$1, long $$2) {
        if (this.hasRequiredMemories($$1)) {
            this.status = Behavior.Status.RUNNING;
            this.orderPolicy.apply(this.behaviors);
            this.runningPolicy.apply(this.behaviors.stream(), $$0, $$1, $$2);
            return true;
        }
        return false;
    }

    @Override
    public final void tickOrStop(ServerLevel $$02, E $$1, long $$2) {
        this.behaviors.stream().filter($$0 -> $$0.getStatus() == Behavior.Status.RUNNING).forEach($$3 -> $$3.tickOrStop($$02, $$1, $$2));
        if (this.behaviors.stream().noneMatch($$0 -> $$0.getStatus() == Behavior.Status.RUNNING)) {
            this.doStop($$02, $$1, $$2);
        }
    }

    @Override
    public final void doStop(ServerLevel $$02, E $$1, long $$2) {
        this.status = Behavior.Status.STOPPED;
        this.behaviors.stream().filter($$0 -> $$0.getStatus() == Behavior.Status.RUNNING).forEach($$3 -> $$3.doStop($$02, $$1, $$2));
        this.exitErasedMemories.forEach(((LivingEntity)$$1).getBrain()::eraseMemory);
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }

    public String toString() {
        Set $$02 = this.behaviors.stream().filter($$0 -> $$0.getStatus() == Behavior.Status.RUNNING).collect(Collectors.toSet());
        return "(" + this.getClass().getSimpleName() + "): " + String.valueOf($$02);
    }

    public static final class OrderPolicy
    extends Enum<OrderPolicy> {
        public static final /* enum */ OrderPolicy ORDERED = new OrderPolicy($$0 -> {});
        public static final /* enum */ OrderPolicy SHUFFLED = new OrderPolicy(ShufflingList::shuffle);
        private final Consumer<ShufflingList<?>> consumer;
        private static final /* synthetic */ OrderPolicy[] $VALUES;

        public static OrderPolicy[] values() {
            return (OrderPolicy[])$VALUES.clone();
        }

        public static OrderPolicy valueOf(String $$0) {
            return Enum.valueOf(OrderPolicy.class, $$0);
        }

        private OrderPolicy(Consumer<ShufflingList<?>> $$0) {
            this.consumer = $$0;
        }

        public void apply(ShufflingList<?> $$0) {
            this.consumer.accept($$0);
        }

        private static /* synthetic */ OrderPolicy[] a() {
            return new OrderPolicy[]{ORDERED, SHUFFLED};
        }

        static {
            $VALUES = OrderPolicy.a();
        }
    }

    public static abstract sealed class RunningPolicy
    extends Enum<RunningPolicy> {
        public static final /* enum */ RunningPolicy RUN_ONE = new RunningPolicy(){

            @Override
            public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> $$02, ServerLevel $$1, E $$2, long $$32) {
                $$02.filter($$0 -> $$0.getStatus() == Behavior.Status.STOPPED).filter($$3 -> $$3.tryStart($$1, $$2, $$32)).findFirst();
            }
        };
        public static final /* enum */ RunningPolicy TRY_ALL = new RunningPolicy(){

            @Override
            public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> $$02, ServerLevel $$1, E $$2, long $$32) {
                $$02.filter($$0 -> $$0.getStatus() == Behavior.Status.STOPPED).forEach($$3 -> $$3.tryStart($$1, $$2, $$32));
            }
        };
        private static final /* synthetic */ RunningPolicy[] $VALUES;

        public static RunningPolicy[] values() {
            return (RunningPolicy[])$VALUES.clone();
        }

        public static RunningPolicy valueOf(String $$0) {
            return Enum.valueOf(RunningPolicy.class, $$0);
        }

        public abstract <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4);

        private static /* synthetic */ RunningPolicy[] a() {
            return new RunningPolicy[]{RUN_ONE, TRY_ALL};
        }

        static {
            $VALUES = RunningPolicy.a();
        }
    }
}


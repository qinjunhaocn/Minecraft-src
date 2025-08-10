/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ProfiledReloadInstance;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;

public class SimpleReloadInstance<S>
implements ReloadInstance {
    private static final int PREPARATION_PROGRESS_WEIGHT = 2;
    private static final int EXTRA_RELOAD_PROGRESS_WEIGHT = 2;
    private static final int LISTENER_PROGRESS_WEIGHT = 1;
    final CompletableFuture<Unit> allPreparations = new CompletableFuture();
    @Nullable
    private CompletableFuture<List<S>> allDone;
    final Set<PreparableReloadListener> preparingListeners;
    private final int listenerCount;
    private final AtomicInteger startedTasks = new AtomicInteger();
    private final AtomicInteger finishedTasks = new AtomicInteger();
    private final AtomicInteger startedReloads = new AtomicInteger();
    private final AtomicInteger finishedReloads = new AtomicInteger();

    public static ReloadInstance of(ResourceManager $$0, List<PreparableReloadListener> $$1, Executor $$2, Executor $$3, CompletableFuture<Unit> $$4) {
        SimpleReloadInstance<Void> $$5 = new SimpleReloadInstance<Void>($$1);
        $$5.startTasks($$2, $$3, $$0, $$1, StateFactory.SIMPLE, $$4);
        return $$5;
    }

    protected SimpleReloadInstance(List<PreparableReloadListener> $$0) {
        this.listenerCount = $$0.size();
        this.preparingListeners = new HashSet<PreparableReloadListener>($$0);
    }

    protected void startTasks(Executor $$0, Executor $$1, ResourceManager $$2, List<PreparableReloadListener> $$3, StateFactory<S> $$4, CompletableFuture<?> $$5) {
        this.allDone = this.prepareTasks($$0, $$1, $$2, $$3, $$4, $$5);
    }

    protected CompletableFuture<List<S>> prepareTasks(Executor $$0, Executor $$12, ResourceManager $$2, List<PreparableReloadListener> $$3, StateFactory<S> $$4, CompletableFuture<?> $$5) {
        Executor $$6 = $$1 -> {
            this.startedTasks.incrementAndGet();
            $$0.execute(() -> {
                $$1.run();
                this.finishedTasks.incrementAndGet();
            });
        };
        Executor $$7 = $$1 -> {
            this.startedReloads.incrementAndGet();
            $$12.execute(() -> {
                $$1.run();
                this.finishedReloads.incrementAndGet();
            });
        };
        this.startedTasks.incrementAndGet();
        $$5.thenRun(this.finishedTasks::incrementAndGet);
        CompletableFuture<Object> $$8 = $$5;
        ArrayList<CompletableFuture<S>> $$9 = new ArrayList<CompletableFuture<S>>();
        for (PreparableReloadListener $$10 : $$3) {
            PreparableReloadListener.PreparationBarrier $$11 = this.createBarrierForListener($$10, $$8, $$12);
            CompletableFuture<S> $$122 = $$4.create($$11, $$2, $$10, $$6, $$7);
            $$9.add($$122);
            $$8 = $$122;
        }
        return Util.sequenceFailFast($$9);
    }

    private PreparableReloadListener.PreparationBarrier createBarrierForListener(final PreparableReloadListener $$0, final CompletableFuture<?> $$1, final Executor $$2) {
        return new PreparableReloadListener.PreparationBarrier(){

            @Override
            public <T> CompletableFuture<T> wait(T $$02) {
                $$2.execute(() -> {
                    SimpleReloadInstance.this.preparingListeners.remove($$0);
                    if (SimpleReloadInstance.this.preparingListeners.isEmpty()) {
                        SimpleReloadInstance.this.allPreparations.complete(Unit.INSTANCE);
                    }
                });
                return SimpleReloadInstance.this.allPreparations.thenCombine((CompletionStage)$$1, ($$1, $$2) -> $$02);
            }
        };
    }

    @Override
    public CompletableFuture<?> done() {
        return Objects.requireNonNull(this.allDone, "not started");
    }

    @Override
    public float getActualProgress() {
        int $$0 = this.listenerCount - this.preparingListeners.size();
        float $$1 = SimpleReloadInstance.weightProgress(this.finishedTasks.get(), this.finishedReloads.get(), $$0);
        float $$2 = SimpleReloadInstance.weightProgress(this.startedTasks.get(), this.startedReloads.get(), this.listenerCount);
        return $$1 / $$2;
    }

    private static int weightProgress(int $$0, int $$1, int $$2) {
        return $$0 * 2 + $$1 * 2 + $$2 * 1;
    }

    public static ReloadInstance create(ResourceManager $$0, List<PreparableReloadListener> $$1, Executor $$2, Executor $$3, CompletableFuture<Unit> $$4, boolean $$5) {
        if ($$5) {
            return ProfiledReloadInstance.of($$0, $$1, $$2, $$3, $$4);
        }
        return SimpleReloadInstance.of($$0, $$1, $$2, $$3, $$4);
    }

    @FunctionalInterface
    protected static interface StateFactory<S> {
        public static final StateFactory<Void> SIMPLE = ($$0, $$1, $$2, $$3, $$4) -> $$2.reload($$0, $$1, $$3, $$4);

        public CompletableFuture<S> create(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, PreparableReloadListener var3, Executor var4, Executor var5);
    }
}


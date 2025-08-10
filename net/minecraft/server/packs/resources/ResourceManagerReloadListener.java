/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener
extends PreparableReloadListener {
    @Override
    default public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$0, ResourceManager $$1, Executor $$2, Executor $$3) {
        return $$0.wait(Unit.INSTANCE).thenRunAsync(() -> {
            ProfilerFiller $$1 = Profiler.get();
            $$1.push("listener");
            this.onResourceManagerReload($$1);
            $$1.pop();
        }, $$3);
    }

    public void onResourceManagerReload(ResourceManager var1);
}


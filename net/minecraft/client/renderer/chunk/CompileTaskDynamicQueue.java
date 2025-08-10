/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.world.phys.Vec3;

public class CompileTaskDynamicQueue {
    private static final int MAX_RECOMPILE_QUOTA = 2;
    private int recompileQuota = 2;
    private final List<SectionRenderDispatcher.RenderSection.CompileTask> tasks = new ObjectArrayList();
    private volatile int size = 0;

    public synchronized void add(SectionRenderDispatcher.RenderSection.CompileTask $$0) {
        this.tasks.add($$0);
        ++this.size;
    }

    @Nullable
    public synchronized SectionRenderDispatcher.RenderSection.CompileTask poll(Vec3 $$0) {
        boolean $$10;
        int $$1 = -1;
        int $$2 = -1;
        double $$3 = Double.MAX_VALUE;
        double $$4 = Double.MAX_VALUE;
        ListIterator<SectionRenderDispatcher.RenderSection.CompileTask> $$5 = this.tasks.listIterator();
        while ($$5.hasNext()) {
            int $$6 = $$5.nextIndex();
            SectionRenderDispatcher.RenderSection.CompileTask $$7 = $$5.next();
            if ($$7.isCancelled.get()) {
                $$5.remove();
                continue;
            }
            double $$8 = $$7.getRenderOrigin().distToCenterSqr($$0);
            if (!$$7.isRecompile() && $$8 < $$3) {
                $$3 = $$8;
                $$1 = $$6;
            }
            if (!$$7.isRecompile() || !($$8 < $$4)) continue;
            $$4 = $$8;
            $$2 = $$6;
        }
        boolean $$9 = $$2 >= 0;
        boolean bl = $$10 = $$1 >= 0;
        if ($$9 && (!$$10 || this.recompileQuota > 0 && $$4 < $$3)) {
            --this.recompileQuota;
            return this.removeTaskByIndex($$2);
        }
        this.recompileQuota = 2;
        return this.removeTaskByIndex($$1);
    }

    public int size() {
        return this.size;
    }

    @Nullable
    private SectionRenderDispatcher.RenderSection.CompileTask removeTaskByIndex(int $$0) {
        if ($$0 >= 0) {
            --this.size;
            return this.tasks.remove($$0);
        }
        return null;
    }

    public synchronized void clear() {
        for (SectionRenderDispatcher.RenderSection.CompileTask $$0 : this.tasks) {
            $$0.cancel();
        }
        this.tasks.clear();
        this.size = 0;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EntitySection<T extends EntityAccess> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ClassInstanceMultiMap<T> storage;
    private Visibility chunkStatus;

    public EntitySection(Class<T> $$0, Visibility $$1) {
        this.chunkStatus = $$1;
        this.storage = new ClassInstanceMultiMap<T>($$0);
    }

    public void add(T $$0) {
        this.storage.add($$0);
    }

    public boolean remove(T $$0) {
        return this.storage.remove($$0);
    }

    public AbortableIterationConsumer.Continuation getEntities(AABB $$0, AbortableIterationConsumer<T> $$1) {
        for (EntityAccess $$2 : this.storage) {
            if (!$$2.getBoundingBox().intersects($$0) || !$$1.accept($$2).shouldAbort()) continue;
            return AbortableIterationConsumer.Continuation.ABORT;
        }
        return AbortableIterationConsumer.Continuation.CONTINUE;
    }

    public <U extends T> AbortableIterationConsumer.Continuation getEntities(EntityTypeTest<T, U> $$0, AABB $$1, AbortableIterationConsumer<? super U> $$2) {
        Collection<T> $$3 = this.storage.find($$0.getBaseClass());
        if ($$3.isEmpty()) {
            return AbortableIterationConsumer.Continuation.CONTINUE;
        }
        for (EntityAccess $$4 : $$3) {
            EntityAccess $$5 = (EntityAccess)$$0.tryCast($$4);
            if ($$5 == null || !$$4.getBoundingBox().intersects($$1) || !$$2.accept($$5).shouldAbort()) continue;
            return AbortableIterationConsumer.Continuation.ABORT;
        }
        return AbortableIterationConsumer.Continuation.CONTINUE;
    }

    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    public Stream<T> getEntities() {
        return this.storage.stream();
    }

    public Visibility getStatus() {
        return this.chunkStatus;
    }

    public Visibility updateChunkStatus(Visibility $$0) {
        Visibility $$1 = this.chunkStatus;
        this.chunkStatus = $$0;
        return $$1;
    }

    @VisibleForDebug
    public int size() {
        return this.storage.size();
    }
}


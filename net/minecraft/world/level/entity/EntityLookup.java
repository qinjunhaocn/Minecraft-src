/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 */
package net.minecraft.world.level.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.slf4j.Logger;

public class EntityLookup<T extends EntityAccess> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Int2ObjectMap<T> byId = new Int2ObjectLinkedOpenHashMap();
    private final Map<UUID, T> byUuid = Maps.newHashMap();

    public <U extends T> void getEntities(EntityTypeTest<T, U> $$0, AbortableIterationConsumer<U> $$1) {
        for (EntityAccess $$2 : this.byId.values()) {
            EntityAccess $$3 = (EntityAccess)$$0.tryCast($$2);
            if ($$3 == null || !$$1.accept($$3).shouldAbort()) continue;
            return;
        }
    }

    public Iterable<T> getAllEntities() {
        return Iterables.unmodifiableIterable(this.byId.values());
    }

    public void add(T $$0) {
        UUID $$1 = $$0.getUUID();
        if (this.byUuid.containsKey($$1)) {
            LOGGER.warn("Duplicate entity UUID {}: {}", (Object)$$1, (Object)$$0);
            return;
        }
        this.byUuid.put($$1, $$0);
        this.byId.put($$0.getId(), $$0);
    }

    public void remove(T $$0) {
        this.byUuid.remove($$0.getUUID());
        this.byId.remove($$0.getId());
    }

    @Nullable
    public T getEntity(int $$0) {
        return (T)((EntityAccess)this.byId.get($$0));
    }

    @Nullable
    public T getEntity(UUID $$0) {
        return (T)((EntityAccess)this.byUuid.get($$0));
    }

    public int count() {
        return this.byUuid.size();
    }
}


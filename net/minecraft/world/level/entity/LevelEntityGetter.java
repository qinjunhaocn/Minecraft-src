/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

public interface LevelEntityGetter<T extends EntityAccess> {
    @Nullable
    public T get(int var1);

    @Nullable
    public T get(UUID var1);

    public Iterable<T> getAll();

    public <U extends T> void get(EntityTypeTest<T, U> var1, AbortableIterationConsumer<U> var2);

    public void get(AABB var1, Consumer<T> var2);

    public <U extends T> void get(EntityTypeTest<T, U> var1, AABB var2, AbortableIterationConsumer<U> var3);
}


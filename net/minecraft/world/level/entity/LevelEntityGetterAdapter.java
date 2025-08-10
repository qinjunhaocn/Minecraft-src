/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;

public class LevelEntityGetterAdapter<T extends EntityAccess>
implements LevelEntityGetter<T> {
    private final EntityLookup<T> visibleEntities;
    private final EntitySectionStorage<T> sectionStorage;

    public LevelEntityGetterAdapter(EntityLookup<T> $$0, EntitySectionStorage<T> $$1) {
        this.visibleEntities = $$0;
        this.sectionStorage = $$1;
    }

    @Override
    @Nullable
    public T get(int $$0) {
        return this.visibleEntities.getEntity($$0);
    }

    @Override
    @Nullable
    public T get(UUID $$0) {
        return this.visibleEntities.getEntity($$0);
    }

    @Override
    public Iterable<T> getAll() {
        return this.visibleEntities.getAllEntities();
    }

    @Override
    public <U extends T> void get(EntityTypeTest<T, U> $$0, AbortableIterationConsumer<U> $$1) {
        this.visibleEntities.getEntities($$0, $$1);
    }

    @Override
    public void get(AABB $$0, Consumer<T> $$1) {
        this.sectionStorage.getEntities($$0, AbortableIterationConsumer.forConsumer($$1));
    }

    @Override
    public <U extends T> void get(EntityTypeTest<T, U> $$0, AABB $$1, AbortableIterationConsumer<U> $$2) {
        this.sectionStorage.getEntities($$0, $$1, $$2);
    }
}


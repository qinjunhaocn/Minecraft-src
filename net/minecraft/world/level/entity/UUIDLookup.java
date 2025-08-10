/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.level.entity.UniquelyIdentifyable;

public interface UUIDLookup<IdentifiedType extends UniquelyIdentifyable> {
    @Nullable
    public IdentifiedType getEntity(UUID var1);
}


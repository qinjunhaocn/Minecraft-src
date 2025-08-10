/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.entity;

import java.util.UUID;

public interface UniquelyIdentifyable {
    public UUID getUUID();

    public boolean isRemoved();
}


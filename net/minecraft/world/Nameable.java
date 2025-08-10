/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public interface Nameable {
    public Component getName();

    default public boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    default public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    default public Component getCustomName() {
        return null;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.scores;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;

public interface ScoreAccess {
    public int get();

    public void set(int var1);

    default public int add(int $$0) {
        int $$1 = this.get() + $$0;
        this.set($$1);
        return $$1;
    }

    default public int increment() {
        return this.add(1);
    }

    default public void reset() {
        this.set(0);
    }

    public boolean locked();

    public void unlock();

    public void lock();

    @Nullable
    public Component display();

    public void display(@Nullable Component var1);

    public void numberFormatOverride(@Nullable NumberFormat var1);
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.PaletteResize;

public interface Palette<T> {
    public int idFor(T var1);

    public boolean maybeHas(Predicate<T> var1);

    public T valueFor(int var1);

    public void read(FriendlyByteBuf var1);

    public void write(FriendlyByteBuf var1);

    public int getSerializedSize();

    public int getSize();

    public Palette<T> copy(PaletteResize<T> var1);

    public static interface Factory {
        public <A> Palette<A> create(int var1, IdMap<A> var2, PaletteResize<A> var3, List<A> var4);
    }
}


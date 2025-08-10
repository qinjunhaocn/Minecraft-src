/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.saveddata;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public record SavedDataType<T extends SavedData>(String id, Function<SavedData.Context, T> constructor, Function<SavedData.Context, Codec<T>> codec, DataFixTypes dataFixType) {
    public SavedDataType(String $$0, Supplier<T> $$12, Codec<T> $$2, DataFixTypes $$3) {
        this($$0, (SavedData.Context $$1) -> (SavedData)$$12.get(), (SavedData.Context $$1) -> $$2, $$3);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (!($$0 instanceof SavedDataType)) return false;
        SavedDataType $$1 = (SavedDataType)((Object)$$0);
        if (!this.id.equals($$1.id)) return false;
        return true;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "SavedDataType[" + this.id + "]";
    }
}


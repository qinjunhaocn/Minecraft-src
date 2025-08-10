/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.syncher;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;

public interface EntityDataSerializer<T> {
    public StreamCodec<? super RegistryFriendlyByteBuf, T> codec();

    default public EntityDataAccessor<T> createAccessor(int $$0) {
        return new EntityDataAccessor($$0, this);
    }

    public T copy(T var1);

    public static <T> EntityDataSerializer<T> forValueType(StreamCodec<? super RegistryFriendlyByteBuf, T> $$0) {
        return () -> $$0;
    }

    public static interface ForValueType<T>
    extends EntityDataSerializer<T> {
        @Override
        default public T copy(T $$0) {
            return $$0;
        }
    }
}


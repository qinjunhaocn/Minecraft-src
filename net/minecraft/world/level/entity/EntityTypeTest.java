/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.entity;

import javax.annotation.Nullable;

public interface EntityTypeTest<B, T extends B> {
    public static <B, T extends B> EntityTypeTest<B, T> forClass(final Class<T> $$0) {
        return new EntityTypeTest<B, T>(){

            @Override
            @Nullable
            public T tryCast(B $$02) {
                return $$0.isInstance($$02) ? $$02 : null;
            }

            @Override
            public Class<? extends B> getBaseClass() {
                return $$0;
            }
        };
    }

    public static <B, T extends B> EntityTypeTest<B, T> forExactClass(final Class<T> $$0) {
        return new EntityTypeTest<B, T>(){

            @Override
            @Nullable
            public T tryCast(B $$02) {
                return $$0.equals($$02.getClass()) ? $$02 : null;
            }

            @Override
            public Class<? extends B> getBaseClass() {
                return $$0;
            }
        };
    }

    @Nullable
    public T tryCast(B var1);

    public Class<? extends B> getBaseClass();
}


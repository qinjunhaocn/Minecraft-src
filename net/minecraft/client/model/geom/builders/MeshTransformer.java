/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model.geom.builders;

import net.minecraft.client.model.geom.builders.MeshDefinition;

@FunctionalInterface
public interface MeshTransformer {
    public static final MeshTransformer IDENTITY = $$0 -> $$0;

    public static MeshTransformer scaling(float $$0) {
        float $$1 = 24.016f * (1.0f - $$0);
        return $$22 -> $$22.transformed($$2 -> $$2.scaled($$0).translated(0.0f, $$1, 0.0f));
    }

    public MeshDefinition apply(MeshDefinition var1);
}


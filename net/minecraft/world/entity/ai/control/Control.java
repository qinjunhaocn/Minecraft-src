/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;

public interface Control {
    default public float rotateTowards(float $$0, float $$1, float $$2) {
        float $$3 = Mth.degreesDifference($$0, $$1);
        float $$4 = Mth.clamp($$3, -$$2, $$2);
        return $$0 + $$4;
    }
}


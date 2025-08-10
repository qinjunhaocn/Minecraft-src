/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrays
 *  org.joml.Vector3f
 */
package com.mojang.blaze3d.vertex;

import com.google.common.primitives.Floats;
import it.unimi.dsi.fastutil.ints.IntArrays;
import org.joml.Vector3f;

public interface VertexSorting {
    public static final VertexSorting DISTANCE_TO_ORIGIN = VertexSorting.byDistance(0.0f, 0.0f, 0.0f);
    public static final VertexSorting ORTHOGRAPHIC_Z = VertexSorting.byDistance((Vector3f $$0) -> -$$0.z());

    public static VertexSorting byDistance(float $$0, float $$1, float $$2) {
        return VertexSorting.byDistance(new Vector3f($$0, $$1, $$2));
    }

    public static VertexSorting byDistance(Vector3f $$0) {
        return VertexSorting.byDistance(arg_0 -> ((Vector3f)$$0).distanceSquared(arg_0));
    }

    public static VertexSorting byDistance(DistanceFunction $$0) {
        return $$12 -> {
            float[] $$22 = new float[$$12.length];
            int[] $$3 = new int[$$12.length];
            for (int $$4 = 0; $$4 < $$12.length; ++$$4) {
                $$22[$$4] = $$0.apply($$12[$$4]);
                $$3[$$4] = $$4;
            }
            IntArrays.mergeSort((int[])$$3, ($$1, $$2) -> Floats.compare($$22[$$2], $$22[$$1]));
            return $$3;
        };
    }

    public int[] sort(Vector3f[] var1);

    public static interface DistanceFunction {
        public float apply(Vector3f var1);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Math
 *  org.joml.Matrix3f
 *  org.joml.Quaternionf
 */
package com.mojang.math;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

public record GivensParameters(float sinHalf, float cosHalf) {
    public static GivensParameters fromUnnormalized(float $$0, float $$1) {
        float $$2 = Math.invsqrt((float)($$0 * $$0 + $$1 * $$1));
        return new GivensParameters($$2 * $$0, $$2 * $$1);
    }

    public static GivensParameters fromPositiveAngle(float $$0) {
        float $$1 = Math.sin((float)($$0 / 2.0f));
        float $$2 = Math.cosFromSin((float)$$1, (float)($$0 / 2.0f));
        return new GivensParameters($$1, $$2);
    }

    public GivensParameters inverse() {
        return new GivensParameters(-this.sinHalf, this.cosHalf);
    }

    public Quaternionf aroundX(Quaternionf $$0) {
        return $$0.set(this.sinHalf, 0.0f, 0.0f, this.cosHalf);
    }

    public Quaternionf aroundY(Quaternionf $$0) {
        return $$0.set(0.0f, this.sinHalf, 0.0f, this.cosHalf);
    }

    public Quaternionf aroundZ(Quaternionf $$0) {
        return $$0.set(0.0f, 0.0f, this.sinHalf, this.cosHalf);
    }

    public float cos() {
        return this.cosHalf * this.cosHalf - this.sinHalf * this.sinHalf;
    }

    public float sin() {
        return 2.0f * this.sinHalf * this.cosHalf;
    }

    public Matrix3f aroundX(Matrix3f $$0) {
        $$0.m01 = 0.0f;
        $$0.m02 = 0.0f;
        $$0.m10 = 0.0f;
        $$0.m20 = 0.0f;
        float $$1 = this.cos();
        float $$2 = this.sin();
        $$0.m11 = $$1;
        $$0.m22 = $$1;
        $$0.m12 = $$2;
        $$0.m21 = -$$2;
        $$0.m00 = 1.0f;
        return $$0;
    }

    public Matrix3f aroundY(Matrix3f $$0) {
        $$0.m01 = 0.0f;
        $$0.m10 = 0.0f;
        $$0.m12 = 0.0f;
        $$0.m21 = 0.0f;
        float $$1 = this.cos();
        float $$2 = this.sin();
        $$0.m00 = $$1;
        $$0.m22 = $$1;
        $$0.m02 = -$$2;
        $$0.m20 = $$2;
        $$0.m11 = 1.0f;
        return $$0;
    }

    public Matrix3f aroundZ(Matrix3f $$0) {
        $$0.m02 = 0.0f;
        $$0.m12 = 0.0f;
        $$0.m20 = 0.0f;
        $$0.m21 = 0.0f;
        float $$1 = this.cos();
        float $$2 = this.sin();
        $$0.m00 = $$1;
        $$0.m11 = $$1;
        $$0.m01 = $$2;
        $$0.m10 = -$$2;
        $$0.m22 = 1.0f;
        return $$0;
    }
}


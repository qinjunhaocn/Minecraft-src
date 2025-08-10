/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.client.animation;

import org.joml.Vector3f;

public class KeyframeAnimations {
    public static Vector3f posVec(float $$0, float $$1, float $$2) {
        return new Vector3f($$0, -$$1, $$2);
    }

    public static Vector3f degreeVec(float $$0, float $$1, float $$2) {
        return new Vector3f($$0 * ((float)Math.PI / 180), $$1 * ((float)Math.PI / 180), $$2 * ((float)Math.PI / 180));
    }

    public static Vector3f scaleVec(double $$0, double $$1, double $$2) {
        return new Vector3f((float)($$0 - 1.0), (float)($$1 - 1.0), (float)($$2 - 1.0));
    }
}


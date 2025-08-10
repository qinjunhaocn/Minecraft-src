/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model.geom.builders;

public class UVPair {
    private final float u;
    private final float v;

    public UVPair(float $$0, float $$1) {
        this.u = $$0;
        this.v = $$1;
    }

    public float u() {
        return this.u;
    }

    public float v() {
        return this.v;
    }

    public String toString() {
        return "(" + this.u + "," + this.v + ")";
    }
}


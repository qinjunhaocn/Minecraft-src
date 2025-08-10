/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model.geom.builders;

public class CubeDeformation {
    public static final CubeDeformation NONE = new CubeDeformation(0.0f);
    final float growX;
    final float growY;
    final float growZ;

    public CubeDeformation(float $$0, float $$1, float $$2) {
        this.growX = $$0;
        this.growY = $$1;
        this.growZ = $$2;
    }

    public CubeDeformation(float $$0) {
        this($$0, $$0, $$0);
    }

    public CubeDeformation extend(float $$0) {
        return new CubeDeformation(this.growX + $$0, this.growY + $$0, this.growZ + $$0);
    }

    public CubeDeformation extend(float $$0, float $$1, float $$2) {
        return new CubeDeformation(this.growX + $$0, this.growY + $$1, this.growZ + $$2);
    }
}


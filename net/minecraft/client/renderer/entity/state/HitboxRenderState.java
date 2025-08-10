/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

public record HitboxRenderState(double x0, double y0, double z0, double x1, double y1, double z1, float offsetX, float offsetY, float offsetZ, float red, float green, float blue) {
    public HitboxRenderState(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, float $$6, float $$7, float $$8) {
        this($$0, $$1, $$2, $$3, $$4, $$5, 0.0f, 0.0f, 0.0f, $$6, $$7, $$8);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class SheetedDecalTextureGenerator
implements VertexConsumer {
    private final VertexConsumer delegate;
    private final Matrix4f cameraInversePose;
    private final Matrix3f normalInversePose;
    private final float textureScale;
    private final Vector3f worldPos = new Vector3f();
    private final Vector3f normal = new Vector3f();
    private float x;
    private float y;
    private float z;

    public SheetedDecalTextureGenerator(VertexConsumer $$0, PoseStack.Pose $$1, float $$2) {
        this.delegate = $$0;
        this.cameraInversePose = new Matrix4f((Matrix4fc)$$1.pose()).invert();
        this.normalInversePose = new Matrix3f((Matrix3fc)$$1.normal()).invert();
        this.textureScale = $$2;
    }

    @Override
    public VertexConsumer addVertex(float $$0, float $$1, float $$2) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
        this.delegate.addVertex($$0, $$1, $$2);
        return this;
    }

    @Override
    public VertexConsumer setColor(int $$0, int $$1, int $$2, int $$3) {
        this.delegate.setColor(-1);
        return this;
    }

    @Override
    public VertexConsumer setUv(float $$0, float $$1) {
        return this;
    }

    @Override
    public VertexConsumer setUv1(int $$0, int $$1) {
        this.delegate.setUv1($$0, $$1);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int $$0, int $$1) {
        this.delegate.setUv2($$0, $$1);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float $$0, float $$1, float $$2) {
        this.delegate.setNormal($$0, $$1, $$2);
        Vector3f $$3 = this.normalInversePose.transform($$0, $$1, $$2, this.normal);
        Direction $$4 = Direction.getApproximateNearest($$3.x(), $$3.y(), $$3.z());
        Vector3f $$5 = this.cameraInversePose.transformPosition(this.x, this.y, this.z, this.worldPos);
        $$5.rotateY((float)Math.PI);
        $$5.rotateX(-1.5707964f);
        $$5.rotate((Quaternionfc)$$4.getRotation());
        this.delegate.setUv(-$$5.x() * this.textureScale, -$$5.y() * this.textureScale);
        return this;
    }
}


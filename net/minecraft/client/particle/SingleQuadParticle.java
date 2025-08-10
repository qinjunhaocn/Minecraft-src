/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public abstract class SingleQuadParticle
extends Particle {
    protected float quadSize;

    protected SingleQuadParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
        super($$0, $$1, $$2, $$3);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected SingleQuadParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOKAT_XYZ;
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        Quaternionf $$3 = new Quaternionf();
        this.getFacingCameraMode().setRotation($$3, $$1, $$2);
        if (this.roll != 0.0f) {
            $$3.rotateZ(Mth.lerp($$2, this.oRoll, this.roll));
        }
        this.renderRotatedQuad($$0, $$1, $$3, $$2);
    }

    protected void renderRotatedQuad(VertexConsumer $$0, Camera $$1, Quaternionf $$2, float $$3) {
        Vec3 $$4 = $$1.getPosition();
        float $$5 = (float)(Mth.lerp((double)$$3, this.xo, this.x) - $$4.x());
        float $$6 = (float)(Mth.lerp((double)$$3, this.yo, this.y) - $$4.y());
        float $$7 = (float)(Mth.lerp((double)$$3, this.zo, this.z) - $$4.z());
        this.renderRotatedQuad($$0, $$2, $$5, $$6, $$7, $$3);
    }

    protected void renderRotatedQuad(VertexConsumer $$0, Quaternionf $$1, float $$2, float $$3, float $$4, float $$5) {
        float $$6 = this.getQuadSize($$5);
        float $$7 = this.getU0();
        float $$8 = this.getU1();
        float $$9 = this.getV0();
        float $$10 = this.getV1();
        int $$11 = this.getLightColor($$5);
        this.renderVertex($$0, $$1, $$2, $$3, $$4, 1.0f, -1.0f, $$6, $$8, $$10, $$11);
        this.renderVertex($$0, $$1, $$2, $$3, $$4, 1.0f, 1.0f, $$6, $$8, $$9, $$11);
        this.renderVertex($$0, $$1, $$2, $$3, $$4, -1.0f, 1.0f, $$6, $$7, $$9, $$11);
        this.renderVertex($$0, $$1, $$2, $$3, $$4, -1.0f, -1.0f, $$6, $$7, $$10, $$11);
    }

    private void renderVertex(VertexConsumer $$0, Quaternionf $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, int $$10) {
        Vector3f $$11 = new Vector3f($$5, $$6, 0.0f).rotate((Quaternionfc)$$1).mul($$7).add($$2, $$3, $$4);
        $$0.addVertex($$11.x(), $$11.y(), $$11.z()).setUv($$8, $$9).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight($$10);
    }

    public float getQuadSize(float $$0) {
        return this.quadSize;
    }

    @Override
    public Particle scale(float $$0) {
        this.quadSize *= $$0;
        return super.scale($$0);
    }

    protected abstract float getU0();

    protected abstract float getU1();

    protected abstract float getV0();

    protected abstract float getV1();

    public static interface FacingCameraMode {
        public static final FacingCameraMode LOOKAT_XYZ = ($$0, $$1, $$2) -> $$0.set((Quaternionfc)$$1.rotation());
        public static final FacingCameraMode LOOKAT_Y = ($$0, $$1, $$2) -> $$0.set(0.0f, $$1.rotation().y, 0.0f, $$1.rotation().w);

        public void setRotation(Quaternionf var1, Camera var2, float var3);
    }
}


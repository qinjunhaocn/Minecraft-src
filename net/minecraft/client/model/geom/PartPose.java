/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model.geom;

public record PartPose(float x, float y, float z, float xRot, float yRot, float zRot, float xScale, float yScale, float zScale) {
    public static final PartPose ZERO = PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

    public static PartPose offset(float $$0, float $$1, float $$2) {
        return PartPose.offsetAndRotation($$0, $$1, $$2, 0.0f, 0.0f, 0.0f);
    }

    public static PartPose rotation(float $$0, float $$1, float $$2) {
        return PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, $$0, $$1, $$2);
    }

    public static PartPose offsetAndRotation(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        return new PartPose($$0, $$1, $$2, $$3, $$4, $$5, 1.0f, 1.0f, 1.0f);
    }

    public PartPose translated(float $$0, float $$1, float $$2) {
        return new PartPose(this.x + $$0, this.y + $$1, this.z + $$2, this.xRot, this.yRot, this.zRot, this.xScale, this.yScale, this.zScale);
    }

    public PartPose withScale(float $$0) {
        return new PartPose(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot, $$0, $$0, $$0);
    }

    public PartPose scaled(float $$0) {
        if ($$0 == 1.0f) {
            return this;
        }
        return this.scaled($$0, $$0, $$0);
    }

    public PartPose scaled(float $$0, float $$1, float $$2) {
        return new PartPose(this.x * $$0, this.y * $$1, this.z * $$2, this.xRot, this.yRot, this.zRot, this.xScale * $$0, this.yScale * $$1, this.zScale * $$2);
    }
}


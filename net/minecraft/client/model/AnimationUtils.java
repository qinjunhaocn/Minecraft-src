/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class AnimationUtils {
    public static void animateCrossbowHold(ModelPart $$0, ModelPart $$1, ModelPart $$2, boolean $$3) {
        ModelPart $$4 = $$3 ? $$0 : $$1;
        ModelPart $$5 = $$3 ? $$1 : $$0;
        $$4.yRot = ($$3 ? -0.3f : 0.3f) + $$2.yRot;
        $$5.yRot = ($$3 ? 0.6f : -0.6f) + $$2.yRot;
        $$4.xRot = -1.5707964f + $$2.xRot + 0.1f;
        $$5.xRot = -1.5f + $$2.xRot;
    }

    public static void animateCrossbowCharge(ModelPart $$0, ModelPart $$1, float $$2, int $$3, boolean $$4) {
        ModelPart $$5 = $$4 ? $$0 : $$1;
        ModelPart $$6 = $$4 ? $$1 : $$0;
        $$5.yRot = $$4 ? -0.8f : 0.8f;
        $$6.xRot = $$5.xRot = -0.97079635f;
        float $$7 = Mth.clamp((float)$$3, 0.0f, $$2);
        float $$8 = $$7 / $$2;
        $$6.yRot = Mth.lerp($$8, 0.4f, 0.85f) * (float)($$4 ? 1 : -1);
        $$6.xRot = Mth.lerp($$8, $$6.xRot, -1.5707964f);
    }

    public static void swingWeaponDown(ModelPart $$0, ModelPart $$1, HumanoidArm $$2, float $$3, float $$4) {
        float $$5 = Mth.sin($$3 * (float)Math.PI);
        float $$6 = Mth.sin((1.0f - (1.0f - $$3) * (1.0f - $$3)) * (float)Math.PI);
        $$0.zRot = 0.0f;
        $$1.zRot = 0.0f;
        $$0.yRot = 0.15707964f;
        $$1.yRot = -0.15707964f;
        if ($$2 == HumanoidArm.RIGHT) {
            $$0.xRot = -1.8849558f + Mth.cos($$4 * 0.09f) * 0.15f;
            $$1.xRot = -0.0f + Mth.cos($$4 * 0.19f) * 0.5f;
            $$0.xRot += $$5 * 2.2f - $$6 * 0.4f;
            $$1.xRot += $$5 * 1.2f - $$6 * 0.4f;
        } else {
            $$0.xRot = -0.0f + Mth.cos($$4 * 0.19f) * 0.5f;
            $$1.xRot = -1.8849558f + Mth.cos($$4 * 0.09f) * 0.15f;
            $$0.xRot += $$5 * 1.2f - $$6 * 0.4f;
            $$1.xRot += $$5 * 2.2f - $$6 * 0.4f;
        }
        AnimationUtils.bobArms($$0, $$1, $$4);
    }

    public static void bobModelPart(ModelPart $$0, float $$1, float $$2) {
        $$0.zRot += $$2 * (Mth.cos($$1 * 0.09f) * 0.05f + 0.05f);
        $$0.xRot += $$2 * (Mth.sin($$1 * 0.067f) * 0.05f);
    }

    public static void bobArms(ModelPart $$0, ModelPart $$1, float $$2) {
        AnimationUtils.bobModelPart($$0, $$2, 1.0f);
        AnimationUtils.bobModelPart($$1, $$2, -1.0f);
    }

    public static void animateZombieArms(ModelPart $$0, ModelPart $$1, boolean $$2, float $$3, float $$4) {
        float $$7;
        float $$5 = Mth.sin($$3 * (float)Math.PI);
        float $$6 = Mth.sin((1.0f - (1.0f - $$3) * (1.0f - $$3)) * (float)Math.PI);
        $$1.zRot = 0.0f;
        $$0.zRot = 0.0f;
        $$1.yRot = -(0.1f - $$5 * 0.6f);
        $$0.yRot = 0.1f - $$5 * 0.6f;
        $$1.xRot = $$7 = (float)(-Math.PI) / ($$2 ? 1.5f : 2.25f);
        $$0.xRot = $$7;
        $$1.xRot += $$5 * 1.2f - $$6 * 0.4f;
        $$0.xRot += $$5 * 1.2f - $$6 * 0.4f;
        AnimationUtils.bobArms($$1, $$0, $$4);
    }
}


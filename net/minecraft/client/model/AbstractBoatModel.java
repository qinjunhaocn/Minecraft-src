/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.util.Mth;

public abstract class AbstractBoatModel
extends EntityModel<BoatRenderState> {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;

    public AbstractBoatModel(ModelPart $$0) {
        super($$0);
        this.leftPaddle = $$0.getChild("left_paddle");
        this.rightPaddle = $$0.getChild("right_paddle");
    }

    @Override
    public void setupAnim(BoatRenderState $$0) {
        super.setupAnim($$0);
        AbstractBoatModel.animatePaddle($$0.rowingTimeLeft, 0, this.leftPaddle);
        AbstractBoatModel.animatePaddle($$0.rowingTimeRight, 1, this.rightPaddle);
    }

    private static void animatePaddle(float $$0, int $$1, ModelPart $$2) {
        $$2.xRot = Mth.clampedLerp(-1.0471976f, -0.2617994f, (Mth.sin(-$$0) + 1.0f) / 2.0f);
        $$2.yRot = Mth.clampedLerp(-0.7853982f, 0.7853982f, (Mth.sin(-$$0 + 1.0f) + 1.0f) / 2.0f);
        if ($$1 == 1) {
            $$2.yRot = (float)Math.PI - $$2.yRot;
        }
    }
}


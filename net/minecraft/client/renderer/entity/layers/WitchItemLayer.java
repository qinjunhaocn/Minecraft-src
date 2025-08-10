/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import org.joml.Quaternionfc;

public class WitchItemLayer
extends CrossedArmsItemLayer<WitchRenderState, WitchModel> {
    public WitchItemLayer(RenderLayerParent<WitchRenderState, WitchModel> $$0) {
        super($$0);
    }

    @Override
    protected void applyTranslation(WitchRenderState $$0, PoseStack $$1) {
        if ($$0.isHoldingPotion) {
            ((WitchModel)this.getParentModel()).root().translateAndRotate($$1);
            ((WitchModel)this.getParentModel()).getHead().translateAndRotate($$1);
            ((WitchModel)this.getParentModel()).getNose().translateAndRotate($$1);
            $$1.translate(0.0625f, 0.25f, 0.0f);
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0f));
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(140.0f));
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(10.0f));
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(180.0f));
            return;
        }
        super.applyTranslation($$0, $$1);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Quaternionfc;

public class CustomHeadLayer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    private static final float ITEM_SCALE = 0.625f;
    private static final float SKULL_SCALE = 1.1875f;
    private final Transforms transforms;
    private final Function<SkullBlock.Type, SkullModelBase> skullModels;

    public CustomHeadLayer(RenderLayerParent<S, M> $$0, EntityModelSet $$1) {
        this($$0, $$1, Transforms.DEFAULT);
    }

    public CustomHeadLayer(RenderLayerParent<S, M> $$0, EntityModelSet $$12, Transforms $$2) {
        super($$0);
        this.transforms = $$2;
        this.skullModels = Util.memoize($$1 -> SkullBlockRenderer.createModel($$12, $$1));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        if (((LivingEntityRenderState)$$3).headItem.isEmpty() && ((LivingEntityRenderState)$$3).wornHeadType == null) {
            return;
        }
        $$0.pushPose();
        $$0.scale(this.transforms.horizontalScale(), 1.0f, this.transforms.horizontalScale());
        Object $$6 = this.getParentModel();
        ((Model)$$6).root().translateAndRotate($$0);
        ((HeadedModel)$$6).getHead().translateAndRotate($$0);
        if (((LivingEntityRenderState)$$3).wornHeadType != null) {
            $$0.translate(0.0f, this.transforms.skullYOffset(), 0.0f);
            $$0.scale(1.1875f, -1.1875f, -1.1875f);
            $$0.translate(-0.5, 0.0, -0.5);
            SkullBlock.Type $$7 = ((LivingEntityRenderState)$$3).wornHeadType;
            SkullModelBase $$8 = this.skullModels.apply($$7);
            RenderType $$9 = SkullBlockRenderer.getRenderType($$7, ((LivingEntityRenderState)$$3).wornHeadProfile);
            SkullBlockRenderer.renderSkull(null, 180.0f, ((LivingEntityRenderState)$$3).wornHeadAnimationPos, $$0, $$1, $$2, $$8, $$9);
        } else {
            CustomHeadLayer.translateToHead($$0, this.transforms);
            ((LivingEntityRenderState)$$3).headItem.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        }
        $$0.popPose();
    }

    public static void translateToHead(PoseStack $$0, Transforms $$1) {
        $$0.translate(0.0f, -0.25f + $$1.yOffset(), 0.0f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
        $$0.scale(0.625f, -0.625f, -0.625f);
    }

    public record Transforms(float yOffset, float skullYOffset, float horizontalScale) {
        public static final Transforms DEFAULT = new Transforms(0.0f, 0.0f, 1.0f);
    }
}


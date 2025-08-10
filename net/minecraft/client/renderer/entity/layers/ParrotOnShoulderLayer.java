/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotOnShoulderLayer
extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final ParrotModel model;
    private final ParrotRenderState parrotState = new ParrotRenderState();

    public ParrotOnShoulderLayer(RenderLayerParent<PlayerRenderState, PlayerModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new ParrotModel($$1.bakeLayer(ModelLayers.PARROT));
        this.parrotState.pose = ParrotModel.Pose.ON_SHOULDER;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, PlayerRenderState $$3, float $$4, float $$5) {
        Parrot.Variant $$7;
        Parrot.Variant $$6 = $$3.parrotOnLeftShoulder;
        if ($$6 != null) {
            this.renderOnShoulder($$0, $$1, $$2, $$3, $$6, $$4, $$5, true);
        }
        if (($$7 = $$3.parrotOnRightShoulder) != null) {
            this.renderOnShoulder($$0, $$1, $$2, $$3, $$7, $$4, $$5, false);
        }
    }

    private void renderOnShoulder(PoseStack $$0, MultiBufferSource $$1, int $$2, PlayerRenderState $$3, Parrot.Variant $$4, float $$5, float $$6, boolean $$7) {
        $$0.pushPose();
        $$0.translate($$7 ? 0.4f : -0.4f, $$3.isCrouching ? -1.3f : -1.5f, 0.0f);
        this.parrotState.ageInTicks = $$3.ageInTicks;
        this.parrotState.walkAnimationPos = $$3.walkAnimationPos;
        this.parrotState.walkAnimationSpeed = $$3.walkAnimationSpeed;
        this.parrotState.yRot = $$5;
        this.parrotState.xRot = $$6;
        this.model.setupAnim(this.parrotState);
        this.model.renderToBuffer($$0, $$1.getBuffer(this.model.renderType(ParrotRenderer.getVariantTexture($$4))), $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}


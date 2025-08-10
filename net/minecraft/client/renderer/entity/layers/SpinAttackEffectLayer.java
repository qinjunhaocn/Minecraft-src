/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SpinAttackEffectModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SpinAttackEffectLayer
extends RenderLayer<PlayerRenderState, PlayerModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/trident_riptide.png");
    private final SpinAttackEffectModel model;

    public SpinAttackEffectLayer(RenderLayerParent<PlayerRenderState, PlayerModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new SpinAttackEffectModel($$1.bakeLayer(ModelLayers.PLAYER_SPIN_ATTACK));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, PlayerRenderState $$3, float $$4, float $$5) {
        if (!$$3.isAutoSpinAttack) {
            return;
        }
        VertexConsumer $$6 = $$1.getBuffer(this.model.renderType(TEXTURE));
        this.model.setupAnim($$3);
        this.model.renderToBuffer($$0, $$6, $$2, OverlayTexture.NO_OVERLAY);
    }
}


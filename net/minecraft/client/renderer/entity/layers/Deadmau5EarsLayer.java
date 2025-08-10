/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerEarsModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class Deadmau5EarsLayer
extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final HumanoidModel<PlayerRenderState> model;

    public Deadmau5EarsLayer(RenderLayerParent<PlayerRenderState, PlayerModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new PlayerEarsModel($$1.bakeLayer(ModelLayers.PLAYER_EARS));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, PlayerRenderState $$3, float $$4, float $$5) {
        if (!"deadmau5".equals($$3.name) || $$3.isInvisible) {
            return;
        }
        VertexConsumer $$6 = $$1.getBuffer(RenderType.entitySolid($$3.skin.texture()));
        int $$7 = LivingEntityRenderer.getOverlayCoords($$3, 0.0f);
        ((PlayerModel)this.getParentModel()).copyPropertiesTo(this.model);
        this.model.setupAnim($$3);
        this.model.renderToBuffer($$0, $$6, $$2, $$7);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;

public class SheepWoolLayer
extends RenderLayer<SheepRenderState, SheepModel> {
    private static final ResourceLocation SHEEP_WOOL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep_wool.png");
    private final EntityModel<SheepRenderState> adultModel;
    private final EntityModel<SheepRenderState> babyModel;

    public SheepWoolLayer(RenderLayerParent<SheepRenderState, SheepModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.adultModel = new SheepFurModel($$1.bakeLayer(ModelLayers.SHEEP_WOOL));
        this.babyModel = new SheepFurModel($$1.bakeLayer(ModelLayers.SHEEP_BABY_WOOL));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, SheepRenderState $$3, float $$4, float $$5) {
        EntityModel<SheepRenderState> $$6;
        if ($$3.isSheared) {
            return;
        }
        EntityModel<SheepRenderState> entityModel = $$6 = $$3.isBaby ? this.babyModel : this.adultModel;
        if ($$3.isInvisible) {
            if ($$3.appearsGlowing) {
                $$6.setupAnim($$3);
                VertexConsumer $$7 = $$1.getBuffer(RenderType.outline(SHEEP_WOOL_LOCATION));
                $$6.renderToBuffer($$0, $$7, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f), -16777216);
            }
            return;
        }
        SheepWoolLayer.coloredCutoutModelCopyLayerRender($$6, SHEEP_WOOL_LOCATION, $$0, $$1, $$2, $$3, $$3.getWoolColor());
    }
}


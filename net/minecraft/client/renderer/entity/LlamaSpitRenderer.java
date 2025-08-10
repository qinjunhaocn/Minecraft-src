/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LlamaSpitRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.LlamaSpit;
import org.joml.Quaternionfc;

public class LlamaSpitRenderer
extends EntityRenderer<LlamaSpit, LlamaSpitRenderState> {
    private static final ResourceLocation LLAMA_SPIT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/llama/spit.png");
    private final LlamaSpitModel model;

    public LlamaSpitRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.model = new LlamaSpitModel($$0.bakeLayer(ModelLayers.LLAMA_SPIT));
    }

    @Override
    public void render(LlamaSpitRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.translate(0.0f, 0.15f, 0.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$0.yRot - 90.0f));
        $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$0.xRot));
        this.model.setupAnim($$0);
        VertexConsumer $$4 = $$2.getBuffer(this.model.renderType(LLAMA_SPIT_LOCATION));
        this.model.renderToBuffer($$1, $$4, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public LlamaSpitRenderState createRenderState() {
        return new LlamaSpitRenderState();
    }

    @Override
    public void extractRenderState(LlamaSpit $$0, LlamaSpitRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.xRot = $$0.getXRot($$2);
        $$1.yRot = $$0.getYRot($$2);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


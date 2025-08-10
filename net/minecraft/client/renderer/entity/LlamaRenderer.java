/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;

public class LlamaRenderer
extends AgeableMobRenderer<Llama, LlamaRenderState, LlamaModel> {
    private static final ResourceLocation CREAMY = ResourceLocation.withDefaultNamespace("textures/entity/llama/creamy.png");
    private static final ResourceLocation WHITE = ResourceLocation.withDefaultNamespace("textures/entity/llama/white.png");
    private static final ResourceLocation BROWN = ResourceLocation.withDefaultNamespace("textures/entity/llama/brown.png");
    private static final ResourceLocation GRAY = ResourceLocation.withDefaultNamespace("textures/entity/llama/gray.png");

    public LlamaRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1, ModelLayerLocation $$2) {
        super($$0, new LlamaModel($$0.bakeLayer($$1)), new LlamaModel($$0.bakeLayer($$2)), 0.7f);
        this.addLayer(new LlamaDecorLayer(this, $$0.getModelSet(), $$0.getEquipmentRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(LlamaRenderState $$0) {
        return switch ($$0.variant) {
            default -> throw new MatchException(null, null);
            case Llama.Variant.CREAMY -> CREAMY;
            case Llama.Variant.WHITE -> WHITE;
            case Llama.Variant.BROWN -> BROWN;
            case Llama.Variant.GRAY -> GRAY;
        };
    }

    @Override
    public LlamaRenderState createRenderState() {
        return new LlamaRenderState();
    }

    @Override
    public void extractRenderState(Llama $$0, LlamaRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.variant = $$0.getVariant();
        $$1.hasChest = !$$0.isBaby() && $$0.hasChest();
        $$1.bodyItem = $$0.getBodyArmorItem();
        $$1.isTraderLlama = $$0.isTraderLlama();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((LlamaRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


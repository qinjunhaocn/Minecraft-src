/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.layers.SheepWoolUndercoatLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.sheep.Sheep;

public class SheepRenderer
extends AgeableMobRenderer<Sheep, SheepRenderState, SheepModel> {
    private static final ResourceLocation SHEEP_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep.png");

    public SheepRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SheepModel($$0.bakeLayer(ModelLayers.SHEEP)), new SheepModel($$0.bakeLayer(ModelLayers.SHEEP_BABY)), 0.7f);
        this.addLayer(new SheepWoolUndercoatLayer(this, $$0.getModelSet()));
        this.addLayer(new SheepWoolLayer(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(SheepRenderState $$0) {
        return SHEEP_LOCATION;
    }

    @Override
    public SheepRenderState createRenderState() {
        return new SheepRenderState();
    }

    @Override
    public void extractRenderState(Sheep $$0, SheepRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.headEatAngleScale = $$0.getHeadEatAngleScale($$2);
        $$1.headEatPositionScale = $$0.getHeadEatPositionScale($$2);
        $$1.isSheared = $$0.isSheared();
        $$1.woolColor = $$0.getColor();
        $$1.id = $$0.getId();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SheepRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


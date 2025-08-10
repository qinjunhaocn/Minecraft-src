/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperRenderer
extends MobRenderer<Creeper, CreeperRenderState, CreeperModel> {
    private static final ResourceLocation CREEPER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png");

    public CreeperRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CreeperModel($$0.bakeLayer(ModelLayers.CREEPER)), 0.5f);
        this.addLayer(new CreeperPowerLayer(this, $$0.getModelSet()));
    }

    @Override
    protected void scale(CreeperRenderState $$0, PoseStack $$1) {
        float $$2 = $$0.swelling;
        float $$3 = 1.0f + Mth.sin($$2 * 100.0f) * $$2 * 0.01f;
        $$2 = Mth.clamp($$2, 0.0f, 1.0f);
        $$2 *= $$2;
        $$2 *= $$2;
        float $$4 = (1.0f + $$2 * 0.4f) * $$3;
        float $$5 = (1.0f + $$2 * 0.1f) / $$3;
        $$1.scale($$4, $$5, $$4);
    }

    @Override
    protected float getWhiteOverlayProgress(CreeperRenderState $$0) {
        float $$1 = $$0.swelling;
        if ((int)($$1 * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return Mth.clamp($$1, 0.5f, 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(CreeperRenderState $$0) {
        return CREEPER_LOCATION;
    }

    @Override
    public CreeperRenderState createRenderState() {
        return new CreeperRenderState();
    }

    @Override
    public void extractRenderState(Creeper $$0, CreeperRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.swelling = $$0.getSwelling($$2);
        $$1.isPowered = $$0.isPowered();
    }

    @Override
    protected /* synthetic */ float getWhiteOverlayProgress(LivingEntityRenderState livingEntityRenderState) {
        return this.getWhiteOverlayProgress((CreeperRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


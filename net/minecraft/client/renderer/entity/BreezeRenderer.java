/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import net.minecraft.client.renderer.entity.layers.BreezeWindLayer;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeRenderer
extends MobRenderer<Breeze, BreezeRenderState, BreezeModel> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze.png");

    public BreezeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new BreezeModel($$0.bakeLayer(ModelLayers.BREEZE)), 0.5f);
        this.addLayer(new BreezeWindLayer($$0, this));
        this.addLayer(new BreezeEyesLayer(this));
    }

    @Override
    public void render(BreezeRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        BreezeModel $$4 = (BreezeModel)this.getModel();
        BreezeRenderer.a($$4, $$4.head(), $$4.rods());
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public ResourceLocation getTextureLocation(BreezeRenderState $$0) {
        return TEXTURE_LOCATION;
    }

    @Override
    public BreezeRenderState createRenderState() {
        return new BreezeRenderState();
    }

    @Override
    public void extractRenderState(Breeze $$0, BreezeRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.idle.copyFrom($$0.idle);
        $$1.shoot.copyFrom($$0.shoot);
        $$1.slide.copyFrom($$0.slide);
        $$1.slideBack.copyFrom($$0.slideBack);
        $$1.inhale.copyFrom($$0.inhale);
        $$1.longJump.copyFrom($$0.longJump);
    }

    public static BreezeModel a(BreezeModel $$0, ModelPart ... $$1) {
        $$0.head().visible = false;
        $$0.eyes().visible = false;
        $$0.rods().visible = false;
        $$0.wind().visible = false;
        for (ModelPart $$2 : $$1) {
            $$2.visible = true;
        }
        return $$0;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((BreezeRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


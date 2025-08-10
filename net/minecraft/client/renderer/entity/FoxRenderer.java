/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Fox;
import org.joml.Quaternionfc;

public class FoxRenderer
extends AgeableMobRenderer<Fox, FoxRenderState, FoxModel> {
    private static final ResourceLocation RED_FOX_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/fox.png");
    private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/fox_sleep.png");
    private static final ResourceLocation SNOW_FOX_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/snow_fox.png");
    private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/snow_fox_sleep.png");

    public FoxRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new FoxModel($$0.bakeLayer(ModelLayers.FOX)), new FoxModel($$0.bakeLayer(ModelLayers.FOX_BABY)), 0.4f);
        this.addLayer(new FoxHeldItemLayer(this));
    }

    @Override
    protected void setupRotations(FoxRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        if ($$0.isPouncing || $$0.isFaceplanted) {
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-$$0.xRot));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FoxRenderState $$0) {
        if ($$0.variant == Fox.Variant.RED) {
            return $$0.isSleeping ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
        }
        return $$0.isSleeping ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
    }

    @Override
    public FoxRenderState createRenderState() {
        return new FoxRenderState();
    }

    @Override
    public void extractRenderState(Fox $$0, FoxRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        HoldingEntityRenderState.extractHoldingEntityRenderState($$0, $$1, this.itemModelResolver);
        $$1.headRollAngle = $$0.getHeadRollAngle($$2);
        $$1.isCrouching = $$0.isCrouching();
        $$1.crouchAmount = $$0.getCrouchAmount($$2);
        $$1.isSleeping = $$0.isSleeping();
        $$1.isSitting = $$0.isSitting();
        $$1.isFaceplanted = $$0.isFaceplanted();
        $$1.isPouncing = $$0.isPouncing();
        $$1.variant = $$0.getVariant();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((FoxRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


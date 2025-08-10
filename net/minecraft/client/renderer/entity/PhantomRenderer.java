/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import org.joml.Quaternionfc;

public class PhantomRenderer
extends MobRenderer<Phantom, PhantomRenderState, PhantomModel> {
    private static final ResourceLocation PHANTOM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/phantom.png");

    public PhantomRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new PhantomModel($$0.bakeLayer(ModelLayers.PHANTOM)), 0.75f);
        this.addLayer(new PhantomEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PhantomRenderState $$0) {
        return PHANTOM_LOCATION;
    }

    @Override
    public PhantomRenderState createRenderState() {
        return new PhantomRenderState();
    }

    @Override
    public void extractRenderState(Phantom $$0, PhantomRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.flapTime = (float)$$0.getUniqueFlapTickOffset() + $$1.ageInTicks;
        $$1.size = $$0.getPhantomSize();
    }

    @Override
    protected void scale(PhantomRenderState $$0, PoseStack $$1) {
        float $$2 = 1.0f + 0.15f * (float)$$0.size;
        $$1.scale($$2, $$2, $$2);
        $$1.translate(0.0f, 1.3125f, 0.1875f);
    }

    @Override
    protected void setupRotations(PhantomRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$0.xRot));
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((PhantomRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


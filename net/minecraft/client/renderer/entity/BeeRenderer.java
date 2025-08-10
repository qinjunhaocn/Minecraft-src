/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public class BeeRenderer
extends AgeableMobRenderer<Bee, BeeRenderState, BeeModel> {
    private static final ResourceLocation ANGRY_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry.png");
    private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry_nectar.png");
    private static final ResourceLocation BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee.png");
    private static final ResourceLocation NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_nectar.png");

    public BeeRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new BeeModel($$0.bakeLayer(ModelLayers.BEE)), new BeeModel($$0.bakeLayer(ModelLayers.BEE_BABY)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(BeeRenderState $$0) {
        if ($$0.isAngry) {
            if ($$0.hasNectar) {
                return ANGRY_NECTAR_BEE_TEXTURE;
            }
            return ANGRY_BEE_TEXTURE;
        }
        if ($$0.hasNectar) {
            return NECTAR_BEE_TEXTURE;
        }
        return BEE_TEXTURE;
    }

    @Override
    public BeeRenderState createRenderState() {
        return new BeeRenderState();
    }

    @Override
    public void extractRenderState(Bee $$0, BeeRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.rollAmount = $$0.getRollAmount($$2);
        $$1.hasStinger = !$$0.hasStung();
        $$1.isOnGround = $$0.onGround() && $$0.getDeltaMovement().lengthSqr() < 1.0E-7;
        $$1.isAngry = $$0.isAngry();
        $$1.hasNectar = $$0.hasNectar();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((BeeRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


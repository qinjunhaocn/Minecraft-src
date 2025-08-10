/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.phys.Vec3;

public class EndermanRenderer
extends MobRenderer<EnderMan, EndermanRenderState, EndermanModel<EndermanRenderState>> {
    private static final ResourceLocation ENDERMAN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderman/enderman.png");
    private final RandomSource random = RandomSource.create();

    public EndermanRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new EndermanModel($$0.bakeLayer(ModelLayers.ENDERMAN)), 0.5f);
        this.addLayer(new EnderEyesLayer(this));
        this.addLayer(new CarriedBlockLayer(this, $$0.getBlockRenderDispatcher()));
    }

    @Override
    public Vec3 getRenderOffset(EndermanRenderState $$0) {
        Vec3 $$1 = super.getRenderOffset($$0);
        if ($$0.isCreepy) {
            double $$2 = 0.02 * (double)$$0.scale;
            return $$1.add(this.random.nextGaussian() * $$2, 0.0, this.random.nextGaussian() * $$2);
        }
        return $$1;
    }

    @Override
    public ResourceLocation getTextureLocation(EndermanRenderState $$0) {
        return ENDERMAN_LOCATION;
    }

    @Override
    public EndermanRenderState createRenderState() {
        return new EndermanRenderState();
    }

    @Override
    public void extractRenderState(EnderMan $$0, EndermanRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        HumanoidMobRenderer.extractHumanoidRenderState($$0, $$1, $$2, this.itemModelResolver);
        $$1.isCreepy = $$0.isCreepy();
        $$1.carriedBlock = $$0.getCarriedBlock();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


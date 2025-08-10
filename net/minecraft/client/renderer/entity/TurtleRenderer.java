/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleRenderer
extends AgeableMobRenderer<Turtle, TurtleRenderState, TurtleModel> {
    private static final ResourceLocation TURTLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/turtle/big_sea_turtle.png");

    public TurtleRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new TurtleModel($$0.bakeLayer(ModelLayers.TURTLE)), new TurtleModel($$0.bakeLayer(ModelLayers.TURTLE_BABY)), 0.7f);
    }

    @Override
    protected float getShadowRadius(TurtleRenderState $$0) {
        float $$1 = super.getShadowRadius($$0);
        if ($$0.isBaby) {
            return $$1 * 0.83f;
        }
        return $$1;
    }

    @Override
    public TurtleRenderState createRenderState() {
        return new TurtleRenderState();
    }

    @Override
    public void extractRenderState(Turtle $$0, TurtleRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isOnLand = !$$0.isInWater() && $$0.onGround();
        $$1.isLayingEgg = $$0.isLayingEgg();
        $$1.hasEgg = !$$0.isBaby() && $$0.hasEgg();
    }

    @Override
    public ResourceLocation getTextureLocation(TurtleRenderState $$0) {
        return TURTLE_LOCATION;
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((TurtleRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
        return this.getShadowRadius((TurtleRenderState)entityRenderState);
    }
}


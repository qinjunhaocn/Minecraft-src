/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;

public class HuskRenderer
extends ZombieRenderer {
    private static final ResourceLocation HUSK_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/husk.png");

    public HuskRenderer(EntityRendererProvider.Context $$0) {
        super($$0, ModelLayers.HUSK, ModelLayers.HUSK_BABY, ModelLayers.HUSK_INNER_ARMOR, ModelLayers.HUSK_OUTER_ARMOR, ModelLayers.HUSK_BABY_INNER_ARMOR, ModelLayers.HUSK_BABY_OUTER_ARMOR);
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieRenderState $$0) {
        return HUSK_LOCATION;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ZombieRenderState)livingEntityRenderState);
    }
}


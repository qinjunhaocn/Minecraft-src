/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class ElderGuardianRenderer
extends GuardianRenderer {
    public static final ResourceLocation GUARDIAN_ELDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian_elder.png");

    public ElderGuardianRenderer(EntityRendererProvider.Context $$0) {
        super($$0, 1.2f, ModelLayers.ELDER_GUARDIAN);
    }

    @Override
    public ResourceLocation getTextureLocation(GuardianRenderState $$0) {
        return GUARDIAN_ELDER_LOCATION;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((GuardianRenderState)livingEntityRenderState);
    }
}


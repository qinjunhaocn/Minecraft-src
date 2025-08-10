/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SquidRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.GlowSquid;

public class GlowSquidRenderer
extends SquidRenderer<GlowSquid> {
    private static final ResourceLocation GLOW_SQUID_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/squid/glow_squid.png");

    public GlowSquidRenderer(EntityRendererProvider.Context $$0, SquidModel $$1, SquidModel $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public ResourceLocation getTextureLocation(SquidRenderState $$0) {
        return GLOW_SQUID_LOCATION;
    }

    @Override
    protected int getBlockLightLevel(GlowSquid $$0, BlockPos $$1) {
        int $$2 = (int)Mth.clampedLerp(0.0f, 15.0f, 1.0f - (float)$$0.getDarkTicksRemaining() / 10.0f);
        if ($$2 == 15) {
            return 15;
        }
        return Math.max($$2, super.getBlockLightLevel($$0, $$1));
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SquidRenderState)livingEntityRenderState);
    }
}


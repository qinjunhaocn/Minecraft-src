/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.MinecartTntRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.block.state.BlockState;

public class TntMinecartRenderer
extends AbstractMinecartRenderer<MinecartTNT, MinecartTntRenderState> {
    private final BlockRenderDispatcher blockRenderer;

    public TntMinecartRenderer(EntityRendererProvider.Context $$0) {
        super($$0, ModelLayers.TNT_MINECART);
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    protected void renderMinecartContents(MinecartTntRenderState $$0, BlockState $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        float $$5 = $$0.fuseRemainingInTicks;
        if ($$5 > -1.0f && $$5 < 10.0f) {
            float $$6 = 1.0f - $$5 / 10.0f;
            $$6 = Mth.clamp($$6, 0.0f, 1.0f);
            $$6 *= $$6;
            $$6 *= $$6;
            float $$7 = 1.0f + $$6 * 0.3f;
            $$2.scale($$7, $$7, $$7);
        }
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, $$1, $$2, $$3, $$4, $$5 > -1.0f && (int)$$5 / 5 % 2 == 0);
    }

    public static void renderWhiteSolidBlock(BlockRenderDispatcher $$0, BlockState $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, boolean $$5) {
        int $$7;
        if ($$5) {
            int $$6 = OverlayTexture.pack(OverlayTexture.u(1.0f), 10);
        } else {
            $$7 = OverlayTexture.NO_OVERLAY;
        }
        $$0.renderSingleBlock($$1, $$2, $$3, $$4, $$7);
    }

    @Override
    public MinecartTntRenderState createRenderState() {
        return new MinecartTntRenderState();
    }

    @Override
    public void extractRenderState(MinecartTNT $$0, MinecartTntRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.fuseRemainingInTicks = $$0.getFuse() > -1 ? (float)$$0.getFuse() - $$2 + 1.0f : -1.0f;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;
import org.joml.Quaternionfc;

public class TntRenderer
extends EntityRenderer<PrimedTnt, TntRenderState> {
    private final BlockRenderDispatcher blockRenderer;

    public TntRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5f;
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    public void render(TntRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.translate(0.0f, 0.5f, 0.0f);
        float $$4 = $$0.fuseRemainingInTicks;
        if ($$0.fuseRemainingInTicks < 10.0f) {
            float $$5 = 1.0f - $$0.fuseRemainingInTicks / 10.0f;
            $$5 = Mth.clamp($$5, 0.0f, 1.0f);
            $$5 *= $$5;
            $$5 *= $$5;
            float $$6 = 1.0f + $$5 * 0.3f;
            $$1.scale($$6, $$6, $$6);
        }
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-90.0f));
        $$1.translate(-0.5f, -0.5f, 0.5f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0f));
        if ($$0.blockState != null) {
            TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, $$0.blockState, $$1, $$2, $$3, (int)$$4 / 5 % 2 == 0);
        }
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public TntRenderState createRenderState() {
        return new TntRenderState();
    }

    @Override
    public void extractRenderState(PrimedTnt $$0, TntRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.fuseRemainingInTicks = (float)$$0.getFuse() - $$2 + 1.0f;
        $$1.blockState = $$0.getBlockState();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


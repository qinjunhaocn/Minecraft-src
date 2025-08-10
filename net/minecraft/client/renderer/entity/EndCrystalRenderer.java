/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EndCrystalModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;

public class EndCrystalRenderer
extends EntityRenderer<EndCrystal, EndCrystalRenderState> {
    private static final ResourceLocation END_CRYSTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
    private final EndCrystalModel model;

    public EndCrystalRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5f;
        this.model = new EndCrystalModel($$0.bakeLayer(ModelLayers.END_CRYSTAL));
    }

    @Override
    public void render(EndCrystalRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.scale(2.0f, 2.0f, 2.0f);
        $$1.translate(0.0f, -0.5f, 0.0f);
        this.model.setupAnim($$0);
        this.model.renderToBuffer($$1, $$2.getBuffer(RENDER_TYPE), $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        Vec3 $$4 = $$0.beamOffset;
        if ($$4 != null) {
            float $$5 = EndCrystalRenderer.getY($$0.ageInTicks);
            float $$6 = (float)$$4.x;
            float $$7 = (float)$$4.y;
            float $$8 = (float)$$4.z;
            $$1.translate($$4);
            EnderDragonRenderer.renderCrystalBeams(-$$6, -$$7 + $$5, -$$8, $$0.ageInTicks, $$1, $$2, $$3);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    public static float getY(float $$0) {
        float $$1 = Mth.sin($$0 * 0.2f) / 2.0f + 0.5f;
        $$1 = ($$1 * $$1 + $$1) * 0.4f;
        return $$1 - 1.4f;
    }

    @Override
    public EndCrystalRenderState createRenderState() {
        return new EndCrystalRenderState();
    }

    @Override
    public void extractRenderState(EndCrystal $$0, EndCrystalRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.ageInTicks = (float)$$0.time + $$2;
        $$1.showsBottom = $$0.showsBottom();
        BlockPos $$3 = $$0.getBeamTarget();
        $$1.beamOffset = $$3 != null ? Vec3.atCenterOf($$3).subtract($$0.getPosition($$2)) : null;
    }

    @Override
    public boolean shouldRender(EndCrystal $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        return super.shouldRender($$0, $$1, $$2, $$3, $$4) || $$0.getBeamTarget() != null;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


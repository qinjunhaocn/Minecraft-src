/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import org.joml.Quaternionfc;

public class ExperienceOrbRenderer
extends EntityRenderer<ExperienceOrb, ExperienceOrbRenderState> {
    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

    public ExperienceOrbRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    @Override
    protected int getBlockLightLevel(ExperienceOrb $$0, BlockPos $$1) {
        return Mth.clamp(super.getBlockLightLevel($$0, $$1) + 7, 0, 15);
    }

    @Override
    public void render(ExperienceOrbRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        int $$4 = $$0.icon;
        float $$5 = (float)($$4 % 4 * 16 + 0) / 64.0f;
        float $$6 = (float)($$4 % 4 * 16 + 16) / 64.0f;
        float $$7 = (float)($$4 / 4 * 16 + 0) / 64.0f;
        float $$8 = (float)($$4 / 4 * 16 + 16) / 64.0f;
        float $$9 = 1.0f;
        float $$10 = 0.5f;
        float $$11 = 0.25f;
        float $$12 = 255.0f;
        float $$13 = $$0.ageInTicks / 2.0f;
        int $$14 = (int)((Mth.sin($$13 + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int $$15 = 255;
        int $$16 = (int)((Mth.sin($$13 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        $$1.translate(0.0f, 0.1f, 0.0f);
        $$1.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
        float $$17 = 0.3f;
        $$1.scale(0.3f, 0.3f, 0.3f);
        VertexConsumer $$18 = $$2.getBuffer(RENDER_TYPE);
        PoseStack.Pose $$19 = $$1.last();
        ExperienceOrbRenderer.vertex($$18, $$19, -0.5f, -0.25f, $$14, 255, $$16, $$5, $$8, $$3);
        ExperienceOrbRenderer.vertex($$18, $$19, 0.5f, -0.25f, $$14, 255, $$16, $$6, $$8, $$3);
        ExperienceOrbRenderer.vertex($$18, $$19, 0.5f, 0.75f, $$14, 255, $$16, $$6, $$7, $$3);
        ExperienceOrbRenderer.vertex($$18, $$19, -0.5f, 0.75f, $$14, 255, $$16, $$5, $$7, $$3);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    private static void vertex(VertexConsumer $$0, PoseStack.Pose $$1, float $$2, float $$3, int $$4, int $$5, int $$6, float $$7, float $$8, int $$9) {
        $$0.addVertex($$1, $$2, $$3, 0.0f).setColor($$4, $$5, $$6, 128).setUv($$7, $$8).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$9).setNormal($$1, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public ExperienceOrbRenderState createRenderState() {
        return new ExperienceOrbRenderState();
    }

    @Override
    public void extractRenderState(ExperienceOrb $$0, ExperienceOrbRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.icon = $$0.getIcon();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


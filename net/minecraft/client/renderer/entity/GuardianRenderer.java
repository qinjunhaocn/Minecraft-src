/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class GuardianRenderer
extends MobRenderer<Guardian, GuardianRenderState, GuardianModel> {
    private static final ResourceLocation GUARDIAN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    public GuardianRenderer(EntityRendererProvider.Context $$0) {
        this($$0, 0.5f, ModelLayers.GUARDIAN);
    }

    protected GuardianRenderer(EntityRendererProvider.Context $$0, float $$1, ModelLayerLocation $$2) {
        super($$0, new GuardianModel($$0.bakeLayer($$2)), $$1);
    }

    @Override
    public boolean shouldRender(Guardian $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        LivingEntity $$5;
        if (super.shouldRender($$0, $$1, $$2, $$3, $$4)) {
            return true;
        }
        if ($$0.hasActiveAttackTarget() && ($$5 = $$0.getActiveAttackTarget()) != null) {
            Vec3 $$6 = this.getPosition($$5, (double)$$5.getBbHeight() * 0.5, 1.0f);
            Vec3 $$7 = this.getPosition($$0, $$0.getEyeHeight(), 1.0f);
            return $$1.isVisible(new AABB($$7.x, $$7.y, $$7.z, $$6.x, $$6.y, $$6.z));
        }
        return false;
    }

    private Vec3 getPosition(LivingEntity $$0, double $$1, float $$2) {
        double $$3 = Mth.lerp((double)$$2, $$0.xOld, $$0.getX());
        double $$4 = Mth.lerp((double)$$2, $$0.yOld, $$0.getY()) + $$1;
        double $$5 = Mth.lerp((double)$$2, $$0.zOld, $$0.getZ());
        return new Vec3($$3, $$4, $$5);
    }

    @Override
    public void render(GuardianRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        super.render($$0, $$1, $$2, $$3);
        Vec3 $$4 = $$0.attackTargetPosition;
        if ($$4 != null) {
            float $$5 = $$0.attackTime * 0.5f % 1.0f;
            $$1.pushPose();
            $$1.translate(0.0f, $$0.eyeHeight, 0.0f);
            GuardianRenderer.renderBeam($$1, $$2.getBuffer(BEAM_RENDER_TYPE), $$4.subtract($$0.eyePosition), $$0.attackTime, $$0.attackScale, $$5);
            $$1.popPose();
        }
    }

    private static void renderBeam(PoseStack $$0, VertexConsumer $$1, Vec3 $$2, float $$3, float $$4, float $$5) {
        float $$6 = (float)($$2.length() + 1.0);
        $$2 = $$2.normalize();
        float $$7 = (float)Math.acos($$2.y);
        float $$8 = 1.5707964f - (float)Math.atan2($$2.z, $$2.x);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$8 * 57.295776f));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$7 * 57.295776f));
        float $$9 = $$3 * 0.05f * -1.5f;
        float $$10 = $$4 * $$4;
        int $$11 = 64 + (int)($$10 * 191.0f);
        int $$12 = 32 + (int)($$10 * 191.0f);
        int $$13 = 128 - (int)($$10 * 64.0f);
        float $$14 = 0.2f;
        float $$15 = 0.282f;
        float $$16 = Mth.cos($$9 + 2.3561945f) * 0.282f;
        float $$17 = Mth.sin($$9 + 2.3561945f) * 0.282f;
        float $$18 = Mth.cos($$9 + 0.7853982f) * 0.282f;
        float $$19 = Mth.sin($$9 + 0.7853982f) * 0.282f;
        float $$20 = Mth.cos($$9 + 3.926991f) * 0.282f;
        float $$21 = Mth.sin($$9 + 3.926991f) * 0.282f;
        float $$22 = Mth.cos($$9 + 5.4977875f) * 0.282f;
        float $$23 = Mth.sin($$9 + 5.4977875f) * 0.282f;
        float $$24 = Mth.cos($$9 + (float)Math.PI) * 0.2f;
        float $$25 = Mth.sin($$9 + (float)Math.PI) * 0.2f;
        float $$26 = Mth.cos($$9 + 0.0f) * 0.2f;
        float $$27 = Mth.sin($$9 + 0.0f) * 0.2f;
        float $$28 = Mth.cos($$9 + 1.5707964f) * 0.2f;
        float $$29 = Mth.sin($$9 + 1.5707964f) * 0.2f;
        float $$30 = Mth.cos($$9 + 4.712389f) * 0.2f;
        float $$31 = Mth.sin($$9 + 4.712389f) * 0.2f;
        float $$32 = $$6;
        float $$33 = 0.0f;
        float $$34 = 0.4999f;
        float $$35 = -1.0f + $$5;
        float $$36 = $$35 + $$6 * 2.5f;
        PoseStack.Pose $$37 = $$0.last();
        GuardianRenderer.vertex($$1, $$37, $$24, $$32, $$25, $$11, $$12, $$13, 0.4999f, $$36);
        GuardianRenderer.vertex($$1, $$37, $$24, 0.0f, $$25, $$11, $$12, $$13, 0.4999f, $$35);
        GuardianRenderer.vertex($$1, $$37, $$26, 0.0f, $$27, $$11, $$12, $$13, 0.0f, $$35);
        GuardianRenderer.vertex($$1, $$37, $$26, $$32, $$27, $$11, $$12, $$13, 0.0f, $$36);
        GuardianRenderer.vertex($$1, $$37, $$28, $$32, $$29, $$11, $$12, $$13, 0.4999f, $$36);
        GuardianRenderer.vertex($$1, $$37, $$28, 0.0f, $$29, $$11, $$12, $$13, 0.4999f, $$35);
        GuardianRenderer.vertex($$1, $$37, $$30, 0.0f, $$31, $$11, $$12, $$13, 0.0f, $$35);
        GuardianRenderer.vertex($$1, $$37, $$30, $$32, $$31, $$11, $$12, $$13, 0.0f, $$36);
        float $$38 = Mth.floor($$3) % 2 == 0 ? 0.5f : 0.0f;
        GuardianRenderer.vertex($$1, $$37, $$16, $$32, $$17, $$11, $$12, $$13, 0.5f, $$38 + 0.5f);
        GuardianRenderer.vertex($$1, $$37, $$18, $$32, $$19, $$11, $$12, $$13, 1.0f, $$38 + 0.5f);
        GuardianRenderer.vertex($$1, $$37, $$22, $$32, $$23, $$11, $$12, $$13, 1.0f, $$38);
        GuardianRenderer.vertex($$1, $$37, $$20, $$32, $$21, $$11, $$12, $$13, 0.5f, $$38);
    }

    private static void vertex(VertexConsumer $$0, PoseStack.Pose $$1, float $$2, float $$3, float $$4, int $$5, int $$6, int $$7, float $$8, float $$9) {
        $$0.addVertex($$1, $$2, $$3, $$4).setColor($$5, $$6, $$7, 255).setUv($$8, $$9).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal($$1, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(GuardianRenderState $$0) {
        return GUARDIAN_LOCATION;
    }

    @Override
    public GuardianRenderState createRenderState() {
        return new GuardianRenderState();
    }

    @Override
    public void extractRenderState(Guardian $$0, GuardianRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.spikesAnimation = $$0.getSpikesAnimation($$2);
        $$1.tailAnimation = $$0.getTailAnimation($$2);
        $$1.eyePosition = $$0.getEyePosition($$2);
        Entity $$3 = GuardianRenderer.getEntityToLookAt($$0);
        if ($$3 != null) {
            $$1.lookDirection = $$0.getViewVector($$2);
            $$1.lookAtPosition = $$3.getEyePosition($$2);
        } else {
            $$1.lookDirection = null;
            $$1.lookAtPosition = null;
        }
        LivingEntity $$4 = $$0.getActiveAttackTarget();
        if ($$4 != null) {
            $$1.attackScale = $$0.getAttackAnimationScale($$2);
            $$1.attackTime = $$0.getClientSideAttackTime() + $$2;
            $$1.attackTargetPosition = this.getPosition($$4, (double)$$4.getBbHeight() * 0.5, $$2);
        } else {
            $$1.attackTargetPosition = null;
        }
    }

    @Nullable
    private static Entity getEntityToLookAt(Guardian $$0) {
        Entity $$1 = Minecraft.getInstance().getCameraEntity();
        if ($$0.hasActiveAttackTarget()) {
            return $$0.getActiveAttackTarget();
        }
        return $$1;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((GuardianRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


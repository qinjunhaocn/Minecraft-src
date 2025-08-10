/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class FishingHookRenderer
extends EntityRenderer<FishingHook, FishingHookRenderState> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
    private static final double VIEW_BOBBING_SCALE = 960.0;

    public FishingHookRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public boolean shouldRender(FishingHook $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        return super.shouldRender($$0, $$1, $$2, $$3, $$4) && $$0.getPlayerOwner() != null;
    }

    @Override
    public void render(FishingHookRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.pushPose();
        $$1.scale(0.5f, 0.5f, 0.5f);
        $$1.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
        PoseStack.Pose $$4 = $$1.last();
        VertexConsumer $$5 = $$2.getBuffer(RENDER_TYPE);
        FishingHookRenderer.vertex($$5, $$4, $$3, 0.0f, 0, 0, 1);
        FishingHookRenderer.vertex($$5, $$4, $$3, 1.0f, 0, 1, 1);
        FishingHookRenderer.vertex($$5, $$4, $$3, 1.0f, 1, 1, 0);
        FishingHookRenderer.vertex($$5, $$4, $$3, 0.0f, 1, 0, 0);
        $$1.popPose();
        float $$6 = (float)$$0.lineOriginOffset.x;
        float $$7 = (float)$$0.lineOriginOffset.y;
        float $$8 = (float)$$0.lineOriginOffset.z;
        VertexConsumer $$9 = $$2.getBuffer(RenderType.lineStrip());
        PoseStack.Pose $$10 = $$1.last();
        int $$11 = 16;
        for (int $$12 = 0; $$12 <= 16; ++$$12) {
            FishingHookRenderer.stringVertex($$6, $$7, $$8, $$9, $$10, FishingHookRenderer.fraction($$12, 16), FishingHookRenderer.fraction($$12 + 1, 16));
        }
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    public static HumanoidArm getHoldingArm(Player $$0) {
        return $$0.getMainHandItem().getItem() instanceof FishingRodItem ? $$0.getMainArm() : $$0.getMainArm().getOpposite();
    }

    private Vec3 getPlayerHandPos(Player $$0, float $$1, float $$2) {
        int $$3;
        int n = $$3 = FishingHookRenderer.getHoldingArm($$0) == HumanoidArm.RIGHT ? 1 : -1;
        if (!this.entityRenderDispatcher.options.getCameraType().isFirstPerson() || $$0 != Minecraft.getInstance().player) {
            float $$4 = Mth.lerp($$2, $$0.yBodyRotO, $$0.yBodyRot) * ((float)Math.PI / 180);
            double $$5 = Mth.sin($$4);
            double $$6 = Mth.cos($$4);
            float $$7 = $$0.getScale();
            double $$8 = (double)$$3 * 0.35 * (double)$$7;
            double $$9 = 0.8 * (double)$$7;
            float $$10 = $$0.isCrouching() ? -0.1875f : 0.0f;
            return $$0.getEyePosition($$2).add(-$$6 * $$8 - $$5 * $$9, (double)$$10 - 0.45 * (double)$$7, -$$5 * $$8 + $$6 * $$9);
        }
        double $$11 = 960.0 / (double)this.entityRenderDispatcher.options.fov().get().intValue();
        Vec3 $$12 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)$$3 * 0.525f, -0.1f).scale($$11).yRot($$1 * 0.5f).xRot(-$$1 * 0.7f);
        return $$0.getEyePosition($$2).add($$12);
    }

    private static float fraction(int $$0, int $$1) {
        return (float)$$0 / (float)$$1;
    }

    private static void vertex(VertexConsumer $$0, PoseStack.Pose $$1, int $$2, float $$3, int $$4, int $$5, int $$6) {
        $$0.addVertex($$1, $$3 - 0.5f, (float)$$4 - 0.5f, 0.0f).setColor(-1).setUv($$5, $$6).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$2).setNormal($$1, 0.0f, 1.0f, 0.0f);
    }

    private static void stringVertex(float $$0, float $$1, float $$2, VertexConsumer $$3, PoseStack.Pose $$4, float $$5, float $$6) {
        float $$7 = $$0 * $$5;
        float $$8 = $$1 * ($$5 * $$5 + $$5) * 0.5f + 0.25f;
        float $$9 = $$2 * $$5;
        float $$10 = $$0 * $$6 - $$7;
        float $$11 = $$1 * ($$6 * $$6 + $$6) * 0.5f + 0.25f - $$8;
        float $$12 = $$2 * $$6 - $$9;
        float $$13 = Mth.sqrt($$10 * $$10 + $$11 * $$11 + $$12 * $$12);
        $$3.addVertex($$4, $$7, $$8, $$9).setColor(-16777216).setNormal($$4, $$10 /= $$13, $$11 /= $$13, $$12 /= $$13);
    }

    @Override
    public FishingHookRenderState createRenderState() {
        return new FishingHookRenderState();
    }

    @Override
    public void extractRenderState(FishingHook $$0, FishingHookRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        Player $$3 = $$0.getPlayerOwner();
        if ($$3 == null) {
            $$1.lineOriginOffset = Vec3.ZERO;
            return;
        }
        float $$4 = $$3.getAttackAnim($$2);
        float $$5 = Mth.sin(Mth.sqrt($$4) * (float)Math.PI);
        Vec3 $$6 = this.getPlayerHandPos($$3, $$5, $$2);
        Vec3 $$7 = $$0.getPosition($$2).add(0.0, 0.25, 0.0);
        $$1.lineOriginOffset = $$6.subtract($$7);
    }

    @Override
    protected boolean affectedByCulling(FishingHook $$0) {
        return false;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ boolean affectedByCulling(Entity entity) {
        return this.affectedByCulling((FishingHook)entity);
    }
}


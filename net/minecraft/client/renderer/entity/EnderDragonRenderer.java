/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.dragon.EnderDragonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class EnderDragonRenderer
extends EntityRenderer<EnderDragon, EnderDragonRenderState> {
    public static final ResourceLocation CRYSTAL_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation DRAGON_EYES_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
    private static final RenderType DECAL = RenderType.entityDecal(DRAGON_LOCATION);
    private static final RenderType EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
    private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
    private final EnderDragonModel model;

    public EnderDragonRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5f;
        this.model = new EnderDragonModel($$0.bakeLayer(ModelLayers.ENDER_DRAGON));
    }

    @Override
    public void render(EnderDragonRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        float $$4 = $$0.getHistoricalPos(7).yRot();
        float $$5 = (float)($$0.getHistoricalPos(5).y() - $$0.getHistoricalPos(10).y());
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-$$4));
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$5 * 10.0f));
        $$1.translate(0.0f, 0.0f, 1.0f);
        $$1.scale(-1.0f, -1.0f, 1.0f);
        $$1.translate(0.0f, -1.501f, 0.0f);
        this.model.setupAnim($$0);
        if ($$0.deathTime > 0.0f) {
            float $$6 = $$0.deathTime / 200.0f;
            int $$7 = ARGB.color(Mth.floor($$6 * 255.0f), -1);
            VertexConsumer $$8 = $$2.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION));
            this.model.renderToBuffer($$1, $$8, $$3, OverlayTexture.NO_OVERLAY, $$7);
            VertexConsumer $$9 = $$2.getBuffer(DECAL);
            this.model.renderToBuffer($$1, $$9, $$3, OverlayTexture.pack(0.0f, $$0.hasRedOverlay));
        } else {
            VertexConsumer $$10 = $$2.getBuffer(RENDER_TYPE);
            this.model.renderToBuffer($$1, $$10, $$3, OverlayTexture.pack(0.0f, $$0.hasRedOverlay));
        }
        VertexConsumer $$11 = $$2.getBuffer(EYES);
        this.model.renderToBuffer($$1, $$11, $$3, OverlayTexture.NO_OVERLAY);
        if ($$0.deathTime > 0.0f) {
            float $$12 = $$0.deathTime / 200.0f;
            $$1.pushPose();
            $$1.translate(0.0f, -1.0f, -2.0f);
            EnderDragonRenderer.renderRays($$1, $$12, $$2.getBuffer(RenderType.dragonRays()));
            EnderDragonRenderer.renderRays($$1, $$12, $$2.getBuffer(RenderType.dragonRaysDepth()));
            $$1.popPose();
        }
        $$1.popPose();
        if ($$0.beamOffset != null) {
            EnderDragonRenderer.renderCrystalBeams((float)$$0.beamOffset.x, (float)$$0.beamOffset.y, (float)$$0.beamOffset.z, $$0.ageInTicks, $$1, $$2, $$3);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private static void renderRays(PoseStack $$0, float $$1, VertexConsumer $$2) {
        $$0.pushPose();
        float $$3 = Math.min($$1 > 0.8f ? ($$1 - 0.8f) / 0.2f : 0.0f, 1.0f);
        int $$4 = ARGB.colorFromFloat(1.0f - $$3, 1.0f, 1.0f, 1.0f);
        int $$5 = 0xFF00FF;
        RandomSource $$6 = RandomSource.create(432L);
        Vector3f $$7 = new Vector3f();
        Vector3f $$8 = new Vector3f();
        Vector3f $$9 = new Vector3f();
        Vector3f $$10 = new Vector3f();
        Quaternionf $$11 = new Quaternionf();
        int $$12 = Mth.floor(($$1 + $$1 * $$1) / 2.0f * 60.0f);
        for (int $$13 = 0; $$13 < $$12; ++$$13) {
            $$11.rotationXYZ($$6.nextFloat() * ((float)Math.PI * 2), $$6.nextFloat() * ((float)Math.PI * 2), $$6.nextFloat() * ((float)Math.PI * 2)).rotateXYZ($$6.nextFloat() * ((float)Math.PI * 2), $$6.nextFloat() * ((float)Math.PI * 2), $$6.nextFloat() * ((float)Math.PI * 2) + $$1 * 1.5707964f);
            $$0.mulPose((Quaternionfc)$$11);
            float $$14 = $$6.nextFloat() * 20.0f + 5.0f + $$3 * 10.0f;
            float $$15 = $$6.nextFloat() * 2.0f + 1.0f + $$3 * 2.0f;
            $$8.set(-HALF_SQRT_3 * $$15, $$14, -0.5f * $$15);
            $$9.set(HALF_SQRT_3 * $$15, $$14, -0.5f * $$15);
            $$10.set(0.0f, $$14, $$15);
            PoseStack.Pose $$16 = $$0.last();
            $$2.addVertex($$16, $$7).setColor($$4);
            $$2.addVertex($$16, $$8).setColor(0xFF00FF);
            $$2.addVertex($$16, $$9).setColor(0xFF00FF);
            $$2.addVertex($$16, $$7).setColor($$4);
            $$2.addVertex($$16, $$9).setColor(0xFF00FF);
            $$2.addVertex($$16, $$10).setColor(0xFF00FF);
            $$2.addVertex($$16, $$7).setColor($$4);
            $$2.addVertex($$16, $$10).setColor(0xFF00FF);
            $$2.addVertex($$16, $$8).setColor(0xFF00FF);
        }
        $$0.popPose();
    }

    public static void renderCrystalBeams(float $$0, float $$1, float $$2, float $$3, PoseStack $$4, MultiBufferSource $$5, int $$6) {
        float $$7 = Mth.sqrt($$0 * $$0 + $$2 * $$2);
        float $$8 = Mth.sqrt($$0 * $$0 + $$1 * $$1 + $$2 * $$2);
        $$4.pushPose();
        $$4.translate(0.0f, 2.0f, 0.0f);
        $$4.mulPose((Quaternionfc)Axis.YP.rotation((float)(-Math.atan2($$2, $$0)) - 1.5707964f));
        $$4.mulPose((Quaternionfc)Axis.XP.rotation((float)(-Math.atan2($$7, $$1)) - 1.5707964f));
        VertexConsumer $$9 = $$5.getBuffer(BEAM);
        float $$10 = 0.0f - $$3 * 0.01f;
        float $$11 = $$8 / 32.0f - $$3 * 0.01f;
        int $$12 = 8;
        float $$13 = 0.0f;
        float $$14 = 0.75f;
        float $$15 = 0.0f;
        PoseStack.Pose $$16 = $$4.last();
        for (int $$17 = 1; $$17 <= 8; ++$$17) {
            float $$18 = Mth.sin((float)$$17 * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float $$19 = Mth.cos((float)$$17 * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float $$20 = (float)$$17 / 8.0f;
            $$9.addVertex($$16, $$13 * 0.2f, $$14 * 0.2f, 0.0f).setColor(-16777216).setUv($$15, $$10).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$6).setNormal($$16, 0.0f, -1.0f, 0.0f);
            $$9.addVertex($$16, $$13, $$14, $$8).setColor(-1).setUv($$15, $$11).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$6).setNormal($$16, 0.0f, -1.0f, 0.0f);
            $$9.addVertex($$16, $$18, $$19, $$8).setColor(-1).setUv($$20, $$11).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$6).setNormal($$16, 0.0f, -1.0f, 0.0f);
            $$9.addVertex($$16, $$18 * 0.2f, $$19 * 0.2f, 0.0f).setColor(-16777216).setUv($$20, $$10).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$6).setNormal($$16, 0.0f, -1.0f, 0.0f);
            $$13 = $$18;
            $$14 = $$19;
            $$15 = $$20;
        }
        $$4.popPose();
    }

    @Override
    public EnderDragonRenderState createRenderState() {
        return new EnderDragonRenderState();
    }

    @Override
    public void extractRenderState(EnderDragon $$0, EnderDragonRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.flapTime = Mth.lerp($$2, $$0.oFlapTime, $$0.flapTime);
        $$1.deathTime = $$0.dragonDeathTime > 0 ? (float)$$0.dragonDeathTime + $$2 : 0.0f;
        $$1.hasRedOverlay = $$0.hurtTime > 0;
        EndCrystal $$3 = $$0.nearestCrystal;
        if ($$3 != null) {
            Vec3 $$4 = $$3.getPosition($$2).add(0.0, EndCrystalRenderer.getY((float)$$3.time + $$2), 0.0);
            $$1.beamOffset = $$4.subtract($$0.getPosition($$2));
        } else {
            $$1.beamOffset = null;
        }
        DragonPhaseInstance $$5 = $$0.getPhaseManager().getCurrentPhase();
        $$1.isLandingOrTakingOff = $$5 == EnderDragonPhase.LANDING || $$5 == EnderDragonPhase.TAKEOFF;
        $$1.isSitting = $$5.isSitting();
        BlockPos $$6 = $$0.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation($$0.getFightOrigin()));
        $$1.distanceToEgg = $$6.distToCenterSqr($$0.position());
        $$1.partialTicks = $$0.isDeadOrDying() ? 0.0f : $$2;
        $$1.flightHistory.copyFrom($$0.flightHistory);
    }

    @Override
    protected void extractAdditionalHitboxes(EnderDragon $$0, ImmutableList.Builder<HitboxRenderState> $$1, float $$2) {
        super.extractAdditionalHitboxes($$0, $$1, $$2);
        double $$3 = -Mth.lerp((double)$$2, $$0.xOld, $$0.getX());
        double $$4 = -Mth.lerp((double)$$2, $$0.yOld, $$0.getY());
        double $$5 = -Mth.lerp((double)$$2, $$0.zOld, $$0.getZ());
        for (EnderDragonPart $$6 : $$0.t()) {
            AABB $$7 = $$6.getBoundingBox();
            HitboxRenderState $$8 = new HitboxRenderState($$7.minX - $$6.getX(), $$7.minY - $$6.getY(), $$7.minZ - $$6.getZ(), $$7.maxX - $$6.getX(), $$7.maxY - $$6.getY(), $$7.maxZ - $$6.getZ(), (float)($$3 + Mth.lerp((double)$$2, $$6.xOld, $$6.getX())), (float)($$4 + Mth.lerp((double)$$2, $$6.yOld, $$6.getY())), (float)($$5 + Mth.lerp((double)$$2, $$6.zOld, $$6.getZ())), 0.25f, 1.0f, 0.0f);
            $$1.add((Object)$$8);
        }
    }

    @Override
    protected boolean affectedByCulling(EnderDragon $$0) {
        return false;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ boolean affectedByCulling(Entity entity) {
        return this.affectedByCulling((EnderDragon)entity);
    }
}


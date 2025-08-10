/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
    protected static final float NAMETAG_SCALE = 0.025f;
    public static final int LEASH_RENDER_STEPS = 24;
    public static final float LEASH_WIDTH = 0.05f;
    protected final EntityRenderDispatcher entityRenderDispatcher;
    private final Font font;
    protected float shadowRadius;
    protected float shadowStrength = 1.0f;
    private final S reusedState = this.createRenderState();

    protected EntityRenderer(EntityRendererProvider.Context $$0) {
        this.entityRenderDispatcher = $$0.getEntityRenderDispatcher();
        this.font = $$0.getFont();
    }

    public final int getPackedLightCoords(T $$0, float $$1) {
        BlockPos $$2 = BlockPos.containing(((Entity)$$0).getLightProbePosition($$1));
        return LightTexture.pack(this.getBlockLightLevel($$0, $$2), this.getSkyLightLevel($$0, $$2));
    }

    protected int getSkyLightLevel(T $$0, BlockPos $$1) {
        return ((Entity)$$0).level().getBrightness(LightLayer.SKY, $$1);
    }

    protected int getBlockLightLevel(T $$0, BlockPos $$1) {
        if (((Entity)$$0).isOnFire()) {
            return 15;
        }
        return ((Entity)$$0).level().getBrightness(LightLayer.BLOCK, $$1);
    }

    public boolean shouldRender(T $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        Leashable $$6;
        Entity $$7;
        if (!((Entity)$$0).shouldRender($$2, $$3, $$4)) {
            return false;
        }
        if (!this.affectedByCulling($$0)) {
            return true;
        }
        AABB $$5 = this.getBoundingBoxForCulling($$0).inflate(0.5);
        if ($$5.hasNaN() || $$5.getSize() == 0.0) {
            $$5 = new AABB(((Entity)$$0).getX() - 2.0, ((Entity)$$0).getY() - 2.0, ((Entity)$$0).getZ() - 2.0, ((Entity)$$0).getX() + 2.0, ((Entity)$$0).getY() + 2.0, ((Entity)$$0).getZ() + 2.0);
        }
        if ($$1.isVisible($$5)) {
            return true;
        }
        if ($$0 instanceof Leashable && ($$7 = ($$6 = (Leashable)$$0).getLeashHolder()) != null) {
            AABB $$8 = this.entityRenderDispatcher.getRenderer($$7).getBoundingBoxForCulling($$7);
            return $$1.isVisible($$8) || $$1.isVisible($$5.minmax($$8));
        }
        return false;
    }

    protected AABB getBoundingBoxForCulling(T $$0) {
        return ((Entity)$$0).getBoundingBox();
    }

    protected boolean affectedByCulling(T $$0) {
        return true;
    }

    public Vec3 getRenderOffset(S $$0) {
        if (((EntityRenderState)$$0).passengerOffset != null) {
            return ((EntityRenderState)$$0).passengerOffset;
        }
        return Vec3.ZERO;
    }

    public void render(S $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if (((EntityRenderState)$$0).leashStates != null) {
            for (EntityRenderState.LeashState $$4 : ((EntityRenderState)$$0).leashStates) {
                EntityRenderer.renderLeash($$1, $$2, $$4);
            }
        }
        if (((EntityRenderState)$$0).nameTag != null) {
            this.renderNameTag($$0, ((EntityRenderState)$$0).nameTag, $$1, $$2, $$3);
        }
    }

    private static void renderLeash(PoseStack $$0, MultiBufferSource $$1, EntityRenderState.LeashState $$2) {
        float $$3 = (float)($$2.end.x - $$2.start.x);
        float $$4 = (float)($$2.end.y - $$2.start.y);
        float $$5 = (float)($$2.end.z - $$2.start.z);
        float $$6 = Mth.invSqrt($$3 * $$3 + $$5 * $$5) * 0.05f / 2.0f;
        float $$7 = $$5 * $$6;
        float $$8 = $$3 * $$6;
        $$0.pushPose();
        $$0.translate($$2.offset);
        VertexConsumer $$9 = $$1.getBuffer(RenderType.leash());
        Matrix4f $$10 = $$0.last().pose();
        for (int $$11 = 0; $$11 <= 24; ++$$11) {
            EntityRenderer.addVertexPair($$9, $$10, $$3, $$4, $$5, 0.05f, 0.05f, $$7, $$8, $$11, false, $$2);
        }
        for (int $$12 = 24; $$12 >= 0; --$$12) {
            EntityRenderer.addVertexPair($$9, $$10, $$3, $$4, $$5, 0.05f, 0.0f, $$7, $$8, $$12, true, $$2);
        }
        $$0.popPose();
    }

    private static void addVertexPair(VertexConsumer $$0, Matrix4f $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, int $$9, boolean $$10, EntityRenderState.LeashState $$11) {
        float $$22;
        float $$12 = (float)$$9 / 24.0f;
        int $$13 = (int)Mth.lerp($$12, $$11.startBlockLight, $$11.endBlockLight);
        int $$14 = (int)Mth.lerp($$12, $$11.startSkyLight, $$11.endSkyLight);
        int $$15 = LightTexture.pack($$13, $$14);
        float $$16 = $$9 % 2 == ($$10 ? 1 : 0) ? 0.7f : 1.0f;
        float $$17 = 0.5f * $$16;
        float $$18 = 0.4f * $$16;
        float $$19 = 0.3f * $$16;
        float $$20 = $$2 * $$12;
        if ($$11.slack) {
            float $$21 = $$3 > 0.0f ? $$3 * $$12 * $$12 : $$3 - $$3 * (1.0f - $$12) * (1.0f - $$12);
        } else {
            $$22 = $$3 * $$12;
        }
        float $$23 = $$4 * $$12;
        $$0.addVertex($$1, $$20 - $$7, $$22 + $$6, $$23 + $$8).setColor($$17, $$18, $$19, 1.0f).setLight($$15);
        $$0.addVertex($$1, $$20 + $$7, $$22 + $$5 - $$6, $$23 - $$8).setColor($$17, $$18, $$19, 1.0f).setLight($$15);
    }

    protected boolean shouldShowName(T $$0, double $$1) {
        return ((Entity)$$0).shouldShowName() || ((Entity)$$0).hasCustomName() && $$0 == this.entityRenderDispatcher.crosshairPickEntity;
    }

    public Font getFont() {
        return this.font;
    }

    protected void renderNameTag(S $$0, Component $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        Vec3 $$5 = ((EntityRenderState)$$0).nameTagAttachment;
        if ($$5 == null) {
            return;
        }
        boolean $$6 = !((EntityRenderState)$$0).isDiscrete;
        int $$7 = "deadmau5".equals($$1.getString()) ? -10 : 0;
        $$2.pushPose();
        $$2.translate($$5.x, $$5.y + 0.5, $$5.z);
        $$2.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
        $$2.scale(0.025f, -0.025f, 0.025f);
        Matrix4f $$8 = $$2.last().pose();
        Font $$9 = this.getFont();
        float $$10 = (float)(-$$9.width($$1)) / 2.0f;
        int $$11 = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25f) * 255.0f) << 24;
        $$9.drawInBatch($$1, $$10, (float)$$7, -2130706433, false, $$8, $$3, $$6 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, $$11, $$4);
        if ($$6) {
            $$9.drawInBatch($$1, $$10, (float)$$7, -1, false, $$8, $$3, Font.DisplayMode.NORMAL, 0, LightTexture.lightCoordsWithEmission($$4, 2));
        }
        $$2.popPose();
    }

    @Nullable
    protected Component getNameTag(T $$0) {
        return ((Entity)$$0).getDisplayName();
    }

    protected float getShadowRadius(S $$0) {
        return this.shadowRadius;
    }

    protected float getShadowStrength(S $$0) {
        return this.shadowStrength;
    }

    public abstract S createRenderState();

    public final S createRenderState(T $$0, float $$1) {
        S $$2 = this.reusedState;
        this.extractRenderState($$0, $$2, $$1);
        return $$2;
    }

    public void extractRenderState(T $$0, S $$1, float $$2) {
        Leashable $$9;
        Entity entity;
        boolean $$8;
        NewMinecartBehavior $$4;
        AbstractMinecart $$3;
        Object object;
        ((EntityRenderState)$$1).entityType = ((Entity)$$0).getType();
        ((EntityRenderState)$$1).x = Mth.lerp((double)$$2, ((Entity)$$0).xOld, ((Entity)$$0).getX());
        ((EntityRenderState)$$1).y = Mth.lerp((double)$$2, ((Entity)$$0).yOld, ((Entity)$$0).getY());
        ((EntityRenderState)$$1).z = Mth.lerp((double)$$2, ((Entity)$$0).zOld, ((Entity)$$0).getZ());
        ((EntityRenderState)$$1).isInvisible = ((Entity)$$0).isInvisible();
        ((EntityRenderState)$$1).ageInTicks = (float)((Entity)$$0).tickCount + $$2;
        ((EntityRenderState)$$1).boundingBoxWidth = ((Entity)$$0).getBbWidth();
        ((EntityRenderState)$$1).boundingBoxHeight = ((Entity)$$0).getBbHeight();
        ((EntityRenderState)$$1).eyeHeight = ((Entity)$$0).getEyeHeight();
        if (((Entity)$$0).isPassenger() && (object = ((Entity)$$0).getVehicle()) instanceof AbstractMinecart && (object = ($$3 = (AbstractMinecart)object).getBehavior()) instanceof NewMinecartBehavior && ($$4 = (NewMinecartBehavior)object).cartHasPosRotLerp()) {
            double $$5 = Mth.lerp((double)$$2, $$3.xOld, $$3.getX());
            double $$6 = Mth.lerp((double)$$2, $$3.yOld, $$3.getY());
            double $$7 = Mth.lerp((double)$$2, $$3.zOld, $$3.getZ());
            ((EntityRenderState)$$1).passengerOffset = $$4.getCartLerpPosition($$2).subtract(new Vec3($$5, $$6, $$7));
        } else {
            ((EntityRenderState)$$1).passengerOffset = null;
        }
        ((EntityRenderState)$$1).distanceToCameraSq = this.entityRenderDispatcher.distanceToSqr((Entity)$$0);
        boolean bl = $$8 = ((EntityRenderState)$$1).distanceToCameraSq < 4096.0 && this.shouldShowName($$0, ((EntityRenderState)$$1).distanceToCameraSq);
        if ($$8) {
            ((EntityRenderState)$$1).nameTag = this.getNameTag($$0);
            ((EntityRenderState)$$1).nameTagAttachment = ((Entity)$$0).getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, ((Entity)$$0).getYRot($$2));
        } else {
            ((EntityRenderState)$$1).nameTag = null;
        }
        ((EntityRenderState)$$1).isDiscrete = ((Entity)$$0).isDiscrete();
        if ($$0 instanceof Leashable && (entity = ($$9 = (Leashable)$$0).getLeashHolder()) instanceof Entity) {
            int $$20;
            Entity $$10 = entity;
            float $$11 = ((Entity)$$0).getPreciseBodyRotation($$2) * ((float)Math.PI / 180);
            Vec3 $$12 = $$9.getLeashOffset($$2);
            BlockPos $$13 = BlockPos.containing(((Entity)$$0).getEyePosition($$2));
            BlockPos $$14 = BlockPos.containing($$10.getEyePosition($$2));
            int $$15 = this.getBlockLightLevel($$0, $$13);
            int $$16 = this.entityRenderDispatcher.getRenderer($$10).getBlockLightLevel($$10, $$14);
            int $$17 = ((Entity)$$0).level().getBrightness(LightLayer.SKY, $$13);
            int $$18 = ((Entity)$$0).level().getBrightness(LightLayer.SKY, $$14);
            boolean $$19 = $$10.supportQuadLeashAsHolder() && $$9.supportQuadLeash();
            int n = $$20 = $$19 ? 4 : 1;
            if (((EntityRenderState)$$1).leashStates == null || ((EntityRenderState)$$1).leashStates.size() != $$20) {
                ((EntityRenderState)$$1).leashStates = new ArrayList<EntityRenderState.LeashState>($$20);
                for (int $$21 = 0; $$21 < $$20; ++$$21) {
                    ((EntityRenderState)$$1).leashStates.add(new EntityRenderState.LeashState());
                }
            }
            if ($$19) {
                float $$22 = $$10.getPreciseBodyRotation($$2) * ((float)Math.PI / 180);
                Vec3 $$23 = $$10.getPosition($$2);
                Vec3[] $$24 = $$9.E();
                Vec3[] $$25 = $$10.dK();
                for (int $$26 = 0; $$26 < $$20; ++$$26) {
                    EntityRenderState.LeashState $$27 = ((EntityRenderState)$$1).leashStates.get($$26);
                    $$27.offset = $$24[$$26].yRot(-$$11);
                    $$27.start = ((Entity)$$0).getPosition($$2).add($$27.offset);
                    $$27.end = $$23.add($$25[$$26].yRot(-$$22));
                    $$27.startBlockLight = $$15;
                    $$27.endBlockLight = $$16;
                    $$27.startSkyLight = $$17;
                    $$27.endSkyLight = $$18;
                    $$27.slack = false;
                }
            } else {
                Vec3 $$28 = $$12.yRot(-$$11);
                EntityRenderState.LeashState $$29 = (EntityRenderState.LeashState)((EntityRenderState)$$1).leashStates.getFirst();
                $$29.offset = $$28;
                $$29.start = ((Entity)$$0).getPosition($$2).add($$28);
                $$29.end = $$10.getRopeHoldPosition($$2);
                $$29.startBlockLight = $$15;
                $$29.endBlockLight = $$16;
                $$29.startSkyLight = $$17;
                $$29.endSkyLight = $$18;
            }
        } else {
            ((EntityRenderState)$$1).leashStates = null;
        }
        ((EntityRenderState)$$1).displayFireAnimation = ((Entity)$$0).displayFireAnimation();
        Minecraft $$30 = Minecraft.getInstance();
        if ($$30.getEntityRenderDispatcher().shouldRenderHitBoxes() && !((EntityRenderState)$$1).isInvisible && !$$30.showOnlyReducedInfo()) {
            this.extractHitboxes($$0, $$1, $$2);
        } else {
            ((EntityRenderState)$$1).hitboxesRenderState = null;
            ((EntityRenderState)$$1).serverHitboxesRenderState = null;
        }
    }

    private void extractHitboxes(T $$0, S $$1, float $$2) {
        ((EntityRenderState)$$1).hitboxesRenderState = this.extractHitboxes($$0, $$2, false);
        ((EntityRenderState)$$1).serverHitboxesRenderState = null;
    }

    private HitboxesRenderState extractHitboxes(T $$0, float $$1, boolean $$2) {
        HitboxRenderState $$6;
        ImmutableList.Builder<HitboxRenderState> $$3 = new ImmutableList.Builder<HitboxRenderState>();
        AABB $$4 = ((Entity)$$0).getBoundingBox();
        if ($$2) {
            HitboxRenderState $$5 = new HitboxRenderState($$4.minX - ((Entity)$$0).getX(), $$4.minY - ((Entity)$$0).getY(), $$4.minZ - ((Entity)$$0).getZ(), $$4.maxX - ((Entity)$$0).getX(), $$4.maxY - ((Entity)$$0).getY(), $$4.maxZ - ((Entity)$$0).getZ(), 0.0f, 1.0f, 0.0f);
        } else {
            $$6 = new HitboxRenderState($$4.minX - ((Entity)$$0).getX(), $$4.minY - ((Entity)$$0).getY(), $$4.minZ - ((Entity)$$0).getZ(), $$4.maxX - ((Entity)$$0).getX(), $$4.maxY - ((Entity)$$0).getY(), $$4.maxZ - ((Entity)$$0).getZ(), 1.0f, 1.0f, 1.0f);
        }
        $$3.add((Object)$$6);
        Entity $$7 = ((Entity)$$0).getVehicle();
        if ($$7 != null) {
            float $$8 = Math.min($$7.getBbWidth(), ((Entity)$$0).getBbWidth()) / 2.0f;
            float $$9 = 0.0625f;
            Vec3 $$10 = $$7.getPassengerRidingPosition((Entity)$$0).subtract(((Entity)$$0).position());
            HitboxRenderState $$11 = new HitboxRenderState($$10.x - (double)$$8, $$10.y, $$10.z - (double)$$8, $$10.x + (double)$$8, $$10.y + 0.0625, $$10.z + (double)$$8, 1.0f, 1.0f, 0.0f);
            $$3.add((Object)$$11);
        }
        this.extractAdditionalHitboxes($$0, $$3, $$1);
        Vec3 $$12 = ((Entity)$$0).getViewVector($$1);
        return new HitboxesRenderState($$12.x, $$12.y, $$12.z, (ImmutableList<HitboxRenderState>)$$3.build());
    }

    protected void extractAdditionalHitboxes(T $$0, ImmutableList.Builder<HitboxRenderState> $$1, float $$2) {
    }

    @Nullable
    private static Entity getServerSideEntity(Entity $$0) {
        ServerLevel $$2;
        IntegratedServer $$1 = Minecraft.getInstance().getSingleplayerServer();
        if ($$1 != null && ($$2 = $$1.getLevel($$0.level().dimension())) != null) {
            return $$2.getEntity($$0.getId());
        }
        return null;
    }
}


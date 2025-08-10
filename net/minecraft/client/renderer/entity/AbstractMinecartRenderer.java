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
import java.util.Objects;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.entity.vehicle.OldMinecartBehavior;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public abstract class AbstractMinecartRenderer<T extends AbstractMinecart, S extends MinecartRenderState>
extends EntityRenderer<T, S> {
    private static final ResourceLocation MINECART_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/minecart.png");
    private static final float DISPLAY_BLOCK_SCALE = 0.75f;
    protected final MinecartModel model;
    private final BlockRenderDispatcher blockRenderer;

    public AbstractMinecartRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1) {
        super($$0);
        this.shadowRadius = 0.7f;
        this.model = new MinecartModel($$0.bakeLayer($$1));
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    public void render(S $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        BlockState $$9;
        super.render($$0, $$1, $$2, $$3);
        $$1.pushPose();
        long $$4 = ((MinecartRenderState)$$0).offsetSeed;
        float $$5 = (((float)($$4 >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float $$6 = (((float)($$4 >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float $$7 = (((float)($$4 >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        $$1.translate($$5, $$6, $$7);
        if (((MinecartRenderState)$$0).isNewRender) {
            AbstractMinecartRenderer.newRender($$0, $$1);
        } else {
            AbstractMinecartRenderer.oldRender($$0, $$1);
        }
        float $$8 = ((MinecartRenderState)$$0).hurtTime;
        if ($$8 > 0.0f) {
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.sin($$8) * $$8 * ((MinecartRenderState)$$0).damageTime / 10.0f * (float)((MinecartRenderState)$$0).hurtDir));
        }
        if (($$9 = ((MinecartRenderState)$$0).displayBlockState).getRenderShape() != RenderShape.INVISIBLE) {
            $$1.pushPose();
            $$1.scale(0.75f, 0.75f, 0.75f);
            $$1.translate(-0.5f, (float)(((MinecartRenderState)$$0).displayOffset - 8) / 16.0f, 0.5f);
            $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0f));
            this.renderMinecartContents($$0, $$9, $$1, $$2, $$3);
            $$1.popPose();
        }
        $$1.scale(-1.0f, -1.0f, 1.0f);
        this.model.setupAnim($$0);
        VertexConsumer $$10 = $$2.getBuffer(this.model.renderType(MINECART_LOCATION));
        this.model.renderToBuffer($$1, $$10, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
    }

    private static <S extends MinecartRenderState> void newRender(S $$0, PoseStack $$1) {
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$0.yRot));
        $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(-$$0.xRot));
        $$1.translate(0.0f, 0.375f, 0.0f);
    }

    private static <S extends MinecartRenderState> void oldRender(S $$0, PoseStack $$1) {
        double $$2 = $$0.x;
        double $$3 = $$0.y;
        double $$4 = $$0.z;
        float $$5 = $$0.xRot;
        float $$6 = $$0.yRot;
        if ($$0.posOnRail != null && $$0.frontPos != null && $$0.backPos != null) {
            Vec3 $$7 = $$0.frontPos;
            Vec3 $$8 = $$0.backPos;
            $$1.translate($$0.posOnRail.x - $$2, ($$7.y + $$8.y) / 2.0 - $$3, $$0.posOnRail.z - $$4);
            Vec3 $$9 = $$8.add(-$$7.x, -$$7.y, -$$7.z);
            if ($$9.length() != 0.0) {
                $$9 = $$9.normalize();
                $$6 = (float)(Math.atan2($$9.z, $$9.x) * 180.0 / Math.PI);
                $$5 = (float)(Math.atan($$9.y) * 73.0);
            }
        }
        $$1.translate(0.0f, 0.375f, 0.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - $$6));
        $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(-$$5));
    }

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        MinecartBehavior minecartBehavior = ((AbstractMinecart)$$0).getBehavior();
        if (minecartBehavior instanceof NewMinecartBehavior) {
            NewMinecartBehavior $$3 = (NewMinecartBehavior)minecartBehavior;
            AbstractMinecartRenderer.newExtractState($$0, $$3, $$1, $$2);
            ((MinecartRenderState)$$1).isNewRender = true;
        } else {
            minecartBehavior = ((AbstractMinecart)$$0).getBehavior();
            if (minecartBehavior instanceof OldMinecartBehavior) {
                OldMinecartBehavior $$4 = (OldMinecartBehavior)minecartBehavior;
                AbstractMinecartRenderer.oldExtractState($$0, $$4, $$1, $$2);
                ((MinecartRenderState)$$1).isNewRender = false;
            }
        }
        long $$5 = (long)((Entity)$$0).getId() * 493286711L;
        ((MinecartRenderState)$$1).offsetSeed = $$5 * $$5 * 4392167121L + $$5 * 98761L;
        ((MinecartRenderState)$$1).hurtTime = (float)((VehicleEntity)$$0).getHurtTime() - $$2;
        ((MinecartRenderState)$$1).hurtDir = ((VehicleEntity)$$0).getHurtDir();
        ((MinecartRenderState)$$1).damageTime = Math.max(((VehicleEntity)$$0).getDamage() - $$2, 0.0f);
        ((MinecartRenderState)$$1).displayOffset = ((AbstractMinecart)$$0).getDisplayOffset();
        ((MinecartRenderState)$$1).displayBlockState = ((AbstractMinecart)$$0).getDisplayBlockState();
    }

    private static <T extends AbstractMinecart, S extends MinecartRenderState> void newExtractState(T $$0, NewMinecartBehavior $$1, S $$2, float $$3) {
        if ($$1.cartHasPosRotLerp()) {
            $$2.renderPos = $$1.getCartLerpPosition($$3);
            $$2.xRot = $$1.getCartLerpXRot($$3);
            $$2.yRot = $$1.getCartLerpYRot($$3);
        } else {
            $$2.renderPos = null;
            $$2.xRot = $$0.getXRot();
            $$2.yRot = $$0.getYRot();
        }
    }

    private static <T extends AbstractMinecart, S extends MinecartRenderState> void oldExtractState(T $$0, OldMinecartBehavior $$1, S $$2, float $$3) {
        float $$4 = 0.3f;
        $$2.xRot = $$0.getXRot($$3);
        $$2.yRot = $$0.getYRot($$3);
        double $$5 = $$2.x;
        double $$6 = $$2.y;
        double $$7 = $$2.z;
        Vec3 $$8 = $$1.getPos($$5, $$6, $$7);
        if ($$8 != null) {
            $$2.posOnRail = $$8;
            Vec3 $$9 = $$1.getPosOffs($$5, $$6, $$7, 0.3f);
            Vec3 $$10 = $$1.getPosOffs($$5, $$6, $$7, -0.3f);
            $$2.frontPos = (Vec3)Objects.requireNonNullElse((Object)$$9, (Object)$$8);
            $$2.backPos = (Vec3)Objects.requireNonNullElse((Object)$$10, (Object)$$8);
        } else {
            $$2.posOnRail = null;
            $$2.frontPos = null;
            $$2.backPos = null;
        }
    }

    protected void renderMinecartContents(S $$0, BlockState $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        this.blockRenderer.renderSingleBlock($$1, $$2, $$3, $$4, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected AABB getBoundingBoxForCulling(T $$0) {
        AABB $$1 = super.getBoundingBoxForCulling($$0);
        if (!((AbstractMinecart)$$0).getDisplayBlockState().isAir()) {
            return $$1.expandTowards(0.0, (float)((AbstractMinecart)$$0).getDisplayOffset() * 0.75f / 16.0f, 0.0);
        }
        return $$1;
    }

    @Override
    public Vec3 getRenderOffset(S $$0) {
        Vec3 $$1 = super.getRenderOffset($$0);
        if (((MinecartRenderState)$$0).isNewRender && ((MinecartRenderState)$$0).renderPos != null) {
            return $$1.add(((MinecartRenderState)$$0).renderPos.x - ((MinecartRenderState)$$0).x, ((MinecartRenderState)$$0).renderPos.y - ((MinecartRenderState)$$0).y, ((MinecartRenderState)$$0).renderPos.z - ((MinecartRenderState)$$0).z);
        }
        return $$1;
    }
}


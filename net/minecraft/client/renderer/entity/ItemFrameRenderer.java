/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BlockStateDefinitions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class ItemFrameRenderer<T extends ItemFrame>
extends EntityRenderer<T, ItemFrameRenderState> {
    public static final int GLOW_FRAME_BRIGHTNESS = 5;
    public static final int BRIGHT_MAP_LIGHT_ADJUSTMENT = 30;
    private final ItemModelResolver itemModelResolver;
    private final MapRenderer mapRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public ItemFrameRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.itemModelResolver = $$0.getItemModelResolver();
        this.mapRenderer = $$0.getMapRenderer();
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    protected int getBlockLightLevel(T $$0, BlockPos $$1) {
        if (((Entity)$$0).getType() == EntityType.GLOW_ITEM_FRAME) {
            return Math.max(5, super.getBlockLightLevel($$0, $$1));
        }
        return super.getBlockLightLevel($$0, $$1);
    }

    @Override
    public void render(ItemFrameRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        float $$10;
        float $$9;
        super.render($$0, $$1, $$2, $$3);
        $$1.pushPose();
        Direction $$4 = $$0.direction;
        Vec3 $$5 = this.getRenderOffset($$0);
        $$1.translate(-$$5.x(), -$$5.y(), -$$5.z());
        double $$6 = 0.46875;
        $$1.translate((double)$$4.getStepX() * 0.46875, (double)$$4.getStepY() * 0.46875, (double)$$4.getStepZ() * 0.46875);
        if ($$4.getAxis().isHorizontal()) {
            float $$7 = 0.0f;
            float $$8 = 180.0f - $$4.toYRot();
        } else {
            $$9 = -90 * $$4.getAxisDirection().getStep();
            $$10 = 180.0f;
        }
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$9));
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$10));
        if (!$$0.isInvisible) {
            BlockState $$11 = BlockStateDefinitions.getItemFrameFakeState($$0.isGlowFrame, $$0.mapId != null);
            BlockStateModel $$12 = this.blockRenderer.getBlockModel($$11);
            $$1.pushPose();
            $$1.translate(-0.5f, -0.5f, -0.5f);
            ModelBlockRenderer.renderModel($$1.last(), $$2.getBuffer(RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS)), $$12, 1.0f, 1.0f, 1.0f, $$3, OverlayTexture.NO_OVERLAY);
            $$1.popPose();
        }
        if ($$0.isInvisible) {
            $$1.translate(0.0f, 0.0f, 0.5f);
        } else {
            $$1.translate(0.0f, 0.0f, 0.4375f);
        }
        if ($$0.mapId != null) {
            int $$13 = $$0.rotation % 4 * 2;
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$13 * 360.0f / 8.0f));
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0f));
            float $$14 = 0.0078125f;
            $$1.scale(0.0078125f, 0.0078125f, 0.0078125f);
            $$1.translate(-64.0f, -64.0f, 0.0f);
            $$1.translate(0.0f, 0.0f, -1.0f);
            int $$15 = this.getLightCoords($$0.isGlowFrame, 15728850, $$3);
            this.mapRenderer.render($$0.mapRenderState, $$1, $$2, true, $$15);
        } else if (!$$0.item.isEmpty()) {
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$0.rotation * 360.0f / 8.0f));
            int $$16 = this.getLightCoords($$0.isGlowFrame, 0xF000F0, $$3);
            $$1.scale(0.5f, 0.5f, 0.5f);
            $$0.item.render($$1, $$2, $$16, OverlayTexture.NO_OVERLAY);
        }
        $$1.popPose();
    }

    private int getLightCoords(boolean $$0, int $$1, int $$2) {
        return $$0 ? $$1 : $$2;
    }

    @Override
    public Vec3 getRenderOffset(ItemFrameRenderState $$0) {
        return new Vec3((float)$$0.direction.getStepX() * 0.3f, -0.25, (float)$$0.direction.getStepZ() * 0.3f);
    }

    @Override
    protected boolean shouldShowName(T $$0, double $$1) {
        return Minecraft.renderNames() && this.entityRenderDispatcher.crosshairPickEntity == $$0 && ((ItemFrame)$$0).getItem().getCustomName() != null;
    }

    @Override
    protected Component getNameTag(T $$0) {
        return ((ItemFrame)$$0).getItem().getHoverName();
    }

    @Override
    public ItemFrameRenderState createRenderState() {
        return new ItemFrameRenderState();
    }

    @Override
    public void extractRenderState(T $$0, ItemFrameRenderState $$1, float $$2) {
        MapItemSavedData $$5;
        MapId $$4;
        super.extractRenderState($$0, $$1, $$2);
        $$1.direction = ((HangingEntity)$$0).getDirection();
        ItemStack $$3 = ((ItemFrame)$$0).getItem();
        this.itemModelResolver.updateForNonLiving($$1.item, $$3, ItemDisplayContext.FIXED, (Entity)$$0);
        $$1.rotation = ((ItemFrame)$$0).getRotation();
        $$1.isGlowFrame = ((Entity)$$0).getType() == EntityType.GLOW_ITEM_FRAME;
        $$1.mapId = null;
        if (!$$3.isEmpty() && ($$4 = ((ItemFrame)$$0).getFramedMapId($$3)) != null && ($$5 = ((Entity)$$0).level().getMapData($$4)) != null) {
            this.mapRenderer.extractRenderState($$4, $$5, $$1.mapRenderState);
            $$1.mapId = $$4;
        }
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ Component getNameTag(Entity entity) {
        return this.getNameTag((T)((ItemFrame)entity));
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Set;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class BannerRenderer
implements BlockEntityRenderer<BannerBlockEntity> {
    private static final int MAX_PATTERNS = 16;
    private static final float SIZE = 0.6666667f;
    private final BannerModel standingModel;
    private final BannerModel wallModel;
    private final BannerFlagModel standingFlagModel;
    private final BannerFlagModel wallFlagModel;

    public BannerRenderer(BlockEntityRendererProvider.Context $$0) {
        this($$0.getModelSet());
    }

    public BannerRenderer(EntityModelSet $$0) {
        this.standingModel = new BannerModel($$0.bakeLayer(ModelLayers.STANDING_BANNER));
        this.wallModel = new BannerModel($$0.bakeLayer(ModelLayers.WALL_BANNER));
        this.standingFlagModel = new BannerFlagModel($$0.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
        this.wallFlagModel = new BannerFlagModel($$0.bakeLayer(ModelLayers.WALL_BANNER_FLAG));
    }

    @Override
    public void render(BannerBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        BannerFlagModel $$13;
        BannerModel $$12;
        float $$11;
        BlockState $$7 = $$0.getBlockState();
        if ($$7.getBlock() instanceof BannerBlock) {
            float $$8 = -RotationSegment.convertToDegrees($$7.getValue(BannerBlock.ROTATION));
            BannerModel $$9 = this.standingModel;
            BannerFlagModel $$10 = this.standingFlagModel;
        } else {
            $$11 = -$$7.getValue(WallBannerBlock.FACING).toYRot();
            $$12 = this.wallModel;
            $$13 = this.wallFlagModel;
        }
        long $$14 = $$0.getLevel().getGameTime();
        BlockPos $$15 = $$0.getBlockPos();
        float $$16 = ((float)Math.floorMod((long)($$15.getX() * 7 + $$15.getY() * 9 + $$15.getZ() * 13) + $$14, 100L) + $$1) / 100.0f;
        BannerRenderer.renderBanner($$2, $$3, $$4, $$5, $$11, $$12, $$13, $$16, $$0.getBaseColor(), $$0.getPatterns());
    }

    public void renderInHand(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, DyeColor $$4, BannerPatternLayers $$5) {
        BannerRenderer.renderBanner($$0, $$1, $$2, $$3, 0.0f, this.standingModel, this.standingFlagModel, 0.0f, $$4, $$5);
    }

    private static void renderBanner(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, float $$4, BannerModel $$5, BannerFlagModel $$6, float $$7, DyeColor $$8, BannerPatternLayers $$9) {
        $$0.pushPose();
        $$0.translate(0.5f, 0.0f, 0.5f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$4));
        $$0.scale(0.6666667f, -0.6666667f, -0.6666667f);
        $$5.renderToBuffer($$0, ModelBakery.BANNER_BASE.buffer($$1, RenderType::entitySolid), $$2, $$3);
        $$6.setupAnim($$7);
        BannerRenderer.renderPatterns($$0, $$1, $$2, $$3, $$6.root(), ModelBakery.BANNER_BASE, true, $$8, $$9);
        $$0.popPose();
    }

    public static void renderPatterns(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, ModelPart $$4, Material $$5, boolean $$6, DyeColor $$7, BannerPatternLayers $$8) {
        BannerRenderer.renderPatterns($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, false, true);
    }

    public static void renderPatterns(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, ModelPart $$4, Material $$5, boolean $$6, DyeColor $$7, BannerPatternLayers $$8, boolean $$9, boolean $$10) {
        $$4.render($$0, $$5.buffer($$1, RenderType::entitySolid, $$10, $$9), $$2, $$3);
        BannerRenderer.renderPatternLayer($$0, $$1, $$2, $$3, $$4, $$6 ? Sheets.BANNER_BASE : Sheets.SHIELD_BASE, $$7);
        for (int $$11 = 0; $$11 < 16 && $$11 < $$8.layers().size(); ++$$11) {
            BannerPatternLayers.Layer $$12 = $$8.layers().get($$11);
            Material $$13 = $$6 ? Sheets.getBannerMaterial($$12.pattern()) : Sheets.getShieldMaterial($$12.pattern());
            BannerRenderer.renderPatternLayer($$0, $$1, $$2, $$3, $$4, $$13, $$12.color());
        }
    }

    private static void renderPatternLayer(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, ModelPart $$4, Material $$5, DyeColor $$6) {
        int $$7 = $$6.getTextureDiffuseColor();
        $$4.render($$0, $$5.buffer($$1, RenderType::entityNoOutline), $$2, $$3, $$7);
    }

    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        $$1.translate(0.5f, 0.0f, 0.5f);
        $$1.scale(0.6666667f, -0.6666667f, -0.6666667f);
        this.standingModel.root().getExtentsForGui($$1, $$0);
        this.standingFlagModel.setupAnim(0.0f);
        this.standingFlagModel.root().getExtentsForGui($$1, $$0);
    }
}


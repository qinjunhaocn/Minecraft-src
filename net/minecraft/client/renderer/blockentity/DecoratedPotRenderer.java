/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class DecoratedPotRenderer
implements BlockEntityRenderer<DecoratedPotBlockEntity> {
    private static final String NECK = "neck";
    private static final String FRONT = "front";
    private static final String BACK = "back";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private final ModelPart neck;
    private final ModelPart frontSide;
    private final ModelPart backSide;
    private final ModelPart leftSide;
    private final ModelPart rightSide;
    private final ModelPart top;
    private final ModelPart bottom;
    private static final float WOBBLE_AMPLITUDE = 0.125f;

    public DecoratedPotRenderer(BlockEntityRendererProvider.Context $$0) {
        this($$0.getModelSet());
    }

    public DecoratedPotRenderer(EntityModelSet $$0) {
        ModelPart $$1 = $$0.bakeLayer(ModelLayers.DECORATED_POT_BASE);
        this.neck = $$1.getChild(NECK);
        this.top = $$1.getChild(TOP);
        this.bottom = $$1.getChild(BOTTOM);
        ModelPart $$2 = $$0.bakeLayer(ModelLayers.DECORATED_POT_SIDES);
        this.frontSide = $$2.getChild(FRONT);
        this.backSide = $$2.getChild(BACK);
        this.leftSide = $$2.getChild(LEFT);
        this.rightSide = $$2.getChild(RIGHT);
    }

    public static LayerDefinition createBaseLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeDeformation $$2 = new CubeDeformation(0.2f);
        CubeDeformation $$3 = new CubeDeformation(-0.1f);
        $$1.addOrReplaceChild(NECK, CubeListBuilder.create().texOffs(0, 0).addBox(4.0f, 17.0f, 4.0f, 8.0f, 3.0f, 8.0f, $$3).texOffs(0, 5).addBox(5.0f, 20.0f, 5.0f, 6.0f, 1.0f, 6.0f, $$2), PartPose.offsetAndRotation(0.0f, 37.0f, 16.0f, (float)Math.PI, 0.0f, 0.0f));
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(-14, 13).addBox(0.0f, 0.0f, 0.0f, 14.0f, 0.0f, 14.0f);
        $$1.addOrReplaceChild(TOP, $$4, PartPose.offsetAndRotation(1.0f, 16.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        $$1.addOrReplaceChild(BOTTOM, $$4, PartPose.offsetAndRotation(1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    public static LayerDefinition createSidesLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeListBuilder $$2 = CubeListBuilder.create().texOffs(1, 0).addBox(0.0f, 0.0f, 0.0f, 14.0f, 16.0f, 0.0f, EnumSet.of(Direction.NORTH));
        $$1.addOrReplaceChild(BACK, $$2, PartPose.offsetAndRotation(15.0f, 16.0f, 1.0f, 0.0f, 0.0f, (float)Math.PI));
        $$1.addOrReplaceChild(LEFT, $$2, PartPose.offsetAndRotation(1.0f, 16.0f, 1.0f, 0.0f, -1.5707964f, (float)Math.PI));
        $$1.addOrReplaceChild(RIGHT, $$2, PartPose.offsetAndRotation(15.0f, 16.0f, 15.0f, 0.0f, 1.5707964f, (float)Math.PI));
        $$1.addOrReplaceChild(FRONT, $$2, PartPose.offsetAndRotation(1.0f, 16.0f, 15.0f, (float)Math.PI, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 16, 16);
    }

    private static Material getSideMaterial(Optional<Item> $$0) {
        Material $$1;
        if ($$0.isPresent() && ($$1 = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getPatternFromItem($$0.get()))) != null) {
            return $$1;
        }
        return Sheets.DECORATED_POT_SIDE;
    }

    @Override
    public void render(DecoratedPotBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        float $$9;
        $$2.pushPose();
        Direction $$7 = $$0.getDirection();
        $$2.translate(0.5, 0.0, 0.5);
        $$2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - $$7.toYRot()));
        $$2.translate(-0.5, 0.0, -0.5);
        DecoratedPotBlockEntity.WobbleStyle $$8 = $$0.lastWobbleStyle;
        if ($$8 != null && $$0.getLevel() != null && ($$9 = ((float)($$0.getLevel().getGameTime() - $$0.wobbleStartedAtTick) + $$1) / (float)$$8.duration) >= 0.0f && $$9 <= 1.0f) {
            if ($$8 == DecoratedPotBlockEntity.WobbleStyle.POSITIVE) {
                float $$10 = 0.015625f;
                float $$11 = $$9 * ((float)Math.PI * 2);
                float $$12 = -1.5f * (Mth.cos($$11) + 0.5f) * Mth.sin($$11 / 2.0f);
                $$2.rotateAround((Quaternionfc)Axis.XP.rotation($$12 * 0.015625f), 0.5f, 0.0f, 0.5f);
                float $$13 = Mth.sin($$11);
                $$2.rotateAround((Quaternionfc)Axis.ZP.rotation($$13 * 0.015625f), 0.5f, 0.0f, 0.5f);
            } else {
                float $$14 = Mth.sin(-$$9 * 3.0f * (float)Math.PI) * 0.125f;
                float $$15 = 1.0f - $$9;
                $$2.rotateAround((Quaternionfc)Axis.YP.rotation($$14 * $$15), 0.5f, 0.0f, 0.5f);
            }
        }
        this.render($$2, $$3, $$4, $$5, $$0.getDecorations());
        $$2.popPose();
    }

    public void renderInHand(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, PotDecorations $$4) {
        this.render($$0, $$1, $$2, $$3, $$4);
    }

    private void render(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, PotDecorations $$4) {
        VertexConsumer $$5 = Sheets.DECORATED_POT_BASE.buffer($$1, RenderType::entitySolid);
        this.neck.render($$0, $$5, $$2, $$3);
        this.top.render($$0, $$5, $$2, $$3);
        this.bottom.render($$0, $$5, $$2, $$3);
        this.renderSide(this.frontSide, $$0, $$1, $$2, $$3, DecoratedPotRenderer.getSideMaterial($$4.front()));
        this.renderSide(this.backSide, $$0, $$1, $$2, $$3, DecoratedPotRenderer.getSideMaterial($$4.back()));
        this.renderSide(this.leftSide, $$0, $$1, $$2, $$3, DecoratedPotRenderer.getSideMaterial($$4.left()));
        this.renderSide(this.rightSide, $$0, $$1, $$2, $$3, DecoratedPotRenderer.getSideMaterial($$4.right()));
    }

    private void renderSide(ModelPart $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, Material $$5) {
        $$0.render($$1, $$5.buffer($$2, RenderType::entitySolid), $$3, $$4);
    }

    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        this.neck.getExtentsForGui($$1, $$0);
        this.top.getExtentsForGui($$1, $$0);
        this.bottom.getExtentsForGui($$1, $$0);
    }
}


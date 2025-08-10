/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class SignRenderer
extends AbstractSignRenderer {
    public static final float RENDER_SCALE = 0.6666667f;
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);
    private final Map<WoodType, Models> signModels = WoodType.values().collect(ImmutableMap.toImmutableMap($$0 -> $$0, $$1 -> new Models(SignRenderer.createSignModel($$02.getModelSet(), $$1, true), SignRenderer.createSignModel($$02.getModelSet(), $$1, false))));

    public SignRenderer(BlockEntityRendererProvider.Context $$02) {
        super($$02);
    }

    @Override
    protected Model getSignModel(BlockState $$0, WoodType $$1) {
        Models $$2 = this.signModels.get((Object)$$1);
        return $$0.getBlock() instanceof StandingSignBlock ? $$2.standing() : $$2.wall();
    }

    @Override
    protected Material getSignMaterial(WoodType $$0) {
        return Sheets.getSignMaterial($$0);
    }

    @Override
    protected float getSignModelRenderScale() {
        return 0.6666667f;
    }

    @Override
    protected float getSignTextRenderScale() {
        return 0.6666667f;
    }

    private static void translateBase(PoseStack $$0, float $$1) {
        $$0.translate(0.5f, 0.5f, 0.5f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$1));
    }

    @Override
    protected void translateSign(PoseStack $$0, float $$1, BlockState $$2) {
        SignRenderer.translateBase($$0, $$1);
        if (!($$2.getBlock() instanceof StandingSignBlock)) {
            $$0.translate(0.0f, -0.3125f, -0.4375f);
        }
    }

    @Override
    protected Vec3 getTextOffset() {
        return TEXT_OFFSET;
    }

    public static void renderInHand(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, Model $$4, Material $$5) {
        $$0.pushPose();
        SignRenderer.applyInHandTransforms($$0);
        VertexConsumer $$6 = $$5.buffer($$1, $$4::renderType);
        $$4.renderToBuffer($$0, $$6, $$2, $$3);
        $$0.popPose();
    }

    public static void applyInHandTransforms(PoseStack $$0) {
        SignRenderer.translateBase($$0, 0.0f);
        $$0.scale(0.6666667f, -0.6666667f, -0.6666667f);
    }

    public static Model createSignModel(EntityModelSet $$0, WoodType $$1, boolean $$2) {
        ModelLayerLocation $$3 = $$2 ? ModelLayers.createStandingSignModelName($$1) : ModelLayers.createWallSignModelName($$1);
        return new Model.Simple($$0.bakeLayer($$3), RenderType::entityCutoutNoCull);
    }

    public static LayerDefinition createSignLayer(boolean $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f), PartPose.ZERO);
        if ($$0) {
            $$2.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f), PartPose.ZERO);
        }
        return LayerDefinition.create($$1, 64, 32);
    }

    record Models(Model standing, Model wall) {
    }
}


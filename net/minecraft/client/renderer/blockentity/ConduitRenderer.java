/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ConduitRenderer
implements BlockEntityRenderer<ConduitBlockEntity> {
    public static final MaterialMapper MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "entity/conduit");
    public static final Material SHELL_TEXTURE = MAPPER.defaultNamespaceApply("base");
    public static final Material ACTIVE_SHELL_TEXTURE = MAPPER.defaultNamespaceApply("cage");
    public static final Material WIND_TEXTURE = MAPPER.defaultNamespaceApply("wind");
    public static final Material VERTICAL_WIND_TEXTURE = MAPPER.defaultNamespaceApply("wind_vertical");
    public static final Material OPEN_EYE_TEXTURE = MAPPER.defaultNamespaceApply("open_eye");
    public static final Material CLOSED_EYE_TEXTURE = MAPPER.defaultNamespaceApply("closed_eye");
    private final ModelPart eye;
    private final ModelPart wind;
    private final ModelPart shell;
    private final ModelPart cage;
    private final BlockEntityRenderDispatcher renderer;

    public ConduitRenderer(BlockEntityRendererProvider.Context $$0) {
        this.renderer = $$0.getBlockEntityRenderDispatcher();
        this.eye = $$0.bakeLayer(ModelLayers.CONDUIT_EYE);
        this.wind = $$0.bakeLayer(ModelLayers.CONDUIT_WIND);
        this.shell = $$0.bakeLayer(ModelLayers.CONDUIT_SHELL);
        this.cage = $$0.bakeLayer(ModelLayers.CONDUIT_CAGE);
    }

    public static LayerDefinition createEyeLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, new CubeDeformation(0.01f)), PartPose.ZERO);
        return LayerDefinition.create($$0, 16, 16);
    }

    public static LayerDefinition createWindLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    public static LayerDefinition createShellLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 32, 16);
    }

    public static LayerDefinition createCageLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 32, 16);
    }

    @Override
    public void render(ConduitBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        float $$7 = (float)$$0.tickCount + $$1;
        if (!$$0.isActive()) {
            float $$8 = $$0.getActiveRotation(0.0f);
            VertexConsumer $$9 = SHELL_TEXTURE.buffer($$3, RenderType::entitySolid);
            $$2.pushPose();
            $$2.translate(0.5f, 0.5f, 0.5f);
            $$2.mulPose((Quaternionfc)new Quaternionf().rotationY($$8 * ((float)Math.PI / 180)));
            this.shell.render($$2, $$9, $$4, $$5);
            $$2.popPose();
            return;
        }
        float $$10 = $$0.getActiveRotation($$1) * 57.295776f;
        float $$11 = Mth.sin($$7 * 0.1f) / 2.0f + 0.5f;
        $$11 = $$11 * $$11 + $$11;
        $$2.pushPose();
        $$2.translate(0.5f, 0.3f + $$11 * 0.2f, 0.5f);
        Vector3f $$12 = new Vector3f(0.5f, 1.0f, 0.5f).normalize();
        $$2.mulPose((Quaternionfc)new Quaternionf().rotationAxis($$10 * ((float)Math.PI / 180), (Vector3fc)$$12));
        this.cage.render($$2, ACTIVE_SHELL_TEXTURE.buffer($$3, RenderType::entityCutoutNoCull), $$4, $$5);
        $$2.popPose();
        int $$13 = $$0.tickCount / 66 % 3;
        $$2.pushPose();
        $$2.translate(0.5f, 0.5f, 0.5f);
        if ($$13 == 1) {
            $$2.mulPose((Quaternionfc)new Quaternionf().rotationX(1.5707964f));
        } else if ($$13 == 2) {
            $$2.mulPose((Quaternionfc)new Quaternionf().rotationZ(1.5707964f));
        }
        VertexConsumer $$14 = ($$13 == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer($$3, RenderType::entityCutoutNoCull);
        this.wind.render($$2, $$14, $$4, $$5);
        $$2.popPose();
        $$2.pushPose();
        $$2.translate(0.5f, 0.5f, 0.5f);
        $$2.scale(0.875f, 0.875f, 0.875f);
        $$2.mulPose((Quaternionfc)new Quaternionf().rotationXYZ((float)Math.PI, 0.0f, (float)Math.PI));
        this.wind.render($$2, $$14, $$4, $$5);
        $$2.popPose();
        Camera $$15 = this.renderer.camera;
        $$2.pushPose();
        $$2.translate(0.5f, 0.3f + $$11 * 0.2f, 0.5f);
        $$2.scale(0.5f, 0.5f, 0.5f);
        float $$16 = -$$15.getYRot();
        $$2.mulPose((Quaternionfc)new Quaternionf().rotationYXZ($$16 * ((float)Math.PI / 180), $$15.getXRot() * ((float)Math.PI / 180), (float)Math.PI));
        float $$17 = 1.3333334f;
        $$2.scale(1.3333334f, 1.3333334f, 1.3333334f);
        this.eye.render($$2, ($$0.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer($$3, RenderType::entityCutoutNoCull), $$4, $$5);
        $$2.popPose();
    }
}


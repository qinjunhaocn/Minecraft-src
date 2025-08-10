/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class HangingSignRenderer
extends AbstractSignRenderer {
    private static final String PLANK = "plank";
    private static final String V_CHAINS = "vChains";
    private static final String NORMAL_CHAINS = "normalChains";
    private static final String CHAIN_L_1 = "chainL1";
    private static final String CHAIN_L_2 = "chainL2";
    private static final String CHAIN_R_1 = "chainR1";
    private static final String CHAIN_R_2 = "chainR2";
    private static final String BOARD = "board";
    public static final float MODEL_RENDER_SCALE = 1.0f;
    private static final float TEXT_RENDER_SCALE = 0.9f;
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.32f, 0.073f);
    private final Map<ModelKey, Model> hangingSignModels;

    public HangingSignRenderer(BlockEntityRendererProvider.Context $$02) {
        super($$02);
        Stream<ImmutableMap<ModelKey, Model>> $$12 = WoodType.values().flatMap($$0 -> Arrays.stream(AttachmentType.values()).map($$1 -> new ModelKey((WoodType)((Object)$$0), (AttachmentType)$$1)));
        this.hangingSignModels = $$12.collect(ImmutableMap.toImmutableMap($$0 -> $$0, $$1 -> HangingSignRenderer.createSignModel($$02.getModelSet(), $$1.woodType, $$1.attachmentType)));
    }

    public static Model createSignModel(EntityModelSet $$0, WoodType $$1, AttachmentType $$2) {
        return new Model.Simple($$0.bakeLayer(ModelLayers.createHangingSignModelName($$1, $$2)), RenderType::entityCutoutNoCull);
    }

    @Override
    protected float getSignModelRenderScale() {
        return 1.0f;
    }

    @Override
    protected float getSignTextRenderScale() {
        return 0.9f;
    }

    public static void translateBase(PoseStack $$0, float $$1) {
        $$0.translate(0.5, 0.9375, 0.5);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$1));
        $$0.translate(0.0f, -0.3125f, 0.0f);
    }

    @Override
    protected void translateSign(PoseStack $$0, float $$1, BlockState $$2) {
        HangingSignRenderer.translateBase($$0, $$1);
    }

    @Override
    protected Model getSignModel(BlockState $$0, WoodType $$1) {
        AttachmentType $$2 = AttachmentType.byBlockState($$0);
        return this.hangingSignModels.get((Object)new ModelKey($$1, $$2));
    }

    @Override
    protected Material getSignMaterial(WoodType $$0) {
        return Sheets.getHangingSignMaterial($$0);
    }

    @Override
    protected Vec3 getTextOffset() {
        return TEXT_OFFSET;
    }

    public static void renderInHand(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, Model $$4, Material $$5) {
        $$0.pushPose();
        HangingSignRenderer.translateBase($$0, 0.0f);
        $$0.scale(1.0f, -1.0f, -1.0f);
        VertexConsumer $$6 = $$5.buffer($$1, $$4::renderType);
        $$4.renderToBuffer($$0, $$6, $$2, $$3);
        $$0.popPose();
    }

    public static LayerDefinition createHangingSignLayer(AttachmentType $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild(BOARD, CubeListBuilder.create().texOffs(0, 12).addBox(-7.0f, 0.0f, -1.0f, 14.0f, 10.0f, 2.0f), PartPose.ZERO);
        if ($$0 == AttachmentType.WALL) {
            $$2.addOrReplaceChild(PLANK, CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -6.0f, -2.0f, 16.0f, 2.0f, 4.0f), PartPose.ZERO);
        }
        if ($$0 == AttachmentType.WALL || $$0 == AttachmentType.CEILING) {
            PartDefinition $$3 = $$2.addOrReplaceChild(NORMAL_CHAINS, CubeListBuilder.create(), PartPose.ZERO);
            $$3.addOrReplaceChild(CHAIN_L_1, CubeListBuilder.create().texOffs(0, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(-5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
            $$3.addOrReplaceChild(CHAIN_L_2, CubeListBuilder.create().texOffs(6, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(-5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
            $$3.addOrReplaceChild(CHAIN_R_1, CubeListBuilder.create().texOffs(0, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
            $$3.addOrReplaceChild(CHAIN_R_2, CubeListBuilder.create().texOffs(6, 6).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), PartPose.offsetAndRotation(5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        }
        if ($$0 == AttachmentType.CEILING_MIDDLE) {
            $$2.addOrReplaceChild(V_CHAINS, CubeListBuilder.create().texOffs(14, 6).addBox(-6.0f, -6.0f, 0.0f, 12.0f, 6.0f, 0.0f), PartPose.ZERO);
        }
        return LayerDefinition.create($$1, 64, 32);
    }

    public static final class AttachmentType
    extends Enum<AttachmentType>
    implements StringRepresentable {
        public static final /* enum */ AttachmentType WALL = new AttachmentType("wall");
        public static final /* enum */ AttachmentType CEILING = new AttachmentType("ceiling");
        public static final /* enum */ AttachmentType CEILING_MIDDLE = new AttachmentType("ceiling_middle");
        private final String name;
        private static final /* synthetic */ AttachmentType[] $VALUES;

        public static AttachmentType[] values() {
            return (AttachmentType[])$VALUES.clone();
        }

        public static AttachmentType valueOf(String $$0) {
            return Enum.valueOf(AttachmentType.class, $$0);
        }

        private AttachmentType(String $$0) {
            this.name = $$0;
        }

        public static AttachmentType byBlockState(BlockState $$0) {
            if ($$0.getBlock() instanceof CeilingHangingSignBlock) {
                return $$0.getValue(BlockStateProperties.ATTACHED) != false ? CEILING_MIDDLE : CEILING;
            }
            return WALL;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ AttachmentType[] a() {
            return new AttachmentType[]{WALL, CEILING, CEILING_MIDDLE};
        }

        static {
            $VALUES = AttachmentType.a();
        }
    }

    public static final class ModelKey
    extends Record {
        final WoodType woodType;
        final AttachmentType attachmentType;

        public ModelKey(WoodType $$0, AttachmentType $$1) {
            this.woodType = $$0;
            this.attachmentType = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelKey.class, "woodType;attachmentType", "woodType", "attachmentType"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelKey.class, "woodType;attachmentType", "woodType", "attachmentType"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelKey.class, "woodType;attachmentType", "woodType", "attachmentType"}, this, $$0);
        }

        public WoodType woodType() {
            return this.woodType;
        }

        public AttachmentType attachmentType() {
            return this.attachmentType;
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionfc;

public abstract class StuckInBodyLayer<M extends PlayerModel>
extends RenderLayer<PlayerRenderState, M> {
    private final Model model;
    private final ResourceLocation texture;
    private final PlacementStyle placementStyle;

    public StuckInBodyLayer(LivingEntityRenderer<?, PlayerRenderState, M> $$0, Model $$1, ResourceLocation $$2, PlacementStyle $$3) {
        super($$0);
        this.model = $$1;
        this.texture = $$2;
        this.placementStyle = $$3;
    }

    protected abstract int numStuck(PlayerRenderState var1);

    private void renderStuckItem(PoseStack $$0, MultiBufferSource $$1, int $$2, float $$3, float $$4, float $$5) {
        float $$6 = Mth.sqrt($$3 * $$3 + $$5 * $$5);
        float $$7 = (float)(Math.atan2($$3, $$5) * 57.2957763671875);
        float $$8 = (float)(Math.atan2($$4, $$6) * 57.2957763671875);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$7 - 90.0f));
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$8));
        this.model.renderToBuffer($$0, $$1.getBuffer(this.model.renderType(this.texture)), $$2, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, PlayerRenderState $$3, float $$4, float $$5) {
        int $$6 = this.numStuck($$3);
        if ($$6 <= 0) {
            return;
        }
        RandomSource $$7 = RandomSource.create($$3.id);
        for (int $$8 = 0; $$8 < $$6; ++$$8) {
            $$0.pushPose();
            ModelPart $$9 = ((PlayerModel)this.getParentModel()).getRandomBodyPart($$7);
            ModelPart.Cube $$10 = $$9.getRandomCube($$7);
            $$9.translateAndRotate($$0);
            float $$11 = $$7.nextFloat();
            float $$12 = $$7.nextFloat();
            float $$13 = $$7.nextFloat();
            if (this.placementStyle == PlacementStyle.ON_SURFACE) {
                int $$14 = $$7.nextInt(3);
                switch ($$14) {
                    case 0: {
                        $$11 = StuckInBodyLayer.snapToFace($$11);
                        break;
                    }
                    case 1: {
                        $$12 = StuckInBodyLayer.snapToFace($$12);
                        break;
                    }
                    default: {
                        $$13 = StuckInBodyLayer.snapToFace($$13);
                    }
                }
            }
            $$0.translate(Mth.lerp($$11, $$10.minX, $$10.maxX) / 16.0f, Mth.lerp($$12, $$10.minY, $$10.maxY) / 16.0f, Mth.lerp($$13, $$10.minZ, $$10.maxZ) / 16.0f);
            this.renderStuckItem($$0, $$1, $$2, -($$11 * 2.0f - 1.0f), -($$12 * 2.0f - 1.0f), -($$13 * 2.0f - 1.0f));
            $$0.popPose();
        }
    }

    private static float snapToFace(float $$0) {
        return $$0 > 0.5f ? 1.0f : 0.5f;
    }

    public static final class PlacementStyle
    extends Enum<PlacementStyle> {
        public static final /* enum */ PlacementStyle IN_CUBE = new PlacementStyle();
        public static final /* enum */ PlacementStyle ON_SURFACE = new PlacementStyle();
        private static final /* synthetic */ PlacementStyle[] $VALUES;

        public static PlacementStyle[] values() {
            return (PlacementStyle[])$VALUES.clone();
        }

        public static PlacementStyle valueOf(String $$0) {
            return Enum.valueOf(PlacementStyle.class, $$0);
        }

        private static /* synthetic */ PlacementStyle[] a() {
            return new PlacementStyle[]{IN_CUBE, ON_SURFACE};
        }

        static {
            $VALUES = PlacementStyle.a();
        }
    }
}


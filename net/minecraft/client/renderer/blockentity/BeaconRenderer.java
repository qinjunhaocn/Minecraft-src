/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class BeaconRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 2048;
    private static final float BEAM_SCALE_THRESHOLD = 96.0f;
    public static final float SOLID_BEAM_RADIUS = 0.2f;
    public static final float BEAM_GLOW_RADIUS = 0.25f;

    public BeaconRenderer(BlockEntityRendererProvider.Context $$0) {
    }

    @Override
    public void render(T $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        long $$7 = ((BlockEntity)$$0).getLevel().getGameTime();
        float $$8 = (float)$$6.subtract(((BlockEntity)$$0).getBlockPos().getCenter()).horizontalDistance();
        LocalPlayer $$9 = Minecraft.getInstance().player;
        float $$10 = $$9 != null && $$9.isScoping() ? 1.0f : Math.max(1.0f, $$8 / 96.0f);
        List<BeaconBeamOwner.Section> $$11 = ((BeaconBeamOwner)$$0).getBeamSections();
        int $$12 = 0;
        for (int $$13 = 0; $$13 < $$11.size(); ++$$13) {
            BeaconBeamOwner.Section $$14 = $$11.get($$13);
            BeaconRenderer.renderBeaconBeam($$2, $$3, $$1, $$10, $$7, $$12, $$13 == $$11.size() - 1 ? 2048 : $$14.getHeight(), $$14.getColor());
            $$12 += $$14.getHeight();
        }
    }

    private static void renderBeaconBeam(PoseStack $$0, MultiBufferSource $$1, float $$2, float $$3, long $$4, int $$5, int $$6, int $$7) {
        BeaconRenderer.renderBeaconBeam($$0, $$1, BEAM_LOCATION, $$2, 1.0f, $$4, $$5, $$6, $$7, 0.2f * $$3, 0.25f * $$3);
    }

    public static void renderBeaconBeam(PoseStack $$0, MultiBufferSource $$1, ResourceLocation $$2, float $$3, float $$4, long $$5, int $$6, int $$7, int $$8, float $$9, float $$10) {
        int $$11 = $$6 + $$7;
        $$0.pushPose();
        $$0.translate(0.5, 0.0, 0.5);
        float $$12 = (float)Math.floorMod((long)$$5, (int)40) + $$3;
        float $$13 = $$7 < 0 ? $$12 : -$$12;
        float $$14 = Mth.frac($$13 * 0.2f - (float)Mth.floor($$13 * 0.1f));
        $$0.pushPose();
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$12 * 2.25f - 45.0f));
        float $$15 = 0.0f;
        float $$16 = $$9;
        float $$17 = $$9;
        float $$18 = 0.0f;
        float $$19 = -$$9;
        float $$20 = 0.0f;
        float $$21 = 0.0f;
        float $$22 = -$$9;
        float $$23 = 0.0f;
        float $$24 = 1.0f;
        float $$25 = -1.0f + $$14;
        float $$26 = (float)$$7 * $$4 * (0.5f / $$9) + $$25;
        BeaconRenderer.renderPart($$0, $$1.getBuffer(RenderType.beaconBeam($$2, false)), $$8, $$6, $$11, 0.0f, $$16, $$17, 0.0f, $$19, 0.0f, 0.0f, $$22, 0.0f, 1.0f, $$26, $$25);
        $$0.popPose();
        float $$27 = -$$10;
        float $$28 = -$$10;
        float $$29 = $$10;
        float $$30 = -$$10;
        float $$31 = -$$10;
        float $$32 = $$10;
        float $$33 = $$10;
        float $$34 = $$10;
        float $$35 = 0.0f;
        float $$36 = 1.0f;
        float $$37 = -1.0f + $$14;
        float $$38 = (float)$$7 * $$4 + $$37;
        BeaconRenderer.renderPart($$0, $$1.getBuffer(RenderType.beaconBeam($$2, true)), ARGB.color(32, $$8), $$6, $$11, $$27, $$28, $$29, $$30, $$31, $$32, $$33, $$34, 0.0f, 1.0f, $$38, $$37);
        $$0.popPose();
    }

    private static void renderPart(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, int $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11, float $$12, float $$13, float $$14, float $$15, float $$16) {
        PoseStack.Pose $$17 = $$0.last();
        BeaconRenderer.renderQuad($$17, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$13, $$14, $$15, $$16);
        BeaconRenderer.renderQuad($$17, $$1, $$2, $$3, $$4, $$11, $$12, $$9, $$10, $$13, $$14, $$15, $$16);
        BeaconRenderer.renderQuad($$17, $$1, $$2, $$3, $$4, $$7, $$8, $$11, $$12, $$13, $$14, $$15, $$16);
        BeaconRenderer.renderQuad($$17, $$1, $$2, $$3, $$4, $$9, $$10, $$5, $$6, $$13, $$14, $$15, $$16);
    }

    private static void renderQuad(PoseStack.Pose $$0, VertexConsumer $$1, int $$2, int $$3, int $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11, float $$12) {
        BeaconRenderer.addVertex($$0, $$1, $$2, $$4, $$5, $$6, $$10, $$11);
        BeaconRenderer.addVertex($$0, $$1, $$2, $$3, $$5, $$6, $$10, $$12);
        BeaconRenderer.addVertex($$0, $$1, $$2, $$3, $$7, $$8, $$9, $$12);
        BeaconRenderer.addVertex($$0, $$1, $$2, $$4, $$7, $$8, $$9, $$11);
    }

    private static void addVertex(PoseStack.Pose $$0, VertexConsumer $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
        $$1.addVertex($$0, $$4, (float)$$3, $$5).setColor($$2).setUv($$6, $$7).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal($$0, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }

    @Override
    public boolean shouldRender(T $$0, Vec3 $$1) {
        return Vec3.atCenterOf(((BlockEntity)$$0).getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan($$1.multiply(1.0, 0.0, 1.0), this.getViewDistance());
    }
}


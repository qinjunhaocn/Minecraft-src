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
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public abstract class AbstractSignRenderer
implements BlockEntityRenderer<SignBlockEntity> {
    private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Font font;

    public AbstractSignRenderer(BlockEntityRendererProvider.Context $$0) {
        this.font = $$0.getFont();
    }

    protected abstract Model getSignModel(BlockState var1, WoodType var2);

    protected abstract Material getSignMaterial(WoodType var1);

    protected abstract float getSignModelRenderScale();

    protected abstract float getSignTextRenderScale();

    protected abstract Vec3 getTextOffset();

    protected abstract void translateSign(PoseStack var1, float var2, BlockState var3);

    @Override
    public void render(SignBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        BlockState $$7 = $$0.getBlockState();
        SignBlock $$8 = (SignBlock)$$7.getBlock();
        Model $$9 = this.getSignModel($$7, $$8.type());
        this.renderSignWithText($$0, $$2, $$3, $$4, $$5, $$7, $$8, $$8.type(), $$9);
    }

    private void renderSignWithText(SignBlockEntity $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, BlockState $$5, SignBlock $$6, WoodType $$7, Model $$8) {
        $$1.pushPose();
        this.translateSign($$1, -$$6.getYRotationDegrees($$5), $$5);
        this.renderSign($$1, $$2, $$3, $$4, $$7, $$8);
        this.renderSignText($$0.getBlockPos(), $$0.getFrontText(), $$1, $$2, $$3, $$0.getTextLineHeight(), $$0.getMaxTextLineWidth(), true);
        this.renderSignText($$0.getBlockPos(), $$0.getBackText(), $$1, $$2, $$3, $$0.getTextLineHeight(), $$0.getMaxTextLineWidth(), false);
        $$1.popPose();
    }

    protected void renderSign(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, WoodType $$4, Model $$5) {
        $$0.pushPose();
        float $$6 = this.getSignModelRenderScale();
        $$0.scale($$6, -$$6, -$$6);
        Material $$7 = this.getSignMaterial($$4);
        VertexConsumer $$8 = $$7.buffer($$1, $$5::renderType);
        $$5.renderToBuffer($$0, $$8, $$2, $$3);
        $$0.popPose();
    }

    private void renderSignText(BlockPos $$0, SignText $$12, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, int $$6, boolean $$7) {
        int $$16;
        boolean $$15;
        int $$14;
        $$2.pushPose();
        this.translateSignText($$2, $$7, this.getTextOffset());
        int $$8 = AbstractSignRenderer.getDarkColor($$12);
        int $$9 = 4 * $$5 / 2;
        FormattedCharSequence[] $$10 = $$12.a(Minecraft.getInstance().isTextFilteringEnabled(), $$1 -> {
            List<FormattedCharSequence> $$2 = this.font.split((FormattedText)$$1, $$6);
            return $$2.isEmpty() ? FormattedCharSequence.EMPTY : $$2.get(0);
        });
        if ($$12.hasGlowingText()) {
            int $$11 = $$12.getColor().getTextColor();
            boolean $$122 = AbstractSignRenderer.isOutlineVisible($$0, $$11);
            int $$13 = 0xF000F0;
        } else {
            $$14 = $$8;
            $$15 = false;
            $$16 = $$4;
        }
        for (int $$17 = 0; $$17 < 4; ++$$17) {
            FormattedCharSequence $$18 = $$10[$$17];
            float $$19 = -this.font.width($$18) / 2;
            if ($$15) {
                this.font.drawInBatch8xOutline($$18, $$19, $$17 * $$5 - $$9, $$14, $$8, $$2.last().pose(), $$3, $$16);
                continue;
            }
            this.font.drawInBatch($$18, $$19, (float)($$17 * $$5 - $$9), $$14, false, $$2.last().pose(), $$3, Font.DisplayMode.POLYGON_OFFSET, 0, $$16);
        }
        $$2.popPose();
    }

    private void translateSignText(PoseStack $$0, boolean $$1, Vec3 $$2) {
        if (!$$1) {
            $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
        }
        float $$3 = 0.015625f * this.getSignTextRenderScale();
        $$0.translate($$2);
        $$0.scale($$3, -$$3, $$3);
    }

    private static boolean isOutlineVisible(BlockPos $$0, int $$1) {
        if ($$1 == DyeColor.BLACK.getTextColor()) {
            return true;
        }
        Minecraft $$2 = Minecraft.getInstance();
        LocalPlayer $$3 = $$2.player;
        if ($$3 != null && $$2.options.getCameraType().isFirstPerson() && $$3.isScoping()) {
            return true;
        }
        Entity $$4 = $$2.getCameraEntity();
        return $$4 != null && $$4.distanceToSqr(Vec3.atCenterOf($$0)) < (double)OUTLINE_RENDER_DISTANCE;
    }

    public static int getDarkColor(SignText $$0) {
        int $$1 = $$0.getColor().getTextColor();
        if ($$1 == DyeColor.BLACK.getTextColor() && $$0.hasGlowingText()) {
            return -988212;
        }
        return ARGB.scaleRGB($$1, 0.4f);
    }
}


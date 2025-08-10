/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public class ScreenEffectRenderer {
    private static final ResourceLocation UNDERWATER_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
    private final Minecraft minecraft;
    private final MultiBufferSource bufferSource;
    public static final int ITEM_ACTIVATION_ANIMATION_LENGTH = 40;
    @Nullable
    private ItemStack itemActivationItem;
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;

    public ScreenEffectRenderer(Minecraft $$0, MultiBufferSource $$1) {
        this.minecraft = $$0;
        this.bufferSource = $$1;
    }

    public void tick() {
        if (this.itemActivationTicks > 0) {
            --this.itemActivationTicks;
            if (this.itemActivationTicks == 0) {
                this.itemActivationItem = null;
            }
        }
    }

    public void renderScreenEffect(boolean $$0, float $$1) {
        PoseStack $$2 = new PoseStack();
        LocalPlayer $$3 = this.minecraft.player;
        if (this.minecraft.options.getCameraType().isFirstPerson() && !$$0) {
            BlockState $$4;
            if (!$$3.noPhysics && ($$4 = ScreenEffectRenderer.getViewBlockingState($$3)) != null) {
                ScreenEffectRenderer.renderTex(this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon($$4), $$2, this.bufferSource);
            }
            if (!this.minecraft.player.isSpectator()) {
                if (this.minecraft.player.isEyeInFluid(FluidTags.WATER)) {
                    ScreenEffectRenderer.renderWater(this.minecraft, $$2, this.bufferSource);
                }
                if (this.minecraft.player.isOnFire()) {
                    ScreenEffectRenderer.renderFire($$2, this.bufferSource);
                }
            }
        }
        if (!this.minecraft.options.hideGui) {
            this.renderItemActivationAnimation($$2, $$1);
        }
    }

    private void renderItemActivationAnimation(PoseStack $$0, float $$1) {
        if (this.itemActivationItem == null || this.itemActivationTicks <= 0) {
            return;
        }
        int $$2 = 40 - this.itemActivationTicks;
        float $$3 = ((float)$$2 + $$1) / 40.0f;
        float $$4 = $$3 * $$3;
        float $$5 = $$3 * $$4;
        float $$6 = 10.25f * $$5 * $$4 - 24.95f * $$4 * $$4 + 25.5f * $$5 - 13.8f * $$4 + 4.0f * $$3;
        float $$7 = $$6 * (float)Math.PI;
        float $$8 = (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight();
        float $$9 = this.itemActivationOffX * 0.3f * $$8;
        float $$10 = this.itemActivationOffY * 0.3f;
        $$0.pushPose();
        $$0.translate($$9 * Mth.abs(Mth.sin($$7 * 2.0f)), $$10 * Mth.abs(Mth.sin($$7 * 2.0f)), -10.0f + 9.0f * Mth.sin($$7));
        float $$11 = 0.8f;
        $$0.scale(0.8f, 0.8f, 0.8f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(900.0f * Mth.abs(Mth.sin($$7))));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(6.0f * Mth.cos($$3 * 8.0f)));
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(6.0f * Mth.cos($$3 * 8.0f)));
        this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemDisplayContext.FIXED, 0xF000F0, OverlayTexture.NO_OVERLAY, $$0, this.bufferSource, this.minecraft.level, 0);
        $$0.popPose();
    }

    public void resetItemActivation() {
        this.itemActivationItem = null;
    }

    public void displayItemActivation(ItemStack $$0, RandomSource $$1) {
        this.itemActivationItem = $$0;
        this.itemActivationTicks = 40;
        this.itemActivationOffX = $$1.nextFloat() * 2.0f - 1.0f;
        this.itemActivationOffY = $$1.nextFloat() * 2.0f - 1.0f;
    }

    @Nullable
    private static BlockState getViewBlockingState(Player $$0) {
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        for (int $$2 = 0; $$2 < 8; ++$$2) {
            double $$3 = $$0.getX() + (double)(((float)(($$2 >> 0) % 2) - 0.5f) * $$0.getBbWidth() * 0.8f);
            double $$4 = $$0.getEyeY() + (double)(((float)(($$2 >> 1) % 2) - 0.5f) * 0.1f * $$0.getScale());
            double $$5 = $$0.getZ() + (double)(((float)(($$2 >> 2) % 2) - 0.5f) * $$0.getBbWidth() * 0.8f);
            $$1.set($$3, $$4, $$5);
            BlockState $$6 = $$0.level().getBlockState($$1);
            if ($$6.getRenderShape() == RenderShape.INVISIBLE || !$$6.isViewBlocking($$0.level(), $$1)) continue;
            return $$6;
        }
        return null;
    }

    private static void renderTex(TextureAtlasSprite $$0, PoseStack $$1, MultiBufferSource $$2) {
        float $$3 = 0.1f;
        int $$4 = ARGB.colorFromFloat(1.0f, 0.1f, 0.1f, 0.1f);
        float $$5 = -1.0f;
        float $$6 = 1.0f;
        float $$7 = -1.0f;
        float $$8 = 1.0f;
        float $$9 = -0.5f;
        float $$10 = $$0.getU0();
        float $$11 = $$0.getU1();
        float $$12 = $$0.getV0();
        float $$13 = $$0.getV1();
        Matrix4f $$14 = $$1.last().pose();
        VertexConsumer $$15 = $$2.getBuffer(RenderType.blockScreenEffect($$0.atlasLocation()));
        $$15.addVertex($$14, -1.0f, -1.0f, -0.5f).setUv($$11, $$13).setColor($$4);
        $$15.addVertex($$14, 1.0f, -1.0f, -0.5f).setUv($$10, $$13).setColor($$4);
        $$15.addVertex($$14, 1.0f, 1.0f, -0.5f).setUv($$10, $$12).setColor($$4);
        $$15.addVertex($$14, -1.0f, 1.0f, -0.5f).setUv($$11, $$12).setColor($$4);
    }

    private static void renderWater(Minecraft $$0, PoseStack $$1, MultiBufferSource $$2) {
        BlockPos $$3 = BlockPos.containing($$0.player.getX(), $$0.player.getEyeY(), $$0.player.getZ());
        float $$4 = LightTexture.getBrightness($$0.player.level().dimensionType(), $$0.player.level().getMaxLocalRawBrightness($$3));
        int $$5 = ARGB.colorFromFloat(0.1f, $$4, $$4, $$4);
        float $$6 = 4.0f;
        float $$7 = -1.0f;
        float $$8 = 1.0f;
        float $$9 = -1.0f;
        float $$10 = 1.0f;
        float $$11 = -0.5f;
        float $$12 = -$$0.player.getYRot() / 64.0f;
        float $$13 = $$0.player.getXRot() / 64.0f;
        Matrix4f $$14 = $$1.last().pose();
        VertexConsumer $$15 = $$2.getBuffer(RenderType.blockScreenEffect(UNDERWATER_LOCATION));
        $$15.addVertex($$14, -1.0f, -1.0f, -0.5f).setUv(4.0f + $$12, 4.0f + $$13).setColor($$5);
        $$15.addVertex($$14, 1.0f, -1.0f, -0.5f).setUv(0.0f + $$12, 4.0f + $$13).setColor($$5);
        $$15.addVertex($$14, 1.0f, 1.0f, -0.5f).setUv(0.0f + $$12, 0.0f + $$13).setColor($$5);
        $$15.addVertex($$14, -1.0f, 1.0f, -0.5f).setUv(4.0f + $$12, 0.0f + $$13).setColor($$5);
    }

    private static void renderFire(PoseStack $$0, MultiBufferSource $$1) {
        TextureAtlasSprite $$2 = ModelBakery.FIRE_1.sprite();
        VertexConsumer $$3 = $$1.getBuffer(RenderType.fireScreenEffect($$2.atlasLocation()));
        float $$4 = $$2.getU0();
        float $$5 = $$2.getU1();
        float $$6 = ($$4 + $$5) / 2.0f;
        float $$7 = $$2.getV0();
        float $$8 = $$2.getV1();
        float $$9 = ($$7 + $$8) / 2.0f;
        float $$10 = $$2.uvShrinkRatio();
        float $$11 = Mth.lerp($$10, $$4, $$6);
        float $$12 = Mth.lerp($$10, $$5, $$6);
        float $$13 = Mth.lerp($$10, $$7, $$9);
        float $$14 = Mth.lerp($$10, $$8, $$9);
        float $$15 = 1.0f;
        for (int $$16 = 0; $$16 < 2; ++$$16) {
            $$0.pushPose();
            float $$17 = -0.5f;
            float $$18 = 0.5f;
            float $$19 = -0.5f;
            float $$20 = 0.5f;
            float $$21 = -0.5f;
            $$0.translate((float)(-($$16 * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)($$16 * 2 - 1) * 10.0f));
            Matrix4f $$22 = $$0.last().pose();
            $$3.addVertex($$22, -0.5f, -0.5f, -0.5f).setUv($$12, $$14).setColor(1.0f, 1.0f, 1.0f, 0.9f);
            $$3.addVertex($$22, 0.5f, -0.5f, -0.5f).setUv($$11, $$14).setColor(1.0f, 1.0f, 1.0f, 0.9f);
            $$3.addVertex($$22, 0.5f, 0.5f, -0.5f).setUv($$11, $$13).setColor(1.0f, 1.0f, 1.0f, 0.9f);
            $$3.addVertex($$22, -0.5f, 0.5f, -0.5f).setUv($$12, $$13).setColor(1.0f, 1.0f, 1.0f, 0.9f);
            $$0.popPose();
        }
    }
}


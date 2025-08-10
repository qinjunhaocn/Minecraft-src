/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public class ItemInHandRenderer {
    private static final RenderType MAP_BACKGROUND = RenderType.text(ResourceLocation.withDefaultNamespace("textures/map/map_background.png"));
    private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text(ResourceLocation.withDefaultNamespace("textures/map/map_background_checkerboard.png"));
    private static final float ITEM_SWING_X_POS_SCALE = -0.4f;
    private static final float ITEM_SWING_Y_POS_SCALE = 0.2f;
    private static final float ITEM_SWING_Z_POS_SCALE = -0.2f;
    private static final float ITEM_HEIGHT_SCALE = -0.6f;
    private static final float ITEM_POS_X = 0.56f;
    private static final float ITEM_POS_Y = -0.52f;
    private static final float ITEM_POS_Z = -0.72f;
    private static final float ITEM_PRESWING_ROT_Y = 45.0f;
    private static final float ITEM_SWING_X_ROT_AMOUNT = -80.0f;
    private static final float ITEM_SWING_Y_ROT_AMOUNT = -20.0f;
    private static final float ITEM_SWING_Z_ROT_AMOUNT = -20.0f;
    private static final float EAT_JIGGLE_X_ROT_AMOUNT = 10.0f;
    private static final float EAT_JIGGLE_Y_ROT_AMOUNT = 90.0f;
    private static final float EAT_JIGGLE_Z_ROT_AMOUNT = 30.0f;
    private static final float EAT_JIGGLE_X_POS_SCALE = 0.6f;
    private static final float EAT_JIGGLE_Y_POS_SCALE = -0.5f;
    private static final float EAT_JIGGLE_Z_POS_SCALE = 0.0f;
    private static final double EAT_JIGGLE_EXPONENT = 27.0;
    private static final float EAT_EXTRA_JIGGLE_CUTOFF = 0.8f;
    private static final float EAT_EXTRA_JIGGLE_SCALE = 0.1f;
    private static final float ARM_SWING_X_POS_SCALE = -0.3f;
    private static final float ARM_SWING_Y_POS_SCALE = 0.4f;
    private static final float ARM_SWING_Z_POS_SCALE = -0.4f;
    private static final float ARM_SWING_Y_ROT_AMOUNT = 70.0f;
    private static final float ARM_SWING_Z_ROT_AMOUNT = -20.0f;
    private static final float ARM_HEIGHT_SCALE = -0.6f;
    private static final float ARM_POS_SCALE = 0.8f;
    private static final float ARM_POS_X = 0.8f;
    private static final float ARM_POS_Y = -0.75f;
    private static final float ARM_POS_Z = -0.9f;
    private static final float ARM_PRESWING_ROT_Y = 45.0f;
    private static final float ARM_PREROTATION_X_OFFSET = -1.0f;
    private static final float ARM_PREROTATION_Y_OFFSET = 3.6f;
    private static final float ARM_PREROTATION_Z_OFFSET = 3.5f;
    private static final float ARM_POSTROTATION_X_OFFSET = 5.6f;
    private static final int ARM_ROT_X = 200;
    private static final int ARM_ROT_Y = -135;
    private static final int ARM_ROT_Z = 120;
    private static final float MAP_SWING_X_POS_SCALE = -0.4f;
    private static final float MAP_SWING_Z_POS_SCALE = -0.2f;
    private static final float MAP_HANDS_POS_X = 0.0f;
    private static final float MAP_HANDS_POS_Y = 0.04f;
    private static final float MAP_HANDS_POS_Z = -0.72f;
    private static final float MAP_HANDS_HEIGHT_SCALE = -1.2f;
    private static final float MAP_HANDS_TILT_SCALE = -0.5f;
    private static final float MAP_PLAYER_PITCH_SCALE = 45.0f;
    private static final float MAP_HANDS_Z_ROT_AMOUNT = -85.0f;
    private static final float MAPHAND_X_ROT_AMOUNT = 45.0f;
    private static final float MAPHAND_Y_ROT_AMOUNT = 92.0f;
    private static final float MAPHAND_Z_ROT_AMOUNT = -41.0f;
    private static final float MAP_HAND_X_POS = 0.3f;
    private static final float MAP_HAND_Y_POS = -1.1f;
    private static final float MAP_HAND_Z_POS = 0.45f;
    private static final float MAP_SWING_X_ROT_AMOUNT = 20.0f;
    private static final float MAP_PRE_ROT_SCALE = 0.38f;
    private static final float MAP_GLOBAL_X_POS = -0.5f;
    private static final float MAP_GLOBAL_Y_POS = -0.5f;
    private static final float MAP_GLOBAL_Z_POS = 0.0f;
    private static final float MAP_FINAL_SCALE = 0.0078125f;
    private static final int MAP_BORDER = 7;
    private static final int MAP_HEIGHT = 128;
    private static final int MAP_WIDTH = 128;
    private static final float BOW_CHARGE_X_POS_SCALE = 0.0f;
    private static final float BOW_CHARGE_Y_POS_SCALE = 0.0f;
    private static final float BOW_CHARGE_Z_POS_SCALE = 0.04f;
    private static final float BOW_CHARGE_SHAKE_X_SCALE = 0.0f;
    private static final float BOW_CHARGE_SHAKE_Y_SCALE = 0.004f;
    private static final float BOW_CHARGE_SHAKE_Z_SCALE = 0.0f;
    private static final float BOW_CHARGE_Z_SCALE = 0.2f;
    private static final float BOW_MIN_SHAKE_CHARGE = 0.1f;
    private final Minecraft minecraft;
    private final MapRenderState mapRenderState = new MapRenderState();
    private ItemStack mainHandItem = ItemStack.EMPTY;
    private ItemStack offHandItem = ItemStack.EMPTY;
    private float mainHandHeight;
    private float oMainHandHeight;
    private float offHandHeight;
    private float oOffHandHeight;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;
    private final ItemModelResolver itemModelResolver;

    public ItemInHandRenderer(Minecraft $$0, EntityRenderDispatcher $$1, ItemRenderer $$2, ItemModelResolver $$3) {
        this.minecraft = $$0;
        this.entityRenderDispatcher = $$1;
        this.itemRenderer = $$2;
        this.itemModelResolver = $$3;
    }

    public void renderItem(LivingEntity $$0, ItemStack $$1, ItemDisplayContext $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        if ($$1.isEmpty()) {
            return;
        }
        this.itemRenderer.renderStatic($$0, $$1, $$2, $$3, $$4, $$0.level(), $$5, OverlayTexture.NO_OVERLAY, $$0.getId() + $$2.ordinal());
    }

    private float calculateMapTilt(float $$0) {
        float $$1 = 1.0f - $$0 / 45.0f + 0.1f;
        $$1 = Mth.clamp($$1, 0.0f, 1.0f);
        $$1 = -Mth.cos($$1 * (float)Math.PI) * 0.5f + 0.5f;
        return $$1;
    }

    private void renderMapHand(PoseStack $$0, MultiBufferSource $$1, int $$2, HumanoidArm $$3) {
        PlayerRenderer $$4 = (PlayerRenderer)this.entityRenderDispatcher.getRenderer(this.minecraft.player);
        $$0.pushPose();
        float $$5 = $$3 == HumanoidArm.RIGHT ? 1.0f : -1.0f;
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(92.0f));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(45.0f));
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$5 * -41.0f));
        $$0.translate($$5 * 0.3f, -1.1f, 0.45f);
        ResourceLocation $$6 = this.minecraft.player.getSkin().texture();
        if ($$3 == HumanoidArm.RIGHT) {
            $$4.renderRightHand($$0, $$1, $$2, $$6, this.minecraft.player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
        } else {
            $$4.renderLeftHand($$0, $$1, $$2, $$6, this.minecraft.player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE));
        }
        $$0.popPose();
    }

    private void renderOneHandedMap(PoseStack $$0, MultiBufferSource $$1, int $$2, float $$3, HumanoidArm $$4, float $$5, ItemStack $$6) {
        float $$7 = $$4 == HumanoidArm.RIGHT ? 1.0f : -1.0f;
        $$0.translate($$7 * 0.125f, -0.125f, 0.0f);
        if (!this.minecraft.player.isInvisible()) {
            $$0.pushPose();
            $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$7 * 10.0f));
            this.renderPlayerArm($$0, $$1, $$2, $$3, $$5, $$4);
            $$0.popPose();
        }
        $$0.pushPose();
        $$0.translate($$7 * 0.51f, -0.08f + $$3 * -1.2f, -0.75f);
        float $$8 = Mth.sqrt($$5);
        float $$9 = Mth.sin($$8 * (float)Math.PI);
        float $$10 = -0.5f * $$9;
        float $$11 = 0.4f * Mth.sin($$8 * ((float)Math.PI * 2));
        float $$12 = -0.3f * Mth.sin($$5 * (float)Math.PI);
        $$0.translate($$7 * $$10, $$11 - 0.3f * $$9, $$12);
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$9 * -45.0f));
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$7 * $$9 * -30.0f));
        this.renderMap($$0, $$1, $$2, $$6);
        $$0.popPose();
    }

    private void renderTwoHandedMap(PoseStack $$0, MultiBufferSource $$1, int $$2, float $$3, float $$4, float $$5) {
        float $$6 = Mth.sqrt($$5);
        float $$7 = -0.2f * Mth.sin($$5 * (float)Math.PI);
        float $$8 = -0.4f * Mth.sin($$6 * (float)Math.PI);
        $$0.translate(0.0f, -$$7 / 2.0f, $$8);
        float $$9 = this.calculateMapTilt($$3);
        $$0.translate(0.0f, 0.04f + $$4 * -1.2f + $$9 * -0.5f, -0.72f);
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$9 * -85.0f));
        if (!this.minecraft.player.isInvisible()) {
            $$0.pushPose();
            $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0f));
            this.renderMapHand($$0, $$1, $$2, HumanoidArm.RIGHT);
            this.renderMapHand($$0, $$1, $$2, HumanoidArm.LEFT);
            $$0.popPose();
        }
        float $$10 = Mth.sin($$6 * (float)Math.PI);
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$10 * 20.0f));
        $$0.scale(2.0f, 2.0f, 2.0f);
        this.renderMap($$0, $$1, $$2, this.mainHandItem);
    }

    private void renderMap(PoseStack $$0, MultiBufferSource $$1, int $$2, ItemStack $$3) {
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0f));
        $$0.scale(0.38f, 0.38f, 0.38f);
        $$0.translate(-0.5f, -0.5f, 0.0f);
        $$0.scale(0.0078125f, 0.0078125f, 0.0078125f);
        MapId $$4 = $$3.get(DataComponents.MAP_ID);
        MapItemSavedData $$5 = MapItem.getSavedData($$4, (Level)this.minecraft.level);
        VertexConsumer $$6 = $$1.getBuffer($$5 == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f $$7 = $$0.last().pose();
        $$6.addVertex($$7, -7.0f, 135.0f, 0.0f).setColor(-1).setUv(0.0f, 1.0f).setLight($$2);
        $$6.addVertex($$7, 135.0f, 135.0f, 0.0f).setColor(-1).setUv(1.0f, 1.0f).setLight($$2);
        $$6.addVertex($$7, 135.0f, -7.0f, 0.0f).setColor(-1).setUv(1.0f, 0.0f).setLight($$2);
        $$6.addVertex($$7, -7.0f, -7.0f, 0.0f).setColor(-1).setUv(0.0f, 0.0f).setLight($$2);
        if ($$5 != null) {
            MapRenderer $$8 = this.minecraft.getMapRenderer();
            $$8.extractRenderState($$4, $$5, this.mapRenderState);
            $$8.render(this.mapRenderState, $$0, $$1, false, $$2);
        }
    }

    private void renderPlayerArm(PoseStack $$0, MultiBufferSource $$1, int $$2, float $$3, float $$4, HumanoidArm $$5) {
        boolean $$6 = $$5 != HumanoidArm.LEFT;
        float $$7 = $$6 ? 1.0f : -1.0f;
        float $$8 = Mth.sqrt($$4);
        float $$9 = -0.3f * Mth.sin($$8 * (float)Math.PI);
        float $$10 = 0.4f * Mth.sin($$8 * ((float)Math.PI * 2));
        float $$11 = -0.4f * Mth.sin($$4 * (float)Math.PI);
        $$0.translate($$7 * ($$9 + 0.64000005f), $$10 + -0.6f + $$3 * -0.6f, $$11 + -0.71999997f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$7 * 45.0f));
        float $$12 = Mth.sin($$4 * $$4 * (float)Math.PI);
        float $$13 = Mth.sin($$8 * (float)Math.PI);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$7 * $$13 * 70.0f));
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$7 * $$12 * -20.0f));
        LocalPlayer $$14 = this.minecraft.player;
        $$0.translate($$7 * -1.0f, 3.6f, 3.5f);
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$7 * 120.0f));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(200.0f));
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$7 * -135.0f));
        $$0.translate($$7 * 5.6f, 0.0f, 0.0f);
        PlayerRenderer $$15 = (PlayerRenderer)this.entityRenderDispatcher.getRenderer($$14);
        ResourceLocation $$16 = $$14.getSkin().texture();
        if ($$6) {
            $$15.renderRightHand($$0, $$1, $$2, $$16, $$14.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
        } else {
            $$15.renderLeftHand($$0, $$1, $$2, $$16, $$14.isModelPartShown(PlayerModelPart.LEFT_SLEEVE));
        }
    }

    private void applyEatTransform(PoseStack $$0, float $$1, HumanoidArm $$2, ItemStack $$3, Player $$4) {
        float $$5 = (float)$$4.getUseItemRemainingTicks() - $$1 + 1.0f;
        float $$6 = $$5 / (float)$$3.getUseDuration($$4);
        if ($$6 < 0.8f) {
            float $$7 = Mth.abs(Mth.cos($$5 / 4.0f * (float)Math.PI) * 0.1f);
            $$0.translate(0.0f, $$7, 0.0f);
        }
        float $$8 = 1.0f - (float)Math.pow($$6, 27.0);
        int $$9 = $$2 == HumanoidArm.RIGHT ? 1 : -1;
        $$0.translate($$8 * 0.6f * (float)$$9, $$8 * -0.5f, $$8 * 0.0f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$9 * $$8 * 90.0f));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$8 * 10.0f));
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$9 * $$8 * 30.0f));
    }

    private void applyBrushTransform(PoseStack $$0, float $$1, HumanoidArm $$2, ItemStack $$3, Player $$4, float $$5) {
        this.applyItemArmTransform($$0, $$2, $$5);
        float $$6 = $$4.getUseItemRemainingTicks() % 10;
        float $$7 = $$6 - $$1 + 1.0f;
        float $$8 = 1.0f - $$7 / 10.0f;
        float $$9 = -90.0f;
        float $$10 = 60.0f;
        float $$11 = 150.0f;
        float $$12 = -15.0f;
        int $$13 = 2;
        float $$14 = -15.0f + 75.0f * Mth.cos($$8 * 2.0f * (float)Math.PI);
        if ($$2 != HumanoidArm.RIGHT) {
            $$0.translate(0.1, 0.83, 0.35);
            $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-80.0f));
            $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-90.0f));
            $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$14));
            $$0.translate(-0.3, 0.22, 0.35);
        } else {
            $$0.translate(-0.25, 0.22, 0.35);
            $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-80.0f));
            $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0f));
            $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(0.0f));
            $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$14));
        }
    }

    private void applyItemArmAttackTransform(PoseStack $$0, HumanoidArm $$1, float $$2) {
        int $$3 = $$1 == HumanoidArm.RIGHT ? 1 : -1;
        float $$4 = Mth.sin($$2 * $$2 * (float)Math.PI);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$3 * (45.0f + $$4 * -20.0f)));
        float $$5 = Mth.sin(Mth.sqrt($$2) * (float)Math.PI);
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$3 * $$5 * -20.0f));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$5 * -80.0f));
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$3 * -45.0f));
    }

    private void applyItemArmTransform(PoseStack $$0, HumanoidArm $$1, float $$2) {
        int $$3 = $$1 == HumanoidArm.RIGHT ? 1 : -1;
        $$0.translate((float)$$3 * 0.56f, -0.52f + $$2 * -0.6f, -0.72f);
    }

    public void renderHandsWithItems(float $$0, PoseStack $$1, MultiBufferSource.BufferSource $$2, LocalPlayer $$3, int $$4) {
        float $$5 = $$3.getAttackAnim($$0);
        InteractionHand $$6 = MoreObjects.firstNonNull($$3.swingingArm, InteractionHand.MAIN_HAND);
        float $$7 = $$3.getXRot($$0);
        HandRenderSelection $$8 = ItemInHandRenderer.evaluateWhichHandsToRender($$3);
        float $$9 = Mth.lerp($$0, $$3.xBobO, $$3.xBob);
        float $$10 = Mth.lerp($$0, $$3.yBobO, $$3.yBob);
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(($$3.getViewXRot($$0) - $$9) * 0.1f));
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(($$3.getViewYRot($$0) - $$10) * 0.1f));
        if ($$8.renderMainHand) {
            float $$11 = $$6 == InteractionHand.MAIN_HAND ? $$5 : 0.0f;
            float $$12 = 1.0f - Mth.lerp($$0, this.oMainHandHeight, this.mainHandHeight);
            this.renderArmWithItem($$3, $$0, $$7, InteractionHand.MAIN_HAND, $$11, this.mainHandItem, $$12, $$1, $$2, $$4);
        }
        if ($$8.renderOffHand) {
            float $$13 = $$6 == InteractionHand.OFF_HAND ? $$5 : 0.0f;
            float $$14 = 1.0f - Mth.lerp($$0, this.oOffHandHeight, this.offHandHeight);
            this.renderArmWithItem($$3, $$0, $$7, InteractionHand.OFF_HAND, $$13, this.offHandItem, $$14, $$1, $$2, $$4);
        }
        $$2.endBatch();
    }

    @VisibleForTesting
    static HandRenderSelection evaluateWhichHandsToRender(LocalPlayer $$0) {
        boolean $$4;
        ItemStack $$1 = $$0.getMainHandItem();
        ItemStack $$2 = $$0.getOffhandItem();
        boolean $$3 = $$1.is(Items.BOW) || $$2.is(Items.BOW);
        boolean bl = $$4 = $$1.is(Items.CROSSBOW) || $$2.is(Items.CROSSBOW);
        if (!$$3 && !$$4) {
            return HandRenderSelection.RENDER_BOTH_HANDS;
        }
        if ($$0.isUsingItem()) {
            return ItemInHandRenderer.selectionUsingItemWhileHoldingBowLike($$0);
        }
        if (ItemInHandRenderer.isChargedCrossbow($$1)) {
            return HandRenderSelection.RENDER_MAIN_HAND_ONLY;
        }
        return HandRenderSelection.RENDER_BOTH_HANDS;
    }

    private static HandRenderSelection selectionUsingItemWhileHoldingBowLike(LocalPlayer $$0) {
        ItemStack $$1 = $$0.getUseItem();
        InteractionHand $$2 = $$0.getUsedItemHand();
        if ($$1.is(Items.BOW) || $$1.is(Items.CROSSBOW)) {
            return HandRenderSelection.onlyForHand($$2);
        }
        return $$2 == InteractionHand.MAIN_HAND && ItemInHandRenderer.isChargedCrossbow($$0.getOffhandItem()) ? HandRenderSelection.RENDER_MAIN_HAND_ONLY : HandRenderSelection.RENDER_BOTH_HANDS;
    }

    private static boolean isChargedCrossbow(ItemStack $$0) {
        return $$0.is(Items.CROSSBOW) && CrossbowItem.isCharged($$0);
    }

    private void renderArmWithItem(AbstractClientPlayer $$0, float $$1, float $$2, InteractionHand $$3, float $$4, ItemStack $$5, float $$6, PoseStack $$7, MultiBufferSource $$8, int $$9) {
        if ($$0.isScoping()) {
            return;
        }
        boolean $$10 = $$3 == InteractionHand.MAIN_HAND;
        HumanoidArm $$11 = $$10 ? $$0.getMainArm() : $$0.getMainArm().getOpposite();
        $$7.pushPose();
        if ($$5.isEmpty()) {
            if ($$10 && !$$0.isInvisible()) {
                this.renderPlayerArm($$7, $$8, $$9, $$6, $$4, $$11);
            }
        } else if ($$5.has(DataComponents.MAP_ID)) {
            if ($$10 && this.offHandItem.isEmpty()) {
                this.renderTwoHandedMap($$7, $$8, $$9, $$2, $$6, $$4);
            } else {
                this.renderOneHandedMap($$7, $$8, $$9, $$6, $$11, $$4, $$5);
            }
        } else if ($$5.is(Items.CROSSBOW)) {
            int $$14;
            boolean $$12 = CrossbowItem.isCharged($$5);
            boolean $$13 = $$11 == HumanoidArm.RIGHT;
            int n = $$14 = $$13 ? 1 : -1;
            if ($$0.isUsingItem() && $$0.getUseItemRemainingTicks() > 0 && $$0.getUsedItemHand() == $$3 && !$$12) {
                this.applyItemArmTransform($$7, $$11, $$6);
                $$7.translate((float)$$14 * -0.4785682f, -0.094387f, 0.05731531f);
                $$7.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-11.935f));
                $$7.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$14 * 65.3f));
                $$7.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$14 * -9.785f));
                float $$15 = (float)$$5.getUseDuration($$0) - ((float)$$0.getUseItemRemainingTicks() - $$1 + 1.0f);
                float $$16 = $$15 / (float)CrossbowItem.getChargeDuration($$5, $$0);
                if ($$16 > 1.0f) {
                    $$16 = 1.0f;
                }
                if ($$16 > 0.1f) {
                    float $$17 = Mth.sin(($$15 - 0.1f) * 1.3f);
                    float $$18 = $$16 - 0.1f;
                    float $$19 = $$17 * $$18;
                    $$7.translate($$19 * 0.0f, $$19 * 0.004f, $$19 * 0.0f);
                }
                $$7.translate($$16 * 0.0f, $$16 * 0.0f, $$16 * 0.04f);
                $$7.scale(1.0f, 1.0f, 1.0f + $$16 * 0.2f);
                $$7.mulPose((Quaternionfc)Axis.YN.rotationDegrees((float)$$14 * 45.0f));
            } else {
                this.swingArm($$4, $$6, $$7, $$14, $$11);
                if ($$12 && $$4 < 0.001f && $$10) {
                    $$7.translate((float)$$14 * -0.641864f, 0.0f, 0.0f);
                    $$7.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$14 * 10.0f));
                }
            }
            this.renderItem($$0, $$5, $$13 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, $$7, $$8, $$9);
        } else {
            int $$21;
            boolean $$20 = $$11 == HumanoidArm.RIGHT;
            int n = $$21 = $$20 ? 1 : -1;
            if ($$0.isUsingItem() && $$0.getUseItemRemainingTicks() > 0 && $$0.getUsedItemHand() == $$3) {
                switch ($$5.getUseAnimation()) {
                    case NONE: {
                        this.applyItemArmTransform($$7, $$11, $$6);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatTransform($$7, $$1, $$11, $$5, $$0);
                        this.applyItemArmTransform($$7, $$11, $$6);
                        break;
                    }
                    case BLOCK: {
                        this.applyItemArmTransform($$7, $$11, $$6);
                        if ($$5.getItem() instanceof ShieldItem) break;
                        $$7.translate((float)$$21 * -0.14142136f, 0.08f, 0.14142136f);
                        $$7.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-102.25f));
                        $$7.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$21 * 13.365f));
                        $$7.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$21 * 78.05f));
                        break;
                    }
                    case BOW: {
                        this.applyItemArmTransform($$7, $$11, $$6);
                        $$7.translate((float)$$21 * -0.2785682f, 0.18344387f, 0.15731531f);
                        $$7.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-13.935f));
                        $$7.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$21 * 35.3f));
                        $$7.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$21 * -9.785f));
                        float $$22 = (float)$$5.getUseDuration($$0) - ((float)$$0.getUseItemRemainingTicks() - $$1 + 1.0f);
                        float $$23 = $$22 / 20.0f;
                        $$23 = ($$23 * $$23 + $$23 * 2.0f) / 3.0f;
                        if ($$23 > 1.0f) {
                            $$23 = 1.0f;
                        }
                        if ($$23 > 0.1f) {
                            float $$24 = Mth.sin(($$22 - 0.1f) * 1.3f);
                            float $$25 = $$23 - 0.1f;
                            float $$26 = $$24 * $$25;
                            $$7.translate($$26 * 0.0f, $$26 * 0.004f, $$26 * 0.0f);
                        }
                        $$7.translate($$23 * 0.0f, $$23 * 0.0f, $$23 * 0.04f);
                        $$7.scale(1.0f, 1.0f, 1.0f + $$23 * 0.2f);
                        $$7.mulPose((Quaternionfc)Axis.YN.rotationDegrees((float)$$21 * 45.0f));
                        break;
                    }
                    case SPEAR: {
                        this.applyItemArmTransform($$7, $$11, $$6);
                        $$7.translate((float)$$21 * -0.5f, 0.7f, 0.1f);
                        $$7.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-55.0f));
                        $$7.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$21 * 35.3f));
                        $$7.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$21 * -9.785f));
                        float $$27 = (float)$$5.getUseDuration($$0) - ((float)$$0.getUseItemRemainingTicks() - $$1 + 1.0f);
                        float $$28 = $$27 / 10.0f;
                        if ($$28 > 1.0f) {
                            $$28 = 1.0f;
                        }
                        if ($$28 > 0.1f) {
                            float $$29 = Mth.sin(($$27 - 0.1f) * 1.3f);
                            float $$30 = $$28 - 0.1f;
                            float $$31 = $$29 * $$30;
                            $$7.translate($$31 * 0.0f, $$31 * 0.004f, $$31 * 0.0f);
                        }
                        $$7.translate(0.0f, 0.0f, $$28 * 0.2f);
                        $$7.scale(1.0f, 1.0f, 1.0f + $$28 * 0.2f);
                        $$7.mulPose((Quaternionfc)Axis.YN.rotationDegrees((float)$$21 * 45.0f));
                        break;
                    }
                    case BRUSH: {
                        this.applyBrushTransform($$7, $$1, $$11, $$5, $$0, $$6);
                        break;
                    }
                    case BUNDLE: {
                        this.swingArm($$4, $$6, $$7, $$21, $$11);
                    }
                }
            } else if ($$0.isAutoSpinAttack()) {
                this.applyItemArmTransform($$7, $$11, $$6);
                $$7.translate((float)$$21 * -0.4f, 0.8f, 0.3f);
                $$7.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)$$21 * 65.0f));
                $$7.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)$$21 * -85.0f));
            } else {
                this.swingArm($$4, $$6, $$7, $$21, $$11);
            }
            this.renderItem($$0, $$5, $$20 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, $$7, $$8, $$9);
        }
        $$7.popPose();
    }

    private void swingArm(float $$0, float $$1, PoseStack $$2, int $$3, HumanoidArm $$4) {
        float $$5 = -0.4f * Mth.sin(Mth.sqrt($$0) * (float)Math.PI);
        float $$6 = 0.2f * Mth.sin(Mth.sqrt($$0) * ((float)Math.PI * 2));
        float $$7 = -0.2f * Mth.sin($$0 * (float)Math.PI);
        $$2.translate((float)$$3 * $$5, $$6, $$7);
        this.applyItemArmTransform($$2, $$4, $$1);
        this.applyItemArmAttackTransform($$2, $$4, $$0);
    }

    private boolean shouldInstantlyReplaceVisibleItem(ItemStack $$0, ItemStack $$1) {
        if (ItemStack.matches($$0, $$1)) {
            return true;
        }
        return !this.itemModelResolver.shouldPlaySwapAnimation($$1);
    }

    public void tick() {
        this.oMainHandHeight = this.mainHandHeight;
        this.oOffHandHeight = this.offHandHeight;
        LocalPlayer $$0 = this.minecraft.player;
        ItemStack $$1 = $$0.getMainHandItem();
        ItemStack $$2 = $$0.getOffhandItem();
        if (this.shouldInstantlyReplaceVisibleItem(this.mainHandItem, $$1)) {
            this.mainHandItem = $$1;
        }
        if (this.shouldInstantlyReplaceVisibleItem(this.offHandItem, $$2)) {
            this.offHandItem = $$2;
        }
        if ($$0.isHandsBusy()) {
            this.mainHandHeight = Mth.clamp(this.mainHandHeight - 0.4f, 0.0f, 1.0f);
            this.offHandHeight = Mth.clamp(this.offHandHeight - 0.4f, 0.0f, 1.0f);
        } else {
            float $$3 = $$0.getAttackStrengthScale(1.0f);
            float $$4 = this.mainHandItem != $$1 ? 0.0f : $$3 * $$3 * $$3;
            float $$5 = this.offHandItem != $$2 ? 0.0f : 1.0f;
            this.mainHandHeight += Mth.clamp($$4 - this.mainHandHeight, -0.4f, 0.4f);
            this.offHandHeight += Mth.clamp($$5 - this.offHandHeight, -0.4f, 0.4f);
        }
        if (this.mainHandHeight < 0.1f) {
            this.mainHandItem = $$1;
        }
        if (this.offHandHeight < 0.1f) {
            this.offHandItem = $$2;
        }
    }

    public void itemUsed(InteractionHand $$0) {
        if ($$0 == InteractionHand.MAIN_HAND) {
            this.mainHandHeight = 0.0f;
        } else {
            this.offHandHeight = 0.0f;
        }
    }

    @VisibleForTesting
    static final class HandRenderSelection
    extends Enum<HandRenderSelection> {
        public static final /* enum */ HandRenderSelection RENDER_BOTH_HANDS = new HandRenderSelection(true, true);
        public static final /* enum */ HandRenderSelection RENDER_MAIN_HAND_ONLY = new HandRenderSelection(true, false);
        public static final /* enum */ HandRenderSelection RENDER_OFF_HAND_ONLY = new HandRenderSelection(false, true);
        final boolean renderMainHand;
        final boolean renderOffHand;
        private static final /* synthetic */ HandRenderSelection[] $VALUES;

        public static HandRenderSelection[] values() {
            return (HandRenderSelection[])$VALUES.clone();
        }

        public static HandRenderSelection valueOf(String $$0) {
            return Enum.valueOf(HandRenderSelection.class, $$0);
        }

        private HandRenderSelection(boolean $$0, boolean $$1) {
            this.renderMainHand = $$0;
            this.renderOffHand = $$1;
        }

        public static HandRenderSelection onlyForHand(InteractionHand $$0) {
            return $$0 == InteractionHand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
        }

        private static /* synthetic */ HandRenderSelection[] a() {
            return new HandRenderSelection[]{RENDER_BOTH_HANDS, RENDER_MAIN_HAND_ONLY, RENDER_OFF_HAND_ONLY};
        }

        static {
            $VALUES = HandRenderSelection.a();
        }
    }
}


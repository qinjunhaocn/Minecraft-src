/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;
import org.joml.Quaternionfc;

public class PlayerRenderer
extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
    public PlayerRenderer(EntityRendererProvider.Context $$0, boolean $$1) {
        super($$0, new PlayerModel($$0.bakeLayer($$1 ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), $$1), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel($$0.bakeLayer($$1 ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel($$0.bakeLayer($$1 ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)), $$0.getEquipmentRenderer()));
        this.addLayer(new PlayerItemInHandLayer<PlayerRenderState, PlayerModel>(this));
        this.addLayer(new ArrowLayer<PlayerModel>(this, $$0));
        this.addLayer(new Deadmau5EarsLayer(this, $$0.getModelSet()));
        this.addLayer(new CapeLayer(this, $$0.getModelSet(), $$0.getEquipmentAssets()));
        this.addLayer(new CustomHeadLayer<PlayerRenderState, PlayerModel>(this, $$0.getModelSet()));
        this.addLayer(new WingsLayer<PlayerRenderState, PlayerModel>(this, $$0.getModelSet(), $$0.getEquipmentRenderer()));
        this.addLayer(new ParrotOnShoulderLayer(this, $$0.getModelSet()));
        this.addLayer(new SpinAttackEffectLayer(this, $$0.getModelSet()));
        this.addLayer(new BeeStingerLayer<PlayerModel>(this, $$0));
    }

    @Override
    protected boolean shouldRenderLayers(PlayerRenderState $$0) {
        return !$$0.isSpectator;
    }

    @Override
    public Vec3 getRenderOffset(PlayerRenderState $$0) {
        Vec3 $$1 = super.getRenderOffset($$0);
        if ($$0.isCrouching) {
            return $$1.add(0.0, (double)($$0.scale * -2.0f) / 16.0, 0.0);
        }
        return $$1;
    }

    private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer $$0, HumanoidArm $$1) {
        ItemStack $$2 = $$0.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack $$3 = $$0.getItemInHand(InteractionHand.OFF_HAND);
        HumanoidModel.ArmPose $$4 = PlayerRenderer.getArmPose($$0, $$2, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose $$5 = PlayerRenderer.getArmPose($$0, $$3, InteractionHand.OFF_HAND);
        if ($$4.isTwoHanded()) {
            HumanoidModel.ArmPose armPose = $$5 = $$3.isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }
        if ($$0.getMainArm() == $$1) {
            return $$4;
        }
        return $$5;
    }

    private static HumanoidModel.ArmPose getArmPose(Player $$0, ItemStack $$1, InteractionHand $$2) {
        if ($$1.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        if (!$$0.swinging && $$1.is(Items.CROSSBOW) && CrossbowItem.isCharged($$1)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        if ($$0.getUsedItemHand() == $$2 && $$0.getUseItemRemainingTicks() > 0) {
            ItemUseAnimation $$3 = $$1.getUseAnimation();
            if ($$3 == ItemUseAnimation.BLOCK) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            if ($$3 == ItemUseAnimation.BOW) {
                return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
            if ($$3 == ItemUseAnimation.SPEAR) {
                return HumanoidModel.ArmPose.THROW_SPEAR;
            }
            if ($$3 == ItemUseAnimation.CROSSBOW) {
                return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
            if ($$3 == ItemUseAnimation.SPYGLASS) {
                return HumanoidModel.ArmPose.SPYGLASS;
            }
            if ($$3 == ItemUseAnimation.TOOT_HORN) {
                return HumanoidModel.ArmPose.TOOT_HORN;
            }
            if ($$3 == ItemUseAnimation.BRUSH) {
                return HumanoidModel.ArmPose.BRUSH;
            }
        }
        return HumanoidModel.ArmPose.ITEM;
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerRenderState $$0) {
        return $$0.skin.texture();
    }

    @Override
    protected void scale(PlayerRenderState $$0, PoseStack $$1) {
        float $$2 = 0.9375f;
        $$1.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderNameTag(PlayerRenderState $$0, Component $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        $$2.pushPose();
        if ($$0.scoreText != null) {
            super.renderNameTag($$0, $$0.scoreText, $$2, $$3, $$4);
            Objects.requireNonNull(this.getFont());
            $$2.translate(0.0f, 9.0f * 1.15f * 0.025f, 0.0f);
        }
        super.renderNameTag($$0, $$1, $$2, $$3, $$4);
        $$2.popPose();
    }

    @Override
    public PlayerRenderState createRenderState() {
        return new PlayerRenderState();
    }

    @Override
    public void extractRenderState(AbstractClientPlayer $$0, PlayerRenderState $$1, float $$2) {
        ItemStack $$7;
        super.extractRenderState($$0, $$1, $$2);
        HumanoidMobRenderer.extractHumanoidRenderState($$0, $$1, $$2, this.itemModelResolver);
        $$1.leftArmPose = PlayerRenderer.getArmPose($$0, HumanoidArm.LEFT);
        $$1.rightArmPose = PlayerRenderer.getArmPose($$0, HumanoidArm.RIGHT);
        $$1.skin = $$0.getSkin();
        $$1.arrowCount = $$0.getArrowCount();
        $$1.stingerCount = $$0.getStingerCount();
        $$1.useItemRemainingTicks = $$0.getUseItemRemainingTicks();
        $$1.swinging = $$0.swinging;
        $$1.isSpectator = $$0.isSpectator();
        $$1.showHat = $$0.isModelPartShown(PlayerModelPart.HAT);
        $$1.showJacket = $$0.isModelPartShown(PlayerModelPart.JACKET);
        $$1.showLeftPants = $$0.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        $$1.showRightPants = $$0.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        $$1.showLeftSleeve = $$0.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        $$1.showRightSleeve = $$0.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
        $$1.showCape = $$0.isModelPartShown(PlayerModelPart.CAPE);
        PlayerRenderer.extractFlightData($$0, $$1, $$2);
        PlayerRenderer.extractCapeState($$0, $$1, $$2);
        if ($$1.distanceToCameraSq < 100.0) {
            Scoreboard $$3 = $$0.getScoreboard();
            Objective $$4 = $$3.getDisplayObjective(DisplaySlot.BELOW_NAME);
            if ($$4 != null) {
                ReadOnlyScoreInfo $$5 = $$3.getPlayerScoreInfo($$0, $$4);
                MutableComponent $$6 = ReadOnlyScoreInfo.safeFormatValue($$5, $$4.numberFormatOrDefault(StyledFormat.NO_STYLE));
                $$1.scoreText = Component.empty().append($$6).append(CommonComponents.SPACE).append($$4.getDisplayName());
            } else {
                $$1.scoreText = null;
            }
        } else {
            $$1.scoreText = null;
        }
        $$1.parrotOnLeftShoulder = PlayerRenderer.getParrotOnShoulder($$0, true);
        $$1.parrotOnRightShoulder = PlayerRenderer.getParrotOnShoulder($$0, false);
        $$1.id = $$0.getId();
        $$1.name = $$0.getGameProfile().getName();
        $$1.heldOnHead.clear();
        if ($$1.isUsingItem && ($$7 = $$0.getItemInHand($$1.useItemHand)).is(Items.SPYGLASS)) {
            this.itemModelResolver.updateForLiving($$1.heldOnHead, $$7, ItemDisplayContext.HEAD, $$0);
        }
    }

    private static void extractFlightData(AbstractClientPlayer $$0, PlayerRenderState $$1, float $$2) {
        $$1.fallFlyingTimeInTicks = (float)$$0.getFallFlyingTicks() + $$2;
        Vec3 $$3 = $$0.getViewVector($$2);
        Vec3 $$4 = $$0.getDeltaMovementLerped($$2);
        if ($$4.horizontalDistanceSqr() > (double)1.0E-5f && $$3.horizontalDistanceSqr() > (double)1.0E-5f) {
            $$1.shouldApplyFlyingYRot = true;
            double $$5 = $$4.horizontal().normalize().dot($$3.horizontal().normalize());
            double $$6 = $$4.x * $$3.z - $$4.z * $$3.x;
            $$1.flyingYRot = (float)(Math.signum($$6) * Math.acos(Math.min(1.0, Math.abs($$5))));
        } else {
            $$1.shouldApplyFlyingYRot = false;
            $$1.flyingYRot = 0.0f;
        }
    }

    private static void extractCapeState(AbstractClientPlayer $$0, PlayerRenderState $$1, float $$2) {
        double $$3 = Mth.lerp((double)$$2, $$0.xCloakO, $$0.xCloak) - Mth.lerp((double)$$2, $$0.xo, $$0.getX());
        double $$4 = Mth.lerp((double)$$2, $$0.yCloakO, $$0.yCloak) - Mth.lerp((double)$$2, $$0.yo, $$0.getY());
        double $$5 = Mth.lerp((double)$$2, $$0.zCloakO, $$0.zCloak) - Mth.lerp((double)$$2, $$0.zo, $$0.getZ());
        float $$6 = Mth.rotLerp($$2, $$0.yBodyRotO, $$0.yBodyRot);
        double $$7 = Mth.sin($$6 * ((float)Math.PI / 180));
        double $$8 = -Mth.cos($$6 * ((float)Math.PI / 180));
        $$1.capeFlap = (float)$$4 * 10.0f;
        $$1.capeFlap = Mth.clamp($$1.capeFlap, -6.0f, 32.0f);
        $$1.capeLean = (float)($$3 * $$7 + $$5 * $$8) * 100.0f;
        $$1.capeLean *= 1.0f - $$1.fallFlyingScale();
        $$1.capeLean = Mth.clamp($$1.capeLean, 0.0f, 150.0f);
        $$1.capeLean2 = (float)($$3 * $$8 - $$5 * $$7) * 100.0f;
        $$1.capeLean2 = Mth.clamp($$1.capeLean2, -20.0f, 20.0f);
        float $$9 = Mth.lerp($$2, $$0.oBob, $$0.bob);
        float $$10 = Mth.lerp($$2, $$0.walkDistO, $$0.walkDist);
        $$1.capeFlap += Mth.sin($$10 * 6.0f) * 32.0f * $$9;
    }

    @Nullable
    private static Parrot.Variant getParrotOnShoulder(AbstractClientPlayer $$0, boolean $$1) {
        CompoundTag $$2;
        CompoundTag compoundTag = $$2 = $$1 ? $$0.getShoulderEntityLeft() : $$0.getShoulderEntityRight();
        if ($$2.isEmpty()) {
            return null;
        }
        EntityType $$3 = $$2.read("id", EntityType.CODEC).orElse(null);
        if ($$3 == EntityType.PARROT) {
            return $$2.read("Variant", Parrot.Variant.LEGACY_CODEC).orElse(Parrot.Variant.RED_BLUE);
        }
        return null;
    }

    public void renderRightHand(PoseStack $$0, MultiBufferSource $$1, int $$2, ResourceLocation $$3, boolean $$4) {
        this.renderHand($$0, $$1, $$2, $$3, ((PlayerModel)this.model).rightArm, $$4);
    }

    public void renderLeftHand(PoseStack $$0, MultiBufferSource $$1, int $$2, ResourceLocation $$3, boolean $$4) {
        this.renderHand($$0, $$1, $$2, $$3, ((PlayerModel)this.model).leftArm, $$4);
    }

    private void renderHand(PoseStack $$0, MultiBufferSource $$1, int $$2, ResourceLocation $$3, ModelPart $$4, boolean $$5) {
        PlayerModel $$6 = (PlayerModel)this.getModel();
        $$4.resetPose();
        $$4.visible = true;
        $$6.leftSleeve.visible = $$5;
        $$6.rightSleeve.visible = $$5;
        $$6.leftArm.zRot = -0.1f;
        $$6.rightArm.zRot = 0.1f;
        $$4.render($$0, $$1.getBuffer(RenderType.entityTranslucent($$3)), $$2, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected void setupRotations(PlayerRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        float $$4 = $$0.swimAmount;
        float $$5 = $$0.xRot;
        if ($$0.isFallFlying) {
            super.setupRotations($$0, $$1, $$2, $$3);
            float $$6 = $$0.fallFlyingScale();
            if (!$$0.isAutoSpinAttack) {
                $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$6 * (-90.0f - $$5)));
            }
            if ($$0.shouldApplyFlyingYRot) {
                $$1.mulPose((Quaternionfc)Axis.YP.rotation($$0.flyingYRot));
            }
        } else if ($$4 > 0.0f) {
            super.setupRotations($$0, $$1, $$2, $$3);
            float $$7 = $$0.isInWater ? -90.0f - $$5 : -90.0f;
            float $$8 = Mth.lerp($$4, 0.0f, $$7);
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$8));
            if ($$0.isVisuallySwimming) {
                $$1.translate(0.0f, -1.0f, 0.3f);
            }
        } else {
            super.setupRotations($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((PlayerRenderState)livingEntityRenderState);
    }

    @Override
    protected /* synthetic */ boolean shouldRenderLayers(LivingEntityRenderState livingEntityRenderState) {
        return this.shouldRenderLayers((PlayerRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    public /* synthetic */ Vec3 getRenderOffset(EntityRenderState entityRenderState) {
        return this.getRenderOffset((PlayerRenderState)entityRenderState);
    }
}


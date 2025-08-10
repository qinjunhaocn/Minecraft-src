/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.joml.Quaternionfc;

public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends EntityRenderer<T, S>
implements RenderLayerParent<S, M> {
    private static final float EYE_BED_OFFSET = 0.1f;
    protected M model;
    protected final ItemModelResolver itemModelResolver;
    protected final List<RenderLayer<S, M>> layers = Lists.newArrayList();

    public LivingEntityRenderer(EntityRendererProvider.Context $$0, M $$1, float $$2) {
        super($$0);
        this.itemModelResolver = $$0.getItemModelResolver();
        this.model = $$1;
        this.shadowRadius = $$2;
    }

    protected final boolean addLayer(RenderLayer<S, M> $$0) {
        return this.layers.add($$0);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    protected AABB getBoundingBoxForCulling(T $$0) {
        AABB $$1 = super.getBoundingBoxForCulling($$0);
        if (((LivingEntity)$$0).getItemBySlot(EquipmentSlot.HEAD).is(Items.DRAGON_HEAD)) {
            float $$2 = 0.5f;
            return $$1.inflate(0.5, 0.5, 0.5);
        }
        return $$1;
    }

    @Override
    public void render(S $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        Direction $$4;
        $$1.pushPose();
        if (((LivingEntityRenderState)$$0).hasPose(Pose.SLEEPING) && ($$4 = ((LivingEntityRenderState)$$0).bedOrientation) != null) {
            float $$5 = ((LivingEntityRenderState)$$0).eyeHeight - 0.1f;
            $$1.translate((float)(-$$4.getStepX()) * $$5, 0.0f, (float)(-$$4.getStepZ()) * $$5);
        }
        float $$6 = ((LivingEntityRenderState)$$0).scale;
        $$1.scale($$6, $$6, $$6);
        this.setupRotations($$0, $$1, ((LivingEntityRenderState)$$0).bodyRot, $$6);
        $$1.scale(-1.0f, -1.0f, 1.0f);
        this.scale($$0, $$1);
        $$1.translate(0.0f, -1.501f, 0.0f);
        ((EntityModel)this.model).setupAnim($$0);
        boolean $$7 = this.isBodyVisible($$0);
        boolean $$8 = !$$7 && !((LivingEntityRenderState)$$0).isInvisibleToPlayer;
        RenderType $$9 = this.getRenderType($$0, $$7, $$8, ((LivingEntityRenderState)$$0).appearsGlowing);
        if ($$9 != null) {
            VertexConsumer $$10 = $$2.getBuffer($$9);
            int $$11 = LivingEntityRenderer.getOverlayCoords($$0, this.getWhiteOverlayProgress($$0));
            int $$12 = $$8 ? 0x26FFFFFF : -1;
            int $$13 = ARGB.multiply($$12, this.getModelTint($$0));
            ((Model)this.model).renderToBuffer($$1, $$10, $$3, $$11, $$13);
        }
        if (this.shouldRenderLayers($$0)) {
            for (RenderLayer<S, M> $$14 : this.layers) {
                $$14.render($$1, $$2, $$3, $$0, ((LivingEntityRenderState)$$0).yRot, ((LivingEntityRenderState)$$0).xRot);
            }
        }
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    protected boolean shouldRenderLayers(S $$0) {
        return true;
    }

    protected int getModelTint(S $$0) {
        return -1;
    }

    public abstract ResourceLocation getTextureLocation(S var1);

    @Nullable
    protected RenderType getRenderType(S $$0, boolean $$1, boolean $$2, boolean $$3) {
        ResourceLocation $$4 = this.getTextureLocation($$0);
        if ($$2) {
            return RenderType.itemEntityTranslucentCull($$4);
        }
        if ($$1) {
            return ((Model)this.model).renderType($$4);
        }
        if ($$3) {
            return RenderType.outline($$4);
        }
        return null;
    }

    public static int getOverlayCoords(LivingEntityRenderState $$0, float $$1) {
        return OverlayTexture.pack(OverlayTexture.u($$1), OverlayTexture.v($$0.hasRedOverlay));
    }

    protected boolean isBodyVisible(S $$0) {
        return !((LivingEntityRenderState)$$0).isInvisible;
    }

    private static float sleepDirectionToRotation(Direction $$0) {
        switch ($$0) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean isShaking(S $$0) {
        return ((LivingEntityRenderState)$$0).isFullyFrozen;
    }

    protected void setupRotations(S $$0, PoseStack $$1, float $$2, float $$3) {
        if (this.isShaking($$0)) {
            $$2 += (float)(Math.cos((float)Mth.floor(((LivingEntityRenderState)$$0).ageInTicks) * 3.25f) * Math.PI * (double)0.4f);
        }
        if (!((LivingEntityRenderState)$$0).hasPose(Pose.SLEEPING)) {
            $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - $$2));
        }
        if (((LivingEntityRenderState)$$0).deathTime > 0.0f) {
            float $$4 = (((LivingEntityRenderState)$$0).deathTime - 1.0f) / 20.0f * 1.6f;
            if (($$4 = Mth.sqrt($$4)) > 1.0f) {
                $$4 = 1.0f;
            }
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$4 * this.getFlipDegrees()));
        } else if (((LivingEntityRenderState)$$0).isAutoSpinAttack) {
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0f - ((LivingEntityRenderState)$$0).xRot));
            $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(((LivingEntityRenderState)$$0).ageInTicks * -75.0f));
        } else if (((LivingEntityRenderState)$$0).hasPose(Pose.SLEEPING)) {
            Direction $$5 = ((LivingEntityRenderState)$$0).bedOrientation;
            float $$6 = $$5 != null ? LivingEntityRenderer.sleepDirectionToRotation($$5) : $$2;
            $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$6));
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(this.getFlipDegrees()));
            $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(270.0f));
        } else if (((LivingEntityRenderState)$$0).isUpsideDown) {
            $$1.translate(0.0f, (((LivingEntityRenderState)$$0).boundingBoxHeight + 0.1f) / $$3, 0.0f);
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0f));
        }
    }

    protected float getFlipDegrees() {
        return 90.0f;
    }

    protected float getWhiteOverlayProgress(S $$0) {
        return 0.0f;
    }

    protected void scale(S $$0, PoseStack $$1) {
    }

    @Override
    protected boolean shouldShowName(T $$0, double $$1) {
        boolean $$5;
        if (((Entity)$$0).isDiscrete()) {
            float $$2 = 32.0f;
            if ($$1 >= 1024.0) {
                return false;
            }
        }
        Minecraft $$3 = Minecraft.getInstance();
        LocalPlayer $$4 = $$3.player;
        boolean bl = $$5 = !((Entity)$$0).isInvisibleTo($$4);
        if ($$0 != $$4) {
            PlayerTeam $$6 = ((Entity)$$0).getTeam();
            PlayerTeam $$7 = $$4.getTeam();
            if ($$6 != null) {
                Team.Visibility $$8 = ((Team)$$6).getNameTagVisibility();
                switch ($$8) {
                    case ALWAYS: {
                        return $$5;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return $$7 == null ? $$5 : $$6.isAlliedTo($$7) && (((Team)$$6).canSeeFriendlyInvisibles() || $$5);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return $$7 == null ? $$5 : !$$6.isAlliedTo($$7) && $$5;
                    }
                }
                return true;
            }
        }
        return Minecraft.renderNames() && $$0 != $$3.getCameraEntity() && $$5 && !((Entity)$$0).isVehicle();
    }

    public static boolean isEntityUpsideDown(LivingEntity $$0) {
        String $$1;
        if (($$0 instanceof Player || $$0.hasCustomName()) && ("Dinnerbone".equals($$1 = ChatFormatting.stripFormatting($$0.getName().getString())) || "Grumm".equals($$1))) {
            Player $$2;
            return !($$0 instanceof Player) || ($$2 = (Player)$$0).isModelPartShown(PlayerModelPart.CAPE);
        }
        return false;
    }

    @Override
    protected float getShadowRadius(S $$0) {
        return super.getShadowRadius($$0) * ((LivingEntityRenderState)$$0).scale;
    }

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        BlockItem $$6;
        super.extractRenderState($$0, $$1, $$2);
        float $$3 = Mth.rotLerp($$2, ((LivingEntity)$$0).yHeadRotO, ((LivingEntity)$$0).yHeadRot);
        ((LivingEntityRenderState)$$1).bodyRot = LivingEntityRenderer.solveBodyRot($$0, $$3, $$2);
        ((LivingEntityRenderState)$$1).yRot = Mth.wrapDegrees($$3 - ((LivingEntityRenderState)$$1).bodyRot);
        ((LivingEntityRenderState)$$1).xRot = ((Entity)$$0).getXRot($$2);
        ((LivingEntityRenderState)$$1).customName = ((Entity)$$0).getCustomName();
        ((LivingEntityRenderState)$$1).isUpsideDown = LivingEntityRenderer.isEntityUpsideDown($$0);
        if (((LivingEntityRenderState)$$1).isUpsideDown) {
            ((LivingEntityRenderState)$$1).xRot *= -1.0f;
            ((LivingEntityRenderState)$$1).yRot *= -1.0f;
        }
        if (!((Entity)$$0).isPassenger() && ((LivingEntity)$$0).isAlive()) {
            ((LivingEntityRenderState)$$1).walkAnimationPos = ((LivingEntity)$$0).walkAnimation.position($$2);
            ((LivingEntityRenderState)$$1).walkAnimationSpeed = ((LivingEntity)$$0).walkAnimation.speed($$2);
        } else {
            ((LivingEntityRenderState)$$1).walkAnimationPos = 0.0f;
            ((LivingEntityRenderState)$$1).walkAnimationSpeed = 0.0f;
        }
        Entity entity = ((Entity)$$0).getVehicle();
        if (entity instanceof LivingEntity) {
            LivingEntity $$4 = (LivingEntity)entity;
            ((LivingEntityRenderState)$$1).wornHeadAnimationPos = $$4.walkAnimation.position($$2);
        } else {
            ((LivingEntityRenderState)$$1).wornHeadAnimationPos = ((LivingEntityRenderState)$$1).walkAnimationPos;
        }
        ((LivingEntityRenderState)$$1).scale = ((LivingEntity)$$0).getScale();
        ((LivingEntityRenderState)$$1).ageScale = ((LivingEntity)$$0).getAgeScale();
        ((LivingEntityRenderState)$$1).pose = ((Entity)$$0).getPose();
        ((LivingEntityRenderState)$$1).bedOrientation = ((LivingEntity)$$0).getBedOrientation();
        if (((LivingEntityRenderState)$$1).bedOrientation != null) {
            ((LivingEntityRenderState)$$1).eyeHeight = ((Entity)$$0).getEyeHeight(Pose.STANDING);
        }
        ((LivingEntityRenderState)$$1).isFullyFrozen = ((Entity)$$0).isFullyFrozen();
        ((LivingEntityRenderState)$$1).isBaby = ((LivingEntity)$$0).isBaby();
        ((LivingEntityRenderState)$$1).isInWater = ((Entity)$$0).isInWater();
        ((LivingEntityRenderState)$$1).isAutoSpinAttack = ((LivingEntity)$$0).isAutoSpinAttack();
        ((LivingEntityRenderState)$$1).hasRedOverlay = ((LivingEntity)$$0).hurtTime > 0 || ((LivingEntity)$$0).deathTime > 0;
        ItemStack $$5 = ((LivingEntity)$$0).getItemBySlot(EquipmentSlot.HEAD);
        FeatureElement featureElement = $$5.getItem();
        if (featureElement instanceof BlockItem && (featureElement = ($$6 = (BlockItem)featureElement).getBlock()) instanceof AbstractSkullBlock) {
            AbstractSkullBlock $$7 = (AbstractSkullBlock)featureElement;
            ((LivingEntityRenderState)$$1).wornHeadType = $$7.getType();
            ((LivingEntityRenderState)$$1).wornHeadProfile = $$5.get(DataComponents.PROFILE);
            ((LivingEntityRenderState)$$1).headItem.clear();
        } else {
            ((LivingEntityRenderState)$$1).wornHeadType = null;
            ((LivingEntityRenderState)$$1).wornHeadProfile = null;
            if (!HumanoidArmorLayer.shouldRender($$5, EquipmentSlot.HEAD)) {
                this.itemModelResolver.updateForLiving(((LivingEntityRenderState)$$1).headItem, $$5, ItemDisplayContext.HEAD, (LivingEntity)$$0);
            } else {
                ((LivingEntityRenderState)$$1).headItem.clear();
            }
        }
        ((LivingEntityRenderState)$$1).deathTime = ((LivingEntity)$$0).deathTime > 0 ? (float)((LivingEntity)$$0).deathTime + $$2 : 0.0f;
        Minecraft $$8 = Minecraft.getInstance();
        ((LivingEntityRenderState)$$1).isInvisibleToPlayer = ((LivingEntityRenderState)$$1).isInvisible && ((Entity)$$0).isInvisibleTo($$8.player);
        ((LivingEntityRenderState)$$1).appearsGlowing = $$8.shouldEntityAppearGlowing((Entity)$$0);
    }

    @Override
    protected void extractAdditionalHitboxes(T $$0, ImmutableList.Builder<HitboxRenderState> $$1, float $$2) {
        AABB $$3 = ((Entity)$$0).getBoundingBox();
        float $$4 = 0.01f;
        HitboxRenderState $$5 = new HitboxRenderState($$3.minX - ((Entity)$$0).getX(), ((Entity)$$0).getEyeHeight() - 0.01f, $$3.minZ - ((Entity)$$0).getZ(), $$3.maxX - ((Entity)$$0).getX(), ((Entity)$$0).getEyeHeight() + 0.01f, $$3.maxZ - ((Entity)$$0).getZ(), 1.0f, 0.0f, 0.0f);
        $$1.add((Object)$$5);
    }

    private static float solveBodyRot(LivingEntity $$0, float $$1, float $$2) {
        Entity entity = $$0.getVehicle();
        if (entity instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)entity;
            float $$4 = Mth.rotLerp($$2, $$3.yBodyRotO, $$3.yBodyRot);
            float $$5 = 85.0f;
            float $$6 = Mth.clamp(Mth.wrapDegrees($$1 - $$4), -85.0f, 85.0f);
            $$4 = $$1 - $$6;
            if (Math.abs($$6) > 50.0f) {
                $$4 += $$6 * 0.2f;
            }
            return $$4;
        }
        return Mth.rotLerp($$2, $$0.yBodyRotO, $$0.yBodyRot);
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
        return this.getShadowRadius((S)((LivingEntityRenderState)entityRenderState));
    }
}


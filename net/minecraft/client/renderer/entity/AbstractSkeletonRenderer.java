/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.Items;

public abstract class AbstractSkeletonRenderer<T extends AbstractSkeleton, S extends SkeletonRenderState>
extends HumanoidMobRenderer<T, S, SkeletonModel<S>> {
    public AbstractSkeletonRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1, ModelLayerLocation $$2, ModelLayerLocation $$3) {
        this($$0, $$2, $$3, new SkeletonModel($$0.bakeLayer($$1)));
    }

    public AbstractSkeletonRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1, ModelLayerLocation $$2, SkeletonModel<S> $$3) {
        super($$0, $$3, 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new SkeletonModel($$0.bakeLayer($$1)), new SkeletonModel($$0.bakeLayer($$2)), $$0.getEquipmentRenderer()));
    }

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ((SkeletonRenderState)$$1).isAggressive = ((Mob)$$0).isAggressive();
        ((SkeletonRenderState)$$1).isShaking = ((AbstractSkeleton)$$0).isShaking();
        ((SkeletonRenderState)$$1).isHoldingBow = ((LivingEntity)$$0).getMainHandItem().is(Items.BOW);
    }

    @Override
    protected boolean isShaking(S $$0) {
        return ((SkeletonRenderState)$$0).isShaking;
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(AbstractSkeleton $$0, HumanoidArm $$1) {
        if ($$0.getMainArm() == $$1 && $$0.isAggressive() && $$0.getMainHandItem().is(Items.BOW)) {
            return HumanoidModel.ArmPose.BOW_AND_ARROW;
        }
        return HumanoidModel.ArmPose.EMPTY;
    }
}


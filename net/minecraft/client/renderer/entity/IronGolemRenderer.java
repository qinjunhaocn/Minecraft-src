/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import org.joml.Quaternionfc;

public class IronGolemRenderer
extends MobRenderer<IronGolem, IronGolemRenderState, IronGolemModel> {
    private static final ResourceLocation GOLEM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem.png");

    public IronGolemRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new IronGolemModel($$0.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7f);
        this.addLayer(new IronGolemCrackinessLayer(this));
        this.addLayer(new IronGolemFlowerLayer(this, $$0.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(IronGolemRenderState $$0) {
        return GOLEM_LOCATION;
    }

    @Override
    public IronGolemRenderState createRenderState() {
        return new IronGolemRenderState();
    }

    @Override
    public void extractRenderState(IronGolem $$0, IronGolemRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.attackTicksRemaining = (float)$$0.getAttackAnimationTick() > 0.0f ? (float)$$0.getAttackAnimationTick() - $$2 : 0.0f;
        $$1.offerFlowerTick = $$0.getOfferFlowerTick();
        $$1.crackiness = $$0.getCrackiness();
    }

    @Override
    protected void setupRotations(IronGolemRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        if ((double)$$0.walkAnimationSpeed < 0.01) {
            return;
        }
        float $$4 = 13.0f;
        float $$5 = $$0.walkAnimationPos + 6.0f;
        float $$6 = (Math.abs($$5 % 13.0f - 6.5f) - 3.25f) / 3.25f;
        $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(6.5f * $$6));
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((IronGolemRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


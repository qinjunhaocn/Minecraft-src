/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SquidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;
import org.joml.Quaternionfc;

public class SquidRenderer<T extends Squid>
extends AgeableMobRenderer<T, SquidRenderState, SquidModel> {
    private static final ResourceLocation SQUID_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/squid/squid.png");

    public SquidRenderer(EntityRendererProvider.Context $$0, SquidModel $$1, SquidModel $$2) {
        super($$0, $$1, $$2, 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(SquidRenderState $$0) {
        return SQUID_LOCATION;
    }

    @Override
    public SquidRenderState createRenderState() {
        return new SquidRenderState();
    }

    @Override
    public void extractRenderState(T $$0, SquidRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.tentacleAngle = Mth.lerp($$2, ((Squid)$$0).oldTentacleAngle, ((Squid)$$0).tentacleAngle);
        $$1.xBodyRot = Mth.lerp($$2, ((Squid)$$0).xBodyRotO, ((Squid)$$0).xBodyRot);
        $$1.zBodyRot = Mth.lerp($$2, ((Squid)$$0).zBodyRotO, ((Squid)$$0).zBodyRot);
    }

    @Override
    protected void setupRotations(SquidRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        $$1.translate(0.0f, $$0.isBaby ? 0.25f : 0.5f, 0.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - $$2));
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$0.xBodyRot));
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$0.zBodyRot));
        $$1.translate(0.0f, $$0.isBaby ? -0.6f : -1.2f, 0.0f);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SquidRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cod;
import org.joml.Quaternionfc;

public class CodRenderer
extends MobRenderer<Cod, LivingEntityRenderState, CodModel> {
    private static final ResourceLocation COD_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/cod.png");

    public CodRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CodModel($$0.bakeLayer(ModelLayers.COD)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntityRenderState $$0) {
        return COD_LOCATION;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void setupRotations(LivingEntityRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        float $$4 = 4.3f * Mth.sin(0.6f * $$0.ageInTicks);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$4));
        if (!$$0.isInWater) {
            $$1.translate(0.1f, 0.1f, -0.1f);
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(90.0f));
        }
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


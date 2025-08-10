/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SalmonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Salmon;
import org.joml.Quaternionfc;

public class SalmonRenderer
extends MobRenderer<Salmon, SalmonRenderState, SalmonModel> {
    private static final ResourceLocation SALMON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/salmon.png");
    private final SalmonModel smallSalmonModel;
    private final SalmonModel mediumSalmonModel;
    private final SalmonModel largeSalmonModel;

    public SalmonRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SalmonModel($$0.bakeLayer(ModelLayers.SALMON)), 0.4f);
        this.smallSalmonModel = new SalmonModel($$0.bakeLayer(ModelLayers.SALMON_SMALL));
        this.mediumSalmonModel = new SalmonModel($$0.bakeLayer(ModelLayers.SALMON));
        this.largeSalmonModel = new SalmonModel($$0.bakeLayer(ModelLayers.SALMON_LARGE));
    }

    @Override
    public void extractRenderState(Salmon $$0, SalmonRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.variant = $$0.getVariant();
    }

    @Override
    public ResourceLocation getTextureLocation(SalmonRenderState $$0) {
        return SALMON_LOCATION;
    }

    @Override
    public SalmonRenderState createRenderState() {
        return new SalmonRenderState();
    }

    @Override
    protected void setupRotations(SalmonRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        float $$4 = 1.0f;
        float $$5 = 1.0f;
        if (!$$0.isInWater) {
            $$4 = 1.3f;
            $$5 = 1.7f;
        }
        float $$6 = $$4 * 4.3f * Mth.sin($$5 * 0.6f * $$0.ageInTicks);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$6));
        if (!$$0.isInWater) {
            $$1.translate(0.2f, 0.1f, 0.0f);
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(90.0f));
        }
    }

    @Override
    public void render(SalmonRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        this.model = $$0.variant == Salmon.Variant.SMALL ? this.smallSalmonModel : ($$0.variant == Salmon.Variant.LARGE ? this.largeSalmonModel : this.mediumSalmonModel);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SalmonRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


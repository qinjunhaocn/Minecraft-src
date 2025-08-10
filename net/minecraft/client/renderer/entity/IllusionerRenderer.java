/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.IllusionerRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class IllusionerRenderer
extends IllagerRenderer<Illusioner, IllusionerRenderState> {
    private static final ResourceLocation ILLUSIONER = ResourceLocation.withDefaultNamespace("textures/entity/illager/illusioner.png");

    public IllusionerRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new IllagerModel($$0.bakeLayer(ModelLayers.ILLUSIONER)), 0.5f);
        this.addLayer(new ItemInHandLayer<IllusionerRenderState, IllagerModel<IllusionerRenderState>>(this, (RenderLayerParent)this){

            @Override
            public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, IllusionerRenderState $$3, float $$4, float $$5) {
                if ($$3.isCastingSpell || $$3.isAggressive) {
                    super.render($$0, $$1, $$2, $$3, $$4, $$5);
                }
            }
        });
        ((IllagerModel)this.model).getHat().visible = true;
    }

    @Override
    public ResourceLocation getTextureLocation(IllusionerRenderState $$0) {
        return ILLUSIONER;
    }

    @Override
    public IllusionerRenderState createRenderState() {
        return new IllusionerRenderState();
    }

    @Override
    public void extractRenderState(Illusioner $$0, IllusionerRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        Vec3[] $$3 = $$0.J($$2);
        $$1.illusionOffsets = Arrays.copyOf($$3, $$3.length);
        $$1.isCastingSpell = $$0.isCastingSpell();
    }

    @Override
    public void render(IllusionerRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if ($$0.isInvisible) {
            Vec3[] $$4 = $$0.illusionOffsets;
            for (int $$5 = 0; $$5 < $$4.length; ++$$5) {
                $$1.pushPose();
                $$1.translate($$4[$$5].x + (double)Mth.cos((float)$$5 + $$0.ageInTicks * 0.5f) * 0.025, $$4[$$5].y + (double)Mth.cos((float)$$5 + $$0.ageInTicks * 0.75f) * 0.0125, $$4[$$5].z + (double)Mth.cos((float)$$5 + $$0.ageInTicks * 0.7f) * 0.025);
                super.render($$0, $$1, $$2, $$3);
                $$1.popPose();
            }
        } else {
            super.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    protected boolean isBodyVisible(IllusionerRenderState $$0) {
        return true;
    }

    @Override
    protected AABB getBoundingBoxForCulling(Illusioner $$0) {
        return super.getBoundingBoxForCulling($$0).inflate(3.0, 0.0, 3.0);
    }

    @Override
    protected /* synthetic */ boolean isBodyVisible(LivingEntityRenderState livingEntityRenderState) {
        return this.isBodyVisible((IllusionerRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((IllusionerRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


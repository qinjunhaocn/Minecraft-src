/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.TropicalFish;
import org.joml.Quaternionfc;

public class TropicalFishRenderer
extends MobRenderer<TropicalFish, TropicalFishRenderState, EntityModel<TropicalFishRenderState>> {
    private final EntityModel<TropicalFishRenderState> modelA = this.getModel();
    private final EntityModel<TropicalFishRenderState> modelB;
    private static final ResourceLocation MODEL_A_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a.png");
    private static final ResourceLocation MODEL_B_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b.png");

    public TropicalFishRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new TropicalFishModelA($$0.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)), 0.15f);
        this.modelB = new TropicalFishModelB($$0.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));
        this.addLayer(new TropicalFishPatternLayer(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(TropicalFishRenderState $$0) {
        return switch ($$0.pattern.base()) {
            default -> throw new MatchException(null, null);
            case TropicalFish.Base.SMALL -> MODEL_A_TEXTURE;
            case TropicalFish.Base.LARGE -> MODEL_B_TEXTURE;
        };
    }

    @Override
    public TropicalFishRenderState createRenderState() {
        return new TropicalFishRenderState();
    }

    @Override
    public void extractRenderState(TropicalFish $$0, TropicalFishRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.pattern = $$0.getPattern();
        $$1.baseColor = $$0.getBaseColor().getTextureDiffuseColor();
        $$1.patternColor = $$0.getPatternColor().getTextureDiffuseColor();
    }

    @Override
    public void render(TropicalFishRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        this.model = switch ($$0.pattern.base()) {
            default -> throw new MatchException(null, null);
            case TropicalFish.Base.SMALL -> this.modelA;
            case TropicalFish.Base.LARGE -> this.modelB;
        };
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    protected int getModelTint(TropicalFishRenderState $$0) {
        return $$0.baseColor;
    }

    @Override
    protected void setupRotations(TropicalFishRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        float $$4 = 4.3f * Mth.sin(0.6f * $$0.ageInTicks);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$4));
        if (!$$0.isInWater) {
            $$1.translate(0.2f, 0.1f, 0.0f);
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(90.0f));
        }
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((TropicalFishRenderState)livingEntityRenderState);
    }

    @Override
    protected /* synthetic */ int getModelTint(LivingEntityRenderState livingEntityRenderState) {
        return this.getModelTint((TropicalFishRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


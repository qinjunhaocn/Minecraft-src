/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer
extends RenderLayer<TropicalFishRenderState, EntityModel<TropicalFishRenderState>> {
    private static final ResourceLocation KOB_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_1.png");
    private static final ResourceLocation SUNSTREAK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_2.png");
    private static final ResourceLocation SNOOPER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_3.png");
    private static final ResourceLocation DASHER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_4.png");
    private static final ResourceLocation BRINELY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_5.png");
    private static final ResourceLocation SPOTTY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_6.png");
    private static final ResourceLocation FLOPPER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_1.png");
    private static final ResourceLocation STRIPEY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_2.png");
    private static final ResourceLocation GLITTER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_3.png");
    private static final ResourceLocation BLOCKFISH_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_4.png");
    private static final ResourceLocation BETTY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_5.png");
    private static final ResourceLocation CLAYFISH_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_6.png");
    private final TropicalFishModelA modelA;
    private final TropicalFishModelB modelB;

    public TropicalFishPatternLayer(RenderLayerParent<TropicalFishRenderState, EntityModel<TropicalFishRenderState>> $$0, EntityModelSet $$1) {
        super($$0);
        this.modelA = new TropicalFishModelA($$1.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.modelB = new TropicalFishModelB($$1.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, TropicalFishRenderState $$3, float $$4, float $$5) {
        TropicalFish.Pattern $$6 = $$3.pattern;
        EntityModel $$7 = switch ($$6.base()) {
            default -> throw new MatchException(null, null);
            case TropicalFish.Base.SMALL -> this.modelA;
            case TropicalFish.Base.LARGE -> this.modelB;
        };
        ResourceLocation $$8 = switch ($$6) {
            default -> throw new MatchException(null, null);
            case TropicalFish.Pattern.KOB -> KOB_TEXTURE;
            case TropicalFish.Pattern.SUNSTREAK -> SUNSTREAK_TEXTURE;
            case TropicalFish.Pattern.SNOOPER -> SNOOPER_TEXTURE;
            case TropicalFish.Pattern.DASHER -> DASHER_TEXTURE;
            case TropicalFish.Pattern.BRINELY -> BRINELY_TEXTURE;
            case TropicalFish.Pattern.SPOTTY -> SPOTTY_TEXTURE;
            case TropicalFish.Pattern.FLOPPER -> FLOPPER_TEXTURE;
            case TropicalFish.Pattern.STRIPEY -> STRIPEY_TEXTURE;
            case TropicalFish.Pattern.GLITTER -> GLITTER_TEXTURE;
            case TropicalFish.Pattern.BLOCKFISH -> BLOCKFISH_TEXTURE;
            case TropicalFish.Pattern.BETTY -> BETTY_TEXTURE;
            case TropicalFish.Pattern.CLAYFISH -> CLAYFISH_TEXTURE;
        };
        TropicalFishPatternLayer.coloredCutoutModelCopyLayerRender($$7, $$8, $$0, $$1, $$2, $$3, $$3.patternColor);
    }
}


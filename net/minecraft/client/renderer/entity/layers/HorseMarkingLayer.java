/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Markings;

public class HorseMarkingLayer
extends RenderLayer<HorseRenderState, HorseModel> {
    private static final ResourceLocation INVISIBLE_TEXTURE = ResourceLocation.withDefaultNamespace("invisible");
    private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS = Maps.newEnumMap(Map.of((Object)((Object)Markings.NONE), (Object)INVISIBLE_TEXTURE, (Object)((Object)Markings.WHITE), (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_white.png"), (Object)((Object)Markings.WHITE_FIELD), (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitefield.png"), (Object)((Object)Markings.WHITE_DOTS), (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitedots.png"), (Object)((Object)Markings.BLACK_DOTS), (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_blackdots.png")));

    public HorseMarkingLayer(RenderLayerParent<HorseRenderState, HorseModel> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, HorseRenderState $$3, float $$4, float $$5) {
        ResourceLocation $$6 = LOCATION_BY_MARKINGS.get((Object)$$3.markings);
        if ($$6 == INVISIBLE_TEXTURE || $$3.isInvisible) {
            return;
        }
        VertexConsumer $$7 = $$1.getBuffer(RenderType.entityTranslucent($$6));
        ((HorseModel)this.getParentModel()).renderToBuffer($$0, $$7, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f));
    }
}


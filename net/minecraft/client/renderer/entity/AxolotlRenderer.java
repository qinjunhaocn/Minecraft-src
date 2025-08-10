/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AxolotlRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderer
extends AgeableMobRenderer<Axolotl, AxolotlRenderState, AxolotlModel> {
    private static final Map<Axolotl.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), $$0 -> {
        for (Axolotl.Variant $$1 : Axolotl.Variant.values()) {
            $$0.put($$1, ResourceLocation.withDefaultNamespace(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", $$1.getName())));
        }
    });

    public AxolotlRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new AxolotlModel($$0.bakeLayer(ModelLayers.AXOLOTL)), new AxolotlModel($$0.bakeLayer(ModelLayers.AXOLOTL_BABY)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(AxolotlRenderState $$0) {
        return TEXTURE_BY_TYPE.get($$0.variant);
    }

    @Override
    public AxolotlRenderState createRenderState() {
        return new AxolotlRenderState();
    }

    @Override
    public void extractRenderState(Axolotl $$0, AxolotlRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.variant = $$0.getVariant();
        $$1.playingDeadFactor = $$0.playingDeadAnimator.getFactor($$2);
        $$1.inWaterFactor = $$0.inWaterAnimator.getFactor($$2);
        $$1.onGroundFactor = $$0.onGroundAnimator.getFactor($$2);
        $$1.movingFactor = $$0.movingAnimator.getFactor($$2);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((AxolotlRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


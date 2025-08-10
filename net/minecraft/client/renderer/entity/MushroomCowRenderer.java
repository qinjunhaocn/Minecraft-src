/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.MushroomCowRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;

public class MushroomCowRenderer
extends AgeableMobRenderer<MushroomCow, MushroomCowRenderState, CowModel> {
    private static final Map<MushroomCow.Variant, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put(MushroomCow.Variant.BROWN, ResourceLocation.withDefaultNamespace("textures/entity/cow/brown_mooshroom.png"));
        $$0.put(MushroomCow.Variant.RED, ResourceLocation.withDefaultNamespace("textures/entity/cow/red_mooshroom.png"));
    });

    public MushroomCowRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CowModel($$0.bakeLayer(ModelLayers.MOOSHROOM)), new CowModel($$0.bakeLayer(ModelLayers.MOOSHROOM_BABY)), 0.7f);
        this.addLayer(new MushroomCowMushroomLayer(this, $$0.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(MushroomCowRenderState $$0) {
        return TEXTURES.get($$0.variant);
    }

    @Override
    public MushroomCowRenderState createRenderState() {
        return new MushroomCowRenderState();
    }

    @Override
    public void extractRenderState(MushroomCow $$0, MushroomCowRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.variant = $$0.getVariant();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((MushroomCowRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


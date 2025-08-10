/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.EquineSaddleModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

public final class HorseRenderer
extends AbstractHorseRenderer<Horse, HorseRenderState, HorseModel> {
    private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = Maps.newEnumMap(Map.of((Object)Variant.WHITE, (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_white.png"), (Object)Variant.CREAMY, (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_creamy.png"), (Object)Variant.CHESTNUT, (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_chestnut.png"), (Object)Variant.BROWN, (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_brown.png"), (Object)Variant.BLACK, (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_black.png"), (Object)Variant.GRAY, (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_gray.png"), (Object)Variant.DARK_BROWN, (Object)ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_darkbrown.png")));

    public HorseRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new HorseModel($$02.bakeLayer(ModelLayers.HORSE)), new HorseModel($$02.bakeLayer(ModelLayers.HORSE_BABY)));
        this.addLayer(new HorseMarkingLayer(this));
        this.addLayer(new SimpleEquipmentLayer<HorseRenderState, HorseModel, HorseModel>(this, $$02.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_BODY, $$0 -> $$0.bodyArmorItem, new HorseModel($$02.bakeLayer(ModelLayers.HORSE_ARMOR)), new HorseModel($$02.bakeLayer(ModelLayers.HORSE_BABY_ARMOR))));
        this.addLayer(new SimpleEquipmentLayer<HorseRenderState, HorseModel, EquineSaddleModel>(this, $$02.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_SADDLE, $$0 -> $$0.saddle, new EquineSaddleModel($$02.bakeLayer(ModelLayers.HORSE_SADDLE)), new EquineSaddleModel($$02.bakeLayer(ModelLayers.HORSE_BABY_SADDLE))));
    }

    @Override
    public ResourceLocation getTextureLocation(HorseRenderState $$0) {
        return LOCATION_BY_VARIANT.get($$0.variant);
    }

    @Override
    public HorseRenderState createRenderState() {
        return new HorseRenderState();
    }

    @Override
    public void extractRenderState(Horse $$0, HorseRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.variant = $$0.getVariant();
        $$1.markings = $$0.getMarkings();
        $$1.bodyArmorItem = $$0.getBodyArmorItem().copy();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((HorseRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


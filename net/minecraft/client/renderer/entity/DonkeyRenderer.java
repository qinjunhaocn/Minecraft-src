/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.DonkeyModel;
import net.minecraft.client.model.EquineSaddleModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class DonkeyRenderer<T extends AbstractChestedHorse>
extends AbstractHorseRenderer<T, DonkeyRenderState, DonkeyModel> {
    private final ResourceLocation texture;

    public DonkeyRenderer(EntityRendererProvider.Context $$02, Type $$1) {
        super($$02, new DonkeyModel($$02.bakeLayer($$1.model)), new DonkeyModel($$02.bakeLayer($$1.babyModel)));
        this.texture = $$1.texture;
        this.addLayer(new SimpleEquipmentLayer<DonkeyRenderState, DonkeyModel, EquineSaddleModel>(this, $$02.getEquipmentRenderer(), $$1.saddleLayer, $$0 -> $$0.saddle, new EquineSaddleModel($$02.bakeLayer($$1.saddleModel)), new EquineSaddleModel($$02.bakeLayer($$1.babySaddleModel))));
    }

    @Override
    public ResourceLocation getTextureLocation(DonkeyRenderState $$0) {
        return this.texture;
    }

    @Override
    public DonkeyRenderState createRenderState() {
        return new DonkeyRenderState();
    }

    @Override
    public void extractRenderState(T $$0, DonkeyRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.hasChest = ((AbstractChestedHorse)$$0).hasChest();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((DonkeyRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type DONKEY = new Type(ResourceLocation.withDefaultNamespace("textures/entity/horse/donkey.png"), ModelLayers.DONKEY, ModelLayers.DONKEY_BABY, EquipmentClientInfo.LayerType.DONKEY_SADDLE, ModelLayers.DONKEY_SADDLE, ModelLayers.DONKEY_BABY_SADDLE);
        public static final /* enum */ Type MULE = new Type(ResourceLocation.withDefaultNamespace("textures/entity/horse/mule.png"), ModelLayers.MULE, ModelLayers.MULE_BABY, EquipmentClientInfo.LayerType.MULE_SADDLE, ModelLayers.MULE_SADDLE, ModelLayers.MULE_BABY_SADDLE);
        final ResourceLocation texture;
        final ModelLayerLocation model;
        final ModelLayerLocation babyModel;
        final EquipmentClientInfo.LayerType saddleLayer;
        final ModelLayerLocation saddleModel;
        final ModelLayerLocation babySaddleModel;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(ResourceLocation $$0, ModelLayerLocation $$1, ModelLayerLocation $$2, EquipmentClientInfo.LayerType $$3, ModelLayerLocation $$4, ModelLayerLocation $$5) {
            this.texture = $$0;
            this.model = $$1;
            this.babyModel = $$2;
            this.saddleLayer = $$3;
            this.saddleModel = $$4;
            this.babySaddleModel = $$5;
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{DONKEY, MULE};
        }

        static {
            $VALUES = Type.a();
        }
    }
}


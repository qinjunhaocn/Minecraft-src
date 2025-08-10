/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AbstractEquineModel;
import net.minecraft.client.model.EquineSaddleModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class UndeadHorseRenderer
extends AbstractHorseRenderer<AbstractHorse, EquineRenderState, AbstractEquineModel<EquineRenderState>> {
    private final ResourceLocation texture;

    public UndeadHorseRenderer(EntityRendererProvider.Context $$02, Type $$1) {
        super($$02, new HorseModel($$02.bakeLayer($$1.model)), new HorseModel($$02.bakeLayer($$1.babyModel)));
        this.texture = $$1.texture;
        this.addLayer(new SimpleEquipmentLayer<EquineRenderState, AbstractEquineModel<EquineRenderState>, EquineSaddleModel>(this, $$02.getEquipmentRenderer(), $$1.saddleLayer, $$0 -> $$0.saddle, new EquineSaddleModel($$02.bakeLayer($$1.saddleModel)), new EquineSaddleModel($$02.bakeLayer($$1.babySaddleModel))));
    }

    @Override
    public ResourceLocation getTextureLocation(EquineRenderState $$0) {
        return this.texture;
    }

    @Override
    public EquineRenderState createRenderState() {
        return new EquineRenderState();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((EquineRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type SKELETON = new Type(ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_skeleton.png"), ModelLayers.SKELETON_HORSE, ModelLayers.SKELETON_HORSE_BABY, EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, ModelLayers.SKELETON_HORSE_SADDLE, ModelLayers.SKELETON_HORSE_BABY_SADDLE);
        public static final /* enum */ Type ZOMBIE = new Type(ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_zombie.png"), ModelLayers.ZOMBIE_HORSE, ModelLayers.ZOMBIE_HORSE_BABY, EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, ModelLayers.ZOMBIE_HORSE_SADDLE, ModelLayers.ZOMBIE_HORSE_BABY_SADDLE);
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
            return new Type[]{SKELETON, ZOMBIE};
        }

        static {
            $VALUES = Type.a();
        }
    }
}


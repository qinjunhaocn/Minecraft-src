/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieVillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer
extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerRenderState, ZombieVillagerModel<ZombieVillagerRenderState>> {
    private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY)), 0.5f, VillagerRenderer.CUSTOM_HEAD_TRANSFORMS);
        this.addLayer(new HumanoidArmorLayer(this, new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY_INNER_ARMOR)), new ZombieVillagerModel($$0.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY_OUTER_ARMOR)), $$0.getEquipmentRenderer()));
        this.addLayer(new VillagerProfessionLayer<ZombieVillagerRenderState, ZombieVillagerModel<ZombieVillagerRenderState>>(this, $$0.getResourceManager(), "zombie_villager"));
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieVillagerRenderState $$0) {
        return ZOMBIE_VILLAGER_LOCATION;
    }

    @Override
    public ZombieVillagerRenderState createRenderState() {
        return new ZombieVillagerRenderState();
    }

    @Override
    public void extractRenderState(ZombieVillager $$0, ZombieVillagerRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isConverting = $$0.isConverting();
        $$1.villagerData = $$0.getVillagerData();
        $$1.isAggressive = $$0.isAggressive();
    }

    @Override
    protected boolean isShaking(ZombieVillagerRenderState $$0) {
        return super.isShaking($$0) || $$0.isConverting;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState livingEntityRenderState) {
        return this.isShaking((ZombieVillagerRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ZombieVillagerRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


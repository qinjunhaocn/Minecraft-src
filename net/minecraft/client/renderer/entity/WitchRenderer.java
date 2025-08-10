/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WitchRenderer
extends MobRenderer<Witch, WitchRenderState, WitchModel> {
    private static final ResourceLocation WITCH_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/witch.png");

    public WitchRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new WitchModel($$0.bakeLayer(ModelLayers.WITCH)), 0.5f);
        this.addLayer(new WitchItemLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(WitchRenderState $$0) {
        return WITCH_LOCATION;
    }

    @Override
    public WitchRenderState createRenderState() {
        return new WitchRenderState();
    }

    @Override
    public void extractRenderState(Witch $$0, WitchRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        HoldingEntityRenderState.extractHoldingEntityRenderState($$0, $$1, this.itemModelResolver);
        $$1.entityId = $$0.getId();
        ItemStack $$3 = $$0.getMainHandItem();
        $$1.isHoldingItem = !$$3.isEmpty();
        $$1.isHoldingPotion = $$3.is(Items.POTION);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((WitchRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


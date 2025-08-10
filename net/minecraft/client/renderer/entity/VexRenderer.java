/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;

public class VexRenderer
extends MobRenderer<Vex, VexRenderState, VexModel> {
    private static final ResourceLocation VEX_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex.png");
    private static final ResourceLocation VEX_CHARGING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex_charging.png");

    public VexRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new VexModel($$0.bakeLayer(ModelLayers.VEX)), 0.3f);
        this.addLayer(new ItemInHandLayer<VexRenderState, VexModel>(this));
    }

    @Override
    protected int getBlockLightLevel(Vex $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VexRenderState $$0) {
        if ($$0.isCharging) {
            return VEX_CHARGING_LOCATION;
        }
        return VEX_LOCATION;
    }

    @Override
    public VexRenderState createRenderState() {
        return new VexRenderState();
    }

    @Override
    public void extractRenderState(Vex $$0, VexRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ArmedEntityRenderState.extractArmedEntityRenderState($$0, $$1, this.itemModelResolver);
        $$1.isCharging = $$0.isCharging();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((VexRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


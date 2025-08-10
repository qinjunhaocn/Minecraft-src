/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WitherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossRenderer
extends MobRenderer<WitherBoss, WitherRenderState, WitherBossModel> {
    private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");

    public WitherBossRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new WitherBossModel($$0.bakeLayer(ModelLayers.WITHER)), 1.0f);
        this.addLayer(new WitherArmorLayer(this, $$0.getModelSet()));
    }

    @Override
    protected int getBlockLightLevel(WitherBoss $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(WitherRenderState $$0) {
        int $$1 = Mth.floor($$0.invulnerableTicks);
        if ($$1 <= 0 || $$1 <= 80 && $$1 / 5 % 2 == 1) {
            return WITHER_LOCATION;
        }
        return WITHER_INVULNERABLE_LOCATION;
    }

    @Override
    public WitherRenderState createRenderState() {
        return new WitherRenderState();
    }

    @Override
    protected void scale(WitherRenderState $$0, PoseStack $$1) {
        float $$2 = 2.0f;
        if ($$0.invulnerableTicks > 0.0f) {
            $$2 -= $$0.invulnerableTicks / 220.0f * 0.5f;
        }
        $$1.scale($$2, $$2, $$2);
    }

    @Override
    public void extractRenderState(WitherBoss $$0, WitherRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        int $$3 = $$0.getInvulnerableTicks();
        $$1.invulnerableTicks = $$3 > 0 ? (float)$$3 - $$2 : 0.0f;
        System.arraycopy($$0.t(), 0, $$1.xHeadRots, 0, $$1.xHeadRots.length);
        System.arraycopy($$0.n(), 0, $$1.yHeadRots, 0, $$1.yHeadRots.length);
        $$1.isPowered = $$0.isPowered();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((WitherRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


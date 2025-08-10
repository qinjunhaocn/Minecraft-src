/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.Vec3;

public class TheEndGatewayRenderer
extends TheEndPortalRenderer<TheEndGatewayBlockEntity> {
    private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_gateway_beam.png");

    public TheEndGatewayRenderer(BlockEntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(TheEndGatewayBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        if ($$0.isSpawning() || $$0.isCoolingDown()) {
            float $$7 = $$0.isSpawning() ? $$0.getSpawnPercent($$1) : $$0.getCooldownPercent($$1);
            double $$8 = $$0.isSpawning() ? (double)$$0.getLevel().getMaxY() : 50.0;
            $$7 = Mth.sin($$7 * (float)Math.PI);
            int $$9 = Mth.floor((double)$$7 * $$8);
            int $$10 = $$0.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColor() : DyeColor.PURPLE.getTextureDiffuseColor();
            long $$11 = $$0.getLevel().getGameTime();
            BeaconRenderer.renderBeaconBeam($$2, $$3, BEAM_LOCATION, $$1, $$7, $$11, -$$9, $$9 * 2, $$10, 0.15f, 0.175f);
        }
        super.render($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Override
    protected float getOffsetUp() {
        return 1.0f;
    }

    @Override
    protected float getOffsetDown() {
        return 0.0f;
    }

    @Override
    protected RenderType renderType() {
        return RenderType.endGateway();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}


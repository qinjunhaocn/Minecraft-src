/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionfc;

public class ThrownItemRenderer<T extends Entity>
extends EntityRenderer<T, ThrownItemRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final float scale;
    private final boolean fullBright;

    public ThrownItemRenderer(EntityRendererProvider.Context $$0, float $$1, boolean $$2) {
        super($$0);
        this.itemModelResolver = $$0.getItemModelResolver();
        this.scale = $$1;
        this.fullBright = $$2;
    }

    public ThrownItemRenderer(EntityRendererProvider.Context $$0) {
        this($$0, 1.0f, false);
    }

    @Override
    protected int getBlockLightLevel(T $$0, BlockPos $$1) {
        return this.fullBright ? 15 : super.getBlockLightLevel($$0, $$1);
    }

    @Override
    public void render(ThrownItemRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.scale(this.scale, this.scale, this.scale);
        $$1.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
        $$0.item.render($$1, $$2, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    @Override
    public void extractRenderState(T $$0, ThrownItemRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        this.itemModelResolver.updateForNonLiving($$1.item, ((ItemSupplier)$$0).getItem(), ItemDisplayContext.GROUND, (Entity)$$0);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


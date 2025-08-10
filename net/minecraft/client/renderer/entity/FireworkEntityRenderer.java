/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FireworkRocketRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionfc;

public class FireworkEntityRenderer
extends EntityRenderer<FireworkRocketEntity, FireworkRocketRenderState> {
    private final ItemModelResolver itemModelResolver;

    public FireworkEntityRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.itemModelResolver = $$0.getItemModelResolver();
    }

    @Override
    public void render(FireworkRocketRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
        if ($$0.isShotAtAngle) {
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0f));
            $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0f));
        }
        $$0.item.render($$1, $$2, $$3, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public FireworkRocketRenderState createRenderState() {
        return new FireworkRocketRenderState();
    }

    @Override
    public void extractRenderState(FireworkRocketEntity $$0, FireworkRocketRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isShotAtAngle = $$0.isShotAtAngle();
        this.itemModelResolver.updateForNonLiving($$1.item, $$0.getItem(), ItemDisplayContext.GROUND, $$0);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


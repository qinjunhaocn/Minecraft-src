/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.joml.Quaternionfc;

public class DragonFireballRenderer
extends EntityRenderer<DragonFireball, EntityRenderState> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);

    public DragonFireballRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    protected int getBlockLightLevel(DragonFireball $$0, BlockPos $$1) {
        return 15;
    }

    @Override
    public void render(EntityRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.scale(2.0f, 2.0f, 2.0f);
        $$1.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
        PoseStack.Pose $$4 = $$1.last();
        VertexConsumer $$5 = $$2.getBuffer(RENDER_TYPE);
        DragonFireballRenderer.vertex($$5, $$4, $$3, 0.0f, 0, 0, 1);
        DragonFireballRenderer.vertex($$5, $$4, $$3, 1.0f, 0, 1, 1);
        DragonFireballRenderer.vertex($$5, $$4, $$3, 1.0f, 1, 1, 0);
        DragonFireballRenderer.vertex($$5, $$4, $$3, 0.0f, 1, 0, 0);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    private static void vertex(VertexConsumer $$0, PoseStack.Pose $$1, int $$2, float $$3, int $$4, int $$5, int $$6) {
        $$0.addVertex($$1, $$3 - 0.5f, (float)$$4 - 0.25f, 0.0f).setColor(-1).setUv($$5, $$6).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$2).setNormal($$1, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}


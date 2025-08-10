/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class TheEndPortalRenderer<T extends TheEndPortalBlockEntity>
implements BlockEntityRenderer<T> {
    public static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
    public static final ResourceLocation END_PORTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_portal.png");

    public TheEndPortalRenderer(BlockEntityRendererProvider.Context $$0) {
    }

    @Override
    public void render(T $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        Matrix4f $$7 = $$2.last().pose();
        this.renderCube($$0, $$7, $$3.getBuffer(this.renderType()));
    }

    private void renderCube(T $$0, Matrix4f $$1, VertexConsumer $$2) {
        float $$3 = this.getOffsetDown();
        float $$4 = this.getOffsetUp();
        this.renderFace($$0, $$1, $$2, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        this.renderFace($$0, $$1, $$2, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        this.renderFace($$0, $$1, $$2, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        this.renderFace($$0, $$1, $$2, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        this.renderFace($$0, $$1, $$2, 0.0f, 1.0f, $$3, $$3, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        this.renderFace($$0, $$1, $$2, 0.0f, 1.0f, $$4, $$4, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(T $$0, Matrix4f $$1, VertexConsumer $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, Direction $$11) {
        if (((TheEndPortalBlockEntity)$$0).shouldRenderFace($$11)) {
            $$2.addVertex($$1, $$3, $$5, $$7);
            $$2.addVertex($$1, $$4, $$5, $$8);
            $$2.addVertex($$1, $$4, $$6, $$9);
            $$2.addVertex($$1, $$3, $$6, $$10);
        }
    }

    protected float getOffsetUp() {
        return 0.75f;
    }

    protected float getOffsetDown() {
        return 0.375f;
    }

    protected RenderType renderType() {
        return RenderType.endPortal();
    }
}


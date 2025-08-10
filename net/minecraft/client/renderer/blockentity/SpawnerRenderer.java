/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class SpawnerRenderer
implements BlockEntityRenderer<SpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public SpawnerRenderer(BlockEntityRendererProvider.Context $$0) {
        this.entityRenderer = $$0.getEntityRenderer();
    }

    @Override
    public void render(SpawnerBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        Level $$7 = $$0.getLevel();
        if ($$7 == null) {
            return;
        }
        BaseSpawner $$8 = $$0.getSpawner();
        Entity $$9 = $$8.getOrCreateDisplayEntity($$7, $$0.getBlockPos());
        if ($$9 != null) {
            SpawnerRenderer.renderEntityInSpawner($$1, $$2, $$3, $$4, $$9, this.entityRenderer, $$8.getoSpin(), $$8.getSpin());
        }
    }

    public static void renderEntityInSpawner(float $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, Entity $$4, EntityRenderDispatcher $$5, double $$6, double $$7) {
        $$1.pushPose();
        $$1.translate(0.5f, 0.0f, 0.5f);
        float $$8 = 0.53125f;
        float $$9 = Math.max($$4.getBbWidth(), $$4.getBbHeight());
        if ((double)$$9 > 1.0) {
            $$8 /= $$9;
        }
        $$1.translate(0.0f, 0.4f, 0.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)Mth.lerp((double)$$0, $$6, $$7) * 10.0f));
        $$1.translate(0.0f, -0.2f, 0.0f);
        $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-30.0f));
        $$1.scale($$8, $$8, $$8);
        $$5.render($$4, 0.0, 0.0, 0.0, $$0, $$1, $$2, $$3);
        $$1.popPose();
    }
}


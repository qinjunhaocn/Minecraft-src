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
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionfc;

public class OminousItemSpawnerRenderer
extends EntityRenderer<OminousItemSpawner, ItemClusterRenderState> {
    private static final float ROTATION_SPEED = 40.0f;
    private static final int TICKS_SCALING = 50;
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    protected OminousItemSpawnerRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.itemModelResolver = $$0.getItemModelResolver();
    }

    @Override
    public ItemClusterRenderState createRenderState() {
        return new ItemClusterRenderState();
    }

    @Override
    public void extractRenderState(OminousItemSpawner $$0, ItemClusterRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ItemStack $$3 = $$0.getItem();
        $$1.extractItemGroupRenderState($$0, $$3, this.itemModelResolver);
    }

    @Override
    public void render(ItemClusterRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if ($$0.item.isEmpty()) {
            return;
        }
        $$1.pushPose();
        if ($$0.ageInTicks <= 50.0f) {
            float $$4 = Math.min($$0.ageInTicks, 50.0f) / 50.0f;
            $$1.scale($$4, $$4, $$4);
        }
        float $$5 = Mth.wrapDegrees($$0.ageInTicks * 40.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$5));
        ItemEntityRenderer.renderMultipleFromCount($$1, $$2, 0xF000F0, $$0, this.random);
        $$1.popPose();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


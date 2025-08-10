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
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionfc;

public class ItemEntityRenderer
extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
    private static final float ITEM_MIN_HOVER_HEIGHT = 0.0625f;
    private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15f;
    private static final float FLAT_ITEM_DEPTH_THRESHOLD = 0.0625f;
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public ItemEntityRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.itemModelResolver = $$0.getItemModelResolver();
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    @Override
    public ItemEntityRenderState createRenderState() {
        return new ItemEntityRenderState();
    }

    @Override
    public void extractRenderState(ItemEntity $$0, ItemEntityRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.ageInTicks = (float)$$0.getAge() + $$2;
        $$1.bobOffset = $$0.bobOffs;
        $$1.extractItemGroupRenderState($$0, $$0.getItem(), this.itemModelResolver);
    }

    @Override
    public void render(ItemEntityRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        if ($$0.item.isEmpty()) {
            return;
        }
        $$1.pushPose();
        AABB $$4 = $$0.item.getModelBoundingBox();
        float $$5 = -((float)$$4.minY) + 0.0625f;
        float $$6 = Mth.sin($$0.ageInTicks / 10.0f + $$0.bobOffset) * 0.1f + 0.1f;
        $$1.translate(0.0f, $$6 + $$5, 0.0f);
        float $$7 = ItemEntity.getSpin($$0.ageInTicks, $$0.bobOffset);
        $$1.mulPose((Quaternionfc)Axis.YP.rotation($$7));
        ItemEntityRenderer.renderMultipleFromCount($$1, $$2, $$3, $$0, this.random, $$4);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    public static void renderMultipleFromCount(PoseStack $$0, MultiBufferSource $$1, int $$2, ItemClusterRenderState $$3, RandomSource $$4) {
        ItemEntityRenderer.renderMultipleFromCount($$0, $$1, $$2, $$3, $$4, $$3.item.getModelBoundingBox());
    }

    public static void renderMultipleFromCount(PoseStack $$0, MultiBufferSource $$1, int $$2, ItemClusterRenderState $$3, RandomSource $$4, AABB $$5) {
        int $$6 = $$3.count;
        if ($$6 == 0) {
            return;
        }
        $$4.setSeed($$3.seed);
        ItemStackRenderState $$7 = $$3.item;
        float $$8 = (float)$$5.getZsize();
        if ($$8 > 0.0625f) {
            $$7.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
            for (int $$9 = 1; $$9 < $$6; ++$$9) {
                $$0.pushPose();
                float $$10 = ($$4.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float $$11 = ($$4.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float $$12 = ($$4.nextFloat() * 2.0f - 1.0f) * 0.15f;
                $$0.translate($$10, $$11, $$12);
                $$7.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
                $$0.popPose();
            }
        } else {
            float $$13 = $$8 * 1.5f;
            $$0.translate(0.0f, 0.0f, -($$13 * (float)($$6 - 1) / 2.0f));
            $$7.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
            $$0.translate(0.0f, 0.0f, $$13);
            for (int $$14 = 1; $$14 < $$6; ++$$14) {
                $$0.pushPose();
                float $$15 = ($$4.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                float $$16 = ($$4.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                $$0.translate($$15, $$16, 0.0f);
                $$7.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
                $$0.popPose();
                $$0.translate(0.0f, 0.0f, $$13);
            }
        }
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}


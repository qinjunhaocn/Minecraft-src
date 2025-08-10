/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class BrushableBlockRenderer
implements BlockEntityRenderer<BrushableBlockEntity> {
    private final ItemRenderer itemRenderer;

    public BrushableBlockRenderer(BlockEntityRendererProvider.Context $$0) {
        this.itemRenderer = $$0.getItemRenderer();
    }

    @Override
    public void render(BrushableBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        if ($$0.getLevel() == null) {
            return;
        }
        int $$7 = $$0.getBlockState().getValue(BlockStateProperties.DUSTED);
        if ($$7 <= 0) {
            return;
        }
        Direction $$8 = $$0.getHitDirection();
        if ($$8 == null) {
            return;
        }
        ItemStack $$9 = $$0.getItem();
        if ($$9.isEmpty()) {
            return;
        }
        $$2.pushPose();
        $$2.translate(0.0f, 0.5f, 0.0f);
        float[] $$10 = this.a($$8, $$7);
        $$2.translate($$10[0], $$10[1], $$10[2]);
        $$2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(75.0f));
        boolean $$11 = $$8 == Direction.EAST || $$8 == Direction.WEST;
        $$2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(($$11 ? 90 : 0) + 11));
        $$2.scale(0.5f, 0.5f, 0.5f);
        int $$12 = LevelRenderer.getLightColor(LevelRenderer.BrightnessGetter.DEFAULT, $$0.getLevel(), $$0.getBlockState(), $$0.getBlockPos().relative($$8));
        this.itemRenderer.renderStatic($$9, ItemDisplayContext.FIXED, $$12, OverlayTexture.NO_OVERLAY, $$2, $$3, $$0.getLevel(), 0);
        $$2.popPose();
    }

    private float[] a(Direction $$0, int $$1) {
        float[] $$2 = new float[]{0.5f, 0.0f, 0.5f};
        float $$3 = (float)$$1 / 10.0f * 0.75f;
        switch ($$0) {
            case EAST: {
                $$2[0] = 0.73f + $$3;
                break;
            }
            case WEST: {
                $$2[0] = 0.25f - $$3;
                break;
            }
            case UP: {
                $$2[1] = 0.25f + $$3;
                break;
            }
            case DOWN: {
                $$2[1] = -0.23f - $$3;
                break;
            }
            case NORTH: {
                $$2[2] = 0.25f - $$3;
                break;
            }
            case SOUTH: {
                $$2[2] = 0.73f + $$3;
            }
        }
        return $$2;
    }
}


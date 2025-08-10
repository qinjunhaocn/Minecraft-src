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
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class CampfireRenderer
implements BlockEntityRenderer<CampfireBlockEntity> {
    private static final float SIZE = 0.375f;
    private final ItemRenderer itemRenderer;

    public CampfireRenderer(BlockEntityRendererProvider.Context $$0) {
        this.itemRenderer = $$0.getItemRenderer();
    }

    @Override
    public void render(CampfireBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        Direction $$7 = $$0.getBlockState().getValue(CampfireBlock.FACING);
        NonNullList<ItemStack> $$8 = $$0.getItems();
        int $$9 = (int)$$0.getBlockPos().asLong();
        for (int $$10 = 0; $$10 < $$8.size(); ++$$10) {
            ItemStack $$11 = $$8.get($$10);
            if ($$11 == ItemStack.EMPTY) continue;
            $$2.pushPose();
            $$2.translate(0.5f, 0.44921875f, 0.5f);
            Direction $$12 = Direction.from2DDataValue(($$10 + $$7.get2DDataValue()) % 4);
            float $$13 = -$$12.toYRot();
            $$2.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$13));
            $$2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0f));
            $$2.translate(-0.3125f, -0.3125f, 0.0f);
            $$2.scale(0.375f, 0.375f, 0.375f);
            this.itemRenderer.renderStatic($$11, ItemDisplayContext.FIXED, $$4, $$5, $$2, $$3, $$0.getLevel(), $$9 + $$10);
            $$2.popPose();
        }
    }
}


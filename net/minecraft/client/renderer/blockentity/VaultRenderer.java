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
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultClientData;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class VaultRenderer
implements BlockEntityRenderer<VaultBlockEntity> {
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();
    private final ItemClusterRenderState renderState = new ItemClusterRenderState();

    public VaultRenderer(BlockEntityRendererProvider.Context $$0) {
        this.itemModelResolver = $$0.getItemModelResolver();
    }

    @Override
    public void render(VaultBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        if (!VaultBlockEntity.Client.shouldDisplayActiveEffects($$0.getSharedData())) {
            return;
        }
        Level $$7 = $$0.getLevel();
        if ($$7 == null) {
            return;
        }
        ItemStack $$8 = $$0.getSharedData().getDisplayItem();
        if ($$8.isEmpty()) {
            return;
        }
        this.itemModelResolver.updateForTopItem(this.renderState.item, $$8, ItemDisplayContext.GROUND, $$7, null, 0);
        this.renderState.count = ItemClusterRenderState.getRenderedAmount($$8.getCount());
        this.renderState.seed = ItemClusterRenderState.getSeedForItemStack($$8);
        VaultClientData $$9 = $$0.getClientData();
        $$2.pushPose();
        $$2.translate(0.5f, 0.4f, 0.5f);
        $$2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(Mth.rotLerp($$1, $$9.previousSpin(), $$9.currentSpin())));
        ItemEntityRenderer.renderMultipleFromCount($$2, $$3, $$4, this.renderState, this.random);
        $$2.popPose();
    }
}


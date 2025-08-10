/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public interface BlockEntityRenderer<T extends BlockEntity> {
    public void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7);

    default public boolean shouldRenderOffScreen() {
        return false;
    }

    default public int getViewDistance() {
        return 64;
    }

    default public boolean shouldRender(T $$0, Vec3 $$1) {
        return Vec3.atCenterOf(((BlockEntity)$$0).getBlockPos()).closerThan($$1, this.getViewDistance());
    }
}


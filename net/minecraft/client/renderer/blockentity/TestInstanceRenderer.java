/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.phys.Vec3;

public class TestInstanceRenderer
implements BlockEntityRenderer<TestInstanceBlockEntity> {
    private final BeaconRenderer<TestInstanceBlockEntity> beacon;
    private final BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity> box;

    public TestInstanceRenderer(BlockEntityRendererProvider.Context $$0) {
        this.beacon = new BeaconRenderer($$0);
        this.box = new BlockEntityWithBoundingBoxRenderer($$0);
    }

    @Override
    public void render(TestInstanceBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        this.beacon.render($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.box.render($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return this.beacon.shouldRenderOffScreen() || this.box.shouldRenderOffScreen();
    }

    @Override
    public int getViewDistance() {
        return Math.max(this.beacon.getViewDistance(), this.box.getViewDistance());
    }

    @Override
    public boolean shouldRender(TestInstanceBlockEntity $$0, Vec3 $$1) {
        return this.beacon.shouldRender($$0, $$1) || this.box.shouldRender($$0, $$1);
    }
}


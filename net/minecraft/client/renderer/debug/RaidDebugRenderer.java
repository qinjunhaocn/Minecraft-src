/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class RaidDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST = 160;
    private static final float TEXT_SCALE = 0.04f;
    private final Minecraft minecraft;
    private Collection<BlockPos> raidCenters = Lists.newArrayList();

    public RaidDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void setRaidCenters(Collection<BlockPos> $$0) {
        this.raidCenters = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        BlockPos $$5 = this.getCamera().getBlockPosition();
        for (BlockPos $$6 : this.raidCenters) {
            if (!$$5.closerThan($$6, 160.0)) continue;
            RaidDebugRenderer.highlightRaidCenter($$0, $$1, $$6);
        }
    }

    private static void highlightRaidCenter(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2) {
        DebugRenderer.renderFilledUnitCube($$0, $$1, $$2, 1.0f, 0.0f, 0.0f, 0.15f);
        RaidDebugRenderer.renderTextOverBlock($$0, $$1, "Raid center", $$2, -65536);
    }

    private static void renderTextOverBlock(PoseStack $$0, MultiBufferSource $$1, String $$2, BlockPos $$3, int $$4) {
        double $$5 = (double)$$3.getX() + 0.5;
        double $$6 = (double)$$3.getY() + 1.3;
        double $$7 = (double)$$3.getZ() + 0.5;
        DebugRenderer.renderFloatingText($$0, $$1, $$2, $$5, $$6, $$7, $$4, 0.04f, true, 0.0f, true);
    }

    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

public class LightDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int MAX_RENDER_DIST = 10;

    public LightDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        ClientLevel $$5 = this.minecraft.level;
        BlockPos $$6 = BlockPos.containing($$2, $$3, $$4);
        LongOpenHashSet $$7 = new LongOpenHashSet();
        for (BlockPos $$8 : BlockPos.betweenClosed($$6.offset(-10, -10, -10), $$6.offset(10, 10, 10))) {
            int $$9 = $$5.getBrightness(LightLayer.SKY, $$8);
            float $$10 = (float)(15 - $$9) / 15.0f * 0.5f + 0.16f;
            int $$11 = Mth.hsvToRgb($$10, 0.9f, 0.9f);
            long $$12 = SectionPos.blockToSection($$8.asLong());
            if ($$7.add($$12)) {
                DebugRenderer.renderFloatingText($$0, $$1, $$5.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of($$12)), SectionPos.sectionToBlockCoord(SectionPos.x($$12), 8), SectionPos.sectionToBlockCoord(SectionPos.y($$12), 8), SectionPos.sectionToBlockCoord(SectionPos.z($$12), 8), -65536, 0.3f);
            }
            if ($$9 == 15) continue;
            DebugRenderer.renderFloatingText($$0, $$1, String.valueOf($$9), (double)$$8.getX() + 0.5, (double)$$8.getY() + 0.25, (double)$$8.getZ() + 0.5, $$11);
        }
    }
}


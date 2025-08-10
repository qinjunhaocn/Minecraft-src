/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class GameTestDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final float PADDING = 0.02f;
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos $$0, int $$1, String $$2, int $$3) {
        this.markers.put($$0, new Marker($$1, $$2, Util.getMillis() + (long)$$3));
    }

    @Override
    public void clear() {
        this.markers.clear();
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$12, double $$22, double $$32, double $$4) {
        long $$5 = Util.getMillis();
        this.markers.entrySet().removeIf($$1 -> $$5 > ((Marker)$$1.getValue()).removeAtTime);
        this.markers.forEach(($$2, $$3) -> this.renderMarker($$0, $$12, (BlockPos)$$2, (Marker)$$3));
    }

    private void renderMarker(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2, Marker $$3) {
        DebugRenderer.renderFilledBox($$0, $$1, $$2, 0.02f, $$3.getR(), $$3.getG(), $$3.getB(), $$3.getA() * 0.75f);
        if (!$$3.text.isEmpty()) {
            double $$4 = (double)$$2.getX() + 0.5;
            double $$5 = (double)$$2.getY() + 1.2;
            double $$6 = (double)$$2.getZ() + 0.5;
            DebugRenderer.renderFloatingText($$0, $$1, $$3.text, $$4, $$5, $$6, -1, 0.01f, true, 0.0f, true);
        }
    }

    static class Marker {
        public int color;
        public String text;
        public long removeAtTime;

        public Marker(int $$0, String $$1, long $$2) {
            this.color = $$0;
            this.text = $$1;
            this.removeAtTime = $$2;
        }

        public float getR() {
            return (float)(this.color >> 16 & 0xFF) / 255.0f;
        }

        public float getG() {
            return (float)(this.color >> 8 & 0xFF) / 255.0f;
        }

        public float getB() {
            return (float)(this.color & 0xFF) / 255.0f;
        }

        public float getA() {
            return (float)(this.color >> 24 & 0xFF) / 255.0f;
        }
    }
}


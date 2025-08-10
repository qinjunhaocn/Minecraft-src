/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LightEventListener;

public interface LayerLightEventListener
extends LightEventListener {
    @Nullable
    public DataLayer getDataLayerData(SectionPos var1);

    public int getLightValue(BlockPos var1);

    public static final class DummyLightLayerEventListener
    extends Enum<DummyLightLayerEventListener>
    implements LayerLightEventListener {
        public static final /* enum */ DummyLightLayerEventListener INSTANCE = new DummyLightLayerEventListener();
        private static final /* synthetic */ DummyLightLayerEventListener[] $VALUES;

        public static DummyLightLayerEventListener[] values() {
            return (DummyLightLayerEventListener[])$VALUES.clone();
        }

        public static DummyLightLayerEventListener valueOf(String $$0) {
            return Enum.valueOf(DummyLightLayerEventListener.class, $$0);
        }

        @Override
        @Nullable
        public DataLayer getDataLayerData(SectionPos $$0) {
            return null;
        }

        @Override
        public int getLightValue(BlockPos $$0) {
            return 0;
        }

        @Override
        public void checkBlock(BlockPos $$0) {
        }

        @Override
        public boolean hasLightWork() {
            return false;
        }

        @Override
        public int runLightUpdates() {
            return 0;
        }

        @Override
        public void updateSectionStatus(SectionPos $$0, boolean $$1) {
        }

        @Override
        public void setLightEnabled(ChunkPos $$0, boolean $$1) {
        }

        @Override
        public void propagateLightSources(ChunkPos $$0) {
        }

        private static /* synthetic */ DummyLightLayerEventListener[] c() {
            return new DummyLightLayerEventListener[]{INSTANCE};
        }

        static {
            $VALUES = DummyLightLayerEventListener.c();
        }
    }
}


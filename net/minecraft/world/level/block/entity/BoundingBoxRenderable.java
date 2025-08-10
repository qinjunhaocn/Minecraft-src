/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public interface BoundingBoxRenderable {
    public Mode renderMode();

    public RenderableBox getRenderableBox();

    public static final class Mode
    extends Enum<Mode> {
        public static final /* enum */ Mode NONE = new Mode();
        public static final /* enum */ Mode BOX = new Mode();
        public static final /* enum */ Mode BOX_AND_INVISIBLE_BLOCKS = new Mode();
        private static final /* synthetic */ Mode[] $VALUES;

        public static Mode[] values() {
            return (Mode[])$VALUES.clone();
        }

        public static Mode valueOf(String $$0) {
            return Enum.valueOf(Mode.class, $$0);
        }

        private static /* synthetic */ Mode[] a() {
            return new Mode[]{NONE, BOX, BOX_AND_INVISIBLE_BLOCKS};
        }

        static {
            $VALUES = Mode.a();
        }
    }

    public record RenderableBox(BlockPos localPos, Vec3i size) {
        public static RenderableBox fromCorners(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
            int $$6 = Math.min($$0, $$3);
            int $$7 = Math.min($$1, $$4);
            int $$8 = Math.min($$2, $$5);
            return new RenderableBox(new BlockPos($$6, $$7, $$8), new Vec3i(Math.max($$0, $$3) - $$6, Math.max($$1, $$4) - $$7, Math.max($$2, $$5) - $$8));
        }
    }
}


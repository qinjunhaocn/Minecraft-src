/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.saveddata;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

public abstract class SavedData {
    private boolean dirty;

    public void setDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean $$0) {
        this.dirty = $$0;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public record Context(@Nullable ServerLevel level, long worldSeed) {
        public Context(ServerLevel $$0) {
            this($$0, $$0.getSeed());
        }

        public ServerLevel levelOrThrow() {
            return Objects.requireNonNull(this.level);
        }

        @Nullable
        public ServerLevel level() {
            return this.level;
        }
    }
}


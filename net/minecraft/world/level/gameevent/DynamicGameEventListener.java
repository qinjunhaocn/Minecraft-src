/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.gameevent;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistry;

public class DynamicGameEventListener<T extends GameEventListener> {
    private final T listener;
    @Nullable
    private SectionPos lastSection;

    public DynamicGameEventListener(T $$0) {
        this.listener = $$0;
    }

    public void add(ServerLevel $$0) {
        this.move($$0);
    }

    public T getListener() {
        return this.listener;
    }

    public void remove(ServerLevel $$02) {
        DynamicGameEventListener.ifChunkExists($$02, this.lastSection, $$0 -> $$0.unregister((GameEventListener)this.listener));
    }

    public void move(ServerLevel $$0) {
        this.listener.getListenerSource().getPosition($$0).map(SectionPos::of).ifPresent($$1 -> {
            if (this.lastSection == null || !this.lastSection.equals($$1)) {
                DynamicGameEventListener.ifChunkExists($$0, this.lastSection, $$0 -> $$0.unregister((GameEventListener)this.listener));
                this.lastSection = $$1;
                DynamicGameEventListener.ifChunkExists($$0, this.lastSection, $$0 -> $$0.register((GameEventListener)this.listener));
            }
        });
    }

    private static void ifChunkExists(LevelReader $$0, @Nullable SectionPos $$1, Consumer<GameEventListenerRegistry> $$2) {
        if ($$1 == null) {
            return;
        }
        ChunkAccess $$3 = $$0.getChunk($$1.x(), $$1.z(), ChunkStatus.FULL, false);
        if ($$3 != null) {
            $$2.accept($$3.getListenerRegistry($$1.y()));
        }
    }
}


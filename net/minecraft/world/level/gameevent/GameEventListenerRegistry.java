/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;

public interface GameEventListenerRegistry {
    public static final GameEventListenerRegistry NOOP = new GameEventListenerRegistry(){

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void register(GameEventListener $$0) {
        }

        @Override
        public void unregister(GameEventListener $$0) {
        }

        @Override
        public boolean visitInRangeListeners(Holder<GameEvent> $$0, Vec3 $$1, GameEvent.Context $$2, ListenerVisitor $$3) {
            return false;
        }
    };

    public boolean isEmpty();

    public void register(GameEventListener var1);

    public void unregister(GameEventListener var1);

    public boolean visitInRangeListeners(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3, ListenerVisitor var4);

    @FunctionalInterface
    public static interface ListenerVisitor {
        public void visit(GameEventListener var1, Vec3 var2);
    }
}


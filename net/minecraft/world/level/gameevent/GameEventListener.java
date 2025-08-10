/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public interface GameEventListener {
    public PositionSource getListenerSource();

    public int getListenerRadius();

    public boolean handleGameEvent(ServerLevel var1, Holder<GameEvent> var2, GameEvent.Context var3, Vec3 var4);

    default public DeliveryMode getDeliveryMode() {
        return DeliveryMode.UNSPECIFIED;
    }

    public static final class DeliveryMode
    extends Enum<DeliveryMode> {
        public static final /* enum */ DeliveryMode UNSPECIFIED = new DeliveryMode();
        public static final /* enum */ DeliveryMode BY_DISTANCE = new DeliveryMode();
        private static final /* synthetic */ DeliveryMode[] $VALUES;

        public static DeliveryMode[] values() {
            return (DeliveryMode[])$VALUES.clone();
        }

        public static DeliveryMode valueOf(String $$0) {
            return Enum.valueOf(DeliveryMode.class, $$0);
        }

        private static /* synthetic */ DeliveryMode[] a() {
            return new DeliveryMode[]{UNSPECIFIED, BY_DISTANCE};
        }

        static {
            $VALUES = DeliveryMode.a();
        }
    }

    public static interface Provider<T extends GameEventListener> {
        public T getListener();
    }
}


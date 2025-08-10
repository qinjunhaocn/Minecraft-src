/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public record TicketType(long timeout, boolean persist, TicketUse use) {
    public static final long NO_TIMEOUT = 0L;
    public static final TicketType START = TicketType.register("start", 0L, false, TicketUse.LOADING_AND_SIMULATION);
    public static final TicketType DRAGON = TicketType.register("dragon", 0L, false, TicketUse.LOADING_AND_SIMULATION);
    public static final TicketType PLAYER_LOADING = TicketType.register("player_loading", 0L, false, TicketUse.LOADING);
    public static final TicketType PLAYER_SIMULATION = TicketType.register("player_simulation", 0L, false, TicketUse.SIMULATION);
    public static final TicketType FORCED = TicketType.register("forced", 0L, true, TicketUse.LOADING_AND_SIMULATION);
    public static final TicketType PORTAL = TicketType.register("portal", 300L, true, TicketUse.LOADING_AND_SIMULATION);
    public static final TicketType ENDER_PEARL = TicketType.register("ender_pearl", 40L, false, TicketUse.LOADING_AND_SIMULATION);
    public static final TicketType UNKNOWN = TicketType.register("unknown", 1L, false, TicketUse.LOADING);

    private static TicketType register(String $$0, long $$1, boolean $$2, TicketUse $$3) {
        return Registry.register(BuiltInRegistries.TICKET_TYPE, $$0, new TicketType($$1, $$2, $$3));
    }

    public boolean doesLoad() {
        return this.use == TicketUse.LOADING || this.use == TicketUse.LOADING_AND_SIMULATION;
    }

    public boolean doesSimulate() {
        return this.use == TicketUse.SIMULATION || this.use == TicketUse.LOADING_AND_SIMULATION;
    }

    public boolean hasTimeout() {
        return this.timeout != 0L;
    }

    public static final class TicketUse
    extends Enum<TicketUse> {
        public static final /* enum */ TicketUse LOADING = new TicketUse();
        public static final /* enum */ TicketUse SIMULATION = new TicketUse();
        public static final /* enum */ TicketUse LOADING_AND_SIMULATION = new TicketUse();
        private static final /* synthetic */ TicketUse[] $VALUES;

        public static TicketUse[] values() {
            return (TicketUse[])$VALUES.clone();
        }

        public static TicketUse valueOf(String $$0) {
            return Enum.valueOf(TicketUse.class, $$0);
        }

        private static /* synthetic */ TicketUse[] a() {
            return new TicketUse[]{LOADING, SIMULATION, LOADING_AND_SIMULATION};
        }

        static {
            $VALUES = TicketUse.a();
        }
    }
}


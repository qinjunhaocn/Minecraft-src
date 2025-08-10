/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class Goal {
    private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

    public abstract boolean canUse();

    public boolean canContinueToUse() {
        return this.canUse();
    }

    public boolean isInterruptable() {
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public boolean requiresUpdateEveryTick() {
        return false;
    }

    public void tick() {
    }

    public void setFlags(EnumSet<Flag> $$0) {
        this.flags.clear();
        this.flags.addAll($$0);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public EnumSet<Flag> getFlags() {
        return this.flags;
    }

    protected int adjustedTickDelay(int $$0) {
        return this.requiresUpdateEveryTick() ? $$0 : Goal.reducedTickDelay($$0);
    }

    protected static int reducedTickDelay(int $$0) {
        return Mth.positiveCeilDiv($$0, 2);
    }

    protected static ServerLevel getServerLevel(Entity $$0) {
        return (ServerLevel)$$0.level();
    }

    protected static ServerLevel getServerLevel(Level $$0) {
        return (ServerLevel)$$0;
    }

    public static final class Flag
    extends Enum<Flag> {
        public static final /* enum */ Flag MOVE = new Flag();
        public static final /* enum */ Flag LOOK = new Flag();
        public static final /* enum */ Flag JUMP = new Flag();
        public static final /* enum */ Flag TARGET = new Flag();
        private static final /* synthetic */ Flag[] $VALUES;

        public static Flag[] values() {
            return (Flag[])$VALUES.clone();
        }

        public static Flag valueOf(String $$0) {
            return Enum.valueOf(Flag.class, $$0);
        }

        private static /* synthetic */ Flag[] a() {
            return new Flag[]{MOVE, LOOK, JUMP, TARGET};
        }

        static {
            $VALUES = Flag.a();
        }
    }
}


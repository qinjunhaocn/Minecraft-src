/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;

public interface Portal {
    default public int getPortalTransitionTime(ServerLevel $$0, Entity $$1) {
        return 0;
    }

    @Nullable
    public TeleportTransition getPortalDestination(ServerLevel var1, Entity var2, BlockPos var3);

    default public Transition getLocalTransition() {
        return Transition.NONE;
    }

    public static final class Transition
    extends Enum<Transition> {
        public static final /* enum */ Transition CONFUSION = new Transition();
        public static final /* enum */ Transition NONE = new Transition();
        private static final /* synthetic */ Transition[] $VALUES;

        public static Transition[] values() {
            return (Transition[])$VALUES.clone();
        }

        public static Transition valueOf(String $$0) {
            return Enum.valueOf(Transition.class, $$0);
        }

        private static /* synthetic */ Transition[] a() {
            return new Transition[]{CONFUSION, NONE};
        }

        static {
            $VALUES = Transition.a();
        }
    }
}


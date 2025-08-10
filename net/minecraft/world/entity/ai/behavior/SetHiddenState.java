/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.apache.commons.lang3.mutable.MutableInt;

public class SetHiddenState {
    private static final int HIDE_TIMEOUT = 300;

    public static BehaviorControl<LivingEntity> create(int $$0, int $$1) {
        int $$2 = $$0 * 20;
        MutableInt $$32 = new MutableInt(0);
        return BehaviorBuilder.create($$3 -> $$3.group($$3.present(MemoryModuleType.HIDING_PLACE), $$3.present(MemoryModuleType.HEARD_BELL_TIME)).apply((Applicative)$$3, ($$4, $$5) -> ($$6, $$7, $$8) -> {
            boolean $$10;
            long $$9 = (Long)$$3.get($$5);
            boolean bl = $$10 = $$9 + 300L <= $$8;
            if ($$32.getValue() > $$2 || $$10) {
                $$5.erase();
                $$4.erase();
                $$7.getBrain().updateActivityFromSchedule($$6.getDayTime(), $$6.getGameTime());
                $$32.setValue(0);
                return true;
            }
            BlockPos $$11 = ((GlobalPos)((Object)((Object)((Object)((Object)$$3.get($$4)))))).pos();
            if ($$11.closerThan($$7.blockPosition(), $$1)) {
                $$32.increment();
            }
            return true;
        }));
    }
}


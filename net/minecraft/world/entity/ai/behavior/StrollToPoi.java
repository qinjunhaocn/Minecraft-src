/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.apache.commons.lang3.mutable.MutableLong;

public class StrollToPoi {
    public static BehaviorControl<PathfinderMob> create(MemoryModuleType<GlobalPos> $$0, float $$1, int $$2, int $$3) {
        MutableLong $$4 = new MutableLong(0L);
        return BehaviorBuilder.create($$52 -> $$52.group($$52.registered(MemoryModuleType.WALK_TARGET), $$52.present($$0)).apply((Applicative)$$52, ($$5, $$6) -> ($$7, $$8, $$9) -> {
            GlobalPos $$10 = (GlobalPos)((Object)((Object)((Object)((Object)$$52.get($$6)))));
            if ($$7.dimension() != $$10.dimension() || !$$10.pos().closerToCenterThan($$8.position(), $$3)) {
                return false;
            }
            if ($$9 <= $$4.getValue()) {
                return true;
            }
            $$5.set(new WalkTarget($$10.pos(), $$1, $$2));
            $$4.setValue($$9 + 80L);
            return true;
        }));
    }
}


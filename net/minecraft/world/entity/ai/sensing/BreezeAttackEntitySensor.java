/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeAttackEntitySensor
extends NearestLivingEntitySensor<Breeze> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    @Override
    protected void doTick(ServerLevel $$0, Breeze $$12) {
        super.doTick($$0, $$12);
        $$12.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream).filter(EntitySelector.NO_CREATIVE_OR_SPECTATOR).filter($$2 -> Sensor.isEntityAttackable($$0, $$12, $$2)).findFirst().ifPresentOrElse($$1 -> $$12.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, $$1), () -> $$12.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE));
    }
}


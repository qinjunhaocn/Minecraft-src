/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ResetProfession {
    public static BehaviorControl<Villager> create() {
        return BehaviorBuilder.create($$0 -> $$0.group($$0.absent(MemoryModuleType.JOB_SITE)).apply((Applicative)$$0, $$02 -> ($$0, $$1, $$2) -> {
            boolean $$4;
            VillagerData $$3 = $$1.getVillagerData();
            boolean bl = $$4 = !$$3.profession().is(VillagerProfession.NONE) && !$$3.profession().is(VillagerProfession.NITWIT);
            if ($$4 && $$1.getVillagerXp() == 0 && $$3.level() <= 1) {
                $$1.setVillagerData($$1.getVillagerData().withProfession($$0.registryAccess(), VillagerProfession.NONE));
                $$1.refreshBrain($$0);
                return true;
            }
            return false;
        }));
    }
}


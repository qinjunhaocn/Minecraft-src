/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class Swim<T extends Mob>
extends Behavior<T> {
    private final float chance;

    public Swim(float $$0) {
        super(ImmutableMap.of());
        this.chance = $$0;
    }

    public static <T extends Mob> boolean shouldSwim(T $$0) {
        return $$0.isInWater() && $$0.getFluidHeight(FluidTags.WATER) > $$0.getFluidJumpThreshold() || $$0.isInLava();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Mob $$1) {
        return Swim.shouldSwim($$1);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Mob $$1, long $$2) {
        return this.checkExtraStartConditions($$0, $$1);
    }

    @Override
    protected void tick(ServerLevel $$0, Mob $$1, long $$2) {
        if ($$1.getRandom().nextFloat() < this.chance) {
            $$1.getJumpControl().jump();
        }
    }

    @Override
    protected /* synthetic */ void tick(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.tick(serverLevel, (Mob)livingEntity, l);
    }
}


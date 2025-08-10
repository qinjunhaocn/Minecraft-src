/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SimpleExplosionDamageCalculator
extends ExplosionDamageCalculator {
    private final boolean explodesBlocks;
    private final boolean damagesEntities;
    private final Optional<Float> knockbackMultiplier;
    private final Optional<HolderSet<Block>> immuneBlocks;

    public SimpleExplosionDamageCalculator(boolean $$0, boolean $$1, Optional<Float> $$2, Optional<HolderSet<Block>> $$3) {
        this.explodesBlocks = $$0;
        this.damagesEntities = $$1;
        this.knockbackMultiplier = $$2;
        this.immuneBlocks = $$3;
    }

    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, FluidState $$4) {
        if (this.immuneBlocks.isPresent()) {
            if ($$3.is(this.immuneBlocks.get())) {
                return Optional.of(Float.valueOf(3600000.0f));
            }
            return Optional.empty();
        }
        return super.getBlockExplosionResistance($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean shouldBlockExplode(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, float $$4) {
        return this.explodesBlocks;
    }

    @Override
    public boolean shouldDamageEntity(Explosion $$0, Entity $$1) {
        return this.damagesEntities;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public float getKnockbackMultiplier(Entity $$0) {
        boolean $$2;
        if ($$0 instanceof Player) {
            Player $$1 = (Player)$$0;
            if ($$1.getAbilities().flying) {
                return 0.0f;
            }
        }
        boolean bl = $$2 = false;
        if ($$2) {
            return 0.0f;
        }
        float f = this.knockbackMultiplier.orElseGet(() -> Float.valueOf(super.getKnockbackMultiplier($$0))).floatValue();
        return f;
    }
}


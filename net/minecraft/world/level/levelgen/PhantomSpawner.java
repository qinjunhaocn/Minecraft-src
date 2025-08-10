/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class PhantomSpawner
implements CustomSpawner {
    private int nextTick;

    @Override
    public void tick(ServerLevel $$0, boolean $$1, boolean $$2) {
        if (!$$1) {
            return;
        }
        if (!$$0.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) {
            return;
        }
        RandomSource $$3 = $$0.random;
        --this.nextTick;
        if (this.nextTick > 0) {
            return;
        }
        this.nextTick += (60 + $$3.nextInt(60)) * 20;
        if ($$0.getSkyDarken() < 5 && $$0.dimensionType().hasSkyLight()) {
            return;
        }
        for (ServerPlayer $$4 : $$0.players()) {
            FluidState $$12;
            BlockState $$11;
            BlockPos $$10;
            DifficultyInstance $$6;
            if ($$4.isSpectator()) continue;
            BlockPos $$5 = $$4.blockPosition();
            if ($$0.dimensionType().hasSkyLight() && ($$5.getY() < $$0.getSeaLevel() || !$$0.canSeeSky($$5)) || !($$6 = $$0.getCurrentDifficultyAt($$5)).isHarderThan($$3.nextFloat() * 3.0f)) continue;
            ServerStatsCounter $$7 = $$4.getStats();
            int $$8 = Mth.clamp($$7.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
            int $$9 = 24000;
            if ($$3.nextInt($$8) < 72000 || !NaturalSpawner.isValidEmptySpawnBlock($$0, $$10 = $$5.above(20 + $$3.nextInt(15)).east(-10 + $$3.nextInt(21)).south(-10 + $$3.nextInt(21)), $$11 = $$0.getBlockState($$10), $$12 = $$0.getFluidState($$10), EntityType.PHANTOM)) continue;
            SpawnGroupData $$13 = null;
            int $$14 = 1 + $$3.nextInt($$6.getDifficulty().getId() + 1);
            for (int $$15 = 0; $$15 < $$14; ++$$15) {
                Phantom $$16 = EntityType.PHANTOM.create($$0, EntitySpawnReason.NATURAL);
                if ($$16 == null) continue;
                $$16.snapTo($$10, 0.0f, 0.0f);
                $$13 = $$16.finalizeSpawn($$0, $$6, EntitySpawnReason.NATURAL, $$13);
                $$0.addFreshEntityWithPassengers($$16);
            }
        }
    }
}


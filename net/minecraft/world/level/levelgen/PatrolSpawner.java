/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class PatrolSpawner
implements CustomSpawner {
    private int nextTick;

    @Override
    public void tick(ServerLevel $$0, boolean $$1, boolean $$2) {
        if (!$$1) {
            return;
        }
        if (!$$0.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return;
        }
        RandomSource $$3 = $$0.random;
        --this.nextTick;
        if (this.nextTick > 0) {
            return;
        }
        this.nextTick += 12000 + $$3.nextInt(1200);
        long $$4 = $$0.getDayTime() / 24000L;
        if ($$4 < 5L || !$$0.isBrightOutside()) {
            return;
        }
        if ($$3.nextInt(5) != 0) {
            return;
        }
        int $$5 = $$0.players().size();
        if ($$5 < 1) {
            return;
        }
        Player $$6 = $$0.players().get($$3.nextInt($$5));
        if ($$6.isSpectator()) {
            return;
        }
        if ($$0.isCloseToVillage($$6.blockPosition(), 2)) {
            return;
        }
        int $$7 = (24 + $$3.nextInt(24)) * ($$3.nextBoolean() ? -1 : 1);
        int $$8 = (24 + $$3.nextInt(24)) * ($$3.nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos $$9 = $$6.blockPosition().mutable().move($$7, 0, $$8);
        int $$10 = 10;
        if (!$$0.hasChunksAt($$9.getX() - 10, $$9.getZ() - 10, $$9.getX() + 10, $$9.getZ() + 10)) {
            return;
        }
        Holder<Biome> $$11 = $$0.getBiome($$9);
        if ($$11.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
            return;
        }
        int $$12 = (int)Math.ceil($$0.getCurrentDifficultyAt($$9).getEffectiveDifficulty()) + 1;
        for (int $$13 = 0; $$13 < $$12; ++$$13) {
            $$9.setY($$0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$9).getY());
            if ($$13 == 0) {
                if (!this.spawnPatrolMember($$0, $$9, $$3, true)) {
                    break;
                }
            } else {
                this.spawnPatrolMember($$0, $$9, $$3, false);
            }
            $$9.setX($$9.getX() + $$3.nextInt(5) - $$3.nextInt(5));
            $$9.setZ($$9.getZ() + $$3.nextInt(5) - $$3.nextInt(5));
        }
    }

    private boolean spawnPatrolMember(ServerLevel $$0, BlockPos $$1, RandomSource $$2, boolean $$3) {
        BlockState $$4 = $$0.getBlockState($$1);
        if (!NaturalSpawner.isValidEmptySpawnBlock($$0, $$1, $$4, $$4.getFluidState(), EntityType.PILLAGER)) {
            return false;
        }
        if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, $$0, EntitySpawnReason.PATROL, $$1, $$2)) {
            return false;
        }
        PatrollingMonster $$5 = EntityType.PILLAGER.create($$0, EntitySpawnReason.PATROL);
        if ($$5 != null) {
            if ($$3) {
                $$5.setPatrolLeader(true);
                $$5.findPatrolTarget();
            }
            $$5.setPos($$1.getX(), $$1.getY(), $$1.getZ());
            $$5.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$1), EntitySpawnReason.PATROL, null);
            $$0.addFreshEntityWithPassengers($$5);
            return true;
        }
        return false;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.npc;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public class CatSpawner
implements CustomSpawner {
    private static final int TICK_DELAY = 1200;
    private int nextTick;

    @Override
    public void tick(ServerLevel $$0, boolean $$1, boolean $$2) {
        if (!$$2 || !$$0.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }
        --this.nextTick;
        if (this.nextTick > 0) {
            return;
        }
        this.nextTick = 1200;
        ServerPlayer $$3 = $$0.getRandomPlayer();
        if ($$3 == null) {
            return;
        }
        RandomSource $$4 = $$0.random;
        int $$5 = (8 + $$4.nextInt(24)) * ($$4.nextBoolean() ? -1 : 1);
        int $$6 = (8 + $$4.nextInt(24)) * ($$4.nextBoolean() ? -1 : 1);
        BlockPos $$7 = $$3.blockPosition().offset($$5, 0, $$6);
        int $$8 = 10;
        if (!$$0.hasChunksAt($$7.getX() - 10, $$7.getZ() - 10, $$7.getX() + 10, $$7.getZ() + 10)) {
            return;
        }
        if (SpawnPlacements.isSpawnPositionOk(EntityType.CAT, $$0, $$7)) {
            if ($$0.isCloseToVillage($$7, 2)) {
                this.spawnInVillage($$0, $$7);
            } else if ($$0.structureManager().getStructureWithPieceAt($$7, StructureTags.CATS_SPAWN_IN).isValid()) {
                this.spawnInHut($$0, $$7);
            }
        }
    }

    private void spawnInVillage(ServerLevel $$02, BlockPos $$1) {
        List<Cat> $$3;
        int $$2 = 48;
        if ($$02.getPoiManager().getCountInRange($$0 -> $$0.is(PoiTypes.HOME), $$1, 48, PoiManager.Occupancy.IS_OCCUPIED) > 4L && ($$3 = $$02.getEntitiesOfClass(Cat.class, new AABB($$1).inflate(48.0, 8.0, 48.0))).size() < 5) {
            this.spawnCat($$1, $$02, false);
        }
    }

    private void spawnInHut(ServerLevel $$0, BlockPos $$1) {
        int $$2 = 16;
        List<Cat> $$3 = $$0.getEntitiesOfClass(Cat.class, new AABB($$1).inflate(16.0, 8.0, 16.0));
        if ($$3.isEmpty()) {
            this.spawnCat($$1, $$0, true);
        }
    }

    private void spawnCat(BlockPos $$0, ServerLevel $$1, boolean $$2) {
        Cat $$3 = EntityType.CAT.create($$1, EntitySpawnReason.NATURAL);
        if ($$3 == null) {
            return;
        }
        $$3.finalizeSpawn($$1, $$1.getCurrentDifficultyAt($$0), EntitySpawnReason.NATURAL, null);
        if ($$2) {
            $$3.setPersistenceRequired();
        }
        $$3.snapTo($$0, 0.0f, 0.0f);
        $$1.addFreshEntityWithPassengers($$3);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class BaseSpawner {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String SPAWN_DATA_TAG = "SpawnData";
    private static final int EVENT_SPAWN = 1;
    private static final int DEFAULT_SPAWN_DELAY = 20;
    private static final int DEFAULT_MIN_SPAWN_DELAY = 200;
    private static final int DEFAULT_MAX_SPAWN_DELAY = 800;
    private static final int DEFAULT_SPAWN_COUNT = 4;
    private static final int DEFAULT_MAX_NEARBY_ENTITIES = 6;
    private static final int DEFAULT_REQUIRED_PLAYER_RANGE = 16;
    private static final int DEFAULT_SPAWN_RANGE = 4;
    private int spawnDelay = 20;
    private WeightedList<SpawnData> spawnPotentials = WeightedList.of();
    @Nullable
    private SpawnData nextSpawnData;
    private double spin;
    private double oSpin;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable
    private Entity displayEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    public void setEntityId(EntityType<?> $$0, @Nullable Level $$1, RandomSource $$2, BlockPos $$3) {
        this.getOrCreateNextSpawnData($$1, $$2, $$3).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey($$0).toString());
    }

    private boolean isNearPlayer(Level $$0, BlockPos $$1) {
        return $$0.hasNearbyAlivePlayer((double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, this.requiredPlayerRange);
    }

    public void clientTick(Level $$0, BlockPos $$1) {
        if (!this.isNearPlayer($$0, $$1)) {
            this.oSpin = this.spin;
        } else if (this.displayEntity != null) {
            RandomSource $$2 = $$0.getRandom();
            double $$3 = (double)$$1.getX() + $$2.nextDouble();
            double $$4 = (double)$$1.getY() + $$2.nextDouble();
            double $$5 = (double)$$1.getZ() + $$2.nextDouble();
            $$0.addParticle(ParticleTypes.SMOKE, $$3, $$4, $$5, 0.0, 0.0, 0.0);
            $$0.addParticle(ParticleTypes.FLAME, $$3, $$4, $$5, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.oSpin = this.spin;
            this.spin = (this.spin + (double)(1000.0f / ((float)this.spawnDelay + 200.0f))) % 360.0;
        }
    }

    public void serverTick(ServerLevel $$0, BlockPos $$12) {
        if (!this.isNearPlayer($$0, $$12)) {
            return;
        }
        if (this.spawnDelay == -1) {
            this.delay($$0, $$12);
        }
        if (this.spawnDelay > 0) {
            --this.spawnDelay;
            return;
        }
        boolean $$2 = false;
        RandomSource $$3 = $$0.getRandom();
        SpawnData $$4 = this.getOrCreateNextSpawnData($$0, $$3, $$12);
        for (int $$5 = 0; $$5 < this.spawnCount; ++$$5) {
            try (ProblemReporter.ScopedCollector $$6 = new ProblemReporter.ScopedCollector(this::toString, LOGGER);){
                ValueInput $$7 = TagValueInput.create((ProblemReporter)$$6, (HolderLookup.Provider)$$0.registryAccess(), $$4.getEntityToSpawn());
                Optional<EntityType<?>> $$8 = EntityType.by($$7);
                if ($$8.isEmpty()) {
                    this.delay($$0, $$12);
                    return;
                }
                Vec3 $$9 = $$7.read("Pos", Vec3.CODEC).orElseGet(() -> new Vec3((double)$$12.getX() + ($$3.nextDouble() - $$3.nextDouble()) * (double)this.spawnRange + 0.5, $$12.getY() + $$3.nextInt(3) - 1, (double)$$12.getZ() + ($$3.nextDouble() - $$3.nextDouble()) * (double)this.spawnRange + 0.5));
                if (!$$0.noCollision($$8.get().getSpawnAABB($$9.x, $$9.y, $$9.z))) continue;
                BlockPos $$10 = BlockPos.containing($$9);
                if ($$4.getCustomSpawnRules().isPresent()) {
                    SpawnData.CustomSpawnRules $$11;
                    if (!$$8.get().getCategory().isFriendly() && $$0.getDifficulty() == Difficulty.PEACEFUL || !($$11 = $$4.getCustomSpawnRules().get()).isValidPosition($$10, $$0)) continue;
                } else if (!SpawnPlacements.checkSpawnRules($$8.get(), $$0, EntitySpawnReason.SPAWNER, $$10, $$0.getRandom())) continue;
                Entity $$122 = EntityType.loadEntityRecursive($$7, (Level)$$0, EntitySpawnReason.SPAWNER, $$1 -> {
                    $$1.snapTo($$0.x, $$0.y, $$0.z, $$1.getYRot(), $$1.getXRot());
                    return $$1;
                });
                if ($$122 == null) {
                    this.delay($$0, $$12);
                    return;
                }
                int $$13 = $$0.getEntities(EntityTypeTest.forExactClass($$122.getClass()), new AABB($$12.getX(), $$12.getY(), $$12.getZ(), $$12.getX() + 1, $$12.getY() + 1, $$12.getZ() + 1).inflate(this.spawnRange), EntitySelector.NO_SPECTATORS).size();
                if ($$13 >= this.maxNearbyEntities) {
                    this.delay($$0, $$12);
                    return;
                }
                $$122.snapTo($$122.getX(), $$122.getY(), $$122.getZ(), $$3.nextFloat() * 360.0f, 0.0f);
                if ($$122 instanceof Mob) {
                    boolean $$15;
                    Mob $$14 = (Mob)$$122;
                    if ($$4.getCustomSpawnRules().isEmpty() && !$$14.checkSpawnRules($$0, EntitySpawnReason.SPAWNER) || !$$14.checkSpawnObstruction($$0)) continue;
                    boolean bl = $$15 = $$4.getEntityToSpawn().size() == 1 && $$4.getEntityToSpawn().getString("id").isPresent();
                    if ($$15) {
                        ((Mob)$$122).finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$122.blockPosition()), EntitySpawnReason.SPAWNER, null);
                    }
                    $$4.getEquipment().ifPresent($$14::equip);
                }
                if (!$$0.tryAddFreshEntityWithPassengers($$122)) {
                    this.delay($$0, $$12);
                    return;
                }
                $$0.levelEvent(2004, $$12, 0);
                $$0.gameEvent($$122, GameEvent.ENTITY_PLACE, $$10);
                if ($$122 instanceof Mob) {
                    ((Mob)$$122).spawnAnim();
                }
                $$2 = true;
                continue;
            }
        }
        if ($$2) {
            this.delay($$0, $$12);
        }
    }

    private void delay(Level $$0, BlockPos $$1) {
        RandomSource $$22 = $$0.random;
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + $$22.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        this.spawnPotentials.getRandom($$22).ifPresent($$2 -> this.setNextSpawnData($$0, $$1, (SpawnData)((Object)$$2)));
        this.broadcastEvent($$0, $$1, 1);
    }

    public void load(@Nullable Level $$0, BlockPos $$1, ValueInput $$22) {
        this.spawnDelay = $$22.getShortOr("Delay", (short)20);
        $$22.read(SPAWN_DATA_TAG, SpawnData.CODEC).ifPresent($$2 -> this.setNextSpawnData($$0, $$1, (SpawnData)((Object)$$2)));
        this.spawnPotentials = $$22.read("SpawnPotentials", SpawnData.LIST_CODEC).orElseGet(() -> WeightedList.of(this.nextSpawnData != null ? this.nextSpawnData : new SpawnData()));
        this.minSpawnDelay = $$22.getIntOr("MinSpawnDelay", 200);
        this.maxSpawnDelay = $$22.getIntOr("MaxSpawnDelay", 800);
        this.spawnCount = $$22.getIntOr("SpawnCount", 4);
        this.maxNearbyEntities = $$22.getIntOr("MaxNearbyEntities", 6);
        this.requiredPlayerRange = $$22.getIntOr("RequiredPlayerRange", 16);
        this.spawnRange = $$22.getIntOr("SpawnRange", 4);
        this.displayEntity = null;
    }

    public void save(ValueOutput $$0) {
        $$0.putShort("Delay", (short)this.spawnDelay);
        $$0.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        $$0.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        $$0.putShort("SpawnCount", (short)this.spawnCount);
        $$0.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        $$0.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        $$0.putShort("SpawnRange", (short)this.spawnRange);
        $$0.storeNullable(SPAWN_DATA_TAG, SpawnData.CODEC, this.nextSpawnData);
        $$0.store("SpawnPotentials", SpawnData.LIST_CODEC, this.spawnPotentials);
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(Level $$0, BlockPos $$1) {
        if (this.displayEntity == null) {
            CompoundTag $$2 = this.getOrCreateNextSpawnData($$0, $$0.getRandom(), $$1).getEntityToSpawn();
            if ($$2.getString("id").isEmpty()) {
                return null;
            }
            this.displayEntity = EntityType.loadEntityRecursive($$2, $$0, EntitySpawnReason.SPAWNER, Function.identity());
            if ($$2.size() != 1 || this.displayEntity instanceof Mob) {
                // empty if block
            }
        }
        return this.displayEntity;
    }

    public boolean onEventTriggered(Level $$0, int $$1) {
        if ($$1 == 1) {
            if ($$0.isClientSide) {
                this.spawnDelay = this.minSpawnDelay;
            }
            return true;
        }
        return false;
    }

    protected void setNextSpawnData(@Nullable Level $$0, BlockPos $$1, SpawnData $$2) {
        this.nextSpawnData = $$2;
    }

    private SpawnData getOrCreateNextSpawnData(@Nullable Level $$0, RandomSource $$1, BlockPos $$2) {
        if (this.nextSpawnData != null) {
            return this.nextSpawnData;
        }
        this.setNextSpawnData($$0, $$2, this.spawnPotentials.getRandom($$1).orElseGet(SpawnData::new));
        return this.nextSpawnData;
    }

    public abstract void broadcastEvent(Level var1, BlockPos var2, int var3);

    public double getSpin() {
        return this.spin;
    }

    public double getoSpin() {
        return this.oSpin;
    }
}


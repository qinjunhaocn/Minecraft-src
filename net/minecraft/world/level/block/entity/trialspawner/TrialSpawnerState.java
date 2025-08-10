/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public final class TrialSpawnerState
extends Enum<TrialSpawnerState>
implements StringRepresentable {
    public static final /* enum */ TrialSpawnerState INACTIVE = new TrialSpawnerState("inactive", 0, ParticleEmission.NONE, -1.0, false);
    public static final /* enum */ TrialSpawnerState WAITING_FOR_PLAYERS = new TrialSpawnerState("waiting_for_players", 4, ParticleEmission.SMALL_FLAMES, 200.0, true);
    public static final /* enum */ TrialSpawnerState ACTIVE = new TrialSpawnerState("active", 8, ParticleEmission.FLAMES_AND_SMOKE, 1000.0, true);
    public static final /* enum */ TrialSpawnerState WAITING_FOR_REWARD_EJECTION = new TrialSpawnerState("waiting_for_reward_ejection", 8, ParticleEmission.SMALL_FLAMES, -1.0, false);
    public static final /* enum */ TrialSpawnerState EJECTING_REWARD = new TrialSpawnerState("ejecting_reward", 8, ParticleEmission.SMALL_FLAMES, -1.0, false);
    public static final /* enum */ TrialSpawnerState COOLDOWN = new TrialSpawnerState("cooldown", 0, ParticleEmission.SMOKE_INSIDE_AND_TOP_FACE, -1.0, false);
    private static final float DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB = 40.0f;
    private static final int TIME_BETWEEN_EACH_EJECTION;
    private final String name;
    private final int lightLevel;
    private final double spinningMobSpeed;
    private final ParticleEmission particleEmission;
    private final boolean isCapableOfSpawning;
    private static final /* synthetic */ TrialSpawnerState[] $VALUES;

    public static TrialSpawnerState[] values() {
        return (TrialSpawnerState[])$VALUES.clone();
    }

    public static TrialSpawnerState valueOf(String $$0) {
        return Enum.valueOf(TrialSpawnerState.class, $$0);
    }

    private TrialSpawnerState(String $$0, int $$1, ParticleEmission $$2, double $$3, boolean $$4) {
        this.name = $$0;
        this.lightLevel = $$1;
        this.particleEmission = $$2;
        this.spinningMobSpeed = $$3;
        this.isCapableOfSpawning = $$4;
    }

    TrialSpawnerState tickAndGetNext(BlockPos $$0, TrialSpawner $$1, ServerLevel $$2) {
        TrialSpawnerStateData $$32 = $$1.getStateData();
        TrialSpawnerConfig $$42 = $$1.activeConfig();
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                if ($$32.getOrCreateDisplayEntity($$1, $$2, WAITING_FOR_PLAYERS) == null) {
                    yield this;
                }
                yield WAITING_FOR_PLAYERS;
            }
            case 1 -> {
                if (!$$1.canSpawnInLevel($$2)) {
                    $$32.resetStatistics();
                    yield this;
                }
                if (!$$32.hasMobToSpawn($$1, $$2.random)) {
                    yield INACTIVE;
                }
                $$32.tryDetectPlayers($$2, $$0, $$1);
                if ($$32.detectedPlayers.isEmpty()) {
                    yield this;
                }
                yield ACTIVE;
            }
            case 2 -> {
                if (!$$1.canSpawnInLevel($$2)) {
                    $$32.resetStatistics();
                    yield WAITING_FOR_PLAYERS;
                }
                if (!$$32.hasMobToSpawn($$1, $$2.random)) {
                    yield INACTIVE;
                }
                int $$5 = $$32.countAdditionalPlayers($$0);
                $$32.tryDetectPlayers($$2, $$0, $$1);
                if ($$1.isOminous()) {
                    this.spawnOminousOminousItemSpawner($$2, $$0, $$1);
                }
                if ($$32.hasFinishedSpawningAllMobs($$42, $$5)) {
                    if ($$32.haveAllCurrentMobsDied()) {
                        $$32.cooldownEndsAt = $$2.getGameTime() + (long)$$1.getTargetCooldownLength();
                        $$32.totalMobsSpawned = 0;
                        $$32.nextMobSpawnsAt = 0L;
                        yield WAITING_FOR_REWARD_EJECTION;
                    }
                } else if ($$32.isReadyToSpawnNextMob($$2, $$42, $$5)) {
                    $$1.spawnMob($$2, $$0).ifPresent($$4 -> {
                        $$0.currentMobs.add((UUID)$$4);
                        ++$$0.totalMobsSpawned;
                        $$0.nextMobSpawnsAt = $$2.getGameTime() + (long)$$42.ticksBetweenSpawn();
                        $$42.spawnPotentialsDefinition().getRandom($$2.getRandom()).ifPresent($$2 -> {
                            $$0.nextSpawnData = Optional.of($$2);
                            $$1.markUpdated();
                        });
                    });
                }
                yield this;
            }
            case 3 -> {
                if ($$32.isReadyToOpenShutter($$2, 40.0f, $$1.getTargetCooldownLength())) {
                    $$2.playSound(null, $$0, SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER, SoundSource.BLOCKS);
                    yield EJECTING_REWARD;
                }
                yield this;
            }
            case 4 -> {
                if (!$$32.isReadyToEjectItems($$2, TIME_BETWEEN_EACH_EJECTION, $$1.getTargetCooldownLength())) {
                    yield this;
                }
                if ($$32.detectedPlayers.isEmpty()) {
                    $$2.playSound(null, $$0, SoundEvents.TRIAL_SPAWNER_CLOSE_SHUTTER, SoundSource.BLOCKS);
                    $$32.ejectingLootTable = Optional.empty();
                    yield COOLDOWN;
                }
                if ($$32.ejectingLootTable.isEmpty()) {
                    $$32.ejectingLootTable = $$42.lootTablesToEject().getRandom($$2.getRandom());
                }
                $$32.ejectingLootTable.ifPresent($$3 -> $$1.ejectReward($$2, $$0, (ResourceKey<LootTable>)$$3));
                $$32.detectedPlayers.remove($$32.detectedPlayers.iterator().next());
                yield this;
            }
            case 5 -> {
                $$32.tryDetectPlayers($$2, $$0, $$1);
                if (!$$32.detectedPlayers.isEmpty()) {
                    $$32.totalMobsSpawned = 0;
                    $$32.nextMobSpawnsAt = 0L;
                    yield ACTIVE;
                }
                if ($$32.isCooldownFinished($$2)) {
                    $$1.removeOminous($$2, $$0);
                    $$32.reset();
                    yield WAITING_FOR_PLAYERS;
                }
                yield this;
            }
        };
    }

    private void spawnOminousOminousItemSpawner(ServerLevel $$0, BlockPos $$1, TrialSpawner $$2) {
        TrialSpawnerConfig $$42;
        TrialSpawnerStateData $$3 = $$2.getStateData();
        ItemStack $$5 = $$3.getDispensingItems($$0, $$42 = $$2.activeConfig(), $$1).getRandom($$0.random).orElse(ItemStack.EMPTY);
        if ($$5.isEmpty()) {
            return;
        }
        if (this.timeToSpawnItemSpawner($$0, $$3)) {
            TrialSpawnerState.calculatePositionToSpawnSpawner($$0, $$1, $$2, $$3).ifPresent($$4 -> {
                OminousItemSpawner $$5 = OminousItemSpawner.create($$0, $$5);
                $$5.snapTo((Vec3)$$4);
                $$0.addFreshEntity($$5);
                float $$6 = ($$0.getRandom().nextFloat() - $$0.getRandom().nextFloat()) * 0.2f + 1.0f;
                $$0.playSound(null, BlockPos.containing($$4), SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM_BEGIN, SoundSource.BLOCKS, 1.0f, $$6);
                $$2.cooldownEndsAt = $$0.getGameTime() + $$2.ominousConfig().ticksBetweenItemSpawners();
            });
        }
    }

    private static Optional<Vec3> calculatePositionToSpawnSpawner(ServerLevel $$0, BlockPos $$1, TrialSpawner $$22, TrialSpawnerStateData $$3) {
        List $$4 = $$3.detectedPlayers.stream().map($$0::getPlayerByUUID).filter(Objects::nonNull).filter($$2 -> !$$2.isCreative() && !$$2.isSpectator() && $$2.isAlive() && $$2.distanceToSqr($$1.getCenter()) <= (double)Mth.square($$22.getRequiredPlayerRange())).toList();
        if ($$4.isEmpty()) {
            return Optional.empty();
        }
        Entity $$5 = TrialSpawnerState.selectEntityToSpawnItemAbove($$4, $$3.currentMobs, $$22, $$1, $$0);
        if ($$5 == null) {
            return Optional.empty();
        }
        return TrialSpawnerState.calculatePositionAbove($$5, $$0);
    }

    private static Optional<Vec3> calculatePositionAbove(Entity $$0, ServerLevel $$1) {
        Vec3 $$3;
        Vec3 $$2 = $$0.position();
        BlockHitResult $$4 = $$1.clip(new ClipContext($$2, $$3 = $$2.relative(Direction.UP, $$0.getBbHeight() + 2.0f + (float)$$1.random.nextInt(4)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
        Vec3 $$5 = $$4.getBlockPos().getCenter().relative(Direction.DOWN, 1.0);
        BlockPos $$6 = BlockPos.containing($$5);
        if (!$$1.getBlockState($$6).getCollisionShape($$1, $$6).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of($$5);
    }

    @Nullable
    private static Entity selectEntityToSpawnItemAbove(List<Player> $$0, Set<UUID> $$1, TrialSpawner $$22, BlockPos $$3, ServerLevel $$4) {
        List $$6;
        Stream<Entity> $$5 = $$1.stream().map($$4::getEntity).filter(Objects::nonNull).filter($$2 -> $$2.isAlive() && $$2.distanceToSqr($$3.getCenter()) <= (double)Mth.square($$22.getRequiredPlayerRange()));
        List list = $$6 = $$4.random.nextBoolean() ? $$5.toList() : $$0;
        if ($$6.isEmpty()) {
            return null;
        }
        if ($$6.size() == 1) {
            return (Entity)$$6.getFirst();
        }
        return (Entity)Util.getRandom($$6, $$4.random);
    }

    private boolean timeToSpawnItemSpawner(ServerLevel $$0, TrialSpawnerStateData $$1) {
        return $$0.getGameTime() >= $$1.cooldownEndsAt;
    }

    public int lightLevel() {
        return this.lightLevel;
    }

    public double spinningMobSpeed() {
        return this.spinningMobSpeed;
    }

    public boolean hasSpinningMob() {
        return this.spinningMobSpeed >= 0.0;
    }

    public boolean isCapableOfSpawning() {
        return this.isCapableOfSpawning;
    }

    public void emitParticles(Level $$0, BlockPos $$1, boolean $$2) {
        this.particleEmission.emit($$0, $$0.getRandom(), $$1, $$2);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ TrialSpawnerState[] f() {
        return new TrialSpawnerState[]{INACTIVE, WAITING_FOR_PLAYERS, ACTIVE, WAITING_FOR_REWARD_EJECTION, EJECTING_REWARD, COOLDOWN};
    }

    static {
        $VALUES = TrialSpawnerState.f();
        TIME_BETWEEN_EACH_EJECTION = Mth.floor(30.0f);
    }

    static interface ParticleEmission {
        public static final ParticleEmission NONE = ($$0, $$1, $$2, $$3) -> {};
        public static final ParticleEmission SMALL_FLAMES = ($$0, $$1, $$2, $$3) -> {
            if ($$1.nextInt(2) == 0) {
                Vec3 $$4 = $$2.getCenter().offsetRandom($$1, 0.9f);
                ParticleEmission.addParticle($$3 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, $$4, $$0);
            }
        };
        public static final ParticleEmission FLAMES_AND_SMOKE = ($$0, $$1, $$2, $$3) -> {
            Vec3 $$4 = $$2.getCenter().offsetRandom($$1, 1.0f);
            ParticleEmission.addParticle(ParticleTypes.SMOKE, $$4, $$0);
            ParticleEmission.addParticle($$3 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, $$4, $$0);
        };
        public static final ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = ($$0, $$1, $$2, $$3) -> {
            Vec3 $$4 = $$2.getCenter().offsetRandom($$1, 0.9f);
            if ($$1.nextInt(3) == 0) {
                ParticleEmission.addParticle(ParticleTypes.SMOKE, $$4, $$0);
            }
            if ($$0.getGameTime() % 20L == 0L) {
                Vec3 $$5 = $$2.getCenter().add(0.0, 0.5, 0.0);
                int $$6 = $$0.getRandom().nextInt(4) + 20;
                for (int $$7 = 0; $$7 < $$6; ++$$7) {
                    ParticleEmission.addParticle(ParticleTypes.SMOKE, $$5, $$0);
                }
            }
        };

        private static void addParticle(SimpleParticleType $$0, Vec3 $$1, Level $$2) {
            $$2.addParticle($$0, $$1.x(), $$1.y(), $$1.z(), 0.0, 0.0, 0.0);
        }

        public void emit(Level var1, RandomSource var2, BlockPos var3, boolean var4);
    }

    static class LightLevel {
        private static final int UNLIT = 0;
        private static final int HALF_LIT = 4;
        private static final int LIT = 8;

        private LightLevel() {
        }
    }

    static class SpinningMob {
        private static final double NONE = -1.0;
        private static final double SLOW = 200.0;
        private static final double FAST = 1000.0;

        private SpinningMob() {
        }
    }
}


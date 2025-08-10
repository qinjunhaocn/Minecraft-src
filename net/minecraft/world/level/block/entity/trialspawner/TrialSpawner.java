/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.slf4j.Logger;

public final class TrialSpawner {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
    private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
    private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
    private static final int MAX_MOB_TRACKING_DISTANCE = 47;
    private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
    private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02f;
    private final TrialSpawnerStateData data = new TrialSpawnerStateData();
    private FullConfig config;
    private final StateAccessor stateAccessor;
    private PlayerDetector playerDetector;
    private final PlayerDetector.EntitySelector entitySelector;
    private boolean overridePeacefulAndMobSpawnRule;
    private boolean isOminous;

    public TrialSpawner(FullConfig $$0, StateAccessor $$1, PlayerDetector $$2, PlayerDetector.EntitySelector $$3) {
        this.config = $$0;
        this.stateAccessor = $$1;
        this.playerDetector = $$2;
        this.entitySelector = $$3;
    }

    public TrialSpawnerConfig activeConfig() {
        return this.isOminous ? this.config.ominous().value() : this.config.normal.value();
    }

    public TrialSpawnerConfig normalConfig() {
        return this.config.normal.value();
    }

    public TrialSpawnerConfig ominousConfig() {
        return this.config.ominous.value();
    }

    public void load(ValueInput $$0) {
        $$0.read(TrialSpawnerStateData.Packed.MAP_CODEC).ifPresent(this.data::apply);
        this.config = $$0.read(FullConfig.MAP_CODEC).orElse(FullConfig.DEFAULT);
    }

    public void store(ValueOutput $$0) {
        $$0.store(TrialSpawnerStateData.Packed.MAP_CODEC, this.data.pack());
        $$0.store(FullConfig.MAP_CODEC, this.config);
    }

    public void applyOminous(ServerLevel $$0, BlockPos $$1) {
        $$0.setBlock($$1, (BlockState)$$0.getBlockState($$1).setValue(TrialSpawnerBlock.OMINOUS, true), 3);
        $$0.levelEvent(3020, $$1, 1);
        this.isOminous = true;
        this.data.resetAfterBecomingOminous(this, $$0);
    }

    public void removeOminous(ServerLevel $$0, BlockPos $$1) {
        $$0.setBlock($$1, (BlockState)$$0.getBlockState($$1).setValue(TrialSpawnerBlock.OMINOUS, false), 3);
        this.isOminous = false;
    }

    public boolean isOminous() {
        return this.isOminous;
    }

    public int getTargetCooldownLength() {
        return this.config.targetCooldownLength;
    }

    public int getRequiredPlayerRange() {
        return this.config.requiredPlayerRange;
    }

    public TrialSpawnerState getState() {
        return this.stateAccessor.getState();
    }

    public TrialSpawnerStateData getStateData() {
        return this.data;
    }

    public void setState(Level $$0, TrialSpawnerState $$1) {
        this.stateAccessor.setState($$0, $$1);
    }

    public void markUpdated() {
        this.stateAccessor.markUpdated();
    }

    public PlayerDetector getPlayerDetector() {
        return this.playerDetector;
    }

    public PlayerDetector.EntitySelector getEntitySelector() {
        return this.entitySelector;
    }

    public boolean canSpawnInLevel(ServerLevel $$0) {
        if (this.overridePeacefulAndMobSpawnRule) {
            return true;
        }
        if ($$0.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        return $$0.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
    }

    public Optional<UUID> spawnMob(ServerLevel $$0, BlockPos $$1) {
        RandomSource $$22 = $$0.getRandom();
        SpawnData $$3 = this.data.getOrCreateNextSpawnData(this, $$0.getRandom());
        try (ProblemReporter.ScopedCollector $$4 = new ProblemReporter.ScopedCollector(() -> "spawner@" + String.valueOf($$1), LOGGER);){
            Object $$11;
            SpawnData.CustomSpawnRules $$9;
            ValueInput $$5 = TagValueInput.create((ProblemReporter)$$4, (HolderLookup.Provider)$$0.registryAccess(), $$3.entityToSpawn());
            Optional<EntityType<?>> $$6 = EntityType.by($$5);
            if ($$6.isEmpty()) {
                Optional<UUID> optional = Optional.empty();
                return optional;
            }
            Vec3 $$7 = $$5.read("Pos", Vec3.CODEC).orElseGet(() -> {
                TrialSpawnerConfig $$2 = this.activeConfig();
                return new Vec3((double)$$1.getX() + ($$22.nextDouble() - $$22.nextDouble()) * (double)$$2.spawnRange() + 0.5, $$1.getY() + $$22.nextInt(3) - 1, (double)$$1.getZ() + ($$22.nextDouble() - $$22.nextDouble()) * (double)$$2.spawnRange() + 0.5);
            });
            if (!$$0.noCollision($$6.get().getSpawnAABB($$7.x, $$7.y, $$7.z))) {
                Optional<UUID> optional = Optional.empty();
                return optional;
            }
            if (!TrialSpawner.inLineOfSight($$0, $$1.getCenter(), $$7)) {
                Optional<UUID> optional = Optional.empty();
                return optional;
            }
            BlockPos $$8 = BlockPos.containing($$7);
            if (!SpawnPlacements.checkSpawnRules($$6.get(), $$0, EntitySpawnReason.TRIAL_SPAWNER, $$8, $$0.getRandom())) {
                Optional<UUID> optional = Optional.empty();
                return optional;
            }
            if ($$3.getCustomSpawnRules().isPresent() && !($$9 = $$3.getCustomSpawnRules().get()).isValidPosition($$8, $$0)) {
                Optional<UUID> optional = Optional.empty();
                return optional;
            }
            Entity $$10 = EntityType.loadEntityRecursive($$5, (Level)$$0, EntitySpawnReason.TRIAL_SPAWNER, $$2 -> {
                $$2.snapTo($$0.x, $$0.y, $$0.z, $$22.nextFloat() * 360.0f, 0.0f);
                return $$2;
            });
            if ($$10 == null) {
                Optional<UUID> optional = Optional.empty();
                return optional;
            }
            if ($$10 instanceof Mob) {
                boolean $$12;
                $$11 = (Mob)$$10;
                if (!((Mob)$$11).checkSpawnObstruction($$0)) {
                    Optional<UUID> optional = Optional.empty();
                    return optional;
                }
                boolean bl = $$12 = $$3.getEntityToSpawn().size() == 1 && $$3.getEntityToSpawn().getString("id").isPresent();
                if ($$12) {
                    ((Mob)$$11).finalizeSpawn($$0, $$0.getCurrentDifficultyAt(((Entity)$$11).blockPosition()), EntitySpawnReason.TRIAL_SPAWNER, null);
                }
                ((Mob)$$11).setPersistenceRequired();
                $$3.getEquipment().ifPresent(((Mob)$$11)::equip);
            }
            if (!$$0.tryAddFreshEntityWithPassengers($$10)) {
                $$11 = Optional.empty();
                return $$11;
            }
            FlameParticle $$13 = this.isOminous ? FlameParticle.OMINOUS : FlameParticle.NORMAL;
            $$0.levelEvent(3011, $$1, $$13.encode());
            $$0.levelEvent(3012, $$8, $$13.encode());
            $$0.gameEvent($$10, GameEvent.ENTITY_PLACE, $$8);
            Optional<UUID> optional = Optional.of($$10.getUUID());
            return optional;
        }
    }

    public void ejectReward(ServerLevel $$0, BlockPos $$1, ResourceKey<LootTable> $$2) {
        LootParams $$4;
        LootTable $$3 = $$0.getServer().reloadableRegistries().getLootTable($$2);
        ObjectArrayList<ItemStack> $$5 = $$3.getRandomItems($$4 = new LootParams.Builder($$0).create(LootContextParamSets.EMPTY));
        if (!$$5.isEmpty()) {
            for (ItemStack $$6 : $$5) {
                DefaultDispenseItemBehavior.spawnItem($$0, $$6, 2, Direction.UP, Vec3.atBottomCenterOf($$1).relative(Direction.UP, 1.2));
            }
            $$0.levelEvent(3014, $$1, 0);
        }
    }

    public void tickClient(Level $$0, BlockPos $$1, boolean $$2) {
        RandomSource $$5;
        TrialSpawnerState $$3 = this.getState();
        $$3.emitParticles($$0, $$1, $$2);
        if ($$3.hasSpinningMob()) {
            double $$4 = Math.max(0L, this.data.nextMobSpawnsAt - $$0.getGameTime());
            this.data.oSpin = this.data.spin;
            this.data.spin = (this.data.spin + $$3.spinningMobSpeed() / ($$4 + 200.0)) % 360.0;
        }
        if ($$3.isCapableOfSpawning() && ($$5 = $$0.getRandom()).nextFloat() <= 0.02f) {
            SoundEvent $$6 = $$2 ? SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS : SoundEvents.TRIAL_SPAWNER_AMBIENT;
            $$0.playLocalSound($$1, $$6, SoundSource.BLOCKS, $$5.nextFloat() * 0.25f + 0.75f, $$5.nextFloat() + 0.5f, false);
        }
    }

    public void tickServer(ServerLevel $$0, BlockPos $$1, boolean $$22) {
        TrialSpawnerState $$4;
        this.isOminous = $$22;
        TrialSpawnerState $$3 = this.getState();
        if (this.data.currentMobs.removeIf($$2 -> TrialSpawner.shouldMobBeUntracked($$0, $$1, $$2))) {
            this.data.nextMobSpawnsAt = $$0.getGameTime() + (long)this.activeConfig().ticksBetweenSpawn();
        }
        if (($$4 = $$3.tickAndGetNext($$1, this, $$0)) != $$3) {
            this.setState($$0, $$4);
        }
    }

    private static boolean shouldMobBeUntracked(ServerLevel $$0, BlockPos $$1, UUID $$2) {
        Entity $$3 = $$0.getEntity($$2);
        return $$3 == null || !$$3.isAlive() || !$$3.level().dimension().equals($$0.dimension()) || $$3.blockPosition().distSqr($$1) > (double)MAX_MOB_TRACKING_DISTANCE_SQR;
    }

    private static boolean inLineOfSight(Level $$0, Vec3 $$1, Vec3 $$2) {
        BlockHitResult $$3 = $$0.clip(new ClipContext($$2, $$1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
        return $$3.getBlockPos().equals(BlockPos.containing($$1)) || $$3.getType() == HitResult.Type.MISS;
    }

    public static void addSpawnParticles(Level $$0, BlockPos $$1, RandomSource $$2, SimpleParticleType $$3) {
        for (int $$4 = 0; $$4 < 20; ++$$4) {
            double $$5 = (double)$$1.getX() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
            double $$6 = (double)$$1.getY() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
            double $$7 = (double)$$1.getZ() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
            $$0.addParticle(ParticleTypes.SMOKE, $$5, $$6, $$7, 0.0, 0.0, 0.0);
            $$0.addParticle($$3, $$5, $$6, $$7, 0.0, 0.0, 0.0);
        }
    }

    public static void addBecomeOminousParticles(Level $$0, BlockPos $$1, RandomSource $$2) {
        for (int $$3 = 0; $$3 < 20; ++$$3) {
            double $$4 = (double)$$1.getX() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
            double $$5 = (double)$$1.getY() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
            double $$6 = (double)$$1.getZ() + 0.5 + ($$2.nextDouble() - 0.5) * 2.0;
            double $$7 = $$2.nextGaussian() * 0.02;
            double $$8 = $$2.nextGaussian() * 0.02;
            double $$9 = $$2.nextGaussian() * 0.02;
            $$0.addParticle(ParticleTypes.TRIAL_OMEN, $$4, $$5, $$6, $$7, $$8, $$9);
            $$0.addParticle(ParticleTypes.SOUL_FIRE_FLAME, $$4, $$5, $$6, $$7, $$8, $$9);
        }
    }

    public static void addDetectPlayerParticles(Level $$0, BlockPos $$1, RandomSource $$2, int $$3, ParticleOptions $$4) {
        for (int $$5 = 0; $$5 < 30 + Math.min($$3, 10) * 5; ++$$5) {
            double $$6 = (double)(2.0f * $$2.nextFloat() - 1.0f) * 0.65;
            double $$7 = (double)(2.0f * $$2.nextFloat() - 1.0f) * 0.65;
            double $$8 = (double)$$1.getX() + 0.5 + $$6;
            double $$9 = (double)$$1.getY() + 0.1 + (double)$$2.nextFloat() * 0.8;
            double $$10 = (double)$$1.getZ() + 0.5 + $$7;
            $$0.addParticle($$4, $$8, $$9, $$10, 0.0, 0.0, 0.0);
        }
    }

    public static void addEjectItemParticles(Level $$0, BlockPos $$1, RandomSource $$2) {
        for (int $$3 = 0; $$3 < 20; ++$$3) {
            double $$4 = (double)$$1.getX() + 0.4 + $$2.nextDouble() * 0.2;
            double $$5 = (double)$$1.getY() + 0.4 + $$2.nextDouble() * 0.2;
            double $$6 = (double)$$1.getZ() + 0.4 + $$2.nextDouble() * 0.2;
            double $$7 = $$2.nextGaussian() * 0.02;
            double $$8 = $$2.nextGaussian() * 0.02;
            double $$9 = $$2.nextGaussian() * 0.02;
            $$0.addParticle(ParticleTypes.SMALL_FLAME, $$4, $$5, $$6, $$7, $$8, $$9 * 0.25);
            $$0.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, $$7, $$8, $$9);
        }
    }

    public void overrideEntityToSpawn(EntityType<?> $$0, Level $$1) {
        this.data.reset();
        this.config = this.config.overrideEntity($$0);
        this.setState($$1, TrialSpawnerState.INACTIVE);
    }

    @Deprecated(forRemoval=true)
    @VisibleForTesting
    public void setPlayerDetector(PlayerDetector $$0) {
        this.playerDetector = $$0;
    }

    @Deprecated(forRemoval=true)
    @VisibleForTesting
    public void overridePeacefulAndMobSpawnRule() {
        this.overridePeacefulAndMobSpawnRule = true;
    }

    public static final class FullConfig
    extends Record {
        final Holder<TrialSpawnerConfig> normal;
        final Holder<TrialSpawnerConfig> ominous;
        final int targetCooldownLength;
        final int requiredPlayerRange;
        public static final MapCodec<FullConfig> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)TrialSpawnerConfig.CODEC.optionalFieldOf("normal_config", Holder.direct(TrialSpawnerConfig.DEFAULT)).forGetter(FullConfig::normal), (App)TrialSpawnerConfig.CODEC.optionalFieldOf("ominous_config", Holder.direct(TrialSpawnerConfig.DEFAULT)).forGetter(FullConfig::ominous), (App)ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("target_cooldown_length", (Object)36000).forGetter(FullConfig::targetCooldownLength), (App)Codec.intRange((int)1, (int)128).optionalFieldOf("required_player_range", (Object)14).forGetter(FullConfig::requiredPlayerRange)).apply((Applicative)$$0, FullConfig::new));
        public static final FullConfig DEFAULT = new FullConfig(Holder.direct(TrialSpawnerConfig.DEFAULT), Holder.direct(TrialSpawnerConfig.DEFAULT), 36000, 14);

        public FullConfig(Holder<TrialSpawnerConfig> $$0, Holder<TrialSpawnerConfig> $$1, int $$2, int $$3) {
            this.normal = $$0;
            this.ominous = $$1;
            this.targetCooldownLength = $$2;
            this.requiredPlayerRange = $$3;
        }

        public FullConfig overrideEntity(EntityType<?> $$0) {
            return new FullConfig(Holder.direct(this.normal.value().withSpawning($$0)), Holder.direct(this.ominous.value().withSpawning($$0)), this.targetCooldownLength, this.requiredPlayerRange);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{FullConfig.class, "normal;ominous;targetCooldownLength;requiredPlayerRange", "normal", "ominous", "targetCooldownLength", "requiredPlayerRange"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FullConfig.class, "normal;ominous;targetCooldownLength;requiredPlayerRange", "normal", "ominous", "targetCooldownLength", "requiredPlayerRange"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FullConfig.class, "normal;ominous;targetCooldownLength;requiredPlayerRange", "normal", "ominous", "targetCooldownLength", "requiredPlayerRange"}, this, $$0);
        }

        public Holder<TrialSpawnerConfig> normal() {
            return this.normal;
        }

        public Holder<TrialSpawnerConfig> ominous() {
            return this.ominous;
        }

        public int targetCooldownLength() {
            return this.targetCooldownLength;
        }

        public int requiredPlayerRange() {
            return this.requiredPlayerRange;
        }
    }

    public static interface StateAccessor {
        public void setState(Level var1, TrialSpawnerState var2);

        public TrialSpawnerState getState();

        public void markUpdated();
    }

    public static final class FlameParticle
    extends Enum<FlameParticle> {
        public static final /* enum */ FlameParticle NORMAL = new FlameParticle(ParticleTypes.FLAME);
        public static final /* enum */ FlameParticle OMINOUS = new FlameParticle(ParticleTypes.SOUL_FIRE_FLAME);
        public final SimpleParticleType particleType;
        private static final /* synthetic */ FlameParticle[] $VALUES;

        public static FlameParticle[] values() {
            return (FlameParticle[])$VALUES.clone();
        }

        public static FlameParticle valueOf(String $$0) {
            return Enum.valueOf(FlameParticle.class, $$0);
        }

        private FlameParticle(SimpleParticleType $$0) {
            this.particleType = $$0;
        }

        public static FlameParticle decode(int $$0) {
            FlameParticle[] $$1 = FlameParticle.values();
            if ($$0 > $$1.length || $$0 < 0) {
                return NORMAL;
            }
            return $$1[$$0];
        }

        public int encode() {
            return this.ordinal();
        }

        private static /* synthetic */ FlameParticle[] b() {
            return new FlameParticle[]{NORMAL, OMINOUS};
        }

        static {
            $VALUES = FlameParticle.b();
        }
    }
}


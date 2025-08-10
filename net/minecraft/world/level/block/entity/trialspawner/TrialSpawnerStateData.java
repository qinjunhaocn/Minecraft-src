/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TrialSpawnerStateData {
    private static final String TAG_SPAWN_DATA = "spawn_data";
    private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";
    private static final int DELAY_BETWEEN_PLAYER_SCANS = 20;
    private static final int TRIAL_OMEN_PER_BAD_OMEN_LEVEL = 18000;
    final Set<UUID> detectedPlayers = new HashSet<UUID>();
    final Set<UUID> currentMobs = new HashSet<UUID>();
    long cooldownEndsAt;
    long nextMobSpawnsAt;
    int totalMobsSpawned;
    Optional<SpawnData> nextSpawnData = Optional.empty();
    Optional<ResourceKey<LootTable>> ejectingLootTable = Optional.empty();
    @Nullable
    private Entity displayEntity;
    @Nullable
    private WeightedList<ItemStack> dispensing;
    double spin;
    double oSpin;

    public Packed pack() {
        return new Packed(Set.copyOf(this.detectedPlayers), Set.copyOf(this.currentMobs), this.cooldownEndsAt, this.nextMobSpawnsAt, this.totalMobsSpawned, this.nextSpawnData, this.ejectingLootTable);
    }

    public void apply(Packed $$0) {
        this.detectedPlayers.clear();
        this.detectedPlayers.addAll($$0.detectedPlayers);
        this.currentMobs.clear();
        this.currentMobs.addAll($$0.currentMobs);
        this.cooldownEndsAt = $$0.cooldownEndsAt;
        this.nextMobSpawnsAt = $$0.nextMobSpawnsAt;
        this.totalMobsSpawned = $$0.totalMobsSpawned;
        this.nextSpawnData = $$0.nextSpawnData;
        this.ejectingLootTable = $$0.ejectingLootTable;
    }

    public void reset() {
        this.currentMobs.clear();
        this.nextSpawnData = Optional.empty();
        this.resetStatistics();
    }

    public void resetStatistics() {
        this.detectedPlayers.clear();
        this.totalMobsSpawned = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEndsAt = 0L;
    }

    public boolean hasMobToSpawn(TrialSpawner $$0, RandomSource $$1) {
        boolean $$2 = this.getOrCreateNextSpawnData($$0, $$1).getEntityToSpawn().getString("id").isPresent();
        return $$2 || !$$0.activeConfig().spawnPotentialsDefinition().isEmpty();
    }

    public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig $$0, int $$1) {
        return this.totalMobsSpawned >= $$0.calculateTargetTotalMobs($$1);
    }

    public boolean haveAllCurrentMobsDied() {
        return this.currentMobs.isEmpty();
    }

    public boolean isReadyToSpawnNextMob(ServerLevel $$0, TrialSpawnerConfig $$1, int $$2) {
        return $$0.getGameTime() >= this.nextMobSpawnsAt && this.currentMobs.size() < $$1.calculateTargetSimultaneousMobs($$2);
    }

    public int countAdditionalPlayers(BlockPos $$0) {
        if (this.detectedPlayers.isEmpty()) {
            Util.logAndPauseIfInIde("Trial Spawner at " + String.valueOf($$0) + " has no detected players");
        }
        return Math.max(0, this.detectedPlayers.size() - 1);
    }

    public void tryDetectPlayers(ServerLevel $$0, BlockPos $$1, TrialSpawner $$2) {
        List<UUID> $$9;
        boolean $$7;
        boolean $$32;
        boolean bl = $$32 = ($$1.asLong() + $$0.getGameTime()) % 20L != 0L;
        if ($$32) {
            return;
        }
        if ($$2.getState().equals(TrialSpawnerState.COOLDOWN) && $$2.isOminous()) {
            return;
        }
        List<UUID> $$4 = $$2.getPlayerDetector().detect($$0, $$2.getEntitySelector(), $$1, $$2.getRequiredPlayerRange(), true);
        if ($$2.isOminous() || $$4.isEmpty()) {
            boolean $$5 = false;
        } else {
            Optional<Pair<Player, Holder<MobEffect>>> $$6 = TrialSpawnerStateData.findPlayerWithOminousEffect($$0, $$4);
            $$6.ifPresent($$3 -> {
                Player $$4 = (Player)$$3.getFirst();
                if ($$3.getSecond() == MobEffects.BAD_OMEN) {
                    TrialSpawnerStateData.transformBadOmenIntoTrialOmen($$4);
                }
                $$0.levelEvent(3020, BlockPos.containing($$4.getEyePosition()), 0);
                $$2.applyOminous($$0, $$1);
            });
            $$7 = $$6.isPresent();
        }
        if ($$2.getState().equals(TrialSpawnerState.COOLDOWN) && !$$7) {
            return;
        }
        boolean $$8 = $$2.getStateData().detectedPlayers.isEmpty();
        List<UUID> list = $$9 = $$8 ? $$4 : $$2.getPlayerDetector().detect($$0, $$2.getEntitySelector(), $$1, $$2.getRequiredPlayerRange(), false);
        if (this.detectedPlayers.addAll($$9)) {
            this.nextMobSpawnsAt = Math.max($$0.getGameTime() + 40L, this.nextMobSpawnsAt);
            if (!$$7) {
                int $$10 = $$2.isOminous() ? 3019 : 3013;
                $$0.levelEvent($$10, $$1, this.detectedPlayers.size());
            }
        }
    }

    private static Optional<Pair<Player, Holder<MobEffect>>> findPlayerWithOminousEffect(ServerLevel $$02, List<UUID> $$1) {
        Player $$2 = null;
        for (UUID $$3 : $$1) {
            Player $$4 = $$02.getPlayerByUUID($$3);
            if ($$4 == null) continue;
            Holder<MobEffect> $$5 = MobEffects.TRIAL_OMEN;
            if ($$4.hasEffect($$5)) {
                return Optional.of(Pair.of((Object)$$4, $$5));
            }
            if (!$$4.hasEffect(MobEffects.BAD_OMEN)) continue;
            $$2 = $$4;
        }
        return Optional.ofNullable($$2).map($$0 -> Pair.of((Object)$$0, MobEffects.BAD_OMEN));
    }

    public void resetAfterBecomingOminous(TrialSpawner $$0, ServerLevel $$12) {
        this.currentMobs.stream().map($$12::getEntity).forEach($$1 -> {
            if ($$1 == null) {
                return;
            }
            $$12.levelEvent(3012, $$1.blockPosition(), TrialSpawner.FlameParticle.NORMAL.encode());
            if ($$1 instanceof Mob) {
                Mob $$2 = (Mob)$$1;
                $$2.dropPreservedEquipment($$12);
            }
            $$1.remove(Entity.RemovalReason.DISCARDED);
        });
        if (!$$0.ominousConfig().spawnPotentialsDefinition().isEmpty()) {
            this.nextSpawnData = Optional.empty();
        }
        this.totalMobsSpawned = 0;
        this.currentMobs.clear();
        this.nextMobSpawnsAt = $$12.getGameTime() + (long)$$0.ominousConfig().ticksBetweenSpawn();
        $$0.markUpdated();
        this.cooldownEndsAt = $$12.getGameTime() + $$0.ominousConfig().ticksBetweenItemSpawners();
    }

    private static void transformBadOmenIntoTrialOmen(Player $$0) {
        MobEffectInstance $$1 = $$0.getEffect(MobEffects.BAD_OMEN);
        if ($$1 == null) {
            return;
        }
        int $$2 = $$1.getAmplifier() + 1;
        int $$3 = 18000 * $$2;
        $$0.removeEffect(MobEffects.BAD_OMEN);
        $$0.addEffect(new MobEffectInstance(MobEffects.TRIAL_OMEN, $$3, 0));
    }

    public boolean isReadyToOpenShutter(ServerLevel $$0, float $$1, int $$2) {
        long $$3 = this.cooldownEndsAt - (long)$$2;
        return (float)$$0.getGameTime() >= (float)$$3 + $$1;
    }

    public boolean isReadyToEjectItems(ServerLevel $$0, float $$1, int $$2) {
        long $$3 = this.cooldownEndsAt - (long)$$2;
        return (float)($$0.getGameTime() - $$3) % $$1 == 0.0f;
    }

    public boolean isCooldownFinished(ServerLevel $$0) {
        return $$0.getGameTime() >= this.cooldownEndsAt;
    }

    protected SpawnData getOrCreateNextSpawnData(TrialSpawner $$0, RandomSource $$1) {
        if (this.nextSpawnData.isPresent()) {
            return this.nextSpawnData.get();
        }
        WeightedList<SpawnData> $$2 = $$0.activeConfig().spawnPotentialsDefinition();
        Optional<SpawnData> $$3 = $$2.isEmpty() ? this.nextSpawnData : $$2.getRandom($$1);
        this.nextSpawnData = Optional.of($$3.orElseGet(SpawnData::new));
        $$0.markUpdated();
        return this.nextSpawnData.get();
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(TrialSpawner $$0, Level $$1, TrialSpawnerState $$2) {
        CompoundTag $$3;
        if (!$$2.hasSpinningMob()) {
            return null;
        }
        if (this.displayEntity == null && ($$3 = this.getOrCreateNextSpawnData($$0, $$1.getRandom()).getEntityToSpawn()).getString("id").isPresent()) {
            this.displayEntity = EntityType.loadEntityRecursive($$3, $$1, EntitySpawnReason.TRIAL_SPAWNER, Function.identity());
        }
        return this.displayEntity;
    }

    public CompoundTag getUpdateTag(TrialSpawnerState $$0) {
        CompoundTag $$12 = new CompoundTag();
        if ($$0 == TrialSpawnerState.ACTIVE) {
            $$12.putLong(TAG_NEXT_MOB_SPAWNS_AT, this.nextMobSpawnsAt);
        }
        this.nextSpawnData.ifPresent($$1 -> $$12.store(TAG_SPAWN_DATA, SpawnData.CODEC, $$1));
        return $$12;
    }

    public double getSpin() {
        return this.spin;
    }

    public double getOSpin() {
        return this.oSpin;
    }

    WeightedList<ItemStack> getDispensingItems(ServerLevel $$0, TrialSpawnerConfig $$1, BlockPos $$2) {
        long $$5;
        LootParams $$4;
        if (this.dispensing != null) {
            return this.dispensing;
        }
        LootTable $$3 = $$0.getServer().reloadableRegistries().getLootTable($$1.itemsToDropWhenOminous());
        ObjectArrayList<ItemStack> $$6 = $$3.getRandomItems($$4 = new LootParams.Builder($$0).create(LootContextParamSets.EMPTY), $$5 = TrialSpawnerStateData.lowResolutionPosition($$0, $$2));
        if ($$6.isEmpty()) {
            return WeightedList.of();
        }
        WeightedList.Builder<ItemStack> $$7 = WeightedList.builder();
        for (ItemStack $$8 : $$6) {
            $$7.add($$8.copyWithCount(1), $$8.getCount());
        }
        this.dispensing = $$7.build();
        return this.dispensing;
    }

    private static long lowResolutionPosition(ServerLevel $$0, BlockPos $$1) {
        BlockPos $$2 = new BlockPos(Mth.floor((float)$$1.getX() / 30.0f), Mth.floor((float)$$1.getY() / 20.0f), Mth.floor((float)$$1.getZ() / 30.0f));
        return $$0.getSeed() + $$2.asLong();
    }

    public static final class Packed
    extends Record {
        final Set<UUID> detectedPlayers;
        final Set<UUID> currentMobs;
        final long cooldownEndsAt;
        final long nextMobSpawnsAt;
        final int totalMobsSpawned;
        final Optional<SpawnData> nextSpawnData;
        final Optional<ResourceKey<LootTable>> ejectingLootTable;
        public static final MapCodec<Packed> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)UUIDUtil.CODEC_SET.lenientOptionalFieldOf("registered_players", (Object)Set.of()).forGetter(Packed::detectedPlayers), (App)UUIDUtil.CODEC_SET.lenientOptionalFieldOf("current_mobs", (Object)Set.of()).forGetter(Packed::currentMobs), (App)Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", (Object)0L).forGetter(Packed::cooldownEndsAt), (App)Codec.LONG.lenientOptionalFieldOf(TrialSpawnerStateData.TAG_NEXT_MOB_SPAWNS_AT, (Object)0L).forGetter(Packed::nextMobSpawnsAt), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).lenientOptionalFieldOf("total_mobs_spawned", (Object)0).forGetter(Packed::totalMobsSpawned), (App)SpawnData.CODEC.lenientOptionalFieldOf(TrialSpawnerStateData.TAG_SPAWN_DATA).forGetter(Packed::nextSpawnData), (App)LootTable.KEY_CODEC.lenientOptionalFieldOf("ejecting_loot_table").forGetter(Packed::ejectingLootTable)).apply((Applicative)$$0, Packed::new));

        public Packed(Set<UUID> $$0, Set<UUID> $$1, long $$2, long $$3, int $$4, Optional<SpawnData> $$5, Optional<ResourceKey<LootTable>> $$6) {
            this.detectedPlayers = $$0;
            this.currentMobs = $$1;
            this.cooldownEndsAt = $$2;
            this.nextMobSpawnsAt = $$3;
            this.totalMobsSpawned = $$4;
            this.nextSpawnData = $$5;
            this.ejectingLootTable = $$6;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Packed.class, "detectedPlayers;currentMobs;cooldownEndsAt;nextMobSpawnsAt;totalMobsSpawned;nextSpawnData;ejectingLootTable", "detectedPlayers", "currentMobs", "cooldownEndsAt", "nextMobSpawnsAt", "totalMobsSpawned", "nextSpawnData", "ejectingLootTable"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Packed.class, "detectedPlayers;currentMobs;cooldownEndsAt;nextMobSpawnsAt;totalMobsSpawned;nextSpawnData;ejectingLootTable", "detectedPlayers", "currentMobs", "cooldownEndsAt", "nextMobSpawnsAt", "totalMobsSpawned", "nextSpawnData", "ejectingLootTable"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Packed.class, "detectedPlayers;currentMobs;cooldownEndsAt;nextMobSpawnsAt;totalMobsSpawned;nextSpawnData;ejectingLootTable", "detectedPlayers", "currentMobs", "cooldownEndsAt", "nextMobSpawnsAt", "totalMobsSpawned", "nextSpawnData", "ejectingLootTable"}, this, $$0);
        }

        public Set<UUID> detectedPlayers() {
            return this.detectedPlayers;
        }

        public Set<UUID> currentMobs() {
            return this.currentMobs;
        }

        public long cooldownEndsAt() {
            return this.cooldownEndsAt;
        }

        public long nextMobSpawnsAt() {
            return this.nextMobSpawnsAt;
        }

        public int totalMobsSpawned() {
            return this.totalMobsSpawned;
        }

        public Optional<SpawnData> nextSpawnData() {
            return this.nextSpawnData;
        }

        public Optional<ResourceKey<LootTable>> ejectingLootTable() {
            return this.ejectingLootTable;
        }
    }
}


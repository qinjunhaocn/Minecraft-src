/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.block.entity.trialspawner;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class TrialSpawnerConfigs {
    private static final Keys TRIAL_CHAMBER_BREEZE = Keys.of("trial_chamber/breeze");
    private static final Keys TRIAL_CHAMBER_MELEE_HUSK = Keys.of("trial_chamber/melee/husk");
    private static final Keys TRIAL_CHAMBER_MELEE_SPIDER = Keys.of("trial_chamber/melee/spider");
    private static final Keys TRIAL_CHAMBER_MELEE_ZOMBIE = Keys.of("trial_chamber/melee/zombie");
    private static final Keys TRIAL_CHAMBER_RANGED_POISON_SKELETON = Keys.of("trial_chamber/ranged/poison_skeleton");
    private static final Keys TRIAL_CHAMBER_RANGED_SKELETON = Keys.of("trial_chamber/ranged/skeleton");
    private static final Keys TRIAL_CHAMBER_RANGED_STRAY = Keys.of("trial_chamber/ranged/stray");
    private static final Keys TRIAL_CHAMBER_SLOW_RANGED_POISON_SKELETON = Keys.of("trial_chamber/slow_ranged/poison_skeleton");
    private static final Keys TRIAL_CHAMBER_SLOW_RANGED_SKELETON = Keys.of("trial_chamber/slow_ranged/skeleton");
    private static final Keys TRIAL_CHAMBER_SLOW_RANGED_STRAY = Keys.of("trial_chamber/slow_ranged/stray");
    private static final Keys TRIAL_CHAMBER_SMALL_MELEE_BABY_ZOMBIE = Keys.of("trial_chamber/small_melee/baby_zombie");
    private static final Keys TRIAL_CHAMBER_SMALL_MELEE_CAVE_SPIDER = Keys.of("trial_chamber/small_melee/cave_spider");
    private static final Keys TRIAL_CHAMBER_SMALL_MELEE_SILVERFISH = Keys.of("trial_chamber/small_melee/silverfish");
    private static final Keys TRIAL_CHAMBER_SMALL_MELEE_SLIME = Keys.of("trial_chamber/small_melee/slime");

    public static void bootstrap(BootstrapContext<TrialSpawnerConfig> $$02) {
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_BREEZE, TrialSpawnerConfig.builder().simultaneousMobs(1.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(2.0f).totalMobsAddedPerPlayer(1.0f).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.BREEZE))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(4.0f).totalMobsAddedPerPlayer(1.0f).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.BREEZE))).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_MELEE_HUSK, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.HUSK))).build(), TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.HUSK, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE))).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_MELEE_SPIDER, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.SPIDER))).build(), TrialSpawnerConfigs.trialChamberMeleeOminous().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.SPIDER))).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_MELEE_ZOMBIE, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.ZOMBIE))).build(), TrialSpawnerConfigs.trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.ZOMBIE, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_RANGED_POISON_SKELETON, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.BOGGED))).build(), TrialSpawnerConfigs.trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.BOGGED, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_RANGED_SKELETON, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.SKELETON))).build(), TrialSpawnerConfigs.trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.SKELETON, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_RANGED_STRAY, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.STRAY))).build(), TrialSpawnerConfigs.trialChamberBase().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.STRAY, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_SLOW_RANGED_POISON_SKELETON, TrialSpawnerConfigs.trialChamberSlowRanged().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.BOGGED))).build(), TrialSpawnerConfigs.trialChamberSlowRanged().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.BOGGED, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_SLOW_RANGED_SKELETON, TrialSpawnerConfigs.trialChamberSlowRanged().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.SKELETON))).build(), TrialSpawnerConfigs.trialChamberSlowRanged().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.SKELETON, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_SLOW_RANGED_STRAY, TrialSpawnerConfigs.trialChamberSlowRanged().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.STRAY))).build(), TrialSpawnerConfigs.trialChamberSlowRanged().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnDataWithEquipment(EntityType.STRAY, BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_RANGED))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_SMALL_MELEE_BABY_ZOMBIE, TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.customSpawnDataWithEquipment(EntityType.ZOMBIE, $$0 -> $$0.putBoolean("IsBaby", true), null))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.customSpawnDataWithEquipment(EntityType.ZOMBIE, $$0 -> $$0.putBoolean("IsBaby", true), BuiltInLootTables.EQUIPMENT_TRIAL_CHAMBER_MELEE))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_SMALL_MELEE_CAVE_SPIDER, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.CAVE_SPIDER))).build(), TrialSpawnerConfigs.trialChamberMeleeOminous().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.CAVE_SPIDER))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_SMALL_MELEE_SILVERFISH, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.SILVERFISH))).build(), TrialSpawnerConfigs.trialChamberMeleeOminous().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.of(TrialSpawnerConfigs.spawnData(EntityType.SILVERFISH))).build());
        TrialSpawnerConfigs.register($$02, TRIAL_CHAMBER_SMALL_MELEE_SLIME, TrialSpawnerConfigs.trialChamberBase().spawnPotentialsDefinition(WeightedList.builder().add(TrialSpawnerConfigs.customSpawnData(EntityType.SLIME, $$0 -> $$0.putByte("Size", (byte)1)), 3).add(TrialSpawnerConfigs.customSpawnData(EntityType.SLIME, $$0 -> $$0.putByte("Size", (byte)2)), 1).build()).build(), TrialSpawnerConfigs.trialChamberMeleeOminous().lootTablesToEject(WeightedList.builder().add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY, 3).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES, 7).build()).spawnPotentialsDefinition(WeightedList.builder().add(TrialSpawnerConfigs.customSpawnData(EntityType.SLIME, $$0 -> $$0.putByte("Size", (byte)1)), 3).add(TrialSpawnerConfigs.customSpawnData(EntityType.SLIME, $$0 -> $$0.putByte("Size", (byte)2)), 1).build()).build());
    }

    private static <T extends Entity> SpawnData spawnData(EntityType<T> $$02) {
        return TrialSpawnerConfigs.customSpawnDataWithEquipment($$02, $$0 -> {}, null);
    }

    private static <T extends Entity> SpawnData customSpawnData(EntityType<T> $$0, Consumer<CompoundTag> $$1) {
        return TrialSpawnerConfigs.customSpawnDataWithEquipment($$0, $$1, null);
    }

    private static <T extends Entity> SpawnData spawnDataWithEquipment(EntityType<T> $$02, ResourceKey<LootTable> $$1) {
        return TrialSpawnerConfigs.customSpawnDataWithEquipment($$02, $$0 -> {}, $$1);
    }

    private static <T extends Entity> SpawnData customSpawnDataWithEquipment(EntityType<T> $$02, Consumer<CompoundTag> $$1, @Nullable ResourceKey<LootTable> $$2) {
        CompoundTag $$3 = new CompoundTag();
        $$3.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey($$02).toString());
        $$1.accept($$3);
        Optional<EquipmentTable> $$4 = Optional.ofNullable($$2).map($$0 -> new EquipmentTable((ResourceKey<LootTable>)$$0, 0.0f));
        return new SpawnData($$3, Optional.empty(), $$4);
    }

    private static void register(BootstrapContext<TrialSpawnerConfig> $$0, Keys $$1, TrialSpawnerConfig $$2, TrialSpawnerConfig $$3) {
        $$0.register($$1.normal, $$2);
        $$0.register($$1.ominous, $$3);
    }

    static ResourceKey<TrialSpawnerConfig> registryKey(String $$0) {
        return ResourceKey.create(Registries.TRIAL_SPAWNER_CONFIG, ResourceLocation.withDefaultNamespace($$0));
    }

    private static TrialSpawnerConfig.Builder trialChamberMeleeOminous() {
        return TrialSpawnerConfig.builder().simultaneousMobs(4.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(12.0f);
    }

    private static TrialSpawnerConfig.Builder trialChamberSlowRanged() {
        return TrialSpawnerConfig.builder().simultaneousMobs(4.0f).simultaneousMobsAddedPerPlayer(2.0f).ticksBetweenSpawn(160);
    }

    private static TrialSpawnerConfig.Builder trialChamberBase() {
        return TrialSpawnerConfig.builder().simultaneousMobs(3.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20);
    }

    static final class Keys
    extends Record {
        final ResourceKey<TrialSpawnerConfig> normal;
        final ResourceKey<TrialSpawnerConfig> ominous;

        private Keys(ResourceKey<TrialSpawnerConfig> $$0, ResourceKey<TrialSpawnerConfig> $$1) {
            this.normal = $$0;
            this.ominous = $$1;
        }

        public static Keys of(String $$0) {
            return new Keys(TrialSpawnerConfigs.registryKey($$0 + "/normal"), TrialSpawnerConfigs.registryKey($$0 + "/ominous"));
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Keys.class, "normal;ominous", "normal", "ominous"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Keys.class, "normal;ominous", "normal", "ominous"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Keys.class, "normal;ominous", "normal", "ominous"}, this, $$0);
        }

        public ResourceKey<TrialSpawnerConfig> normal() {
            return this.normal;
        }

        public ResourceKey<TrialSpawnerConfig> ominous() {
            return this.ominous;
        }
    }
}


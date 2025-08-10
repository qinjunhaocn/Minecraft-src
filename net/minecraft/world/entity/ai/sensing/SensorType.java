/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.AdultSensor;
import net.minecraft.world.entity.ai.sensing.AdultSensorAnyType;
import net.minecraft.world.entity.ai.sensing.AxolotlAttackablesSensor;
import net.minecraft.world.entity.ai.sensing.BreezeAttackEntitySensor;
import net.minecraft.world.entity.ai.sensing.DummySensor;
import net.minecraft.world.entity.ai.sensing.FrogAttackablesSensor;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.HoglinSpecificSensor;
import net.minecraft.world.entity.ai.sensing.HurtBySensor;
import net.minecraft.world.entity.ai.sensing.IsInWaterSensor;
import net.minecraft.world.entity.ai.sensing.MobSensor;
import net.minecraft.world.entity.ai.sensing.NearestBedSensor;
import net.minecraft.world.entity.ai.sensing.NearestItemSensor;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.PiglinBruteSpecificSensor;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.ai.sensing.SecondaryPoiSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraft.world.entity.ai.sensing.VillagerBabiesSensor;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.ai.sensing.WardenEntitySensor;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.armadillo.ArmadilloAi;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.animal.camel.CamelAi;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.minecraft.world.entity.animal.goat.GoatAi;
import net.minecraft.world.entity.animal.sniffer.SnifferAi;

public class SensorType<U extends Sensor<?>> {
    public static final SensorType<DummySensor> DUMMY = SensorType.register("dummy", DummySensor::new);
    public static final SensorType<NearestItemSensor> NEAREST_ITEMS = SensorType.register("nearest_items", NearestItemSensor::new);
    public static final SensorType<NearestLivingEntitySensor<LivingEntity>> NEAREST_LIVING_ENTITIES = SensorType.register("nearest_living_entities", NearestLivingEntitySensor::new);
    public static final SensorType<PlayerSensor> NEAREST_PLAYERS = SensorType.register("nearest_players", PlayerSensor::new);
    public static final SensorType<NearestBedSensor> NEAREST_BED = SensorType.register("nearest_bed", NearestBedSensor::new);
    public static final SensorType<HurtBySensor> HURT_BY = SensorType.register("hurt_by", HurtBySensor::new);
    public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = SensorType.register("villager_hostiles", VillagerHostilesSensor::new);
    public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = SensorType.register("villager_babies", VillagerBabiesSensor::new);
    public static final SensorType<SecondaryPoiSensor> SECONDARY_POIS = SensorType.register("secondary_pois", SecondaryPoiSensor::new);
    public static final SensorType<GolemSensor> GOLEM_DETECTED = SensorType.register("golem_detected", GolemSensor::new);
    public static final SensorType<MobSensor<Armadillo>> ARMADILLO_SCARE_DETECTED = SensorType.register("armadillo_scare_detected", () -> new MobSensor<Armadillo>(5, Armadillo::isScaredBy, Armadillo::canStayRolledUp, MemoryModuleType.DANGER_DETECTED_RECENTLY, 80));
    public static final SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = SensorType.register("piglin_specific_sensor", PiglinSpecificSensor::new);
    public static final SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = SensorType.register("piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new);
    public static final SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = SensorType.register("hoglin_specific_sensor", HoglinSpecificSensor::new);
    public static final SensorType<AdultSensor> NEAREST_ADULT = SensorType.register("nearest_adult", AdultSensor::new);
    public static final SensorType<AdultSensor> NEAREST_ADULT_ANY_TYPE = SensorType.register("nearest_adult_any_type", AdultSensorAnyType::new);
    public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = SensorType.register("axolotl_attackables", AxolotlAttackablesSensor::new);
    public static final SensorType<TemptingSensor> AXOLOTL_TEMPTATIONS = SensorType.register("axolotl_temptations", () -> new TemptingSensor(AxolotlAi.getTemptations()));
    public static final SensorType<TemptingSensor> GOAT_TEMPTATIONS = SensorType.register("goat_temptations", () -> new TemptingSensor(GoatAi.getTemptations()));
    public static final SensorType<TemptingSensor> FROG_TEMPTATIONS = SensorType.register("frog_temptations", () -> new TemptingSensor(FrogAi.getTemptations()));
    public static final SensorType<TemptingSensor> CAMEL_TEMPTATIONS = SensorType.register("camel_temptations", () -> new TemptingSensor(CamelAi.getTemptations()));
    public static final SensorType<TemptingSensor> ARMADILLO_TEMPTATIONS = SensorType.register("armadillo_temptations", () -> new TemptingSensor(ArmadilloAi.getTemptations()));
    public static final SensorType<TemptingSensor> HAPPY_GHAST_TEMPTATIONS = SensorType.register("happy_ghast_temptations", () -> new TemptingSensor(HappyGhast.IS_FOOD));
    public static final SensorType<FrogAttackablesSensor> FROG_ATTACKABLES = SensorType.register("frog_attackables", FrogAttackablesSensor::new);
    public static final SensorType<IsInWaterSensor> IS_IN_WATER = SensorType.register("is_in_water", IsInWaterSensor::new);
    public static final SensorType<WardenEntitySensor> WARDEN_ENTITY_SENSOR = SensorType.register("warden_entity_sensor", WardenEntitySensor::new);
    public static final SensorType<TemptingSensor> SNIFFER_TEMPTATIONS = SensorType.register("sniffer_temptations", () -> new TemptingSensor(SnifferAi.getTemptations()));
    public static final SensorType<BreezeAttackEntitySensor> BREEZE_ATTACK_ENTITY_SENSOR = SensorType.register("breeze_attack_entity_sensor", BreezeAttackEntitySensor::new);
    private final Supplier<U> factory;

    private SensorType(Supplier<U> $$0) {
        this.factory = $$0;
    }

    public U create() {
        return (U)((Sensor)this.factory.get());
    }

    private static <U extends Sensor<?>> SensorType<U> register(String $$0, Supplier<U> $$1) {
        return Registry.register(BuiltInRegistries.SENSOR_TYPE, ResourceLocation.withDefaultNamespace($$0), new SensorType<U>($$1));
    }
}


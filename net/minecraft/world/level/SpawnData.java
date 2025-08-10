/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.level.LightLayer;

public record SpawnData(CompoundTag entityToSpawn, Optional<CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
    public static final String ENTITY_TAG = "entity";
    public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)CompoundTag.CODEC.fieldOf(ENTITY_TAG).forGetter($$0 -> $$0.entityToSpawn), (App)CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter($$0 -> $$0.customSpawnRules), (App)EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter($$0 -> $$0.equipment)).apply((Applicative)$$02, SpawnData::new));
    public static final Codec<WeightedList<SpawnData>> LIST_CODEC = WeightedList.codec(CODEC);

    public SpawnData() {
        this(new CompoundTag(), Optional.empty(), Optional.empty());
    }

    public SpawnData {
        Optional<ResourceLocation> $$3 = $$0.read("id", ResourceLocation.CODEC);
        if ($$3.isPresent()) {
            $$0.store("id", ResourceLocation.CODEC, $$3.get());
        } else {
            $$0.remove("id");
        }
    }

    public CompoundTag getEntityToSpawn() {
        return this.entityToSpawn;
    }

    public Optional<CustomSpawnRules> getCustomSpawnRules() {
        return this.customSpawnRules;
    }

    public Optional<EquipmentTable> getEquipment() {
        return this.equipment;
    }

    public record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
        private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<Integer>(0, 15);
        public static final Codec<CustomSpawnRules> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)CustomSpawnRules.lightLimit("block_light_limit").forGetter($$0 -> $$0.blockLightLimit), (App)CustomSpawnRules.lightLimit("sky_light_limit").forGetter($$0 -> $$0.skyLightLimit)).apply((Applicative)$$02, CustomSpawnRules::new));

        private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> $$0) {
            if (!LIGHT_RANGE.contains($$0)) {
                return DataResult.error(() -> "Light values must be withing range " + String.valueOf(LIGHT_RANGE));
            }
            return DataResult.success($$0);
        }

        private static MapCodec<InclusiveRange<Integer>> lightLimit(String $$0) {
            return InclusiveRange.INT.lenientOptionalFieldOf($$0, LIGHT_RANGE).validate(CustomSpawnRules::checkLightBoundaries);
        }

        public boolean isValidPosition(BlockPos $$0, ServerLevel $$1) {
            return this.blockLightLimit.isValueInRange($$1.getBrightness(LightLayer.BLOCK, $$0)) && this.skyLightLimit.isValueInRange($$1.getBrightness(LightLayer.SKY, $$0));
        }
    }
}


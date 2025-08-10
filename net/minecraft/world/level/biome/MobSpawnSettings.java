/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;

public class MobSpawnSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1f;
    public static final WeightedList<SpawnerData> EMPTY_MOB_LIST = WeightedList.of();
    public static final MobSpawnSettings EMPTY = new Builder().build();
    public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.floatRange((float)0.0f, (float)0.9999999f).optionalFieldOf("creature_spawn_probability", (Object)Float.valueOf(0.1f)).forGetter($$0 -> Float.valueOf($$0.creatureGenerationProbability)), (App)Codec.simpleMap(MobCategory.CODEC, (Codec)WeightedList.codec(SpawnerData.CODEC).promotePartial(Util.prefix("Spawn data: ", LOGGER::error)), (Keyable)StringRepresentable.a(MobCategory.values())).fieldOf("spawners").forGetter($$0 -> $$0.spawners), (App)Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter($$0 -> $$0.mobSpawnCosts)).apply((Applicative)$$02, MobSpawnSettings::new));
    private final float creatureGenerationProbability;
    private final Map<MobCategory, WeightedList<SpawnerData>> spawners;
    private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;

    MobSpawnSettings(float $$0, Map<MobCategory, WeightedList<SpawnerData>> $$1, Map<EntityType<?>, MobSpawnCost> $$2) {
        this.creatureGenerationProbability = $$0;
        this.spawners = ImmutableMap.copyOf($$1);
        this.mobSpawnCosts = ImmutableMap.copyOf($$2);
    }

    public WeightedList<SpawnerData> getMobs(MobCategory $$0) {
        return this.spawners.getOrDefault($$0, EMPTY_MOB_LIST);
    }

    @Nullable
    public MobSpawnCost getMobSpawnCost(EntityType<?> $$0) {
        return this.mobSpawnCosts.get($$0);
    }

    public float getCreatureProbability() {
        return this.creatureGenerationProbability;
    }

    public record MobSpawnCost(double energyBudget, double charge) {
        public static final Codec<MobSpawnCost> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.DOUBLE.fieldOf("energy_budget").forGetter($$0 -> $$0.energyBudget), (App)Codec.DOUBLE.fieldOf("charge").forGetter($$0 -> $$0.charge)).apply((Applicative)$$02, MobSpawnCost::new));
    }

    public record SpawnerData(EntityType<?> type, int minCount, int maxCount) {
        public static final MapCodec<SpawnerData> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter($$0 -> $$0.type), (App)ExtraCodecs.POSITIVE_INT.fieldOf("minCount").forGetter($$0 -> $$0.minCount), (App)ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter($$0 -> $$0.maxCount)).apply((Applicative)$$02, SpawnerData::new)).validate($$0 -> {
            if ($$0.minCount > $$0.maxCount) {
                return DataResult.error(() -> "minCount needs to be smaller or equal to maxCount");
            }
            return DataResult.success((Object)$$0);
        });

        public SpawnerData {
            $$0 = $$0.getCategory() == MobCategory.MISC ? EntityType.PIG : $$0;
        }

        public String toString() {
            return String.valueOf(EntityType.getKey(this.type)) + "*(" + this.minCount + "-" + this.maxCount + ")";
        }
    }

    public static class Builder {
        private final Map<MobCategory, WeightedList.Builder<SpawnerData>> spawners = Util.makeEnumMap(MobCategory.class, $$0 -> WeightedList.builder());
        private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
        private float creatureGenerationProbability = 0.1f;

        public Builder addSpawn(MobCategory $$0, int $$1, SpawnerData $$2) {
            this.spawners.get($$0).add($$2, $$1);
            return this;
        }

        public Builder addMobCharge(EntityType<?> $$0, double $$1, double $$2) {
            this.mobSpawnCosts.put($$0, new MobSpawnCost($$2, $$1));
            return this;
        }

        public Builder creatureGenerationProbability(float $$0) {
            this.creatureGenerationProbability = $$0;
            return this;
        }

        public MobSpawnSettings build() {
            return new MobSpawnSettings(this.creatureGenerationProbability, (Map<MobCategory, WeightedList<SpawnerData>>)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> ((WeightedList.Builder)$$0.getValue()).build())), ImmutableMap.copyOf(this.mobSpawnCosts));
        }
    }
}


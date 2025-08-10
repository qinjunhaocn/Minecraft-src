/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import org.slf4j.Logger;

public class TrialSpawnerConfigInRegistryFix
extends NamedEntityFix {
    private static final Logger LOGGER = LogUtils.getLogger();

    public TrialSpawnerConfigInRegistryFix(Schema $$0) {
        super($$0, false, "TrialSpawnerConfigInRegistryFix", References.BLOCK_ENTITY, "minecraft:trial_spawner");
    }

    public Dynamic<?> fixTag(Dynamic<Tag> $$0) {
        Optional $$1 = $$0.get("normal_config").result();
        if ($$1.isEmpty()) {
            return $$0;
        }
        Optional $$2 = $$0.get("ominous_config").result();
        if ($$2.isEmpty()) {
            return $$0;
        }
        ResourceLocation $$3 = VanillaTrialChambers.CONFIGS_TO_KEY.get(Pair.of((Object)((Dynamic)$$1.get()), (Object)((Dynamic)$$2.get())));
        if ($$3 == null) {
            return $$0;
        }
        return $$0.set("normal_config", $$0.createString($$3.withSuffix("/normal").toString())).set("ominous_config", $$0.createString($$3.withSuffix("/ominous").toString()));
    }

    @Override
    protected Typed<?> fix(Typed<?> $$02) {
        return $$02.update(DSL.remainderFinder(), $$0 -> {
            DynamicOps $$1 = $$0.getOps();
            Dynamic<?> $$2 = this.fixTag((Dynamic<Tag>)$$0.convert((DynamicOps)NbtOps.INSTANCE));
            return $$2.convert($$1);
        });
    }

    static final class VanillaTrialChambers {
        public static final Map<Pair<Dynamic<Tag>, Dynamic<Tag>>, ResourceLocation> CONFIGS_TO_KEY = new HashMap<Pair<Dynamic<Tag>, Dynamic<Tag>>, ResourceLocation>();

        private VanillaTrialChambers() {
        }

        private static void register(ResourceLocation $$0, String $$1, String $$2) {
            try {
                CompoundTag $$3 = VanillaTrialChambers.parse($$1);
                CompoundTag $$4 = VanillaTrialChambers.parse($$2);
                CompoundTag $$5 = $$3.copy().merge($$4);
                CompoundTag $$6 = VanillaTrialChambers.removeDefaults($$5.copy());
                Dynamic<Tag> $$7 = VanillaTrialChambers.asDynamic($$3);
                CONFIGS_TO_KEY.put((Pair<Dynamic<Tag>, Dynamic<Tag>>)Pair.of($$7, VanillaTrialChambers.asDynamic($$4)), $$0);
                CONFIGS_TO_KEY.put((Pair<Dynamic<Tag>, Dynamic<Tag>>)Pair.of($$7, VanillaTrialChambers.asDynamic($$5)), $$0);
                CONFIGS_TO_KEY.put((Pair<Dynamic<Tag>, Dynamic<Tag>>)Pair.of($$7, VanillaTrialChambers.asDynamic($$6)), $$0);
            } catch (RuntimeException $$8) {
                throw new IllegalStateException("Failed to parse NBT for " + String.valueOf($$0), $$8);
            }
        }

        private static Dynamic<Tag> asDynamic(CompoundTag $$0) {
            return new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$0);
        }

        private static CompoundTag parse(String $$0) {
            try {
                return TagParser.parseCompoundFully($$0);
            } catch (CommandSyntaxException $$1) {
                throw new IllegalArgumentException("Failed to parse Trial Spawner NBT config: " + $$0, $$1);
            }
        }

        private static CompoundTag removeDefaults(CompoundTag $$0) {
            if ($$0.getIntOr("spawn_range", 0) == 4) {
                $$0.remove("spawn_range");
            }
            if ($$0.getFloatOr("total_mobs", 0.0f) == 6.0f) {
                $$0.remove("total_mobs");
            }
            if ($$0.getFloatOr("simultaneous_mobs", 0.0f) == 2.0f) {
                $$0.remove("simultaneous_mobs");
            }
            if ($$0.getFloatOr("total_mobs_added_per_player", 0.0f) == 2.0f) {
                $$0.remove("total_mobs_added_per_player");
            }
            if ($$0.getFloatOr("simultaneous_mobs_added_per_player", 0.0f) == 1.0f) {
                $$0.remove("simultaneous_mobs_added_per_player");
            }
            if ($$0.getIntOr("ticks_between_spawn", 0) == 40) {
                $$0.remove("ticks_between_spawn");
            }
            return $$0;
        }

        static {
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/breeze"), "{simultaneous_mobs: 1.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:breeze\"}}, weight: 1}], ticks_between_spawn: 20, total_mobs: 2.0f, total_mobs_added_per_player: 1.0f}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 2.0f, total_mobs: 4.0f}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/melee/husk"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:husk\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:husk\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/melee/spider"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:spider\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/melee/zombie"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:zombie\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:zombie\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/ranged/poison_skeleton"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/ranged/skeleton"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/ranged/stray"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/slow_ranged/poison_skeleton"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/slow_ranged/skeleton"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/slow_ranged/stray"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/baby_zombie"), "{simultaneous_mobs: 2.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {IsBaby: 1b, id: \"minecraft:zombie\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {IsBaby: 1b, id: \"minecraft:zombie\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/cave_spider"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:cave_spider\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/silverfish"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:silverfish\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
            VanillaTrialChambers.register(ResourceLocation.withDefaultNamespace("trial_chamber/small_melee/slime"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {Size: 1, id: \"minecraft:slime\"}}, weight: 3}, {data: {entity: {Size: 2, id: \"minecraft:slime\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
        }
    }
}


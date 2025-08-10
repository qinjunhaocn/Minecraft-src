/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.References;

public class ChunkStatusFix2
extends DataFix {
    private static final Map<String, String> RENAMES_AND_DOWNGRADES = ImmutableMap.builder().put("structure_references", "empty").put("biomes", "empty").put("base", "surface").put("carved", "carvers").put("liquid_carved", "liquid_carvers").put("decorated", "features").put("lighted", "light").put("mobs_spawned", "spawn").put("finalized", "heightmaps").put("fullchunk", "full").build();

    public ChunkStatusFix2(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    protected TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.CHUNK);
        Type $$12 = $$0.findFieldType("Level");
        OpticFinder $$2 = DSL.fieldFinder((String)"Level", (Type)$$12);
        return this.fixTypeEverywhereTyped("ChunkStatusFix2", $$0, this.getOutputSchema().getType(References.CHUNK), $$1 -> $$1.updateTyped($$2, $$0 -> {
            String $$3;
            Object $$1 = (Dynamic)$$0.get(DSL.remainderFinder());
            String $$2 = $$1.get("Status").asString("empty");
            if (Objects.equals($$2, $$3 = RENAMES_AND_DOWNGRADES.getOrDefault($$2, "empty"))) {
                return $$0;
            }
            return $$0.set(DSL.remainderFinder(), (Object)$$1.set("Status", $$1.createString($$3)));
        }));
    }
}


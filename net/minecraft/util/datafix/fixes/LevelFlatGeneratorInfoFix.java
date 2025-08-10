/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix
extends DataFix {
    private static final String GENERATOR_OPTIONS = "generatorOptions";
    @VisibleForTesting
    static final String DEFAULT = "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
    private static final Splitter SPLITTER = Splitter.on(';').limit(5);
    private static final Splitter LAYER_SPLITTER = Splitter.on(',');
    private static final Splitter OLD_AMOUNT_SPLITTER = Splitter.on('x').limit(2);
    private static final Splitter AMOUNT_SPLITTER = Splitter.on('*').limit(2);
    private static final Splitter BLOCK_SPLITTER = Splitter.on(':').limit(3);

    public LevelFlatGeneratorInfoFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(References.LEVEL), $$0 -> $$0.update(DSL.remainderFinder(), this::fix));
    }

    private Dynamic<?> fix(Dynamic<?> $$02) {
        if ($$02.get("generatorName").asString("").equalsIgnoreCase("flat")) {
            return $$02.update(GENERATOR_OPTIONS, $$0 -> (Dynamic)DataFixUtils.orElse((Optional)$$0.asString().map(this::fixString).map(arg_0 -> ((Dynamic)$$0).createString(arg_0)).result(), (Object)$$0));
        }
        return $$02;
    }

    @VisibleForTesting
    String fixString(String $$0) {
        String $$6;
        int $$5;
        if ($$0.isEmpty()) {
            return DEFAULT;
        }
        Iterator<String> $$1 = SPLITTER.split($$0).iterator();
        String $$22 = $$1.next();
        if ($$1.hasNext()) {
            int $$3 = NumberUtils.toInt($$22, 0);
            String $$4 = $$1.next();
        } else {
            $$5 = 0;
            $$6 = $$22;
        }
        if ($$5 < 0 || $$5 > 3) {
            return DEFAULT;
        }
        StringBuilder $$7 = new StringBuilder();
        Splitter $$8 = $$5 < 3 ? OLD_AMOUNT_SPLITTER : AMOUNT_SPLITTER;
        $$7.append(StreamSupport.stream(LAYER_SPLITTER.split($$6).spliterator(), false).map($$2 -> {
            String $$7;
            int $$6;
            List<String> $$3 = $$8.splitToList((CharSequence)$$2);
            if ($$3.size() == 2) {
                int $$4 = NumberUtils.toInt($$3.get(0));
                String $$5 = $$3.get(1);
            } else {
                $$6 = 1;
                $$7 = $$3.get(0);
            }
            List<String> $$8 = BLOCK_SPLITTER.splitToList($$7);
            int $$9 = $$8.get(0).equals("minecraft") ? 1 : 0;
            String $$10 = $$8.get($$9);
            int $$11 = $$5 == 3 ? EntityBlockStateFix.getBlockId("minecraft:" + $$10) : NumberUtils.toInt($$10, 0);
            int $$12 = $$9 + 1;
            int $$13 = $$8.size() > $$12 ? NumberUtils.toInt($$8.get($$12), 0) : 0;
            return (String)($$6 == 1 ? "" : $$6 + "*") + BlockStateData.getTag($$11 << 4 | $$13).get("Name").asString("");
        }).collect(Collectors.joining(",")));
        while ($$1.hasNext()) {
            $$7.append(';').append($$1.next());
        }
        return $$7.toString();
    }
}


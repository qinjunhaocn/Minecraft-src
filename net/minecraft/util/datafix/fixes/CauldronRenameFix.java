/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.References;

public class CauldronRenameFix
extends DataFix {
    public CauldronRenameFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    private static Dynamic<?> fix(Dynamic<?> $$0) {
        Optional $$1 = $$0.get("Name").asString().result();
        if ($$1.equals(Optional.of("minecraft:cauldron"))) {
            Dynamic $$2 = $$0.get("Properties").orElseEmptyMap();
            if ($$2.get("level").asString("0").equals("0")) {
                return $$0.remove("Properties");
            }
            return $$0.set("Name", $$0.createString("minecraft:water_cauldron"));
        }
        return $$0;
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("cauldron_rename_fix", this.getInputSchema().getType(References.BLOCK_STATE), $$0 -> $$0.update(DSL.remainderFinder(), CauldronRenameFix::fix));
    }
}


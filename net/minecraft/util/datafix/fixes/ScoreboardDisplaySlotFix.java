/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
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
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.fixes.References;

public class ScoreboardDisplaySlotFix
extends DataFix {
    private static final Map<String, String> SLOT_RENAMES = ImmutableMap.builder().put("slot_0", "list").put("slot_1", "sidebar").put("slot_2", "below_name").put("slot_3", "sidebar.team.black").put("slot_4", "sidebar.team.dark_blue").put("slot_5", "sidebar.team.dark_green").put("slot_6", "sidebar.team.dark_aqua").put("slot_7", "sidebar.team.dark_red").put("slot_8", "sidebar.team.dark_purple").put("slot_9", "sidebar.team.gold").put("slot_10", "sidebar.team.gray").put("slot_11", "sidebar.team.dark_gray").put("slot_12", "sidebar.team.blue").put("slot_13", "sidebar.team.green").put("slot_14", "sidebar.team.aqua").put("slot_15", "sidebar.team.red").put("slot_16", "sidebar.team.light_purple").put("slot_17", "sidebar.team.yellow").put("slot_18", "sidebar.team.white").build();

    public ScoreboardDisplaySlotFix(Schema $$0) {
        super($$0, false);
    }

    @Nullable
    private static String rename(String $$0) {
        return SLOT_RENAMES.get($$0);
    }

    protected TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.SAVED_DATA_SCOREBOARD);
        OpticFinder $$12 = $$0.findField("data");
        return this.fixTypeEverywhereTyped("Scoreboard DisplaySlot rename", $$0, $$1 -> $$1.updateTyped($$12, $$0 -> $$0.update(DSL.remainderFinder(), $$02 -> $$02.update("DisplaySlots", $$0 -> $$0.updateMapValues($$02 -> $$02.mapFirst($$0 -> (Dynamic)DataFixUtils.orElse($$0.asString().result().map(ScoreboardDisplaySlotFix::rename).map(arg_0 -> ((Dynamic)$$0).createString(arg_0)), (Object)$$0)))))));
    }
}


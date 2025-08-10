/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BannerPatternFormatFix
extends NamedEntityFix {
    private static final Map<String, String> PATTERN_ID_MAP = Map.ofEntries((Map.Entry[])new Map.Entry[]{Map.entry((Object)"b", (Object)"minecraft:base"), Map.entry((Object)"bl", (Object)"minecraft:square_bottom_left"), Map.entry((Object)"br", (Object)"minecraft:square_bottom_right"), Map.entry((Object)"tl", (Object)"minecraft:square_top_left"), Map.entry((Object)"tr", (Object)"minecraft:square_top_right"), Map.entry((Object)"bs", (Object)"minecraft:stripe_bottom"), Map.entry((Object)"ts", (Object)"minecraft:stripe_top"), Map.entry((Object)"ls", (Object)"minecraft:stripe_left"), Map.entry((Object)"rs", (Object)"minecraft:stripe_right"), Map.entry((Object)"cs", (Object)"minecraft:stripe_center"), Map.entry((Object)"ms", (Object)"minecraft:stripe_middle"), Map.entry((Object)"drs", (Object)"minecraft:stripe_downright"), Map.entry((Object)"dls", (Object)"minecraft:stripe_downleft"), Map.entry((Object)"ss", (Object)"minecraft:small_stripes"), Map.entry((Object)"cr", (Object)"minecraft:cross"), Map.entry((Object)"sc", (Object)"minecraft:straight_cross"), Map.entry((Object)"bt", (Object)"minecraft:triangle_bottom"), Map.entry((Object)"tt", (Object)"minecraft:triangle_top"), Map.entry((Object)"bts", (Object)"minecraft:triangles_bottom"), Map.entry((Object)"tts", (Object)"minecraft:triangles_top"), Map.entry((Object)"ld", (Object)"minecraft:diagonal_left"), Map.entry((Object)"rd", (Object)"minecraft:diagonal_up_right"), Map.entry((Object)"lud", (Object)"minecraft:diagonal_up_left"), Map.entry((Object)"rud", (Object)"minecraft:diagonal_right"), Map.entry((Object)"mc", (Object)"minecraft:circle"), Map.entry((Object)"mr", (Object)"minecraft:rhombus"), Map.entry((Object)"vh", (Object)"minecraft:half_vertical"), Map.entry((Object)"hh", (Object)"minecraft:half_horizontal"), Map.entry((Object)"vhr", (Object)"minecraft:half_vertical_right"), Map.entry((Object)"hhb", (Object)"minecraft:half_horizontal_bottom"), Map.entry((Object)"bo", (Object)"minecraft:border"), Map.entry((Object)"cbo", (Object)"minecraft:curly_border"), Map.entry((Object)"gra", (Object)"minecraft:gradient"), Map.entry((Object)"gru", (Object)"minecraft:gradient_up"), Map.entry((Object)"bri", (Object)"minecraft:bricks"), Map.entry((Object)"glb", (Object)"minecraft:globe"), Map.entry((Object)"cre", (Object)"minecraft:creeper"), Map.entry((Object)"sku", (Object)"minecraft:skull"), Map.entry((Object)"flo", (Object)"minecraft:flower"), Map.entry((Object)"moj", (Object)"minecraft:mojang"), Map.entry((Object)"pig", (Object)"minecraft:piglin")});

    public BannerPatternFormatFix(Schema $$0) {
        super($$0, false, "BannerPatternFormatFix", References.BLOCK_ENTITY, "minecraft:banner");
    }

    @Override
    protected Typed<?> fix(Typed<?> $$0) {
        return $$0.update(DSL.remainderFinder(), BannerPatternFormatFix::fixTag);
    }

    private static Dynamic<?> fixTag(Dynamic<?> $$02) {
        return $$02.renameAndFixField("Patterns", "patterns", $$0 -> $$0.createList($$0.asStream().map(BannerPatternFormatFix::fixLayer)));
    }

    private static Dynamic<?> fixLayer(Dynamic<?> $$0) {
        $$0 = $$0.renameAndFixField("Pattern", "pattern", $$02 -> (Dynamic)DataFixUtils.orElse((Optional)$$02.asString().map($$0 -> PATTERN_ID_MAP.getOrDefault($$0, (String)$$0)).map(arg_0 -> ((Dynamic)$$02).createString(arg_0)).result(), (Object)$$02));
        $$0 = $$0.set("color", $$0.createString(ExtraDataFixUtils.dyeColorIdToName($$0.get("Color").asInt(0))));
        $$0 = $$0.remove("Color");
        return $$0;
    }
}


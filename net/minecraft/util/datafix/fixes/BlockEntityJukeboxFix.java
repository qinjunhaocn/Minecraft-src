/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityJukeboxFix
extends NamedEntityFix {
    public BlockEntityJukeboxFix(Schema $$0, boolean $$1) {
        super($$0, $$1, "BlockEntityJukeboxFix", References.BLOCK_ENTITY, "minecraft:jukebox");
    }

    @Override
    protected Typed<?> fix(Typed<?> $$0) {
        Type $$1 = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:jukebox");
        Type $$2 = $$1.findFieldType("RecordItem");
        OpticFinder $$3 = DSL.fieldFinder((String)"RecordItem", (Type)$$2);
        Dynamic $$4 = (Dynamic)$$0.get(DSL.remainderFinder());
        int $$5 = $$4.get("Record").asInt(0);
        if ($$5 > 0) {
            $$4.remove("Record");
            String $$6 = ItemStackTheFlatteningFix.updateItem(ItemIdFix.getItem($$5), 0);
            if ($$6 != null) {
                Dynamic $$7 = $$4.emptyMap();
                $$7 = $$7.set("id", $$7.createString($$6));
                $$7 = $$7.set("Count", $$7.createByte((byte)1));
                return $$0.set($$3, (Typed)((Pair)$$2.readTyped($$7).result().orElseThrow(() -> new IllegalStateException("Could not create record item stack."))).getFirst()).set(DSL.remainderFinder(), (Object)$$4);
            }
        }
        return $$0;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1481
extends NamespacedSchema {
    public V1481(int $$0, Schema $$1) {
        super($$0, $$1);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
        Map $$1 = super.registerBlockEntities($$0);
        $$0.registerSimple($$1, "minecraft:conduit");
        return $$1;
    }
}


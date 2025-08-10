/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.fixes.DataComponentRemainderFix;

public class LockComponentPredicateFix
extends DataComponentRemainderFix {
    public static final Escaper ESCAPER = Escapers.builder().addEscape('\"', "\\\"").addEscape('\\', "\\\\").build();

    public LockComponentPredicateFix(Schema $$0) {
        super($$0, "LockComponentPredicateFix", "minecraft:lock");
    }

    @Override
    @Nullable
    protected <T> Dynamic<T> fixComponent(Dynamic<T> $$0) {
        return LockComponentPredicateFix.fixLock($$0);
    }

    @Nullable
    public static <T> Dynamic<T> fixLock(Dynamic<T> $$0) {
        Optional $$1 = $$0.asString().result();
        if ($$1.isEmpty()) {
            return null;
        }
        if (((String)$$1.get()).isEmpty()) {
            return null;
        }
        Dynamic $$2 = $$0.createString("\"" + ESCAPER.escape((String)$$1.get()) + "\"");
        Dynamic $$3 = $$0.emptyMap().set("minecraft:custom_name", $$2);
        return $$0.emptyMap().set("components", $$3);
    }
}


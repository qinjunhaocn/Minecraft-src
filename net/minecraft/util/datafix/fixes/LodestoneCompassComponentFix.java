/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.DataComponentRemainderFix;

public class LodestoneCompassComponentFix
extends DataComponentRemainderFix {
    public LodestoneCompassComponentFix(Schema $$0) {
        super($$0, "LodestoneCompassComponentFix", "minecraft:lodestone_target", "minecraft:lodestone_tracker");
    }

    @Override
    protected <T> Dynamic<T> fixComponent(Dynamic<T> $$0) {
        Optional $$1 = $$0.get("pos").result();
        Optional $$2 = $$0.get("dimension").result();
        $$0 = $$0.remove("pos").remove("dimension");
        if ($$1.isPresent() && $$2.isPresent()) {
            $$0 = $$0.set("target", $$0.emptyMap().set("pos", (Dynamic)$$1.get()).set("dimension", (Dynamic)$$2.get()));
        }
        return $$0;
    }
}


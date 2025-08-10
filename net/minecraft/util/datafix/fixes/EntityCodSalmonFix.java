/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.util.datafix.fixes.SimplestEntityRenameFix;

public class EntityCodSalmonFix
extends SimplestEntityRenameFix {
    public static final Map<String, String> RENAMED_IDS = ImmutableMap.builder().put("minecraft:salmon_mob", "minecraft:salmon").put("minecraft:cod_mob", "minecraft:cod").build();
    public static final Map<String, String> RENAMED_EGG_IDS = ImmutableMap.builder().put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg").put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg").build();

    public EntityCodSalmonFix(Schema $$0, boolean $$1) {
        super("EntityCodSalmonFix", $$0, $$1);
    }

    @Override
    protected String rename(String $$0) {
        return RENAMED_IDS.getOrDefault($$0, $$0);
    }
}


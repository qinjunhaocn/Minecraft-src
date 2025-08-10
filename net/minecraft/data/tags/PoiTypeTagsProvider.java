/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public class PoiTypeTagsProvider
extends KeyTagProvider<PoiType> {
    public PoiTypeTagsProvider(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        super($$0, Registries.POINT_OF_INTEREST_TYPE, $$1);
    }

    @Override
    protected void addTags(HolderLookup.Provider $$0) {
        this.tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).a(PoiTypes.ARMORER, PoiTypes.BUTCHER, PoiTypes.CARTOGRAPHER, PoiTypes.CLERIC, PoiTypes.FARMER, PoiTypes.FISHERMAN, PoiTypes.FLETCHER, PoiTypes.LEATHERWORKER, PoiTypes.LIBRARIAN, PoiTypes.MASON, PoiTypes.SHEPHERD, PoiTypes.TOOLSMITH, PoiTypes.WEAPONSMITH);
        this.tag(PoiTypeTags.VILLAGE).addTag(PoiTypeTags.ACQUIRABLE_JOB_SITE).a(PoiTypes.HOME, PoiTypes.MEETING);
        this.tag(PoiTypeTags.BEE_HOME).a(PoiTypes.BEEHIVE, PoiTypes.BEE_NEST);
    }
}


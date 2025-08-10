/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSelectionEntry> structures, StructurePlacement placement) {
    public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)StructureSelectionEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), (App)StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply((Applicative)$$0, StructureSet::new));
    public static final Codec<Holder<StructureSet>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE_SET, DIRECT_CODEC);

    public StructureSet(Holder<Structure> $$0, StructurePlacement $$1) {
        this(List.of((Object)((Object)new StructureSelectionEntry($$0, 1))), $$1);
    }

    public static StructureSelectionEntry entry(Holder<Structure> $$0, int $$1) {
        return new StructureSelectionEntry($$0, $$1);
    }

    public static StructureSelectionEntry entry(Holder<Structure> $$0) {
        return new StructureSelectionEntry($$0, 1);
    }

    public record StructureSelectionEntry(Holder<Structure> structure, int weight) {
        public static final Codec<StructureSelectionEntry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Structure.CODEC.fieldOf("structure").forGetter(StructureSelectionEntry::structure), (App)ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSelectionEntry::weight)).apply((Applicative)$$0, StructureSelectionEntry::new));
    }
}


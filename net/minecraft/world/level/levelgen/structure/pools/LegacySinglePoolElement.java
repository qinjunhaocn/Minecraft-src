/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class LegacySinglePoolElement
extends SinglePoolElement {
    public static final MapCodec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group(LegacySinglePoolElement.templateCodec(), LegacySinglePoolElement.processorsCodec(), LegacySinglePoolElement.projectionCodec(), LegacySinglePoolElement.overrideLiquidSettingsCodec()).apply((Applicative)$$0, LegacySinglePoolElement::new));

    protected LegacySinglePoolElement(Either<ResourceLocation, StructureTemplate> $$0, Holder<StructureProcessorList> $$1, StructureTemplatePool.Projection $$2, Optional<LiquidSettings> $$3) {
        super($$0, $$1, $$2, $$3);
    }

    @Override
    protected StructurePlaceSettings getSettings(Rotation $$0, BoundingBox $$1, LiquidSettings $$2, boolean $$3) {
        StructurePlaceSettings $$4 = super.getSettings($$0, $$1, $$2, $$3);
        $$4.popProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        $$4.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        return $$4;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.LEGACY;
    }

    @Override
    public String toString() {
        return "LegacySingle[" + String.valueOf(this.template) + "]";
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SinglePoolElement
extends StructurePoolElement {
    private static final Comparator<StructureTemplate.JigsawBlockInfo> HIGHEST_SELECTION_PRIORITY_FIRST = Comparator.comparingInt(StructureTemplate.JigsawBlockInfo::selectionPriority).reversed();
    private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC = Codec.of(SinglePoolElement::encodeTemplate, (Decoder)ResourceLocation.CODEC.map(Either::left));
    public static final MapCodec<SinglePoolElement> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group(SinglePoolElement.templateCodec(), SinglePoolElement.processorsCodec(), SinglePoolElement.projectionCodec(), SinglePoolElement.overrideLiquidSettingsCodec()).apply((Applicative)$$0, SinglePoolElement::new));
    protected final Either<ResourceLocation, StructureTemplate> template;
    protected final Holder<StructureProcessorList> processors;
    protected final Optional<LiquidSettings> overrideLiquidSettings;

    private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> $$0, DynamicOps<T> $$1, T $$2) {
        Optional $$3 = $$0.left();
        if ($$3.isEmpty()) {
            return DataResult.error(() -> "Can not serialize a runtime pool element");
        }
        return ResourceLocation.CODEC.encode((Object)((ResourceLocation)$$3.get()), $$1, $$2);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
        return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter($$0 -> $$0.processors);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Optional<LiquidSettings>> overrideLiquidSettingsCodec() {
        return LiquidSettings.CODEC.optionalFieldOf("override_liquid_settings").forGetter($$0 -> $$0.overrideLiquidSettings);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
        return TEMPLATE_CODEC.fieldOf("location").forGetter($$0 -> $$0.template);
    }

    protected SinglePoolElement(Either<ResourceLocation, StructureTemplate> $$0, Holder<StructureProcessorList> $$1, StructureTemplatePool.Projection $$2, Optional<LiquidSettings> $$3) {
        super($$2);
        this.template = $$0;
        this.processors = $$1;
        this.overrideLiquidSettings = $$3;
    }

    @Override
    public Vec3i getSize(StructureTemplateManager $$0, Rotation $$1) {
        StructureTemplate $$2 = this.getTemplate($$0);
        return $$2.getSize($$1);
    }

    private StructureTemplate getTemplate(StructureTemplateManager $$0) {
        return (StructureTemplate)this.template.map($$0::getOrCreate, Function.identity());
    }

    public List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, boolean $$3) {
        StructureTemplate $$4 = this.getTemplate($$0);
        ObjectArrayList<StructureTemplate.StructureBlockInfo> $$5 = $$4.filterBlocks($$1, new StructurePlaceSettings().setRotation($$2), Blocks.STRUCTURE_BLOCK, $$3);
        ArrayList<StructureTemplate.StructureBlockInfo> $$6 = Lists.newArrayList();
        for (StructureTemplate.StructureBlockInfo $$7 : $$5) {
            StructureMode $$9;
            CompoundTag $$8 = $$7.nbt();
            if ($$8 == null || ($$9 = (StructureMode)$$8.read("mode", StructureMode.LEGACY_CODEC).orElseThrow()) != StructureMode.DATA) continue;
            $$6.add($$7);
        }
        return $$6;
    }

    @Override
    public List<StructureTemplate.JigsawBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, RandomSource $$3) {
        List<StructureTemplate.JigsawBlockInfo> $$4 = this.getTemplate($$0).getJigsaws($$1, $$2);
        Util.shuffle($$4, $$3);
        SinglePoolElement.sortBySelectionPriority($$4);
        return $$4;
    }

    @VisibleForTesting
    static void sortBySelectionPriority(List<StructureTemplate.JigsawBlockInfo> $$0) {
        $$0.sort(HIGHEST_SELECTION_PRIORITY_FIRST);
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2) {
        StructureTemplate $$3 = this.getTemplate($$0);
        return $$3.getBoundingBox(new StructurePlaceSettings().setRotation($$2), $$1);
    }

    @Override
    public boolean place(StructureTemplateManager $$0, WorldGenLevel $$1, StructureManager $$2, ChunkGenerator $$3, BlockPos $$4, BlockPos $$5, Rotation $$6, BoundingBox $$7, RandomSource $$8, LiquidSettings $$9, boolean $$10) {
        StructurePlaceSettings $$12;
        StructureTemplate $$11 = this.getTemplate($$0);
        if ($$11.placeInWorld($$1, $$4, $$5, $$12 = this.getSettings($$6, $$7, $$9, $$10), $$8, 18)) {
            List<StructureTemplate.StructureBlockInfo> $$13 = StructureTemplate.processBlockInfos($$1, $$4, $$5, $$12, this.getDataMarkers($$0, $$4, $$6, false));
            for (StructureTemplate.StructureBlockInfo $$14 : $$13) {
                this.handleDataMarker($$1, $$14, $$4, $$6, $$8, $$7);
            }
            return true;
        }
        return false;
    }

    protected StructurePlaceSettings getSettings(Rotation $$0, BoundingBox $$1, LiquidSettings $$2, boolean $$3) {
        StructurePlaceSettings $$4 = new StructurePlaceSettings();
        $$4.setBoundingBox($$1);
        $$4.setRotation($$0);
        $$4.setKnownShape(true);
        $$4.setIgnoreEntities(false);
        $$4.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        $$4.setFinalizeEntities(true);
        $$4.setLiquidSettings(this.overrideLiquidSettings.orElse($$2));
        if (!$$3) {
            $$4.addProcessor(JigsawReplacementProcessor.INSTANCE);
        }
        this.processors.value().list().forEach($$4::addProcessor);
        this.getProjection().getProcessors().forEach($$4::addProcessor);
        return $$4;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.SINGLE;
    }

    public String toString() {
        return "Single[" + String.valueOf(this.template) + "]";
    }

    @VisibleForTesting
    public ResourceLocation getTemplateLocation() {
        return (ResourceLocation)this.template.orThrow();
    }
}


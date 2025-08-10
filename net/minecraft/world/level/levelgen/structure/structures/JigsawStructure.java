/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.MatchException
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public final class JigsawStructure
extends Structure {
    public static final DimensionPadding DEFAULT_DIMENSION_PADDING = DimensionPadding.ZERO;
    public static final LiquidSettings DEFAULT_LIQUID_SETTINGS = LiquidSettings.APPLY_WATERLOGGING;
    public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
    public static final int MIN_DEPTH = 0;
    public static final int MAX_DEPTH = 20;
    public static final MapCodec<JigsawStructure> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group(JigsawStructure.settingsCodec($$02), (App)StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter($$0 -> $$0.startPool), (App)ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter($$0 -> $$0.startJigsawName), (App)Codec.intRange((int)0, (int)20).fieldOf("size").forGetter($$0 -> $$0.maxDepth), (App)HeightProvider.CODEC.fieldOf("start_height").forGetter($$0 -> $$0.startHeight), (App)Codec.BOOL.fieldOf("use_expansion_hack").forGetter($$0 -> $$0.useExpansionHack), (App)Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter($$0 -> $$0.projectStartToHeightmap), (App)Codec.intRange((int)1, (int)128).fieldOf("max_distance_from_center").forGetter($$0 -> $$0.maxDistanceFromCenter), (App)Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", (Object)List.of()).forGetter($$0 -> $$0.poolAliases), (App)DimensionPadding.CODEC.optionalFieldOf("dimension_padding", (Object)DEFAULT_DIMENSION_PADDING).forGetter($$0 -> $$0.dimensionPadding), (App)LiquidSettings.CODEC.optionalFieldOf("liquid_settings", (Object)DEFAULT_LIQUID_SETTINGS).forGetter($$0 -> $$0.liquidSettings)).apply((Applicative)$$02, JigsawStructure::new)).validate(JigsawStructure::verifyRange);
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    private static DataResult<JigsawStructure> verifyRange(JigsawStructure $$0) {
        int $$1;
        switch ($$0.terrainAdaptation()) {
            default: {
                throw new MatchException(null, null);
            }
            case NONE: {
                int n = 0;
                break;
            }
            case BURY: 
            case BEARD_THIN: 
            case BEARD_BOX: 
            case ENCAPSULATE: {
                int n = $$1 = 12;
            }
        }
        if ($$0.maxDistanceFromCenter + $$1 > 128) {
            return DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128");
        }
        return DataResult.success((Object)$$0);
    }

    public JigsawStructure(Structure.StructureSettings $$0, Holder<StructureTemplatePool> $$1, Optional<ResourceLocation> $$2, int $$3, HeightProvider $$4, boolean $$5, Optional<Heightmap.Types> $$6, int $$7, List<PoolAliasBinding> $$8, DimensionPadding $$9, LiquidSettings $$10) {
        super($$0);
        this.startPool = $$1;
        this.startJigsawName = $$2;
        this.maxDepth = $$3;
        this.startHeight = $$4;
        this.useExpansionHack = $$5;
        this.projectStartToHeightmap = $$6;
        this.maxDistanceFromCenter = $$7;
        this.poolAliases = $$8;
        this.dimensionPadding = $$9;
        this.liquidSettings = $$10;
    }

    public JigsawStructure(Structure.StructureSettings $$0, Holder<StructureTemplatePool> $$1, int $$2, HeightProvider $$3, boolean $$4, Heightmap.Types $$5) {
        this($$0, $$1, Optional.empty(), $$2, $$3, $$4, Optional.of($$5), 80, List.of(), DEFAULT_DIMENSION_PADDING, DEFAULT_LIQUID_SETTINGS);
    }

    public JigsawStructure(Structure.StructureSettings $$0, Holder<StructureTemplatePool> $$1, int $$2, HeightProvider $$3, boolean $$4) {
        this($$0, $$1, Optional.empty(), $$2, $$3, $$4, Optional.empty(), 80, List.of(), DEFAULT_DIMENSION_PADDING, DEFAULT_LIQUID_SETTINGS);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        ChunkPos $$1 = $$0.chunkPos();
        int $$2 = this.startHeight.sample($$0.random(), new WorldGenerationContext($$0.chunkGenerator(), $$0.heightAccessor()));
        BlockPos $$3 = new BlockPos($$1.getMinBlockX(), $$2, $$1.getMinBlockZ());
        return JigsawPlacement.addPieces($$0, this.startPool, this.startJigsawName, this.maxDepth, $$3, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter, PoolAliasLookup.create(this.poolAliases, $$3, $$0.seed()), this.dimensionPadding, this.liquidSettings);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.JIGSAW;
    }

    @VisibleForTesting
    public Holder<StructureTemplatePool> getStartPool() {
        return this.startPool;
    }

    @VisibleForTesting
    public List<PoolAliasBinding> getPoolAliases() {
        return this.poolAliases;
    }
}


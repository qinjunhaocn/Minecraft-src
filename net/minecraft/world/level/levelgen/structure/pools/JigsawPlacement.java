/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SequencedPriorityIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class JigsawPlacement {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int UNSET_HEIGHT = Integer.MIN_VALUE;

    public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext $$02, Holder<StructureTemplatePool> $$1, Optional<ResourceLocation> $$2, int $$3, BlockPos $$4, boolean $$5, Optional<Heightmap.Types> $$6, int $$7, PoolAliasLookup $$8, DimensionPadding $$9, LiquidSettings $$10) {
        BlockPos $$23;
        RegistryAccess $$11 = $$02.registryAccess();
        ChunkGenerator $$12 = $$02.chunkGenerator();
        StructureTemplateManager $$13 = $$02.structureTemplateManager();
        LevelHeightAccessor $$14 = $$02.heightAccessor();
        WorldgenRandom $$15 = $$02.random();
        HolderLookup.RegistryLookup $$16 = $$11.lookupOrThrow(Registries.TEMPLATE_POOL);
        Rotation $$17 = Rotation.getRandom($$15);
        StructureTemplatePool $$18 = $$1.unwrapKey().flatMap(arg_0 -> JigsawPlacement.lambda$addPieces$0((Registry)$$16, $$8, arg_0)).orElse($$1.value());
        StructurePoolElement $$19 = $$18.getRandomTemplate($$15);
        if ($$19 == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }
        if ($$2.isPresent()) {
            ResourceLocation $$20 = $$2.get();
            Optional<BlockPos> $$21 = JigsawPlacement.getRandomNamedJigsaw($$19, $$20, $$4, $$17, $$13, $$15);
            if ($$21.isEmpty()) {
                LOGGER.error("No starting jigsaw {} found in start pool {}", (Object)$$20, (Object)$$1.unwrapKey().map($$0 -> $$0.location().toString()).orElse("<unregistered>"));
                return Optional.empty();
            }
            BlockPos $$22 = $$21.get();
        } else {
            $$23 = $$4;
        }
        BlockPos $$24 = $$23.subtract($$4);
        BlockPos $$25 = $$4.subtract($$24);
        PoolElementStructurePiece $$26 = new PoolElementStructurePiece($$13, $$19, $$25, $$19.getGroundLevelDelta(), $$17, $$19.getBoundingBox($$13, $$25, $$17), $$10);
        BoundingBox $$27 = $$26.getBoundingBox();
        int $$28 = ($$27.maxX() + $$27.minX()) / 2;
        int $$29 = ($$27.maxZ() + $$27.minZ()) / 2;
        int $$30 = $$6.isEmpty() ? $$25.getY() : $$4.getY() + $$12.getFirstFreeHeight($$28, $$29, $$6.get(), $$14, $$02.randomState());
        int $$31 = $$27.minY() + $$26.getGroundLevelDelta();
        $$26.move(0, $$30 - $$31, 0);
        if (JigsawPlacement.isStartTooCloseToWorldHeightLimits($$14, $$9, $$26.getBoundingBox())) {
            LOGGER.debug("Center piece {} with bounding box {} does not fit dimension padding {}", new Object[]{$$19, $$26.getBoundingBox(), $$9});
            return Optional.empty();
        }
        int $$32 = $$30 + $$24.getY();
        return Optional.of(new Structure.GenerationStub(new BlockPos($$28, $$32, $$29), arg_0 -> JigsawPlacement.lambda$addPieces$2($$26, $$3, $$28, $$7, $$32, $$14, $$9, $$29, $$27, $$02, $$5, $$12, $$13, $$15, (Registry)$$16, $$8, $$10, arg_0)));
    }

    private static boolean isStartTooCloseToWorldHeightLimits(LevelHeightAccessor $$0, DimensionPadding $$1, BoundingBox $$2) {
        if ($$1 == DimensionPadding.ZERO) {
            return false;
        }
        int $$3 = $$0.getMinY() + $$1.bottom();
        int $$4 = $$0.getMaxY() - $$1.top();
        return $$2.minY() < $$3 || $$2.maxY() > $$4;
    }

    private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement $$0, ResourceLocation $$1, BlockPos $$2, Rotation $$3, StructureTemplateManager $$4, WorldgenRandom $$5) {
        List<StructureTemplate.JigsawBlockInfo> $$6 = $$0.getShuffledJigsawBlocks($$4, $$2, $$3, $$5);
        for (StructureTemplate.JigsawBlockInfo $$7 : $$6) {
            if (!$$1.equals($$7.name())) continue;
            return Optional.of($$7.info().pos());
        }
        return Optional.empty();
    }

    private static void addPieces(RandomState $$0, int $$1, boolean $$2, ChunkGenerator $$3, StructureTemplateManager $$4, LevelHeightAccessor $$5, RandomSource $$6, Registry<StructureTemplatePool> $$7, PoolElementStructurePiece $$8, List<PoolElementStructurePiece> $$9, VoxelShape $$10, PoolAliasLookup $$11, LiquidSettings $$12) {
        Placer $$13 = new Placer($$7, $$1, $$3, $$4, $$9, $$6);
        $$13.tryPlacingChildren($$8, new MutableObject<VoxelShape>($$10), 0, $$2, $$5, $$0, $$11, $$12);
        while ($$13.placing.hasNext()) {
            PieceState $$14 = (PieceState)((Object)$$13.placing.next());
            $$13.tryPlacingChildren($$14.piece, $$14.free, $$14.depth, $$2, $$5, $$0, $$11, $$12);
        }
    }

    public static boolean generateJigsaw(ServerLevel $$02, Holder<StructureTemplatePool> $$1, ResourceLocation $$2, int $$3, BlockPos $$4, boolean $$5) {
        ChunkGenerator $$6 = $$02.getChunkSource().getGenerator();
        StructureTemplateManager $$7 = $$02.getStructureManager();
        StructureManager $$8 = $$02.structureManager();
        RandomSource $$9 = $$02.getRandom();
        Structure.GenerationContext $$10 = new Structure.GenerationContext($$02.registryAccess(), $$6, $$6.getBiomeSource(), $$02.getChunkSource().randomState(), $$7, $$02.getSeed(), new ChunkPos($$4), $$02, $$0 -> true);
        Optional<Structure.GenerationStub> $$11 = JigsawPlacement.addPieces($$10, $$1, Optional.of($$2), $$3, $$4, false, Optional.empty(), 128, PoolAliasLookup.EMPTY, JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
        if ($$11.isPresent()) {
            StructurePiecesBuilder $$12 = $$11.get().getPiecesBuilder();
            for (StructurePiece $$13 : $$12.build().pieces()) {
                if (!($$13 instanceof PoolElementStructurePiece)) continue;
                PoolElementStructurePiece $$14 = (PoolElementStructurePiece)$$13;
                $$14.place($$02, $$8, $$6, $$9, BoundingBox.infinite(), $$4, $$5);
            }
            return true;
        }
        return false;
    }

    private static /* synthetic */ void lambda$addPieces$2(PoolElementStructurePiece $$0, int $$1, int $$2, int $$3, int $$4, LevelHeightAccessor $$5, DimensionPadding $$6, int $$7, BoundingBox $$8, Structure.GenerationContext $$9, boolean $$10, ChunkGenerator $$11, StructureTemplateManager $$12, WorldgenRandom $$13, Registry $$14, PoolAliasLookup $$15, LiquidSettings $$16, StructurePiecesBuilder $$17) {
        ArrayList<PoolElementStructurePiece> $$18 = Lists.newArrayList();
        $$18.add($$0);
        if ($$1 <= 0) {
            return;
        }
        AABB $$19 = new AABB($$2 - $$3, Math.max($$4 - $$3, $$5.getMinY() + $$6.bottom()), $$7 - $$3, $$2 + $$3 + 1, Math.min($$4 + $$3 + 1, $$5.getMaxY() + 1 - $$6.top()), $$7 + $$3 + 1);
        VoxelShape $$20 = Shapes.join(Shapes.create($$19), Shapes.create(AABB.of($$8)), BooleanOp.ONLY_FIRST);
        JigsawPlacement.addPieces($$9.randomState(), $$1, $$10, $$11, $$12, $$5, $$13, $$14, $$0, $$18, $$20, $$15, $$16);
        $$18.forEach($$17::addPiece);
    }

    private static /* synthetic */ Optional lambda$addPieces$0(Registry $$0, PoolAliasLookup $$1, ResourceKey $$2) {
        return $$0.getOptional($$1.lookup($$2));
    }

    static final class Placer {
        private final Registry<StructureTemplatePool> pools;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final RandomSource random;
        final SequencedPriorityIterator<PieceState> placing = new SequencedPriorityIterator();

        Placer(Registry<StructureTemplatePool> $$0, int $$1, ChunkGenerator $$2, StructureTemplateManager $$3, List<? super PoolElementStructurePiece> $$4, RandomSource $$5) {
            this.pools = $$0;
            this.maxDepth = $$1;
            this.chunkGenerator = $$2;
            this.structureTemplateManager = $$3;
            this.pieces = $$4;
            this.random = $$5;
        }

        void tryPlacingChildren(PoolElementStructurePiece $$02, MutableObject<VoxelShape> $$1, int $$22, boolean $$3, LevelHeightAccessor $$4, RandomState $$5, PoolAliasLookup $$6, LiquidSettings $$7) {
            StructurePoolElement $$8 = $$02.getElement();
            BlockPos $$9 = $$02.getPosition();
            Rotation $$10 = $$02.getRotation();
            StructureTemplatePool.Projection $$11 = $$8.getProjection();
            boolean $$12 = $$11 == StructureTemplatePool.Projection.RIGID;
            MutableObject<VoxelShape> $$13 = new MutableObject<VoxelShape>();
            BoundingBox $$14 = $$02.getBoundingBox();
            int $$15 = $$14.minY();
            block0: for (StructureTemplate.JigsawBlockInfo $$16 : $$8.getShuffledJigsawBlocks(this.structureTemplateManager, $$9, $$10, this.random)) {
                StructurePoolElement $$32;
                MutableObject<VoxelShape> $$29;
                StructureTemplate.StructureBlockInfo $$17 = $$16.info();
                Direction $$18 = JigsawBlock.getFrontFacing($$17.state());
                BlockPos $$19 = $$17.pos();
                BlockPos $$20 = $$19.relative($$18);
                int $$21 = $$19.getY() - $$15;
                int $$222 = Integer.MIN_VALUE;
                ResourceKey<StructureTemplatePool> $$23 = $$6.lookup($$16.pool());
                Optional $$24 = this.pools.get($$23);
                if ($$24.isEmpty()) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)$$23.location());
                    continue;
                }
                Holder $$25 = (Holder)$$24.get();
                if (((StructureTemplatePool)$$25.value()).size() == 0 && !$$25.is(Pools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)$$23.location());
                    continue;
                }
                Holder<StructureTemplatePool> $$26 = ((StructureTemplatePool)$$25.value()).getFallback();
                if ($$26.value().size() == 0 && !$$26.is(Pools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent fallback pool: {}", (Object)$$26.unwrapKey().map($$0 -> $$0.location().toString()).orElse("<unregistered>"));
                    continue;
                }
                boolean $$27 = $$14.isInside($$20);
                if ($$27) {
                    MutableObject<VoxelShape> $$28 = $$13;
                    if ($$13.getValue() == null) {
                        $$13.setValue(Shapes.create(AABB.of($$14)));
                    }
                } else {
                    $$29 = $$1;
                }
                ArrayList<StructurePoolElement> $$30 = Lists.newArrayList();
                if ($$22 != this.maxDepth) {
                    $$30.addAll(((StructureTemplatePool)$$25.value()).getShuffledTemplates(this.random));
                }
                $$30.addAll($$26.value().getShuffledTemplates(this.random));
                int $$31 = $$16.placementPriority();
                Iterator iterator = $$30.iterator();
                while (iterator.hasNext() && ($$32 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (Rotation $$33 : Rotation.getShuffled(this.random)) {
                        int $$37;
                        List<StructureTemplate.JigsawBlockInfo> $$34 = $$32.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, $$33, this.random);
                        BoundingBox $$35 = $$32.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, $$33);
                        if (!$$3 || $$35.getYSpan() > 16) {
                            boolean $$36 = false;
                        } else {
                            $$37 = $$34.stream().mapToInt($$2 -> {
                                StructureTemplate.StructureBlockInfo $$3 = $$2.info();
                                if (!$$35.isInside($$3.pos().relative(JigsawBlock.getFrontFacing($$3.state())))) {
                                    return 0;
                                }
                                ResourceKey<StructureTemplatePool> $$4 = $$6.lookup($$2.pool());
                                Optional $$5 = this.pools.get($$4);
                                Optional<Holder> $$6 = $$5.map($$0 -> ((StructureTemplatePool)$$0.value()).getFallback());
                                int $$7 = $$5.map($$0 -> ((StructureTemplatePool)$$0.value()).getMaxSize(this.structureTemplateManager)).orElse(0);
                                int $$8 = $$6.map($$0 -> ((StructureTemplatePool)$$0.value()).getMaxSize(this.structureTemplateManager)).orElse(0);
                                return Math.max($$7, $$8);
                            }).max().orElse(0);
                        }
                        for (StructureTemplate.JigsawBlockInfo $$38 : $$34) {
                            int $$59;
                            int $$55;
                            int $$48;
                            if (!JigsawBlock.canAttach($$16, $$38)) continue;
                            BlockPos $$39 = $$38.info().pos();
                            BlockPos $$40 = $$20.subtract($$39);
                            BoundingBox $$41 = $$32.getBoundingBox(this.structureTemplateManager, $$40, $$33);
                            int $$42 = $$41.minY();
                            StructureTemplatePool.Projection $$43 = $$32.getProjection();
                            boolean $$44 = $$43 == StructureTemplatePool.Projection.RIGID;
                            int $$45 = $$39.getY();
                            int $$46 = $$21 - $$45 + JigsawBlock.getFrontFacing($$17.state()).getStepY();
                            if ($$12 && $$44) {
                                int $$47 = $$15 + $$46;
                            } else {
                                if ($$222 == Integer.MIN_VALUE) {
                                    $$222 = this.chunkGenerator.getFirstFreeHeight($$19.getX(), $$19.getZ(), Heightmap.Types.WORLD_SURFACE_WG, $$4, $$5);
                                }
                                $$48 = $$222 - $$45;
                            }
                            int $$49 = $$48 - $$42;
                            BoundingBox $$50 = $$41.moved(0, $$49, 0);
                            BlockPos $$51 = $$40.offset(0, $$49, 0);
                            if ($$37 > 0) {
                                int $$52 = Math.max($$37 + 1, $$50.maxY() - $$50.minY());
                                $$50.encapsulate(new BlockPos($$50.minX(), $$50.minY() + $$52, $$50.minZ()));
                            }
                            if (Shapes.joinIsNotEmpty($$29.getValue(), Shapes.create(AABB.of($$50).deflate(0.25)), BooleanOp.ONLY_SECOND)) continue;
                            $$29.setValue(Shapes.joinUnoptimized($$29.getValue(), Shapes.create(AABB.of($$50)), BooleanOp.ONLY_FIRST));
                            int $$53 = $$02.getGroundLevelDelta();
                            if ($$44) {
                                int $$54 = $$53 - $$46;
                            } else {
                                $$55 = $$32.getGroundLevelDelta();
                            }
                            PoolElementStructurePiece $$56 = new PoolElementStructurePiece(this.structureTemplateManager, $$32, $$51, $$55, $$33, $$50, $$7);
                            if ($$12) {
                                int $$57 = $$15 + $$21;
                            } else if ($$44) {
                                int $$58 = $$48 + $$45;
                            } else {
                                if ($$222 == Integer.MIN_VALUE) {
                                    $$222 = this.chunkGenerator.getFirstFreeHeight($$19.getX(), $$19.getZ(), Heightmap.Types.WORLD_SURFACE_WG, $$4, $$5);
                                }
                                $$59 = $$222 + $$46 / 2;
                            }
                            $$02.addJunction(new JigsawJunction($$20.getX(), (int)($$59 - $$21 + $$53), $$20.getZ(), $$46, $$43));
                            $$56.addJunction(new JigsawJunction($$19.getX(), $$59 - $$45 + $$55, $$19.getZ(), -$$46, $$11));
                            this.pieces.add($$56);
                            if ($$22 + 1 > this.maxDepth) continue block0;
                            PieceState $$60 = new PieceState($$56, $$29, $$22 + 1);
                            this.placing.add($$60, $$31);
                            continue block0;
                        }
                    }
                }
            }
        }
    }

    static final class PieceState
    extends Record {
        final PoolElementStructurePiece piece;
        final MutableObject<VoxelShape> free;
        final int depth;

        PieceState(PoolElementStructurePiece $$0, MutableObject<VoxelShape> $$1, int $$2) {
            this.piece = $$0;
            this.free = $$1;
            this.depth = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PieceState.class, "piece;free;depth", "piece", "free", "depth"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PieceState.class, "piece;free;depth", "piece", "free", "depth"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PieceState.class, "piece;free;depth", "piece", "free", "depth"}, this, $$0);
        }

        public PoolElementStructurePiece piece() {
            return this.piece;
        }

        public MutableObject<VoxelShape> free() {
            return this.free;
        }

        public int depth() {
            return this.depth;
        }
    }
}


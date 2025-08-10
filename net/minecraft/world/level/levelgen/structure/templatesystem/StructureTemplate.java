/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.slf4j.Logger;

public class StructureTemplate {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String PALETTE_TAG = "palette";
    public static final String PALETTE_LIST_TAG = "palettes";
    public static final String ENTITIES_TAG = "entities";
    public static final String BLOCKS_TAG = "blocks";
    public static final String BLOCK_TAG_POS = "pos";
    public static final String BLOCK_TAG_STATE = "state";
    public static final String BLOCK_TAG_NBT = "nbt";
    public static final String ENTITY_TAG_POS = "pos";
    public static final String ENTITY_TAG_BLOCKPOS = "blockPos";
    public static final String ENTITY_TAG_NBT = "nbt";
    public static final String SIZE_TAG = "size";
    private final List<Palette> palettes = Lists.newArrayList();
    private final List<StructureEntityInfo> entityInfoList = Lists.newArrayList();
    private Vec3i size = Vec3i.ZERO;
    private String author = "?";

    public Vec3i getSize() {
        return this.size;
    }

    public void setAuthor(String $$0) {
        this.author = $$0;
    }

    public String getAuthor() {
        return this.author;
    }

    public void fillFromWorld(Level $$0, BlockPos $$1, Vec3i $$2, boolean $$3, List<Block> $$4) {
        if ($$2.getX() < 1 || $$2.getY() < 1 || $$2.getZ() < 1) {
            return;
        }
        BlockPos $$5 = $$1.offset($$2).offset(-1, -1, -1);
        ArrayList<StructureBlockInfo> $$6 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> $$7 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> $$8 = Lists.newArrayList();
        BlockPos $$9 = new BlockPos(Math.min($$1.getX(), $$5.getX()), Math.min($$1.getY(), $$5.getY()), Math.min($$1.getZ(), $$5.getZ()));
        BlockPos $$10 = new BlockPos(Math.max($$1.getX(), $$5.getX()), Math.max($$1.getY(), $$5.getY()), Math.max($$1.getZ(), $$5.getZ()));
        this.size = $$2;
        try (ProblemReporter.ScopedCollector $$11 = new ProblemReporter.ScopedCollector(LOGGER);){
            for (BlockPos $$12 : BlockPos.betweenClosed($$9, $$10)) {
                StructureBlockInfo $$18;
                BlockPos $$13 = $$12.subtract($$9);
                BlockState $$14 = $$0.getBlockState($$12);
                if ($$4.stream().anyMatch($$14::is)) continue;
                BlockEntity $$15 = $$0.getBlockEntity($$12);
                if ($$15 != null) {
                    TagValueOutput $$16 = TagValueOutput.createWithContext($$11, $$0.registryAccess());
                    $$15.saveWithId($$16);
                    StructureBlockInfo $$17 = new StructureBlockInfo($$13, $$14, $$16.buildResult());
                } else {
                    $$18 = new StructureBlockInfo($$13, $$14, null);
                }
                StructureTemplate.addToLists($$18, $$6, $$7, $$8);
            }
            List<StructureBlockInfo> $$19 = StructureTemplate.buildInfoList($$6, $$7, $$8);
            this.palettes.clear();
            this.palettes.add(new Palette($$19));
            if ($$3) {
                this.fillEntityList($$0, $$9, $$10, $$11);
            } else {
                this.entityInfoList.clear();
            }
        }
    }

    private static void addToLists(StructureBlockInfo $$0, List<StructureBlockInfo> $$1, List<StructureBlockInfo> $$2, List<StructureBlockInfo> $$3) {
        if ($$0.nbt != null) {
            $$2.add($$0);
        } else if (!$$0.state.getBlock().hasDynamicShape() && $$0.state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
            $$1.add($$0);
        } else {
            $$3.add($$0);
        }
    }

    private static List<StructureBlockInfo> buildInfoList(List<StructureBlockInfo> $$02, List<StructureBlockInfo> $$1, List<StructureBlockInfo> $$2) {
        Comparator<StructureBlockInfo> $$3 = Comparator.comparingInt($$0 -> $$0.pos.getY()).thenComparingInt($$0 -> $$0.pos.getX()).thenComparingInt($$0 -> $$0.pos.getZ());
        $$02.sort($$3);
        $$2.sort($$3);
        $$1.sort($$3);
        ArrayList<StructureBlockInfo> $$4 = Lists.newArrayList();
        $$4.addAll($$02);
        $$4.addAll($$2);
        $$4.addAll($$1);
        return $$4;
    }

    private void fillEntityList(Level $$02, BlockPos $$1, BlockPos $$2, ProblemReporter $$3) {
        List<Entity> $$4 = $$02.getEntitiesOfClass(Entity.class, AABB.encapsulatingFullBlocks($$1, $$2), $$0 -> !($$0 instanceof Player));
        this.entityInfoList.clear();
        for (Entity $$5 : $$4) {
            BlockPos $$10;
            Vec3 $$6 = new Vec3($$5.getX() - (double)$$1.getX(), $$5.getY() - (double)$$1.getY(), $$5.getZ() - (double)$$1.getZ());
            TagValueOutput $$7 = TagValueOutput.createWithContext($$3.forChild($$5.problemPath()), $$5.registryAccess());
            $$5.save($$7);
            if ($$5 instanceof Painting) {
                Painting $$8 = (Painting)$$5;
                BlockPos $$9 = $$8.getPos().subtract($$1);
            } else {
                $$10 = BlockPos.containing($$6);
            }
            this.entityInfoList.add(new StructureEntityInfo($$6, $$10, $$7.buildResult().copy()));
        }
    }

    public List<StructureBlockInfo> filterBlocks(BlockPos $$0, StructurePlaceSettings $$1, Block $$2) {
        return this.filterBlocks($$0, $$1, $$2, true);
    }

    public List<JigsawBlockInfo> getJigsaws(BlockPos $$0, Rotation $$1) {
        if (this.palettes.isEmpty()) {
            return new ArrayList<JigsawBlockInfo>();
        }
        StructurePlaceSettings $$2 = new StructurePlaceSettings().setRotation($$1);
        List<JigsawBlockInfo> $$3 = $$2.getRandomPalette(this.palettes, $$0).jigsaws();
        ArrayList<JigsawBlockInfo> $$4 = new ArrayList<JigsawBlockInfo>($$3.size());
        for (JigsawBlockInfo $$5 : $$3) {
            StructureBlockInfo $$6 = $$5.info;
            $$4.add($$5.withInfo(new StructureBlockInfo(StructureTemplate.calculateRelativePosition($$2, $$6.pos()).offset($$0), $$6.state.rotate($$2.getRotation()), $$6.nbt)));
        }
        return $$4;
    }

    public ObjectArrayList<StructureBlockInfo> filterBlocks(BlockPos $$0, StructurePlaceSettings $$1, Block $$2, boolean $$3) {
        ObjectArrayList $$4 = new ObjectArrayList();
        BoundingBox $$5 = $$1.getBoundingBox();
        if (this.palettes.isEmpty()) {
            return $$4;
        }
        for (StructureBlockInfo $$6 : $$1.getRandomPalette(this.palettes, $$0).blocks($$2)) {
            BlockPos $$7;
            BlockPos blockPos = $$7 = $$3 ? StructureTemplate.calculateRelativePosition($$1, $$6.pos).offset($$0) : $$6.pos;
            if ($$5 != null && !$$5.isInside($$7)) continue;
            $$4.add((Object)new StructureBlockInfo($$7, $$6.state.rotate($$1.getRotation()), $$6.nbt));
        }
        return $$4;
    }

    public BlockPos calculateConnectedPosition(StructurePlaceSettings $$0, BlockPos $$1, StructurePlaceSettings $$2, BlockPos $$3) {
        BlockPos $$4 = StructureTemplate.calculateRelativePosition($$0, $$1);
        BlockPos $$5 = StructureTemplate.calculateRelativePosition($$2, $$3);
        return $$4.subtract($$5);
    }

    public static BlockPos calculateRelativePosition(StructurePlaceSettings $$0, BlockPos $$1) {
        return StructureTemplate.transform($$1, $$0.getMirror(), $$0.getRotation(), $$0.getRotationPivot());
    }

    public boolean placeInWorld(ServerLevelAccessor $$0, BlockPos $$1, BlockPos $$2, StructurePlaceSettings $$3, RandomSource $$4, int $$5) {
        if (this.palettes.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> $$6 = $$3.getRandomPalette(this.palettes, $$1).blocks();
        if ($$6.isEmpty() && ($$3.isIgnoreEntities() || this.entityInfoList.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BoundingBox $$7 = $$3.getBoundingBox();
        ArrayList<BlockPos> $$8 = Lists.newArrayListWithCapacity($$3.shouldApplyWaterlogging() ? $$6.size() : 0);
        ArrayList<BlockPos> $$9 = Lists.newArrayListWithCapacity($$3.shouldApplyWaterlogging() ? $$6.size() : 0);
        ArrayList<Pair> $$10 = Lists.newArrayListWithCapacity($$6.size());
        int $$11 = Integer.MAX_VALUE;
        int $$12 = Integer.MAX_VALUE;
        int $$13 = Integer.MAX_VALUE;
        int $$14 = Integer.MIN_VALUE;
        int $$15 = Integer.MIN_VALUE;
        int $$16 = Integer.MIN_VALUE;
        List<StructureBlockInfo> $$17 = StructureTemplate.processBlockInfos($$0, $$1, $$2, $$3, $$6);
        try (ProblemReporter.ScopedCollector $$18 = new ProblemReporter.ScopedCollector(LOGGER);){
            for (StructureBlockInfo $$19 : $$17) {
                BlockEntity $$23;
                BlockPos $$20 = $$19.pos;
                if ($$7 != null && !$$7.isInside($$20)) continue;
                FluidState $$21 = $$3.shouldApplyWaterlogging() ? $$0.getFluidState($$20) : null;
                BlockState $$22 = $$19.state.mirror($$3.getMirror()).rotate($$3.getRotation());
                if ($$19.nbt != null) {
                    $$0.setBlock($$20, Blocks.BARRIER.defaultBlockState(), 820);
                }
                if (!$$0.setBlock($$20, $$22, $$5)) continue;
                $$11 = Math.min($$11, $$20.getX());
                $$12 = Math.min($$12, $$20.getY());
                $$13 = Math.min($$13, $$20.getZ());
                $$14 = Math.max($$14, $$20.getX());
                $$15 = Math.max($$15, $$20.getY());
                $$16 = Math.max($$16, $$20.getZ());
                $$10.add(Pair.of((Object)$$20, (Object)$$19.nbt));
                if ($$19.nbt != null && ($$23 = $$0.getBlockEntity($$20)) != null) {
                    if ($$23 instanceof RandomizableContainer) {
                        $$19.nbt.putLong("LootTableSeed", $$4.nextLong());
                    }
                    $$23.loadWithComponents(TagValueInput.create($$18.forChild($$23.problemPath()), (HolderLookup.Provider)$$0.registryAccess(), $$19.nbt));
                }
                if ($$21 == null) continue;
                if ($$22.getFluidState().isSource()) {
                    $$9.add($$20);
                    continue;
                }
                if (!($$22.getBlock() instanceof LiquidBlockContainer)) continue;
                ((LiquidBlockContainer)((Object)$$22.getBlock())).placeLiquid($$0, $$20, $$22, $$21);
                if ($$21.isSource()) continue;
                $$8.add($$20);
            }
            boolean $$24 = true;
            Direction[] $$25 = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            while ($$24 && !$$8.isEmpty()) {
                $$24 = false;
                Iterator $$26 = $$8.iterator();
                while ($$26.hasNext()) {
                    BlockState $$32;
                    Object $$33;
                    BlockPos $$27 = (BlockPos)$$26.next();
                    FluidState $$28 = $$0.getFluidState($$27);
                    for (int $$29 = 0; $$29 < $$25.length && !$$28.isSource(); ++$$29) {
                        BlockPos $$30 = $$27.relative($$25[$$29]);
                        FluidState $$31 = $$0.getFluidState($$30);
                        if (!$$31.isSource() || $$9.contains($$30)) continue;
                        $$28 = $$31;
                    }
                    if (!$$28.isSource() || !(($$33 = ($$32 = $$0.getBlockState($$27)).getBlock()) instanceof LiquidBlockContainer)) continue;
                    ((LiquidBlockContainer)$$33).placeLiquid($$0, $$27, $$32, $$28);
                    $$24 = true;
                    $$26.remove();
                }
            }
            if ($$11 <= $$14) {
                if (!$$3.getKnownShape()) {
                    BitSetDiscreteVoxelShape $$34 = new BitSetDiscreteVoxelShape($$14 - $$11 + 1, $$15 - $$12 + 1, $$16 - $$13 + 1);
                    int $$35 = $$11;
                    int $$36 = $$12;
                    int $$37 = $$13;
                    for (Pair $$38 : $$10) {
                        BlockPos $$39 = (BlockPos)$$38.getFirst();
                        ((DiscreteVoxelShape)$$34).fill($$39.getX() - $$35, $$39.getY() - $$36, $$39.getZ() - $$37);
                    }
                    StructureTemplate.updateShapeAtEdge($$0, $$5, $$34, $$35, $$36, $$37);
                }
                for (Pair $$40 : $$10) {
                    BlockEntity $$44;
                    BlockPos $$41 = (BlockPos)$$40.getFirst();
                    if (!$$3.getKnownShape()) {
                        BlockState $$43;
                        BlockState $$42 = $$0.getBlockState($$41);
                        if ($$42 != ($$43 = Block.updateFromNeighbourShapes($$42, $$0, $$41))) {
                            $$0.setBlock($$41, $$43, $$5 & 0xFFFFFFFE | 0x10);
                        }
                        $$0.updateNeighborsAt($$41, $$43.getBlock());
                    }
                    if ($$40.getSecond() == null || ($$44 = $$0.getBlockEntity($$41)) == null) continue;
                    $$44.setChanged();
                }
            }
            if (!$$3.isIgnoreEntities()) {
                this.placeEntities($$0, $$1, $$3.getMirror(), $$3.getRotation(), $$3.getRotationPivot(), $$7, $$3.shouldFinalizeEntities(), $$18);
            }
        }
        return true;
    }

    public static void updateShapeAtEdge(LevelAccessor $$0, int $$1, DiscreteVoxelShape $$2, BlockPos $$3) {
        StructureTemplate.updateShapeAtEdge($$0, $$1, $$2, $$3.getX(), $$3.getY(), $$3.getZ());
    }

    public static void updateShapeAtEdge(LevelAccessor $$0, int $$1, DiscreteVoxelShape $$2, int $$3, int $$4, int $$5) {
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$72 = new BlockPos.MutableBlockPos();
        $$2.forAllFaces(($$7, $$8, $$9, $$10) -> {
            BlockState $$14;
            $$6.set($$3 + $$8, $$4 + $$9, $$5 + $$10);
            $$72.setWithOffset((Vec3i)$$6, $$7);
            BlockState $$11 = $$0.getBlockState($$6);
            BlockState $$12 = $$0.getBlockState($$72);
            BlockState $$13 = $$11.updateShape($$0, $$0, $$6, $$7, $$72, $$12, $$0.getRandom());
            if ($$11 != $$13) {
                $$0.setBlock($$6, $$13, $$1 & 0xFFFFFFFE);
            }
            if ($$12 != ($$14 = $$12.updateShape($$0, $$0, $$72, $$7.getOpposite(), $$6, $$13, $$0.getRandom()))) {
                $$0.setBlock($$72, $$14, $$1 & 0xFFFFFFFE);
            }
        });
    }

    public static List<StructureBlockInfo> processBlockInfos(ServerLevelAccessor $$0, BlockPos $$1, BlockPos $$2, StructurePlaceSettings $$3, List<StructureBlockInfo> $$4) {
        ArrayList<StructureBlockInfo> $$5 = new ArrayList<StructureBlockInfo>();
        List<StructureBlockInfo> $$6 = new ArrayList<StructureBlockInfo>();
        for (StructureBlockInfo $$7 : $$4) {
            BlockPos $$8 = StructureTemplate.calculateRelativePosition($$3, $$7.pos).offset($$1);
            StructureBlockInfo $$9 = new StructureBlockInfo($$8, $$7.state, $$7.nbt != null ? $$7.nbt.copy() : null);
            Iterator<StructureProcessor> $$10 = $$3.getProcessors().iterator();
            while ($$9 != null && $$10.hasNext()) {
                $$9 = $$10.next().processBlock($$0, $$1, $$2, $$7, $$9, $$3);
            }
            if ($$9 == null) continue;
            $$6.add($$9);
            $$5.add($$7);
        }
        for (StructureProcessor $$11 : $$3.getProcessors()) {
            $$6 = $$11.finalizeProcessing($$0, $$1, $$2, $$5, $$6, $$3);
        }
        return $$6;
    }

    private void placeEntities(ServerLevelAccessor $$0, BlockPos $$1, Mirror $$2, Rotation $$3, BlockPos $$4, @Nullable BoundingBox $$52, boolean $$6, ProblemReporter $$7) {
        for (StructureEntityInfo $$8 : this.entityInfoList) {
            BlockPos $$9 = StructureTemplate.transform($$8.blockPos, $$2, $$3, $$4).offset($$1);
            if ($$52 != null && !$$52.isInside($$9)) continue;
            CompoundTag $$10 = $$8.nbt.copy();
            Vec3 $$11 = StructureTemplate.transform($$8.pos, $$2, $$3, $$4);
            Vec3 $$12 = $$11.add($$1.getX(), $$1.getY(), $$1.getZ());
            ListTag $$13 = new ListTag();
            $$13.add(DoubleTag.valueOf($$12.x));
            $$13.add(DoubleTag.valueOf($$12.y));
            $$13.add(DoubleTag.valueOf($$12.z));
            $$10.put("Pos", $$13);
            $$10.remove("UUID");
            StructureTemplate.createEntityIgnoreException($$7, $$0, $$10).ifPresent($$5 -> {
                float $$6 = $$5.rotate($$3);
                $$5.snapTo($$2.x, $$2.y, $$2.z, $$6 += $$5.mirror($$2) - $$5.getYRot(), $$5.getXRot());
                if ($$6 && $$5 instanceof Mob) {
                    ((Mob)$$5).finalizeSpawn($$0, $$0.getCurrentDifficultyAt(BlockPos.containing($$12)), EntitySpawnReason.STRUCTURE, null);
                }
                $$0.addFreshEntityWithPassengers((Entity)$$5);
            });
        }
    }

    private static Optional<Entity> createEntityIgnoreException(ProblemReporter $$0, ServerLevelAccessor $$1, CompoundTag $$2) {
        try {
            return EntityType.create(TagValueInput.create($$0, (HolderLookup.Provider)$$1.registryAccess(), $$2), $$1.getLevel(), EntitySpawnReason.STRUCTURE);
        } catch (Exception $$3) {
            return Optional.empty();
        }
    }

    public Vec3i getSize(Rotation $$0) {
        switch ($$0) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                return new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
            }
        }
        return this.size;
    }

    public static BlockPos transform(BlockPos $$0, Mirror $$1, Rotation $$2, BlockPos $$3) {
        int $$4 = $$0.getX();
        int $$5 = $$0.getY();
        int $$6 = $$0.getZ();
        boolean $$7 = true;
        switch ($$1) {
            case LEFT_RIGHT: {
                $$6 = -$$6;
                break;
            }
            case FRONT_BACK: {
                $$4 = -$$4;
                break;
            }
            default: {
                $$7 = false;
            }
        }
        int $$8 = $$3.getX();
        int $$9 = $$3.getZ();
        switch ($$2) {
            case CLOCKWISE_180: {
                return new BlockPos($$8 + $$8 - $$4, $$5, $$9 + $$9 - $$6);
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos($$8 - $$9 + $$6, $$5, $$8 + $$9 - $$4);
            }
            case CLOCKWISE_90: {
                return new BlockPos($$8 + $$9 - $$6, $$5, $$9 - $$8 + $$4);
            }
        }
        return $$7 ? new BlockPos($$4, $$5, $$6) : $$0;
    }

    public static Vec3 transform(Vec3 $$0, Mirror $$1, Rotation $$2, BlockPos $$3) {
        double $$4 = $$0.x;
        double $$5 = $$0.y;
        double $$6 = $$0.z;
        boolean $$7 = true;
        switch ($$1) {
            case LEFT_RIGHT: {
                $$6 = 1.0 - $$6;
                break;
            }
            case FRONT_BACK: {
                $$4 = 1.0 - $$4;
                break;
            }
            default: {
                $$7 = false;
            }
        }
        int $$8 = $$3.getX();
        int $$9 = $$3.getZ();
        switch ($$2) {
            case CLOCKWISE_180: {
                return new Vec3((double)($$8 + $$8 + 1) - $$4, $$5, (double)($$9 + $$9 + 1) - $$6);
            }
            case COUNTERCLOCKWISE_90: {
                return new Vec3((double)($$8 - $$9) + $$6, $$5, (double)($$8 + $$9 + 1) - $$4);
            }
            case CLOCKWISE_90: {
                return new Vec3((double)($$8 + $$9 + 1) - $$6, $$5, (double)($$9 - $$8) + $$4);
            }
        }
        return $$7 ? new Vec3($$4, $$5, $$6) : $$0;
    }

    public BlockPos getZeroPositionWithTransform(BlockPos $$0, Mirror $$1, Rotation $$2) {
        return StructureTemplate.getZeroPositionWithTransform($$0, $$1, $$2, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos getZeroPositionWithTransform(BlockPos $$0, Mirror $$1, Rotation $$2, int $$3, int $$4) {
        int $$5 = $$1 == Mirror.FRONT_BACK ? --$$3 : 0;
        int $$6 = $$1 == Mirror.LEFT_RIGHT ? --$$4 : 0;
        BlockPos $$7 = $$0;
        switch ($$2) {
            case NONE: {
                $$7 = $$0.offset($$5, 0, $$6);
                break;
            }
            case CLOCKWISE_90: {
                $$7 = $$0.offset($$4 - $$6, 0, $$5);
                break;
            }
            case CLOCKWISE_180: {
                $$7 = $$0.offset($$3 - $$5, 0, $$4 - $$6);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                $$7 = $$0.offset($$6, 0, $$3 - $$5);
            }
        }
        return $$7;
    }

    public BoundingBox getBoundingBox(StructurePlaceSettings $$0, BlockPos $$1) {
        return this.getBoundingBox($$1, $$0.getRotation(), $$0.getRotationPivot(), $$0.getMirror());
    }

    public BoundingBox getBoundingBox(BlockPos $$0, Rotation $$1, BlockPos $$2, Mirror $$3) {
        return StructureTemplate.getBoundingBox($$0, $$1, $$2, $$3, this.size);
    }

    @VisibleForTesting
    protected static BoundingBox getBoundingBox(BlockPos $$0, Rotation $$1, BlockPos $$2, Mirror $$3, Vec3i $$4) {
        Vec3i $$5 = $$4.offset(-1, -1, -1);
        BlockPos $$6 = StructureTemplate.transform(BlockPos.ZERO, $$3, $$1, $$2);
        BlockPos $$7 = StructureTemplate.transform(BlockPos.ZERO.offset($$5), $$3, $$1, $$2);
        return BoundingBox.fromCorners($$6, $$7).move($$0);
    }

    public CompoundTag save(CompoundTag $$0) {
        if (this.palettes.isEmpty()) {
            $$0.put(BLOCKS_TAG, new ListTag());
            $$0.put(PALETTE_TAG, new ListTag());
        } else {
            ArrayList<SimplePalette> $$1 = Lists.newArrayList();
            SimplePalette $$2 = new SimplePalette();
            $$1.add($$2);
            for (int $$3 = 1; $$3 < this.palettes.size(); ++$$3) {
                $$1.add(new SimplePalette());
            }
            ListTag $$4 = new ListTag();
            List<StructureBlockInfo> $$5 = this.palettes.get(0).blocks();
            for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
                StructureBlockInfo $$7 = $$5.get($$6);
                CompoundTag $$8 = new CompoundTag();
                $$8.put("pos", this.a($$7.pos.getX(), $$7.pos.getY(), $$7.pos.getZ()));
                int $$9 = $$2.idFor($$7.state);
                $$8.putInt(BLOCK_TAG_STATE, $$9);
                if ($$7.nbt != null) {
                    $$8.put("nbt", $$7.nbt);
                }
                $$4.add($$8);
                for (int $$10 = 1; $$10 < this.palettes.size(); ++$$10) {
                    SimplePalette $$11 = (SimplePalette)$$1.get($$10);
                    $$11.addMapping(this.palettes.get((int)$$10).blocks().get((int)$$6).state, $$9);
                }
            }
            $$0.put(BLOCKS_TAG, $$4);
            if ($$1.size() == 1) {
                ListTag $$12 = new ListTag();
                for (BlockState $$13 : $$2) {
                    $$12.add(NbtUtils.writeBlockState($$13));
                }
                $$0.put(PALETTE_TAG, $$12);
            } else {
                ListTag $$14 = new ListTag();
                for (SimplePalette $$15 : $$1) {
                    ListTag $$16 = new ListTag();
                    for (BlockState $$17 : $$15) {
                        $$16.add(NbtUtils.writeBlockState($$17));
                    }
                    $$14.add($$16);
                }
                $$0.put(PALETTE_LIST_TAG, $$14);
            }
        }
        ListTag $$18 = new ListTag();
        for (StructureEntityInfo $$19 : this.entityInfoList) {
            CompoundTag $$20 = new CompoundTag();
            $$20.put("pos", this.a($$19.pos.x, $$19.pos.y, $$19.pos.z));
            $$20.put(ENTITY_TAG_BLOCKPOS, this.a($$19.blockPos.getX(), $$19.blockPos.getY(), $$19.blockPos.getZ()));
            if ($$19.nbt != null) {
                $$20.put("nbt", $$19.nbt);
            }
            $$18.add($$20);
        }
        $$0.put(ENTITIES_TAG, $$18);
        $$0.put(SIZE_TAG, this.a(this.size.getX(), this.size.getY(), this.size.getZ()));
        return NbtUtils.addCurrentDataVersion($$0);
    }

    public void load(HolderGetter<Block> $$02, CompoundTag $$1) {
        this.palettes.clear();
        this.entityInfoList.clear();
        ListTag $$2 = $$1.getListOrEmpty(SIZE_TAG);
        this.size = new Vec3i($$2.getIntOr(0, 0), $$2.getIntOr(1, 0), $$2.getIntOr(2, 0));
        ListTag $$3 = $$1.getListOrEmpty(BLOCKS_TAG);
        Optional<ListTag> $$4 = $$1.getList(PALETTE_LIST_TAG);
        if ($$4.isPresent()) {
            for (int $$5 = 0; $$5 < $$4.get().size(); ++$$5) {
                this.loadPalette($$02, $$4.get().getListOrEmpty($$5), $$3);
            }
        } else {
            this.loadPalette($$02, $$1.getListOrEmpty(PALETTE_TAG), $$3);
        }
        $$1.getListOrEmpty(ENTITIES_TAG).compoundStream().forEach($$0 -> {
            ListTag $$1 = $$0.getListOrEmpty("pos");
            Vec3 $$22 = new Vec3($$1.getDoubleOr(0, 0.0), $$1.getDoubleOr(1, 0.0), $$1.getDoubleOr(2, 0.0));
            ListTag $$3 = $$0.getListOrEmpty(ENTITY_TAG_BLOCKPOS);
            BlockPos $$4 = new BlockPos($$3.getIntOr(0, 0), $$3.getIntOr(1, 0), $$3.getIntOr(2, 0));
            $$0.getCompound("nbt").ifPresent($$2 -> this.entityInfoList.add(new StructureEntityInfo($$22, $$4, (CompoundTag)$$2)));
        });
    }

    private void loadPalette(HolderGetter<Block> $$0, ListTag $$1, ListTag $$2) {
        SimplePalette $$3 = new SimplePalette();
        for (int $$42 = 0; $$42 < $$1.size(); ++$$42) {
            $$3.addMapping(NbtUtils.readBlockState($$0, $$1.getCompoundOrEmpty($$42)), $$42);
        }
        ArrayList<StructureBlockInfo> $$5 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> $$6 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> $$7 = Lists.newArrayList();
        $$2.compoundStream().forEach($$4 -> {
            ListTag $$5 = $$4.getListOrEmpty("pos");
            BlockPos $$6 = new BlockPos($$5.getIntOr(0, 0), $$5.getIntOr(1, 0), $$5.getIntOr(2, 0));
            BlockState $$7 = $$3.stateFor($$4.getIntOr(BLOCK_TAG_STATE, 0));
            CompoundTag $$8 = $$4.getCompound("nbt").orElse(null);
            StructureBlockInfo $$9 = new StructureBlockInfo($$6, $$7, $$8);
            StructureTemplate.addToLists($$9, $$5, $$6, $$7);
        });
        List<StructureBlockInfo> $$8 = StructureTemplate.buildInfoList($$5, $$6, $$7);
        this.palettes.add(new Palette($$8));
    }

    private ListTag a(int ... $$0) {
        ListTag $$1 = new ListTag();
        for (int $$2 : $$0) {
            $$1.add(IntTag.valueOf($$2));
        }
        return $$1;
    }

    private ListTag a(double ... $$0) {
        ListTag $$1 = new ListTag();
        for (double $$2 : $$0) {
            $$1.add(DoubleTag.valueOf($$2));
        }
        return $$1;
    }

    public static JigsawBlockEntity.JointType getJointType(CompoundTag $$0, BlockState $$1) {
        return $$0.read("joint", JigsawBlockEntity.JointType.CODEC).orElseGet(() -> StructureTemplate.getDefaultJointType($$1));
    }

    public static JigsawBlockEntity.JointType getDefaultJointType(BlockState $$0) {
        return JigsawBlock.getFrontFacing($$0).getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
    }

    public static final class StructureBlockInfo
    extends Record {
        final BlockPos pos;
        final BlockState state;
        @Nullable
        final CompoundTag nbt;

        public StructureBlockInfo(BlockPos $$0, BlockState $$1, @Nullable CompoundTag $$2) {
            this.pos = $$0;
            this.state = $$1;
            this.nbt = $$2;
        }

        public String toString() {
            return String.format(Locale.ROOT, "<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StructureBlockInfo.class, "pos;state;nbt", "pos", "state", "nbt"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StructureBlockInfo.class, "pos;state;nbt", "pos", "state", "nbt"}, this, $$0);
        }

        public BlockPos pos() {
            return this.pos;
        }

        public BlockState state() {
            return this.state;
        }

        @Nullable
        public CompoundTag nbt() {
            return this.nbt;
        }
    }

    public static final class Palette {
        private final List<StructureBlockInfo> blocks;
        private final Map<Block, List<StructureBlockInfo>> cache = Maps.newHashMap();
        @Nullable
        private List<JigsawBlockInfo> cachedJigsaws;

        Palette(List<StructureBlockInfo> $$0) {
            this.blocks = $$0;
        }

        public List<JigsawBlockInfo> jigsaws() {
            if (this.cachedJigsaws == null) {
                this.cachedJigsaws = this.blocks(Blocks.JIGSAW).stream().map(JigsawBlockInfo::of).toList();
            }
            return this.cachedJigsaws;
        }

        public List<StructureBlockInfo> blocks() {
            return this.blocks;
        }

        public List<StructureBlockInfo> blocks(Block $$02) {
            return this.cache.computeIfAbsent($$02, $$0 -> this.blocks.stream().filter($$1 -> $$1.state.is((Block)$$0)).collect(Collectors.toList()));
        }
    }

    public static class StructureEntityInfo {
        public final Vec3 pos;
        public final BlockPos blockPos;
        public final CompoundTag nbt;

        public StructureEntityInfo(Vec3 $$0, BlockPos $$1, CompoundTag $$2) {
            this.pos = $$0;
            this.blockPos = $$1;
            this.nbt = $$2;
        }
    }

    public static final class JigsawBlockInfo
    extends Record {
        final StructureBlockInfo info;
        private final JigsawBlockEntity.JointType jointType;
        private final ResourceLocation name;
        private final ResourceKey<StructureTemplatePool> pool;
        private final ResourceLocation target;
        private final int placementPriority;
        private final int selectionPriority;

        public JigsawBlockInfo(StructureBlockInfo $$0, JigsawBlockEntity.JointType $$1, ResourceLocation $$2, ResourceKey<StructureTemplatePool> $$3, ResourceLocation $$4, int $$5, int $$6) {
            this.info = $$0;
            this.jointType = $$1;
            this.name = $$2;
            this.pool = $$3;
            this.target = $$4;
            this.placementPriority = $$5;
            this.selectionPriority = $$6;
        }

        public static JigsawBlockInfo of(StructureBlockInfo $$0) {
            CompoundTag $$1 = Objects.requireNonNull($$0.nbt(), () -> String.valueOf((Object)$$0) + " nbt was null");
            return new JigsawBlockInfo($$0, StructureTemplate.getJointType($$1, $$0.state()), $$1.read("name", ResourceLocation.CODEC).orElse(JigsawBlockEntity.EMPTY_ID), $$1.read("pool", JigsawBlockEntity.POOL_CODEC).orElse(Pools.EMPTY), $$1.read("target", ResourceLocation.CODEC).orElse(JigsawBlockEntity.EMPTY_ID), $$1.getIntOr("placement_priority", 0), $$1.getIntOr("selection_priority", 0));
        }

        public String toString() {
            return String.format(Locale.ROOT, "<JigsawBlockInfo | %s | %s | name: %s | pool: %s | target: %s | placement: %d | selection: %d | %s>", this.info.pos, this.info.state, this.name, this.pool.location(), this.target, this.placementPriority, this.selectionPriority, this.info.nbt);
        }

        public JigsawBlockInfo withInfo(StructureBlockInfo $$0) {
            return new JigsawBlockInfo($$0, this.jointType, this.name, this.pool, this.target, this.placementPriority, this.selectionPriority);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{JigsawBlockInfo.class, "info;jointType;name;pool;target;placementPriority;selectionPriority", "info", "jointType", "name", "pool", "target", "placementPriority", "selectionPriority"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{JigsawBlockInfo.class, "info;jointType;name;pool;target;placementPriority;selectionPriority", "info", "jointType", "name", "pool", "target", "placementPriority", "selectionPriority"}, this, $$0);
        }

        public StructureBlockInfo info() {
            return this.info;
        }

        public JigsawBlockEntity.JointType jointType() {
            return this.jointType;
        }

        public ResourceLocation name() {
            return this.name;
        }

        public ResourceKey<StructureTemplatePool> pool() {
            return this.pool;
        }

        public ResourceLocation target() {
            return this.target;
        }

        public int placementPriority() {
            return this.placementPriority;
        }

        public int selectionPriority() {
            return this.selectionPriority;
        }
    }

    static class SimplePalette
    implements Iterable<BlockState> {
        public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        private final IdMapper<BlockState> ids = new IdMapper(16);
        private int lastId;

        SimplePalette() {
        }

        public int idFor(BlockState $$0) {
            int $$1 = this.ids.getId($$0);
            if ($$1 == -1) {
                $$1 = this.lastId++;
                this.ids.addMapping($$0, $$1);
            }
            return $$1;
        }

        @Nullable
        public BlockState stateFor(int $$0) {
            BlockState $$1 = this.ids.byId($$0);
            return $$1 == null ? DEFAULT_BLOCK_STATE : $$1;
        }

        @Override
        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(BlockState $$0, int $$1) {
            this.ids.addMapping($$0, $$1);
        }
    }
}


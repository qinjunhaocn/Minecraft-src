/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.ints.IntArrays
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public class UpgradeData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final UpgradeData EMPTY = new UpgradeData(EmptyBlockGetter.INSTANCE);
    private static final String TAG_INDICES = "Indices";
    private static final Direction8[] DIRECTIONS = Direction8.values();
    private static final Codec<List<SavedTick<Block>>> BLOCK_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.BLOCK.byNameCodec().orElse((Object)Blocks.AIR)).listOf();
    private static final Codec<List<SavedTick<Fluid>>> FLUID_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.FLUID.byNameCodec().orElse((Object)Fluids.EMPTY)).listOf();
    private final EnumSet<Direction8> sides = EnumSet.noneOf(Direction8.class);
    private final List<SavedTick<Block>> neighborBlockTicks = Lists.newArrayList();
    private final List<SavedTick<Fluid>> neighborFluidTicks = Lists.newArrayList();
    private final int[][] index;
    static final Map<Block, BlockFixer> MAP = new IdentityHashMap<Block, BlockFixer>();
    static final Set<BlockFixer> CHUNKY_FIXERS = Sets.newHashSet();

    private UpgradeData(LevelHeightAccessor $$0) {
        this.index = new int[$$0.getSectionsCount()][];
    }

    public UpgradeData(CompoundTag $$02, LevelHeightAccessor $$1) {
        this($$1);
        $$02.getCompound(TAG_INDICES).ifPresent($$0 -> {
            for (int $$1 = 0; $$1 < this.index.length; ++$$1) {
                this.index[$$1] = $$0.getIntArray(String.valueOf($$1)).orElse(null);
            }
        });
        int $$2 = $$02.getIntOr("Sides", 0);
        for (Direction8 $$3 : Direction8.values()) {
            if (($$2 & 1 << $$3.ordinal()) == 0) continue;
            this.sides.add($$3);
        }
        $$02.read("neighbor_block_ticks", BLOCK_TICKS_CODEC).ifPresent(this.neighborBlockTicks::addAll);
        $$02.read("neighbor_fluid_ticks", FLUID_TICKS_CODEC).ifPresent(this.neighborFluidTicks::addAll);
    }

    private UpgradeData(UpgradeData $$0) {
        this.sides.addAll($$0.sides);
        this.neighborBlockTicks.addAll($$0.neighborBlockTicks);
        this.neighborFluidTicks.addAll($$0.neighborFluidTicks);
        this.index = new int[$$0.index.length][];
        for (int $$1 = 0; $$1 < $$0.index.length; ++$$1) {
            int[] $$2 = $$0.index[$$1];
            this.index[$$1] = $$2 != null ? IntArrays.copy((int[])$$2) : null;
        }
    }

    public void upgrade(LevelChunk $$0) {
        this.upgradeInside($$0);
        for (Direction8 $$12 : DIRECTIONS) {
            UpgradeData.upgradeSides($$0, $$12);
        }
        Level $$2 = $$0.getLevel();
        this.neighborBlockTicks.forEach($$1 -> {
            Block $$2 = $$1.type() == Blocks.AIR ? $$2.getBlockState($$1.pos()).getBlock() : (Block)$$1.type();
            $$2.scheduleTick($$1.pos(), $$2, $$1.delay(), $$1.priority());
        });
        this.neighborFluidTicks.forEach($$1 -> {
            Fluid $$2 = $$1.type() == Fluids.EMPTY ? $$2.getFluidState($$1.pos()).getType() : (Fluid)$$1.type();
            $$2.scheduleTick($$1.pos(), $$2, $$1.delay(), $$1.priority());
        });
        CHUNKY_FIXERS.forEach($$1 -> $$1.processChunk($$2));
    }

    private static void upgradeSides(LevelChunk $$0, Direction8 $$1) {
        Level $$2 = $$0.getLevel();
        if (!$$0.getUpgradeData().sides.remove((Object)$$1)) {
            return;
        }
        Set<Direction> $$3 = $$1.getDirections();
        boolean $$4 = false;
        int $$5 = 15;
        boolean $$6 = $$3.contains(Direction.EAST);
        boolean $$7 = $$3.contains(Direction.WEST);
        boolean $$8 = $$3.contains(Direction.SOUTH);
        boolean $$9 = $$3.contains(Direction.NORTH);
        boolean $$10 = $$3.size() == 1;
        ChunkPos $$11 = $$0.getPos();
        int $$12 = $$11.getMinBlockX() + ($$10 && ($$9 || $$8) ? 1 : ($$7 ? 0 : 15));
        int $$13 = $$11.getMinBlockX() + ($$10 && ($$9 || $$8) ? 14 : ($$7 ? 0 : 15));
        int $$14 = $$11.getMinBlockZ() + ($$10 && ($$6 || $$7) ? 1 : ($$9 ? 0 : 15));
        int $$15 = $$11.getMinBlockZ() + ($$10 && ($$6 || $$7) ? 14 : ($$9 ? 0 : 15));
        Direction[] $$16 = Direction.values();
        BlockPos.MutableBlockPos $$17 = new BlockPos.MutableBlockPos();
        for (BlockPos $$18 : BlockPos.betweenClosed($$12, $$2.getMinY(), $$14, $$13, $$2.getMaxY(), $$15)) {
            BlockState $$19;
            BlockState $$20 = $$19 = $$2.getBlockState($$18);
            for (Direction $$21 : $$16) {
                $$17.setWithOffset((Vec3i)$$18, $$21);
                $$20 = UpgradeData.updateState($$20, $$21, $$2, $$18, $$17);
            }
            Block.updateOrDestroy($$19, $$20, $$2, $$18, 18);
        }
    }

    private static BlockState updateState(BlockState $$0, Direction $$1, LevelAccessor $$2, BlockPos $$3, BlockPos $$4) {
        return MAP.getOrDefault($$0.getBlock(), BlockFixers.DEFAULT).updateShape($$0, $$1, $$2.getBlockState($$4), $$2, $$3, $$4);
    }

    private void upgradeInside(LevelChunk $$0) {
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos();
        ChunkPos $$3 = $$0.getPos();
        Level $$4 = $$0.getLevel();
        for (int $$5 = 0; $$5 < this.index.length; ++$$5) {
            LevelChunkSection $$6 = $$0.getSection($$5);
            int[] $$7 = this.index[$$5];
            this.index[$$5] = null;
            if ($$7 == null || $$7.length <= 0) continue;
            Direction[] $$8 = Direction.values();
            PalettedContainer<BlockState> $$9 = $$6.getStates();
            int $$10 = $$0.getSectionYFromSectionIndex($$5);
            int $$11 = SectionPos.sectionToBlockCoord($$10);
            for (int $$12 : $$7) {
                BlockState $$16;
                int $$13 = $$12 & 0xF;
                int $$14 = $$12 >> 8 & 0xF;
                int $$15 = $$12 >> 4 & 0xF;
                $$1.set($$3.getMinBlockX() + $$13, $$11 + $$14, $$3.getMinBlockZ() + $$15);
                BlockState $$17 = $$16 = $$9.get($$12);
                for (Direction $$18 : $$8) {
                    $$2.setWithOffset((Vec3i)$$1, $$18);
                    if (SectionPos.blockToSectionCoord($$1.getX()) != $$3.x || SectionPos.blockToSectionCoord($$1.getZ()) != $$3.z) continue;
                    $$17 = UpgradeData.updateState($$17, $$18, $$4, $$1, $$2);
                }
                Block.updateOrDestroy($$16, $$17, $$4, $$1, 18);
            }
        }
        for (int $$19 = 0; $$19 < this.index.length; ++$$19) {
            if (this.index[$$19] != null) {
                LOGGER.warn("Discarding update data for section {} for chunk ({} {})", $$4.getSectionYFromSectionIndex($$19), $$3.x, $$3.z);
            }
            this.index[$$19] = null;
        }
    }

    public boolean isEmpty() {
        for (int[] $$0 : this.index) {
            if ($$0 == null) continue;
            return false;
        }
        return this.sides.isEmpty();
    }

    public CompoundTag write() {
        CompoundTag $$0 = new CompoundTag();
        CompoundTag $$1 = new CompoundTag();
        for (int $$2 = 0; $$2 < this.index.length; ++$$2) {
            String $$3 = String.valueOf($$2);
            if (this.index[$$2] == null || this.index[$$2].length == 0) continue;
            $$1.a($$3, this.index[$$2]);
        }
        if (!$$1.isEmpty()) {
            $$0.put(TAG_INDICES, $$1);
        }
        int $$4 = 0;
        for (Direction8 $$5 : this.sides) {
            $$4 |= 1 << $$5.ordinal();
        }
        $$0.putByte("Sides", (byte)$$4);
        if (!this.neighborBlockTicks.isEmpty()) {
            $$0.store("neighbor_block_ticks", BLOCK_TICKS_CODEC, this.neighborBlockTicks);
        }
        if (!this.neighborFluidTicks.isEmpty()) {
            $$0.store("neighbor_fluid_ticks", FLUID_TICKS_CODEC, this.neighborFluidTicks);
        }
        return $$0;
    }

    public UpgradeData copy() {
        if (this == EMPTY) {
            return EMPTY;
        }
        return new UpgradeData(this);
    }

    static abstract sealed class BlockFixers
    extends Enum<BlockFixers>
    implements BlockFixer {
        public static final /* enum */ BlockFixers BLACKLIST = new BlockFixers(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN}){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                return $$0;
            }
        };
        public static final /* enum */ BlockFixers DEFAULT = new BlockFixers(new Block[0]){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                return $$0.updateShape($$3, $$3, $$4, $$1, $$5, $$3.getBlockState($$5), $$3.getRandom());
            }
        };
        public static final /* enum */ BlockFixers CHEST = new BlockFixers(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                if ($$2.is($$0.getBlock()) && $$1.getAxis().isHorizontal() && $$0.getValue(ChestBlock.TYPE) == ChestType.SINGLE && $$2.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
                    Direction $$6 = $$0.getValue(ChestBlock.FACING);
                    if ($$1.getAxis() != $$6.getAxis() && $$6 == $$2.getValue(ChestBlock.FACING)) {
                        ChestType $$7 = $$1 == $$6.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                        $$3.setBlock($$5, (BlockState)$$2.setValue(ChestBlock.TYPE, $$7.getOpposite()), 18);
                        if ($$6 == Direction.NORTH || $$6 == Direction.EAST) {
                            BlockEntity $$8 = $$3.getBlockEntity($$4);
                            BlockEntity $$9 = $$3.getBlockEntity($$5);
                            if ($$8 instanceof ChestBlockEntity && $$9 instanceof ChestBlockEntity) {
                                ChestBlockEntity.swapContents((ChestBlockEntity)$$8, (ChestBlockEntity)$$9);
                            }
                        }
                        return (BlockState)$$0.setValue(ChestBlock.TYPE, $$7);
                    }
                }
                return $$0;
            }
        };
        public static final /* enum */ BlockFixers LEAVES = new BlockFixers(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.CHERRY_LEAVES, Blocks.BIRCH_LEAVES, Blocks.PALE_OAK_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}){
            private final ThreadLocal<List<ObjectSet<BlockPos>>> queue = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                BlockState $$6 = $$0.updateShape($$3, $$3, $$4, $$1, $$5, $$3.getBlockState($$5), $$3.getRandom());
                if ($$0 != $$6) {
                    int $$7 = $$6.getValue(BlockStateProperties.DISTANCE);
                    List<ObjectSet<BlockPos>> $$8 = this.queue.get();
                    if ($$8.isEmpty()) {
                        for (int $$9 = 0; $$9 < 7; ++$$9) {
                            $$8.add((ObjectSet<BlockPos>)new ObjectOpenHashSet());
                        }
                    }
                    $$8.get($$7).add((Object)$$4.immutable());
                }
                return $$0;
            }

            @Override
            public void processChunk(LevelAccessor $$0) {
                BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
                List<ObjectSet<BlockPos>> $$2 = this.queue.get();
                for (int $$3 = 2; $$3 < $$2.size(); ++$$3) {
                    int $$4 = $$3 - 1;
                    ObjectSet<BlockPos> $$5 = $$2.get($$4);
                    ObjectSet<BlockPos> $$6 = $$2.get($$3);
                    for (BlockPos $$7 : $$5) {
                        BlockState $$8 = $$0.getBlockState($$7);
                        if ($$8.getValue(BlockStateProperties.DISTANCE) < $$4) continue;
                        $$0.setBlock($$7, (BlockState)$$8.setValue(BlockStateProperties.DISTANCE, $$4), 18);
                        if ($$3 == 7) continue;
                        for (Direction $$9 : DIRECTIONS) {
                            $$1.setWithOffset((Vec3i)$$7, $$9);
                            BlockState $$10 = $$0.getBlockState($$1);
                            if (!$$10.hasProperty(BlockStateProperties.DISTANCE) || $$8.getValue(BlockStateProperties.DISTANCE) <= $$3) continue;
                            $$6.add((Object)$$1.immutable());
                        }
                    }
                }
                $$2.clear();
            }
        };
        public static final /* enum */ BlockFixers STEM_BLOCK = new BlockFixers(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}){

            @Override
            public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
                if ($$0.getValue(StemBlock.AGE) == 7) {
                    Block $$6;
                    Block block = $$6 = $$0.is(Blocks.PUMPKIN_STEM) ? Blocks.PUMPKIN : Blocks.MELON;
                    if ($$2.is($$6)) {
                        return (BlockState)($$0.is(Blocks.PUMPKIN_STEM) ? Blocks.ATTACHED_PUMPKIN_STEM : Blocks.ATTACHED_MELON_STEM).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, $$1);
                    }
                }
                return $$0;
            }
        };
        public static final Direction[] DIRECTIONS;
        private static final /* synthetic */ BlockFixers[] $VALUES;

        public static BlockFixers[] values() {
            return (BlockFixers[])$VALUES.clone();
        }

        public static BlockFixers valueOf(String $$0) {
            return Enum.valueOf(BlockFixers.class, $$0);
        }

        BlockFixers(Block ... $$0) {
            this(false, $$0);
        }

        BlockFixers(boolean $$0, Block ... $$1) {
            for (Block $$2 : $$1) {
                MAP.put($$2, this);
            }
            if ($$0) {
                CHUNKY_FIXERS.add(this);
            }
        }

        private static /* synthetic */ BlockFixers[] a() {
            return new BlockFixers[]{BLACKLIST, DEFAULT, CHEST, LEAVES, STEM_BLOCK};
        }

        static {
            $VALUES = BlockFixers.a();
            DIRECTIONS = Direction.values();
        }
    }

    public static interface BlockFixer {
        public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6);

        default public void processChunk(LevelAccessor $$0) {
        }
    }
}


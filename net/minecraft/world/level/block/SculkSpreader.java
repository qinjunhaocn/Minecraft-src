/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SculkSpreader {
    public static final int MAX_GROWTH_RATE_RADIUS = 24;
    public static final int MAX_CHARGE = 1000;
    public static final float MAX_DECAY_FACTOR = 0.5f;
    private static final int MAX_CURSORS = 32;
    public static final int SHRIEKER_PLACEMENT_RATE = 11;
    public static final int MAX_CURSOR_DISTANCE = 1024;
    final boolean isWorldGeneration;
    private final TagKey<Block> replaceableBlocks;
    private final int growthSpawnCost;
    private final int noGrowthRadius;
    private final int chargeDecayRate;
    private final int additionalDecayRate;
    private List<ChargeCursor> cursors = new ArrayList<ChargeCursor>();

    public SculkSpreader(boolean $$0, TagKey<Block> $$1, int $$2, int $$3, int $$4, int $$5) {
        this.isWorldGeneration = $$0;
        this.replaceableBlocks = $$1;
        this.growthSpawnCost = $$2;
        this.noGrowthRadius = $$3;
        this.chargeDecayRate = $$4;
        this.additionalDecayRate = $$5;
    }

    public static SculkSpreader createLevelSpreader() {
        return new SculkSpreader(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static SculkSpreader createWorldGenSpreader() {
        return new SculkSpreader(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> replaceableBlocks() {
        return this.replaceableBlocks;
    }

    public int growthSpawnCost() {
        return this.growthSpawnCost;
    }

    public int noGrowthRadius() {
        return this.noGrowthRadius;
    }

    public int chargeDecayRate() {
        return this.chargeDecayRate;
    }

    public int additionalDecayRate() {
        return this.additionalDecayRate;
    }

    public boolean isWorldGeneration() {
        return this.isWorldGeneration;
    }

    @VisibleForTesting
    public List<ChargeCursor> getCursors() {
        return this.cursors;
    }

    public void clear() {
        this.cursors.clear();
    }

    public void load(ValueInput $$0) {
        this.cursors.clear();
        $$0.read("cursors", ChargeCursor.CODEC.sizeLimitedListOf(32)).orElse(List.of()).forEach(this::addCursor);
    }

    public void save(ValueOutput $$0) {
        $$0.store("cursors", ChargeCursor.CODEC.listOf(), this.cursors);
    }

    public void addCursors(BlockPos $$0, int $$1) {
        while ($$1 > 0) {
            int $$2 = Math.min($$1, 1000);
            this.addCursor(new ChargeCursor($$0, $$2));
            $$1 -= $$2;
        }
    }

    private void addCursor(ChargeCursor $$0) {
        if (this.cursors.size() >= 32) {
            return;
        }
        this.cursors.add($$0);
    }

    public void updateCursors(LevelAccessor $$0, BlockPos $$12, RandomSource $$22, boolean $$3) {
        if (this.cursors.isEmpty()) {
            return;
        }
        ArrayList<ChargeCursor> $$4 = new ArrayList<ChargeCursor>();
        HashMap<BlockPos, ChargeCursor> $$5 = new HashMap<BlockPos, ChargeCursor>();
        Object2IntOpenHashMap $$6 = new Object2IntOpenHashMap();
        for (ChargeCursor $$7 : this.cursors) {
            if ($$7.isPosUnreasonable($$12)) continue;
            $$7.update($$0, $$12, $$22, this, $$3);
            if ($$7.charge <= 0) {
                $$0.levelEvent(3006, $$7.getPos(), 0);
                continue;
            }
            BlockPos $$8 = $$7.getPos();
            $$6.computeInt((Object)$$8, ($$1, $$2) -> ($$2 == null ? 0 : $$2) + $$0.charge);
            ChargeCursor $$9 = (ChargeCursor)$$5.get($$8);
            if ($$9 == null) {
                $$5.put($$8, $$7);
                $$4.add($$7);
                continue;
            }
            if (!this.isWorldGeneration() && $$7.charge + $$9.charge <= 1000) {
                $$9.mergeWith($$7);
                continue;
            }
            $$4.add($$7);
            if ($$7.charge >= $$9.charge) continue;
            $$5.put($$8, $$7);
        }
        for (Object2IntMap.Entry $$10 : $$6.object2IntEntrySet()) {
            Set<Direction> $$14;
            BlockPos $$11 = (BlockPos)$$10.getKey();
            int $$122 = $$10.getIntValue();
            ChargeCursor $$13 = (ChargeCursor)$$5.get($$11);
            Set<Direction> set = $$14 = $$13 == null ? null : $$13.getFacingData();
            if ($$122 <= 0 || $$14 == null) continue;
            int $$15 = (int)(Math.log1p($$122) / (double)2.3f) + 1;
            int $$16 = ($$15 << 6) + MultifaceBlock.pack($$14);
            $$0.levelEvent(3006, $$11, $$16);
        }
        this.cursors = $$4;
    }

    private static /* synthetic */ Integer lambda$save$0(ChargeCursor $$0) {
        return 1;
    }

    public static class ChargeCursor {
        private static final ObjectArrayList<Vec3i> NON_CORNER_NEIGHBOURS = Util.make(new ObjectArrayList(18), $$02 -> BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter($$0 -> ($$0.getX() == 0 || $$0.getY() == 0 || $$0.getZ() == 0) && !$$0.equals(BlockPos.ZERO)).map(BlockPos::immutable).forEach(arg_0 -> ((ObjectArrayList)$$02).add(arg_0)));
        public static final int MAX_CURSOR_DECAY_DELAY = 1;
        private BlockPos pos;
        int charge;
        private int updateDelay;
        private int decayDelay;
        @Nullable
        private Set<Direction> facings;
        private static final Codec<Set<Direction>> DIRECTION_SET = Direction.CODEC.listOf().xmap($$0 -> Sets.newEnumSet($$0, Direction.class), Lists::newArrayList);
        public static final Codec<ChargeCursor> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockPos.CODEC.fieldOf("pos").forGetter(ChargeCursor::getPos), (App)Codec.intRange((int)0, (int)1000).fieldOf("charge").orElse((Object)0).forGetter(ChargeCursor::getCharge), (App)Codec.intRange((int)0, (int)1).fieldOf("decay_delay").orElse((Object)1).forGetter(ChargeCursor::getDecayDelay), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).fieldOf("update_delay").orElse((Object)0).forGetter($$0 -> $$0.updateDelay), (App)DIRECTION_SET.lenientOptionalFieldOf("facings").forGetter($$0 -> Optional.ofNullable($$0.getFacingData()))).apply((Applicative)$$02, ChargeCursor::new));

        private ChargeCursor(BlockPos $$0, int $$1, int $$2, int $$3, Optional<Set<Direction>> $$4) {
            this.pos = $$0;
            this.charge = $$1;
            this.decayDelay = $$2;
            this.updateDelay = $$3;
            this.facings = $$4.orElse(null);
        }

        public ChargeCursor(BlockPos $$0, int $$1) {
            this($$0, $$1, 1, 0, Optional.empty());
        }

        public BlockPos getPos() {
            return this.pos;
        }

        boolean isPosUnreasonable(BlockPos $$0) {
            return this.pos.distChessboard($$0) > 1024;
        }

        public int getCharge() {
            return this.charge;
        }

        public int getDecayDelay() {
            return this.decayDelay;
        }

        @Nullable
        public Set<Direction> getFacingData() {
            return this.facings;
        }

        private boolean shouldUpdate(LevelAccessor $$0, BlockPos $$1, boolean $$2) {
            if (this.charge <= 0) {
                return false;
            }
            if ($$2) {
                return true;
            }
            if ($$0 instanceof ServerLevel) {
                ServerLevel $$3 = (ServerLevel)$$0;
                return $$3.shouldTickBlocksAt($$1);
            }
            return false;
        }

        public void update(LevelAccessor $$0, BlockPos $$1, RandomSource $$2, SculkSpreader $$3, boolean $$4) {
            if (!this.shouldUpdate($$0, $$1, $$3.isWorldGeneration)) {
                return;
            }
            if (this.updateDelay > 0) {
                --this.updateDelay;
                return;
            }
            BlockState $$5 = $$0.getBlockState(this.pos);
            SculkBehaviour $$6 = ChargeCursor.getBlockBehaviour($$5);
            if ($$4 && $$6.attemptSpreadVein($$0, this.pos, $$5, this.facings, $$3.isWorldGeneration())) {
                if ($$6.canChangeBlockStateOnSpread()) {
                    $$5 = $$0.getBlockState(this.pos);
                    $$6 = ChargeCursor.getBlockBehaviour($$5);
                }
                $$0.playSound(null, this.pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            this.charge = $$6.attemptUseCharge(this, $$0, $$1, $$2, $$3, $$4);
            if (this.charge <= 0) {
                $$6.onDischarged($$0, $$5, this.pos, $$2);
                return;
            }
            BlockPos $$7 = ChargeCursor.getValidMovementPos($$0, this.pos, $$2);
            if ($$7 != null) {
                $$6.onDischarged($$0, $$5, this.pos, $$2);
                this.pos = $$7.immutable();
                if ($$3.isWorldGeneration() && !this.pos.closerThan(new Vec3i($$1.getX(), this.pos.getY(), $$1.getZ()), 15.0)) {
                    this.charge = 0;
                    return;
                }
                $$5 = $$0.getBlockState($$7);
            }
            if ($$5.getBlock() instanceof SculkBehaviour) {
                this.facings = MultifaceBlock.availableFaces($$5);
            }
            this.decayDelay = $$6.updateDecayDelay(this.decayDelay);
            this.updateDelay = $$6.getSculkSpreadDelay();
        }

        void mergeWith(ChargeCursor $$0) {
            this.charge += $$0.charge;
            $$0.charge = 0;
            this.updateDelay = Math.min(this.updateDelay, $$0.updateDelay);
        }

        private static SculkBehaviour getBlockBehaviour(BlockState $$0) {
            SculkBehaviour $$1;
            Block block = $$0.getBlock();
            return block instanceof SculkBehaviour ? ($$1 = (SculkBehaviour)((Object)block)) : SculkBehaviour.DEFAULT;
        }

        private static List<Vec3i> getRandomizedNonCornerNeighbourOffsets(RandomSource $$0) {
            return Util.shuffledCopy(NON_CORNER_NEIGHBOURS, $$0);
        }

        @Nullable
        private static BlockPos getValidMovementPos(LevelAccessor $$0, BlockPos $$1, RandomSource $$2) {
            BlockPos.MutableBlockPos $$3 = $$1.mutable();
            BlockPos.MutableBlockPos $$4 = $$1.mutable();
            for (Vec3i $$5 : ChargeCursor.getRandomizedNonCornerNeighbourOffsets($$2)) {
                $$4.setWithOffset((Vec3i)$$1, $$5);
                BlockState $$6 = $$0.getBlockState($$4);
                if (!($$6.getBlock() instanceof SculkBehaviour) || !ChargeCursor.isMovementUnobstructed($$0, $$1, $$4)) continue;
                $$3.set($$4);
                if (!SculkVeinBlock.hasSubstrateAccess($$0, $$6, $$4)) continue;
                break;
            }
            return $$3.equals($$1) ? null : $$3;
        }

        private static boolean isMovementUnobstructed(LevelAccessor $$0, BlockPos $$1, BlockPos $$2) {
            if ($$1.distManhattan($$2) == 1) {
                return true;
            }
            BlockPos $$3 = $$2.subtract($$1);
            Direction $$4 = Direction.fromAxisAndDirection(Direction.Axis.X, $$3.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction $$5 = Direction.fromAxisAndDirection(Direction.Axis.Y, $$3.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction $$6 = Direction.fromAxisAndDirection(Direction.Axis.Z, $$3.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            if ($$3.getX() == 0) {
                return ChargeCursor.isUnobstructed($$0, $$1, $$5) || ChargeCursor.isUnobstructed($$0, $$1, $$6);
            }
            if ($$3.getY() == 0) {
                return ChargeCursor.isUnobstructed($$0, $$1, $$4) || ChargeCursor.isUnobstructed($$0, $$1, $$6);
            }
            return ChargeCursor.isUnobstructed($$0, $$1, $$4) || ChargeCursor.isUnobstructed($$0, $$1, $$5);
        }

        private static boolean isUnobstructed(LevelAccessor $$0, BlockPos $$1, Direction $$2) {
            BlockPos $$3 = $$1.relative($$2);
            return !$$0.getBlockState($$3).isFaceSturdy($$0, $$3, $$2.getOpposite());
        }
    }
}


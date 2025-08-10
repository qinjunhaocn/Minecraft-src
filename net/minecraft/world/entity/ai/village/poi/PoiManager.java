/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.entity.ai.village.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;

public class PoiManager
extends SectionStorage<PoiSection, PoiSection.Packed> {
    public static final int MAX_VILLAGE_DISTANCE = 6;
    public static final int VILLAGE_SECTION_SIZE = 1;
    private final DistanceTracker distanceTracker;
    private final LongSet loadedChunks = new LongOpenHashSet();

    public PoiManager(RegionStorageInfo $$0, Path $$1, DataFixer $$2, boolean $$3, RegistryAccess $$4, ChunkIOErrorReporter $$5, LevelHeightAccessor $$6) {
        super(new SimpleRegionStorage($$0, $$1, $$2, $$3, DataFixTypes.POI_CHUNK), PoiSection.Packed.CODEC, PoiSection::pack, PoiSection.Packed::unpack, PoiSection::new, $$4, $$5, $$6);
        this.distanceTracker = new DistanceTracker();
    }

    public void add(BlockPos $$0, Holder<PoiType> $$1) {
        ((PoiSection)this.getOrCreate(SectionPos.asLong($$0))).add($$0, $$1);
    }

    public void remove(BlockPos $$0) {
        this.getOrLoad(SectionPos.asLong($$0)).ifPresent($$1 -> $$1.remove($$0));
    }

    public long getCountInRange(Predicate<Holder<PoiType>> $$0, BlockPos $$1, int $$2, Occupancy $$3) {
        return this.getInRange($$0, $$1, $$2, $$3).count();
    }

    public boolean existsAtPosition(ResourceKey<PoiType> $$0, BlockPos $$12) {
        return this.exists($$12, $$1 -> $$1.is($$0));
    }

    public Stream<PoiRecord> getInSquare(Predicate<Holder<PoiType>> $$0, BlockPos $$1, int $$22, Occupancy $$3) {
        int $$4 = Math.floorDiv($$22, 16) + 1;
        return ChunkPos.rangeClosed(new ChunkPos($$1), $$4).flatMap($$2 -> this.getInChunk($$0, (ChunkPos)$$2, $$3)).filter($$2 -> {
            BlockPos $$3 = $$2.getPos();
            return Math.abs($$3.getX() - $$1.getX()) <= $$22 && Math.abs($$3.getZ() - $$1.getZ()) <= $$22;
        });
    }

    public Stream<PoiRecord> getInRange(Predicate<Holder<PoiType>> $$0, BlockPos $$1, int $$22, Occupancy $$3) {
        int $$4 = $$22 * $$22;
        return this.getInSquare($$0, $$1, $$22, $$3).filter($$2 -> $$2.getPos().distSqr($$1) <= (double)$$4);
    }

    @VisibleForDebug
    public Stream<PoiRecord> getInChunk(Predicate<Holder<PoiType>> $$0, ChunkPos $$12, Occupancy $$22) {
        return IntStream.rangeClosed(this.levelHeightAccessor.getMinSectionY(), this.levelHeightAccessor.getMaxSectionY()).boxed().map($$1 -> this.getOrLoad(SectionPos.of($$12, $$1).asLong())).filter(Optional::isPresent).flatMap($$2 -> ((PoiSection)$$2.get()).getRecords($$0, $$22));
    }

    public Stream<BlockPos> findAll(Predicate<Holder<PoiType>> $$0, Predicate<BlockPos> $$1, BlockPos $$2, int $$3, Occupancy $$4) {
        return this.getInRange($$0, $$2, $$3, $$4).map(PoiRecord::getPos).filter($$1);
    }

    public Stream<Pair<Holder<PoiType>, BlockPos>> findAllWithType(Predicate<Holder<PoiType>> $$02, Predicate<BlockPos> $$12, BlockPos $$2, int $$3, Occupancy $$4) {
        return this.getInRange($$02, $$2, $$3, $$4).filter($$1 -> $$12.test($$1.getPos())).map($$0 -> Pair.of($$0.getPoiType(), (Object)$$0.getPos()));
    }

    public Stream<Pair<Holder<PoiType>, BlockPos>> findAllClosestFirstWithType(Predicate<Holder<PoiType>> $$0, Predicate<BlockPos> $$12, BlockPos $$2, int $$3, Occupancy $$4) {
        return this.findAllWithType($$0, $$12, $$2, $$3, $$4).sorted(Comparator.comparingDouble($$1 -> ((BlockPos)$$1.getSecond()).distSqr($$2)));
    }

    public Optional<BlockPos> find(Predicate<Holder<PoiType>> $$0, Predicate<BlockPos> $$1, BlockPos $$2, int $$3, Occupancy $$4) {
        return this.findAll($$0, $$1, $$2, $$3, $$4).findFirst();
    }

    public Optional<BlockPos> findClosest(Predicate<Holder<PoiType>> $$0, BlockPos $$12, int $$2, Occupancy $$3) {
        return this.getInRange($$0, $$12, $$2, $$3).map(PoiRecord::getPos).min(Comparator.comparingDouble($$1 -> $$1.distSqr($$12)));
    }

    public Optional<Pair<Holder<PoiType>, BlockPos>> findClosestWithType(Predicate<Holder<PoiType>> $$02, BlockPos $$12, int $$2, Occupancy $$3) {
        return this.getInRange($$02, $$12, $$2, $$3).min(Comparator.comparingDouble($$1 -> $$1.getPos().distSqr($$12))).map($$0 -> Pair.of($$0.getPoiType(), (Object)$$0.getPos()));
    }

    public Optional<BlockPos> findClosest(Predicate<Holder<PoiType>> $$0, Predicate<BlockPos> $$12, BlockPos $$2, int $$3, Occupancy $$4) {
        return this.getInRange($$0, $$2, $$3, $$4).map(PoiRecord::getPos).filter($$12).min(Comparator.comparingDouble($$1 -> $$1.distSqr($$2)));
    }

    public Optional<BlockPos> take(Predicate<Holder<PoiType>> $$02, BiPredicate<Holder<PoiType>, BlockPos> $$12, BlockPos $$2, int $$3) {
        return this.getInRange($$02, $$2, $$3, Occupancy.HAS_SPACE).filter($$1 -> $$12.test($$1.getPoiType(), $$1.getPos())).findFirst().map($$0 -> {
            $$0.acquireTicket();
            return $$0.getPos();
        });
    }

    public Optional<BlockPos> getRandom(Predicate<Holder<PoiType>> $$0, Predicate<BlockPos> $$12, Occupancy $$2, BlockPos $$3, int $$4, RandomSource $$5) {
        List<PoiRecord> $$6 = Util.toShuffledList(this.getInRange($$0, $$3, $$4, $$2), $$5);
        return $$6.stream().filter($$1 -> $$12.test($$1.getPos())).findFirst().map(PoiRecord::getPos);
    }

    public boolean release(BlockPos $$0) {
        return this.getOrLoad(SectionPos.asLong($$0)).map($$1 -> $$1.release($$0)).orElseThrow(() -> Util.pauseInIde(new IllegalStateException("POI never registered at " + String.valueOf($$0))));
    }

    public boolean exists(BlockPos $$0, Predicate<Holder<PoiType>> $$1) {
        return this.getOrLoad(SectionPos.asLong($$0)).map($$2 -> $$2.exists($$0, $$1)).orElse(false);
    }

    public Optional<Holder<PoiType>> getType(BlockPos $$0) {
        return this.getOrLoad(SectionPos.asLong($$0)).flatMap($$1 -> $$1.getType($$0));
    }

    @Deprecated
    @VisibleForDebug
    public int getFreeTickets(BlockPos $$0) {
        return this.getOrLoad(SectionPos.asLong($$0)).map($$1 -> $$1.getFreeTickets($$0)).orElse(0);
    }

    public int sectionsToVillage(SectionPos $$0) {
        this.distanceTracker.runAllUpdates();
        return this.distanceTracker.getLevel($$0.asLong());
    }

    boolean isVillageCenter(long $$0) {
        Optional $$1 = this.get($$0);
        if ($$1 == null) {
            return false;
        }
        return $$1.map($$02 -> $$02.getRecords($$0 -> $$0.is(PoiTypeTags.VILLAGE), Occupancy.IS_OCCUPIED).findAny().isPresent()).orElse(false);
    }

    @Override
    public void tick(BooleanSupplier $$0) {
        super.tick($$0);
        this.distanceTracker.runAllUpdates();
    }

    @Override
    protected void setDirty(long $$0) {
        super.setDirty($$0);
        this.distanceTracker.update($$0, this.distanceTracker.getLevelFromSource($$0), false);
    }

    @Override
    protected void onSectionLoad(long $$0) {
        this.distanceTracker.update($$0, this.distanceTracker.getLevelFromSource($$0), false);
    }

    public void checkConsistencyWithBlocks(SectionPos $$0, LevelChunkSection $$1) {
        Util.ifElse(this.getOrLoad($$0.asLong()), $$22 -> $$22.refresh($$2 -> {
            if (PoiManager.mayHavePoi($$1)) {
                this.updateFromSection($$1, $$0, (BiConsumer<BlockPos, Holder<PoiType>>)$$2);
            }
        }), () -> {
            if (PoiManager.mayHavePoi($$1)) {
                PoiSection $$2 = (PoiSection)this.getOrCreate($$0.asLong());
                this.updateFromSection($$1, $$0, $$2::add);
            }
        });
    }

    private static boolean mayHavePoi(LevelChunkSection $$0) {
        return $$0.maybeHas(PoiTypes::hasPoi);
    }

    private void updateFromSection(LevelChunkSection $$0, SectionPos $$1, BiConsumer<BlockPos, Holder<PoiType>> $$2) {
        $$1.blocksInside().forEach($$22 -> {
            BlockState $$3 = $$0.getBlockState(SectionPos.sectionRelative($$22.getX()), SectionPos.sectionRelative($$22.getY()), SectionPos.sectionRelative($$22.getZ()));
            PoiTypes.forState($$3).ifPresent($$2 -> $$2.accept((BlockPos)$$22, (Holder<PoiType>)$$2));
        });
    }

    public void ensureLoadedAndValid(LevelReader $$02, BlockPos $$12, int $$2) {
        SectionPos.aroundChunk(new ChunkPos($$12), Math.floorDiv($$2, 16), this.levelHeightAccessor.getMinSectionY(), this.levelHeightAccessor.getMaxSectionY()).map($$0 -> Pair.of((Object)$$0, this.getOrLoad($$0.asLong()))).filter($$0 -> ((Optional)$$0.getSecond()).map(PoiSection::isValid).orElse(false) == false).map($$0 -> ((SectionPos)$$0.getFirst()).chunk()).filter($$0 -> this.loadedChunks.add($$0.toLong())).forEach($$1 -> $$02.getChunk($$1.x, $$1.z, ChunkStatus.EMPTY));
    }

    final class DistanceTracker
    extends SectionTracker {
        private final Long2ByteMap levels;

        protected DistanceTracker() {
            super(7, 16, 256);
            this.levels = new Long2ByteOpenHashMap();
            this.levels.defaultReturnValue((byte)7);
        }

        @Override
        protected int getLevelFromSource(long $$0) {
            return PoiManager.this.isVillageCenter($$0) ? 0 : 7;
        }

        @Override
        protected int getLevel(long $$0) {
            return this.levels.get($$0);
        }

        @Override
        protected void setLevel(long $$0, int $$1) {
            if ($$1 > 6) {
                this.levels.remove($$0);
            } else {
                this.levels.put($$0, (byte)$$1);
            }
        }

        public void runAllUpdates() {
            super.runUpdates(Integer.MAX_VALUE);
        }
    }

    public static final class Occupancy
    extends Enum<Occupancy> {
        public static final /* enum */ Occupancy HAS_SPACE = new Occupancy(PoiRecord::hasSpace);
        public static final /* enum */ Occupancy IS_OCCUPIED = new Occupancy(PoiRecord::isOccupied);
        public static final /* enum */ Occupancy ANY = new Occupancy($$0 -> true);
        private final Predicate<? super PoiRecord> test;
        private static final /* synthetic */ Occupancy[] $VALUES;

        public static Occupancy[] values() {
            return (Occupancy[])$VALUES.clone();
        }

        public static Occupancy valueOf(String $$0) {
            return Enum.valueOf(Occupancy.class, $$0);
        }

        private Occupancy(Predicate<? super PoiRecord> $$0) {
            this.test = $$0;
        }

        public Predicate<? super PoiRecord> getTest() {
            return this.test;
        }

        private static /* synthetic */ Occupancy[] b() {
            return new Occupancy[]{HAS_SPACE, IS_OCCUPIED, ANY};
        }

        static {
            $VALUES = Occupancy.b();
        }
    }
}


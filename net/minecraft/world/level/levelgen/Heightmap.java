/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 */
package net.minecraft.world.level.levelgen;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.BitStorage;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.slf4j.Logger;

public class Heightmap {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Predicate<BlockState> NOT_AIR = $$0 -> !$$0.isAir();
    static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING = BlockBehaviour.BlockStateBase::blocksMotion;
    private final BitStorage data;
    private final Predicate<BlockState> isOpaque;
    private final ChunkAccess chunk;

    public Heightmap(ChunkAccess $$0, Types $$1) {
        this.isOpaque = $$1.isOpaque();
        this.chunk = $$0;
        int $$2 = Mth.ceillog2($$0.getHeight() + 1);
        this.data = new SimpleBitStorage($$2, 256);
    }

    public static void primeHeightmaps(ChunkAccess $$0, Set<Types> $$1) {
        if ($$1.isEmpty()) {
            return;
        }
        int $$2 = $$1.size();
        ObjectArrayList $$3 = new ObjectArrayList($$2);
        ObjectListIterator $$4 = $$3.iterator();
        int $$5 = $$0.getHighestSectionPosition() + 16;
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        for (int $$7 = 0; $$7 < 16; ++$$7) {
            block1: for (int $$8 = 0; $$8 < 16; ++$$8) {
                for (Types $$9 : $$1) {
                    $$3.add((Object)$$0.getOrCreateHeightmapUnprimed($$9));
                }
                for (int $$10 = $$5 - 1; $$10 >= $$0.getMinY(); --$$10) {
                    $$6.set($$7, $$10, $$8);
                    BlockState $$11 = $$0.getBlockState($$6);
                    if ($$11.is(Blocks.AIR)) continue;
                    while ($$4.hasNext()) {
                        Heightmap $$12 = (Heightmap)$$4.next();
                        if (!$$12.isOpaque.test($$11)) continue;
                        $$12.setHeight($$7, $$8, $$10 + 1);
                        $$4.remove();
                    }
                    if ($$3.isEmpty()) continue block1;
                    $$4.back($$2);
                }
            }
        }
    }

    public boolean update(int $$0, int $$1, int $$2, BlockState $$3) {
        int $$4 = this.getFirstAvailable($$0, $$2);
        if ($$1 <= $$4 - 2) {
            return false;
        }
        if (this.isOpaque.test($$3)) {
            if ($$1 >= $$4) {
                this.setHeight($$0, $$2, $$1 + 1);
                return true;
            }
        } else if ($$4 - 1 == $$1) {
            BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
            for (int $$6 = $$1 - 1; $$6 >= this.chunk.getMinY(); --$$6) {
                $$5.set($$0, $$6, $$2);
                if (!this.isOpaque.test(this.chunk.getBlockState($$5))) continue;
                this.setHeight($$0, $$2, $$6 + 1);
                return true;
            }
            this.setHeight($$0, $$2, this.chunk.getMinY());
            return true;
        }
        return false;
    }

    public int getFirstAvailable(int $$0, int $$1) {
        return this.getFirstAvailable(Heightmap.getIndex($$0, $$1));
    }

    public int getHighestTaken(int $$0, int $$1) {
        return this.getFirstAvailable(Heightmap.getIndex($$0, $$1)) - 1;
    }

    private int getFirstAvailable(int $$0) {
        return this.data.get($$0) + this.chunk.getMinY();
    }

    private void setHeight(int $$0, int $$1, int $$2) {
        this.data.set(Heightmap.getIndex($$0, $$1), $$2 - this.chunk.getMinY());
    }

    public void a(ChunkAccess $$0, Types $$1, long[] $$2) {
        long[] $$3 = this.data.a();
        if ($$3.length == $$2.length) {
            System.arraycopy($$2, 0, $$3, 0, $$2.length);
            return;
        }
        LOGGER.warn("Ignoring heightmap data for chunk " + String.valueOf($$0.getPos()) + ", size does not match; expected: " + $$3.length + ", got: " + $$2.length);
        Heightmap.primeHeightmaps($$0, EnumSet.of($$1));
    }

    public long[] a() {
        return this.data.a();
    }

    private static int getIndex(int $$0, int $$1) {
        return $$0 + $$1 * 16;
    }

    public static final class Types
    extends Enum<Types>
    implements StringRepresentable {
        public static final /* enum */ Types WORLD_SURFACE_WG = new Types(0, "WORLD_SURFACE_WG", Usage.WORLDGEN, NOT_AIR);
        public static final /* enum */ Types WORLD_SURFACE = new Types(1, "WORLD_SURFACE", Usage.CLIENT, NOT_AIR);
        public static final /* enum */ Types OCEAN_FLOOR_WG = new Types(2, "OCEAN_FLOOR_WG", Usage.WORLDGEN, MATERIAL_MOTION_BLOCKING);
        public static final /* enum */ Types OCEAN_FLOOR = new Types(3, "OCEAN_FLOOR", Usage.LIVE_WORLD, MATERIAL_MOTION_BLOCKING);
        public static final /* enum */ Types MOTION_BLOCKING = new Types(4, "MOTION_BLOCKING", Usage.CLIENT, $$0 -> $$0.blocksMotion() || !$$0.getFluidState().isEmpty());
        public static final /* enum */ Types MOTION_BLOCKING_NO_LEAVES = new Types(5, "MOTION_BLOCKING_NO_LEAVES", Usage.CLIENT, $$0 -> ($$0.blocksMotion() || !$$0.getFluidState().isEmpty()) && !($$0.getBlock() instanceof LeavesBlock));
        public static final Codec<Types> CODEC;
        private static final IntFunction<Types> BY_ID;
        public static final StreamCodec<ByteBuf, Types> STREAM_CODEC;
        private final int id;
        private final String serializationKey;
        private final Usage usage;
        private final Predicate<BlockState> isOpaque;
        private static final /* synthetic */ Types[] $VALUES;

        public static Types[] values() {
            return (Types[])$VALUES.clone();
        }

        public static Types valueOf(String $$0) {
            return Enum.valueOf(Types.class, $$0);
        }

        private Types(int $$0, String $$1, Usage $$2, Predicate<BlockState> $$3) {
            this.id = $$0;
            this.serializationKey = $$1;
            this.usage = $$2;
            this.isOpaque = $$3;
        }

        public String getSerializationKey() {
            return this.serializationKey;
        }

        public boolean sendToClient() {
            return this.usage == Usage.CLIENT;
        }

        public boolean keepAfterWorldgen() {
            return this.usage != Usage.WORLDGEN;
        }

        public Predicate<BlockState> isOpaque() {
            return this.isOpaque;
        }

        @Override
        public String getSerializedName() {
            return this.serializationKey;
        }

        private static /* synthetic */ Types[] f() {
            return new Types[]{WORLD_SURFACE_WG, WORLD_SURFACE, OCEAN_FLOOR_WG, OCEAN_FLOOR, MOTION_BLOCKING, MOTION_BLOCKING_NO_LEAVES};
        }

        static {
            $VALUES = Types.f();
            CODEC = StringRepresentable.fromEnum(Types::values);
            BY_ID = ByIdMap.a($$0 -> $$0.id, Types.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
        }
    }

    public static final class Usage
    extends Enum<Usage> {
        public static final /* enum */ Usage WORLDGEN = new Usage();
        public static final /* enum */ Usage LIVE_WORLD = new Usage();
        public static final /* enum */ Usage CLIENT = new Usage();
        private static final /* synthetic */ Usage[] $VALUES;

        public static Usage[] values() {
            return (Usage[])$VALUES.clone();
        }

        public static Usage valueOf(String $$0) {
            return Enum.valueOf(Usage.class, $$0);
        }

        private static /* synthetic */ Usage[] a() {
            return new Usage[]{WORLDGEN, LIVE_WORLD, CLIENT};
        }

        static {
            $VALUES = Usage.a();
        }
    }
}


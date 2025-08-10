/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public final class BelowZeroRetrogen {
    private static final BitSet EMPTY = new BitSet(0);
    private static final Codec<BitSet> BITSET_CODEC = Codec.LONG_STREAM.xmap($$0 -> BitSet.valueOf($$0.toArray()), $$0 -> LongStream.of($$0.toLongArray()));
    private static final Codec<ChunkStatus> NON_EMPTY_CHUNK_STATUS = BuiltInRegistries.CHUNK_STATUS.byNameCodec().comapFlatMap($$0 -> $$0 == ChunkStatus.EMPTY ? DataResult.error(() -> "target_status cannot be empty") : DataResult.success((Object)$$0), Function.identity());
    public static final Codec<BelowZeroRetrogen> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)NON_EMPTY_CHUNK_STATUS.fieldOf("target_status").forGetter(BelowZeroRetrogen::targetStatus), (App)BITSET_CODEC.lenientOptionalFieldOf("missing_bedrock").forGetter($$0 -> $$0.missingBedrock.isEmpty() ? Optional.empty() : Optional.of($$0.missingBedrock))).apply((Applicative)$$02, BelowZeroRetrogen::new));
    private static final Set<ResourceKey<Biome>> RETAINED_RETROGEN_BIOMES = Set.of(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK);
    public static final LevelHeightAccessor UPGRADE_HEIGHT_ACCESSOR = new LevelHeightAccessor(){

        @Override
        public int getHeight() {
            return 64;
        }

        @Override
        public int getMinY() {
            return -64;
        }
    };
    private final ChunkStatus targetStatus;
    private final BitSet missingBedrock;

    private BelowZeroRetrogen(ChunkStatus $$0, Optional<BitSet> $$1) {
        this.targetStatus = $$0;
        this.missingBedrock = $$1.orElse(EMPTY);
    }

    public static void replaceOldBedrock(ProtoChunk $$0) {
        int $$12 = 4;
        BlockPos.betweenClosed(0, 0, 0, 15, 4, 15).forEach($$1 -> {
            if ($$0.getBlockState((BlockPos)$$1).is(Blocks.BEDROCK)) {
                $$0.setBlockState((BlockPos)$$1, Blocks.DEEPSLATE.defaultBlockState());
            }
        });
    }

    public void applyBedrockMask(ProtoChunk $$0) {
        LevelHeightAccessor $$12 = $$0.getHeightAccessorForGeneration();
        int $$2 = $$12.getMinY();
        int $$3 = $$12.getMaxY();
        for (int $$4 = 0; $$4 < 16; ++$$4) {
            for (int $$5 = 0; $$5 < 16; ++$$5) {
                if (!this.hasBedrockHole($$4, $$5)) continue;
                BlockPos.betweenClosed($$4, $$2, $$5, $$4, $$3, $$5).forEach($$1 -> $$0.setBlockState((BlockPos)$$1, Blocks.AIR.defaultBlockState()));
            }
        }
    }

    public ChunkStatus targetStatus() {
        return this.targetStatus;
    }

    public boolean hasBedrockHoles() {
        return !this.missingBedrock.isEmpty();
    }

    public boolean hasBedrockHole(int $$0, int $$1) {
        return this.missingBedrock.get(($$1 & 0xF) * 16 + ($$0 & 0xF));
    }

    public static BiomeResolver getBiomeResolver(BiomeResolver $$0, ChunkAccess $$1) {
        if (!$$1.isUpgrading()) {
            return $$0;
        }
        Predicate<ResourceKey> $$2 = RETAINED_RETROGEN_BIOMES::contains;
        return ($$3, $$4, $$5, $$6) -> {
            Holder<Biome> $$7 = $$0.getNoiseBiome($$3, $$4, $$5, $$6);
            if ($$7.is($$2)) {
                return $$7;
            }
            return $$1.getNoiseBiome($$3, 0, $$5);
        };
    }
}


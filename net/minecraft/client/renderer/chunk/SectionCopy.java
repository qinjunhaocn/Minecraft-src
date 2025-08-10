/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.DebugLevelSource;

class SectionCopy {
    private final Map<BlockPos, BlockEntity> blockEntities;
    @Nullable
    private final PalettedContainer<BlockState> section;
    private final boolean debug;
    private final LevelHeightAccessor levelHeightAccessor;

    SectionCopy(LevelChunk $$0, int $$1) {
        this.levelHeightAccessor = $$0;
        this.debug = $$0.getLevel().isDebug();
        this.blockEntities = ImmutableMap.copyOf($$0.getBlockEntities());
        if ($$0 instanceof EmptyLevelChunk) {
            this.section = null;
        } else {
            LevelChunkSection $$3;
            LevelChunkSection[] $$2 = $$0.d();
            this.section = $$1 < 0 || $$1 >= $$2.length ? null : (($$3 = $$2[$$1]).hasOnlyAir() ? null : $$3.getStates().copy());
        }
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return this.blockEntities.get($$0);
    }

    public BlockState getBlockState(BlockPos $$0) {
        int $$1 = $$0.getX();
        int $$2 = $$0.getY();
        int $$3 = $$0.getZ();
        if (this.debug) {
            BlockState $$4 = null;
            if ($$2 == 60) {
                $$4 = Blocks.BARRIER.defaultBlockState();
            }
            if ($$2 == 70) {
                $$4 = DebugLevelSource.getBlockStateFor($$1, $$3);
            }
            return $$4 == null ? Blocks.AIR.defaultBlockState() : $$4;
        }
        if (this.section == null) {
            return Blocks.AIR.defaultBlockState();
        }
        try {
            return this.section.get($$1 & 0xF, $$2 & 0xF, $$3 & 0xF);
        } catch (Throwable $$5) {
            CrashReport $$6 = CrashReport.forThrowable($$5, "Getting block state");
            CrashReportCategory $$7 = $$6.addCategory("Block being got");
            $$7.setDetail("Location", () -> CrashReportCategory.formatLocation(this.levelHeightAccessor, $$1, $$2, $$3));
            throw new ReportedException($$6);
        }
    }
}


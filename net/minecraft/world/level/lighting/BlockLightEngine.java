/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.lighting;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;

public final class BlockLightEngine
extends LightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

    public BlockLightEngine(LightChunkGetter $$0) {
        this($$0, new BlockLightSectionStorage($$0));
    }

    @VisibleForTesting
    public BlockLightEngine(LightChunkGetter $$0, BlockLightSectionStorage $$1) {
        super($$0, $$1);
    }

    @Override
    protected void checkNode(long $$0) {
        int $$4;
        long $$1 = SectionPos.blockToSection($$0);
        if (!((BlockLightSectionStorage)this.storage).storingLightForSection($$1)) {
            return;
        }
        BlockState $$2 = this.getState(this.mutablePos.set($$0));
        int $$3 = this.getEmission($$0, $$2);
        if ($$3 < ($$4 = ((BlockLightSectionStorage)this.storage).getStoredLevel($$0))) {
            ((BlockLightSectionStorage)this.storage).setStoredLevel($$0, 0);
            this.enqueueDecrease($$0, LightEngine.QueueEntry.decreaseAllDirections($$4));
        } else {
            this.enqueueDecrease($$0, PULL_LIGHT_IN_ENTRY);
        }
        if ($$3 > 0) {
            this.enqueueIncrease($$0, LightEngine.QueueEntry.increaseLightFromEmission($$3, BlockLightEngine.isEmptyShape($$2)));
        }
    }

    @Override
    protected void propagateIncrease(long $$0, long $$1, int $$2) {
        BlockState $$3 = null;
        for (Direction $$4 : PROPAGATION_DIRECTIONS) {
            int $$6;
            int $$7;
            long $$5;
            if (!LightEngine.QueueEntry.shouldPropagateInDirection($$1, $$4) || !((BlockLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection($$5 = BlockPos.offset($$0, $$4))) || ($$7 = $$2 - 1) <= ($$6 = ((BlockLightSectionStorage)this.storage).getStoredLevel($$5))) continue;
            this.mutablePos.set($$5);
            BlockState $$8 = this.getState(this.mutablePos);
            int $$9 = $$2 - this.getOpacity($$8);
            if ($$9 <= $$6) continue;
            if ($$3 == null) {
                BlockState blockState = $$3 = LightEngine.QueueEntry.isFromEmptyShape($$1) ? Blocks.AIR.defaultBlockState() : this.getState(this.mutablePos.set($$0));
            }
            if (this.shapeOccludes($$3, $$8, $$4)) continue;
            ((BlockLightSectionStorage)this.storage).setStoredLevel($$5, $$9);
            if ($$9 <= 1) continue;
            this.enqueueIncrease($$5, LightEngine.QueueEntry.increaseSkipOneDirection($$9, BlockLightEngine.isEmptyShape($$8), $$4.getOpposite()));
        }
    }

    @Override
    protected void propagateDecrease(long $$0, long $$1) {
        int $$2 = LightEngine.QueueEntry.getFromLevel($$1);
        for (Direction $$3 : PROPAGATION_DIRECTIONS) {
            int $$5;
            long $$4;
            if (!LightEngine.QueueEntry.shouldPropagateInDirection($$1, $$3) || !((BlockLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection($$4 = BlockPos.offset($$0, $$3))) || ($$5 = ((BlockLightSectionStorage)this.storage).getStoredLevel($$4)) == 0) continue;
            if ($$5 <= $$2 - 1) {
                BlockState $$6 = this.getState(this.mutablePos.set($$4));
                int $$7 = this.getEmission($$4, $$6);
                ((BlockLightSectionStorage)this.storage).setStoredLevel($$4, 0);
                if ($$7 < $$5) {
                    this.enqueueDecrease($$4, LightEngine.QueueEntry.decreaseSkipOneDirection($$5, $$3.getOpposite()));
                }
                if ($$7 <= 0) continue;
                this.enqueueIncrease($$4, LightEngine.QueueEntry.increaseLightFromEmission($$7, BlockLightEngine.isEmptyShape($$6)));
                continue;
            }
            this.enqueueIncrease($$4, LightEngine.QueueEntry.increaseOnlyOneDirection($$5, false, $$3.getOpposite()));
        }
    }

    private int getEmission(long $$0, BlockState $$1) {
        int $$2 = $$1.getLightEmission();
        if ($$2 > 0 && ((BlockLightSectionStorage)this.storage).lightOnInSection(SectionPos.blockToSection($$0))) {
            return $$2;
        }
        return 0;
    }

    @Override
    public void propagateLightSources(ChunkPos $$02) {
        this.setLightEnabled($$02, true);
        LightChunk $$12 = this.chunkSource.getChunkForLighting($$02.x, $$02.z);
        if ($$12 != null) {
            $$12.findBlockLightSources(($$0, $$1) -> {
                int $$2 = $$1.getLightEmission();
                this.enqueueIncrease($$0.asLong(), LightEngine.QueueEntry.increaseLightFromEmission($$2, BlockLightEngine.isEmptyShape($$1)));
            });
        }
    }
}


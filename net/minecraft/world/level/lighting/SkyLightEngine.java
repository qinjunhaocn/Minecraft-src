/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.world.level.lighting;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import org.jetbrains.annotations.VisibleForTesting;

public final class SkyLightEngine
extends LightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, SkyLightSectionStorage> {
    private static final long REMOVE_TOP_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.decreaseAllDirections(15);
    private static final long REMOVE_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.decreaseSkipOneDirection(15, Direction.UP);
    private static final long ADD_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.increaseSkipOneDirection(15, false, Direction.UP);
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private final ChunkSkyLightSources emptyChunkSources;

    public SkyLightEngine(LightChunkGetter $$0) {
        this($$0, new SkyLightSectionStorage($$0));
    }

    @VisibleForTesting
    protected SkyLightEngine(LightChunkGetter $$0, SkyLightSectionStorage $$1) {
        super($$0, $$1);
        this.emptyChunkSources = new ChunkSkyLightSources($$0.getLevel());
    }

    private static boolean isSourceLevel(int $$0) {
        return $$0 == 15;
    }

    private int getLowestSourceY(int $$0, int $$1, int $$2) {
        ChunkSkyLightSources $$3 = this.getChunkSources(SectionPos.blockToSectionCoord($$0), SectionPos.blockToSectionCoord($$1));
        if ($$3 == null) {
            return $$2;
        }
        return $$3.getLowestSourceY(SectionPos.sectionRelative($$0), SectionPos.sectionRelative($$1));
    }

    @Nullable
    private ChunkSkyLightSources getChunkSources(int $$0, int $$1) {
        LightChunk $$2 = this.chunkSource.getChunkForLighting($$0, $$1);
        return $$2 != null ? $$2.getSkyLightSources() : null;
    }

    @Override
    protected void checkNode(long $$0) {
        boolean $$6;
        int $$5;
        int $$1 = BlockPos.getX($$0);
        int $$2 = BlockPos.getY($$0);
        int $$3 = BlockPos.getZ($$0);
        long $$4 = SectionPos.blockToSection($$0);
        int n = $$5 = ((SkyLightSectionStorage)this.storage).lightOnInSection($$4) ? this.getLowestSourceY($$1, $$3, Integer.MAX_VALUE) : Integer.MAX_VALUE;
        if ($$5 != Integer.MAX_VALUE) {
            this.updateSourcesInColumn($$1, $$3, $$5);
        }
        if (!((SkyLightSectionStorage)this.storage).storingLightForSection($$4)) {
            return;
        }
        boolean bl = $$6 = $$2 >= $$5;
        if ($$6) {
            this.enqueueDecrease($$0, REMOVE_SKY_SOURCE_ENTRY);
            this.enqueueIncrease($$0, ADD_SKY_SOURCE_ENTRY);
        } else {
            int $$7 = ((SkyLightSectionStorage)this.storage).getStoredLevel($$0);
            if ($$7 > 0) {
                ((SkyLightSectionStorage)this.storage).setStoredLevel($$0, 0);
                this.enqueueDecrease($$0, LightEngine.QueueEntry.decreaseAllDirections($$7));
            } else {
                this.enqueueDecrease($$0, PULL_LIGHT_IN_ENTRY);
            }
        }
    }

    private void updateSourcesInColumn(int $$0, int $$1, int $$2) {
        int $$3 = SectionPos.sectionToBlockCoord(((SkyLightSectionStorage)this.storage).getBottomSectionY());
        this.removeSourcesBelow($$0, $$1, $$2, $$3);
        this.addSourcesAbove($$0, $$1, $$2, $$3);
    }

    private void removeSourcesBelow(int $$0, int $$1, int $$2, int $$3) {
        if ($$2 <= $$3) {
            return;
        }
        int $$4 = SectionPos.blockToSectionCoord($$0);
        int $$5 = SectionPos.blockToSectionCoord($$1);
        int $$6 = $$2 - 1;
        int $$7 = SectionPos.blockToSectionCoord($$6);
        while (((SkyLightSectionStorage)this.storage).hasLightDataAtOrBelow($$7)) {
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong($$4, $$7, $$5))) {
                int $$8 = SectionPos.sectionToBlockCoord($$7);
                int $$9 = $$8 + 15;
                for (int $$10 = Math.min($$9, $$6); $$10 >= $$8; --$$10) {
                    long $$11 = BlockPos.asLong($$0, $$10, $$1);
                    if (!SkyLightEngine.isSourceLevel(((SkyLightSectionStorage)this.storage).getStoredLevel($$11))) {
                        return;
                    }
                    ((SkyLightSectionStorage)this.storage).setStoredLevel($$11, 0);
                    this.enqueueDecrease($$11, $$10 == $$2 - 1 ? REMOVE_TOP_SKY_SOURCE_ENTRY : REMOVE_SKY_SOURCE_ENTRY);
                }
            }
            --$$7;
        }
    }

    private void addSourcesAbove(int $$0, int $$1, int $$2, int $$3) {
        int $$4 = SectionPos.blockToSectionCoord($$0);
        int $$5 = SectionPos.blockToSectionCoord($$1);
        int $$6 = Math.max(Math.max(this.getLowestSourceY($$0 - 1, $$1, Integer.MIN_VALUE), this.getLowestSourceY($$0 + 1, $$1, Integer.MIN_VALUE)), Math.max(this.getLowestSourceY($$0, $$1 - 1, Integer.MIN_VALUE), this.getLowestSourceY($$0, $$1 + 1, Integer.MIN_VALUE)));
        int $$7 = Math.max($$2, $$3);
        long $$8 = SectionPos.asLong($$4, SectionPos.blockToSectionCoord($$7), $$5);
        while (!((SkyLightSectionStorage)this.storage).isAboveData($$8)) {
            if (((SkyLightSectionStorage)this.storage).storingLightForSection($$8)) {
                int $$9 = SectionPos.sectionToBlockCoord(SectionPos.y($$8));
                int $$10 = $$9 + 15;
                for (int $$11 = Math.max($$9, $$7); $$11 <= $$10; ++$$11) {
                    long $$12 = BlockPos.asLong($$0, $$11, $$1);
                    if (SkyLightEngine.isSourceLevel(((SkyLightSectionStorage)this.storage).getStoredLevel($$12))) {
                        return;
                    }
                    ((SkyLightSectionStorage)this.storage).setStoredLevel($$12, 15);
                    if ($$11 >= $$6 && $$11 != $$2) continue;
                    this.enqueueIncrease($$12, ADD_SKY_SOURCE_ENTRY);
                }
            }
            $$8 = SectionPos.offset($$8, Direction.UP);
        }
    }

    @Override
    protected void propagateIncrease(long $$0, long $$1, int $$2) {
        BlockState $$3 = null;
        int $$4 = this.countEmptySectionsBelowIfAtBorder($$0);
        for (Direction $$5 : PROPAGATION_DIRECTIONS) {
            int $$7;
            int $$8;
            long $$6;
            if (!LightEngine.QueueEntry.shouldPropagateInDirection($$1, $$5) || !((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection($$6 = BlockPos.offset($$0, $$5))) || ($$8 = $$2 - 1) <= ($$7 = ((SkyLightSectionStorage)this.storage).getStoredLevel($$6))) continue;
            this.mutablePos.set($$6);
            BlockState $$9 = this.getState(this.mutablePos);
            int $$10 = $$2 - this.getOpacity($$9);
            if ($$10 <= $$7) continue;
            if ($$3 == null) {
                BlockState blockState = $$3 = LightEngine.QueueEntry.isFromEmptyShape($$1) ? Blocks.AIR.defaultBlockState() : this.getState(this.mutablePos.set($$0));
            }
            if (this.shapeOccludes($$3, $$9, $$5)) continue;
            ((SkyLightSectionStorage)this.storage).setStoredLevel($$6, $$10);
            if ($$10 > 1) {
                this.enqueueIncrease($$6, LightEngine.QueueEntry.increaseSkipOneDirection($$10, SkyLightEngine.isEmptyShape($$9), $$5.getOpposite()));
            }
            this.propagateFromEmptySections($$6, $$5, $$10, true, $$4);
        }
    }

    @Override
    protected void propagateDecrease(long $$0, long $$1) {
        int $$2 = this.countEmptySectionsBelowIfAtBorder($$0);
        int $$3 = LightEngine.QueueEntry.getFromLevel($$1);
        for (Direction $$4 : PROPAGATION_DIRECTIONS) {
            int $$6;
            long $$5;
            if (!LightEngine.QueueEntry.shouldPropagateInDirection($$1, $$4) || !((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection($$5 = BlockPos.offset($$0, $$4))) || ($$6 = ((SkyLightSectionStorage)this.storage).getStoredLevel($$5)) == 0) continue;
            if ($$6 <= $$3 - 1) {
                ((SkyLightSectionStorage)this.storage).setStoredLevel($$5, 0);
                this.enqueueDecrease($$5, LightEngine.QueueEntry.decreaseSkipOneDirection($$6, $$4.getOpposite()));
                this.propagateFromEmptySections($$5, $$4, $$6, false, $$2);
                continue;
            }
            this.enqueueIncrease($$5, LightEngine.QueueEntry.increaseOnlyOneDirection($$6, false, $$4.getOpposite()));
        }
    }

    private int countEmptySectionsBelowIfAtBorder(long $$0) {
        int $$1 = BlockPos.getY($$0);
        int $$2 = SectionPos.sectionRelative($$1);
        if ($$2 != 0) {
            return 0;
        }
        int $$3 = BlockPos.getX($$0);
        int $$4 = BlockPos.getZ($$0);
        int $$5 = SectionPos.sectionRelative($$3);
        int $$6 = SectionPos.sectionRelative($$4);
        if ($$5 == 0 || $$5 == 15 || $$6 == 0 || $$6 == 15) {
            int $$7 = SectionPos.blockToSectionCoord($$3);
            int $$8 = SectionPos.blockToSectionCoord($$1);
            int $$9 = SectionPos.blockToSectionCoord($$4);
            int $$10 = 0;
            while (!((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong($$7, $$8 - $$10 - 1, $$9)) && ((SkyLightSectionStorage)this.storage).hasLightDataAtOrBelow($$8 - $$10 - 1)) {
                ++$$10;
            }
            return $$10;
        }
        return 0;
    }

    private void propagateFromEmptySections(long $$0, Direction $$1, int $$2, boolean $$3, int $$4) {
        if ($$4 == 0) {
            return;
        }
        int $$5 = BlockPos.getX($$0);
        int $$6 = BlockPos.getZ($$0);
        if (!SkyLightEngine.crossedSectionEdge($$1, SectionPos.sectionRelative($$5), SectionPos.sectionRelative($$6))) {
            return;
        }
        int $$7 = BlockPos.getY($$0);
        int $$8 = SectionPos.blockToSectionCoord($$5);
        int $$9 = SectionPos.blockToSectionCoord($$6);
        int $$10 = SectionPos.blockToSectionCoord($$7) - 1;
        int $$11 = $$10 - $$4 + 1;
        while ($$10 >= $$11) {
            if (!((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong($$8, $$10, $$9))) {
                --$$10;
                continue;
            }
            int $$12 = SectionPos.sectionToBlockCoord($$10);
            for (int $$13 = 15; $$13 >= 0; --$$13) {
                long $$14 = BlockPos.asLong($$5, $$12 + $$13, $$6);
                if ($$3) {
                    ((SkyLightSectionStorage)this.storage).setStoredLevel($$14, $$2);
                    if ($$2 <= 1) continue;
                    this.enqueueIncrease($$14, LightEngine.QueueEntry.increaseSkipOneDirection($$2, true, $$1.getOpposite()));
                    continue;
                }
                ((SkyLightSectionStorage)this.storage).setStoredLevel($$14, 0);
                this.enqueueDecrease($$14, LightEngine.QueueEntry.decreaseSkipOneDirection($$2, $$1.getOpposite()));
            }
            --$$10;
        }
    }

    private static boolean crossedSectionEdge(Direction $$0, int $$1, int $$2) {
        return switch ($$0) {
            case Direction.NORTH -> {
                if ($$2 == 15) {
                    yield true;
                }
                yield false;
            }
            case Direction.SOUTH -> {
                if ($$2 == 0) {
                    yield true;
                }
                yield false;
            }
            case Direction.WEST -> {
                if ($$1 == 15) {
                    yield true;
                }
                yield false;
            }
            case Direction.EAST -> {
                if ($$1 == 0) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    @Override
    public void setLightEnabled(ChunkPos $$0, boolean $$1) {
        super.setLightEnabled($$0, $$1);
        if ($$1) {
            ChunkSkyLightSources $$2 = (ChunkSkyLightSources)Objects.requireNonNullElse((Object)this.getChunkSources($$0.x, $$0.z), (Object)this.emptyChunkSources);
            int $$3 = $$2.getHighestLowestSourceY() - 1;
            int $$4 = SectionPos.blockToSectionCoord($$3) + 1;
            long $$5 = SectionPos.getZeroNode($$0.x, $$0.z);
            int $$6 = ((SkyLightSectionStorage)this.storage).getTopSectionY($$5);
            int $$7 = Math.max(((SkyLightSectionStorage)this.storage).getBottomSectionY(), $$4);
            for (int $$8 = $$6 - 1; $$8 >= $$7; --$$8) {
                DataLayer $$9 = ((SkyLightSectionStorage)this.storage).getDataLayerToWrite(SectionPos.asLong($$0.x, $$8, $$0.z));
                if ($$9 == null || !$$9.isEmpty()) continue;
                $$9.fill(15);
            }
        }
    }

    @Override
    public void propagateLightSources(ChunkPos $$0) {
        long $$1 = SectionPos.getZeroNode($$0.x, $$0.z);
        ((SkyLightSectionStorage)this.storage).setLightEnabled($$1, true);
        ChunkSkyLightSources $$2 = (ChunkSkyLightSources)Objects.requireNonNullElse((Object)this.getChunkSources($$0.x, $$0.z), (Object)this.emptyChunkSources);
        ChunkSkyLightSources $$3 = (ChunkSkyLightSources)Objects.requireNonNullElse((Object)this.getChunkSources($$0.x, $$0.z - 1), (Object)this.emptyChunkSources);
        ChunkSkyLightSources $$4 = (ChunkSkyLightSources)Objects.requireNonNullElse((Object)this.getChunkSources($$0.x, $$0.z + 1), (Object)this.emptyChunkSources);
        ChunkSkyLightSources $$5 = (ChunkSkyLightSources)Objects.requireNonNullElse((Object)this.getChunkSources($$0.x - 1, $$0.z), (Object)this.emptyChunkSources);
        ChunkSkyLightSources $$6 = (ChunkSkyLightSources)Objects.requireNonNullElse((Object)this.getChunkSources($$0.x + 1, $$0.z), (Object)this.emptyChunkSources);
        int $$7 = ((SkyLightSectionStorage)this.storage).getTopSectionY($$1);
        int $$8 = ((SkyLightSectionStorage)this.storage).getBottomSectionY();
        int $$9 = SectionPos.sectionToBlockCoord($$0.x);
        int $$10 = SectionPos.sectionToBlockCoord($$0.z);
        for (int $$11 = $$7 - 1; $$11 >= $$8; --$$11) {
            long $$12 = SectionPos.asLong($$0.x, $$11, $$0.z);
            DataLayer $$13 = ((SkyLightSectionStorage)this.storage).getDataLayerToWrite($$12);
            if ($$13 == null) continue;
            int $$14 = SectionPos.sectionToBlockCoord($$11);
            int $$15 = $$14 + 15;
            boolean $$16 = false;
            for (int $$17 = 0; $$17 < 16; ++$$17) {
                for (int $$18 = 0; $$18 < 16; ++$$18) {
                    int $$19 = $$2.getLowestSourceY($$18, $$17);
                    if ($$19 > $$15) continue;
                    int $$20 = $$17 == 0 ? $$3.getLowestSourceY($$18, 15) : $$2.getLowestSourceY($$18, $$17 - 1);
                    int $$21 = $$17 == 15 ? $$4.getLowestSourceY($$18, 0) : $$2.getLowestSourceY($$18, $$17 + 1);
                    int $$22 = $$18 == 0 ? $$5.getLowestSourceY(15, $$17) : $$2.getLowestSourceY($$18 - 1, $$17);
                    int $$23 = $$18 == 15 ? $$6.getLowestSourceY(0, $$17) : $$2.getLowestSourceY($$18 + 1, $$17);
                    int $$24 = Math.max(Math.max($$20, $$21), Math.max($$22, $$23));
                    for (int $$25 = $$15; $$25 >= Math.max($$14, $$19); --$$25) {
                        $$13.set($$18, SectionPos.sectionRelative($$25), $$17, 15);
                        if ($$25 != $$19 && $$25 >= $$24) continue;
                        long $$26 = BlockPos.asLong($$9 + $$18, $$25, $$10 + $$17);
                        this.enqueueIncrease($$26, LightEngine.QueueEntry.increaseSkySourceInDirections($$25 == $$19, $$25 < $$20, $$25 < $$21, $$25 < $$22, $$25 < $$23));
                    }
                    if ($$19 >= $$14) continue;
                    $$16 = true;
                }
            }
            if (!$$16) break;
        }
    }
}


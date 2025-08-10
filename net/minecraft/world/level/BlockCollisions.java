/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import com.google.common.collect.AbstractIterator;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCollisions<T>
extends AbstractIterator<T> {
    private final AABB box;
    private final CollisionContext context;
    private final Cursor3D cursor;
    private final BlockPos.MutableBlockPos pos;
    private final VoxelShape entityShape;
    private final CollisionGetter collisionGetter;
    private final boolean onlySuffocatingBlocks;
    @Nullable
    private BlockGetter cachedBlockGetter;
    private long cachedBlockGetterPos;
    private final BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> resultProvider;

    public BlockCollisions(CollisionGetter $$0, @Nullable Entity $$1, AABB $$2, boolean $$3, BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> $$4) {
        this($$0, $$1 == null ? CollisionContext.empty() : CollisionContext.of($$1), $$2, $$3, $$4);
    }

    public BlockCollisions(CollisionGetter $$0, CollisionContext $$1, AABB $$2, boolean $$3, BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> $$4) {
        this.context = $$1;
        this.pos = new BlockPos.MutableBlockPos();
        this.entityShape = Shapes.create($$2);
        this.collisionGetter = $$0;
        this.box = $$2;
        this.onlySuffocatingBlocks = $$3;
        this.resultProvider = $$4;
        int $$5 = Mth.floor($$2.minX - 1.0E-7) - 1;
        int $$6 = Mth.floor($$2.maxX + 1.0E-7) + 1;
        int $$7 = Mth.floor($$2.minY - 1.0E-7) - 1;
        int $$8 = Mth.floor($$2.maxY + 1.0E-7) + 1;
        int $$9 = Mth.floor($$2.minZ - 1.0E-7) - 1;
        int $$10 = Mth.floor($$2.maxZ + 1.0E-7) + 1;
        this.cursor = new Cursor3D($$5, $$7, $$9, $$6, $$8, $$10);
    }

    @Nullable
    private BlockGetter getChunk(int $$0, int $$1) {
        BlockGetter $$5;
        int $$2 = SectionPos.blockToSectionCoord($$0);
        int $$3 = SectionPos.blockToSectionCoord($$1);
        long $$4 = ChunkPos.asLong($$2, $$3);
        if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == $$4) {
            return this.cachedBlockGetter;
        }
        this.cachedBlockGetter = $$5 = this.collisionGetter.getChunkForCollisions($$2, $$3);
        this.cachedBlockGetterPos = $$4;
        return $$5;
    }

    @Override
    protected T computeNext() {
        while (this.cursor.advance()) {
            BlockGetter $$4;
            int $$0 = this.cursor.nextX();
            int $$1 = this.cursor.nextY();
            int $$2 = this.cursor.nextZ();
            int $$3 = this.cursor.getNextType();
            if ($$3 == 3 || ($$4 = this.getChunk($$0, $$2)) == null) continue;
            this.pos.set($$0, $$1, $$2);
            BlockState $$5 = $$4.getBlockState(this.pos);
            if (this.onlySuffocatingBlocks && !$$5.isSuffocating($$4, this.pos) || $$3 == 1 && !$$5.hasLargeCollisionShape() || $$3 == 2 && !$$5.is(Blocks.MOVING_PISTON)) continue;
            VoxelShape $$6 = this.context.getCollisionShape($$5, this.collisionGetter, this.pos);
            if ($$6 == Shapes.block()) {
                if (!this.box.intersects($$0, $$1, $$2, (double)$$0 + 1.0, (double)$$1 + 1.0, (double)$$2 + 1.0)) continue;
                return this.resultProvider.apply(this.pos, $$6.move(this.pos));
            }
            VoxelShape $$7 = $$6.move(this.pos);
            if ($$7.isEmpty() || !Shapes.joinIsNotEmpty($$7, this.entityShape, BooleanOp.AND)) continue;
            return this.resultProvider.apply(this.pos, $$7);
        }
        return this.endOfData();
    }
}


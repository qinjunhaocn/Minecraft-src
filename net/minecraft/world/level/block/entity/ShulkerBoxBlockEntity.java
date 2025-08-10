/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerBoxBlockEntity
extends RandomizableContainerBlockEntity
implements WorldlyContainer {
    public static final int COLUMNS = 9;
    public static final int ROWS = 3;
    public static final int CONTAINER_SIZE = 27;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    public static final int OPENING_TICK_LENGTH = 10;
    public static final float MAX_LID_HEIGHT = 0.5f;
    public static final float MAX_LID_ROTATION = 270.0f;
    private static final int[] SLOTS = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    private int openCount;
    private AnimationStatus animationStatus = AnimationStatus.CLOSED;
    private float progress;
    private float progressOld;
    @Nullable
    private final DyeColor color;

    public ShulkerBoxBlockEntity(@Nullable DyeColor $$0, BlockPos $$1, BlockState $$2) {
        super(BlockEntityType.SHULKER_BOX, $$1, $$2);
        this.color = $$0;
    }

    public ShulkerBoxBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SHULKER_BOX, $$0, $$1);
        DyeColor dyeColor;
        Block block = $$1.getBlock();
        if (block instanceof ShulkerBoxBlock) {
            ShulkerBoxBlock $$2 = (ShulkerBoxBlock)block;
            dyeColor = $$2.getColor();
        } else {
            dyeColor = null;
        }
        this.color = dyeColor;
    }

    public static void tick(Level $$0, BlockPos $$1, BlockState $$2, ShulkerBoxBlockEntity $$3) {
        $$3.updateAnimation($$0, $$1, $$2);
    }

    private void updateAnimation(Level $$0, BlockPos $$1, BlockState $$2) {
        this.progressOld = this.progress;
        switch (this.animationStatus.ordinal()) {
            case 0: {
                this.progress = 0.0f;
                break;
            }
            case 1: {
                this.progress += 0.1f;
                if (this.progressOld == 0.0f) {
                    ShulkerBoxBlockEntity.doNeighborUpdates($$0, $$1, $$2);
                }
                if (this.progress >= 1.0f) {
                    this.animationStatus = AnimationStatus.OPENED;
                    this.progress = 1.0f;
                    ShulkerBoxBlockEntity.doNeighborUpdates($$0, $$1, $$2);
                }
                this.moveCollidedEntities($$0, $$1, $$2);
                break;
            }
            case 3: {
                this.progress -= 0.1f;
                if (this.progressOld == 1.0f) {
                    ShulkerBoxBlockEntity.doNeighborUpdates($$0, $$1, $$2);
                }
                if (!(this.progress <= 0.0f)) break;
                this.animationStatus = AnimationStatus.CLOSED;
                this.progress = 0.0f;
                ShulkerBoxBlockEntity.doNeighborUpdates($$0, $$1, $$2);
                break;
            }
            case 2: {
                this.progress = 1.0f;
            }
        }
    }

    public AnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }

    public AABB getBoundingBox(BlockState $$0) {
        Vec3 $$1 = new Vec3(0.5, 0.0, 0.5);
        return Shulker.getProgressAabb(1.0f, $$0.getValue(ShulkerBoxBlock.FACING), 0.5f * this.getProgress(1.0f), $$1);
    }

    private void moveCollidedEntities(Level $$0, BlockPos $$1, BlockState $$2) {
        if (!($$2.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        Direction $$3 = $$2.getValue(ShulkerBoxBlock.FACING);
        AABB $$4 = Shulker.getProgressDeltaAabb(1.0f, $$3, this.progressOld, this.progress, $$1.getBottomCenter());
        List<Entity> $$5 = $$0.getEntities(null, $$4);
        if ($$5.isEmpty()) {
            return;
        }
        for (Entity $$6 : $$5) {
            if ($$6.getPistonPushReaction() == PushReaction.IGNORE) continue;
            $$6.move(MoverType.SHULKER_BOX, new Vec3(($$4.getXsize() + 0.01) * (double)$$3.getStepX(), ($$4.getYsize() + 0.01) * (double)$$3.getStepY(), ($$4.getZsize() + 0.01) * (double)$$3.getStepZ()));
        }
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    @Override
    public boolean triggerEvent(int $$0, int $$1) {
        if ($$0 == 1) {
            this.openCount = $$1;
            if ($$1 == 0) {
                this.animationStatus = AnimationStatus.CLOSING;
            }
            if ($$1 == 1) {
                this.animationStatus = AnimationStatus.OPENING;
            }
            return true;
        }
        return super.triggerEvent($$0, $$1);
    }

    private static void doNeighborUpdates(Level $$0, BlockPos $$1, BlockState $$2) {
        $$2.updateNeighbourShapes($$0, $$1, 3);
        $$0.updateNeighborsAt($$1, $$2.getBlock());
    }

    @Override
    public void preRemoveSideEffects(BlockPos $$0, BlockState $$1) {
    }

    @Override
    public void startOpen(Player $$0) {
        if (!this.remove && !$$0.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount == 1) {
                this.level.gameEvent((Entity)$$0, GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    public void stopOpen(Player $$0) {
        if (!this.remove && !$$0.isSpectator()) {
            --this.openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.level.gameEvent((Entity)$$0, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.level.playSound(null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.shulkerBox");
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.loadFromTag($$0);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        if (!this.trySaveLootTable($$0)) {
            ContainerHelper.saveAllItems($$0, this.itemStacks, false);
        }
    }

    public void loadFromTag(ValueInput $$0) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable($$0)) {
            ContainerHelper.loadAllItems($$0, this.itemStacks);
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> $$0) {
        this.itemStacks = $$0;
    }

    @Override
    public int[] a(Direction $$0) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int $$0, ItemStack $$1, @Nullable Direction $$2) {
        return !(Block.byItem($$1.getItem()) instanceof ShulkerBoxBlock);
    }

    @Override
    public boolean canTakeItemThroughFace(int $$0, ItemStack $$1, Direction $$2) {
        return true;
    }

    public float getProgress(float $$0) {
        return Mth.lerp($$0, this.progressOld, this.progress);
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected AbstractContainerMenu createMenu(int $$0, Inventory $$1) {
        return new ShulkerBoxMenu($$0, $$1, this);
    }

    public boolean isClosed() {
        return this.animationStatus == AnimationStatus.CLOSED;
    }

    public static final class AnimationStatus
    extends Enum<AnimationStatus> {
        public static final /* enum */ AnimationStatus CLOSED = new AnimationStatus();
        public static final /* enum */ AnimationStatus OPENING = new AnimationStatus();
        public static final /* enum */ AnimationStatus OPENED = new AnimationStatus();
        public static final /* enum */ AnimationStatus CLOSING = new AnimationStatus();
        private static final /* synthetic */ AnimationStatus[] $VALUES;

        public static AnimationStatus[] values() {
            return (AnimationStatus[])$VALUES.clone();
        }

        public static AnimationStatus valueOf(String $$0) {
            return Enum.valueOf(AnimationStatus.class, $$0);
        }

        private static /* synthetic */ AnimationStatus[] a() {
            return new AnimationStatus[]{CLOSED, OPENING, OPENED, CLOSING};
        }

        static {
            $VALUES = AnimationStatus.a();
        }
    }
}


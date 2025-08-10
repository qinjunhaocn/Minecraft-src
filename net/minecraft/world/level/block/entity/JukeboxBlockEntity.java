/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class JukeboxBlockEntity
extends BlockEntity
implements ContainerSingleItem.BlockContainerSingleItem {
    public static final String SONG_ITEM_TAG_ID = "RecordItem";
    public static final String TICKS_SINCE_SONG_STARTED_TAG_ID = "ticks_since_song_started";
    private ItemStack item = ItemStack.EMPTY;
    private final JukeboxSongPlayer jukeboxSongPlayer = new JukeboxSongPlayer(this::onSongChanged, this.getBlockPos());

    public JukeboxBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.JUKEBOX, $$0, $$1);
    }

    public JukeboxSongPlayer getSongPlayer() {
        return this.jukeboxSongPlayer;
    }

    public void onSongChanged() {
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();
    }

    private void notifyItemChangedInJukebox(boolean $$0) {
        if (this.level == null || this.level.getBlockState(this.getBlockPos()) != this.getBlockState()) {
            return;
        }
        this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, $$0), 2);
        this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
    }

    public void popOutTheItem() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        BlockPos $$0 = this.getBlockPos();
        ItemStack $$1 = this.getTheItem();
        if ($$1.isEmpty()) {
            return;
        }
        this.removeTheItem();
        Vec3 $$2 = Vec3.atLowerCornerWithOffset($$0, 0.5, 1.01, 0.5).offsetRandom(this.level.random, 0.7f);
        ItemStack $$3 = $$1.copy();
        ItemEntity $$4 = new ItemEntity(this.level, $$2.x(), $$2.y(), $$2.z(), $$3);
        $$4.setDefaultPickUpDelay();
        this.level.addFreshEntity($$4);
    }

    public static void tick(Level $$0, BlockPos $$1, BlockState $$2, JukeboxBlockEntity $$3) {
        $$3.jukeboxSongPlayer.tick($$0, $$2);
    }

    public int getComparatorOutput() {
        return JukeboxSong.fromStack(this.level.registryAccess(), this.item).map(Holder::value).map(JukeboxSong::comparatorOutput).orElse(0);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        ItemStack $$1 = $$0.read(SONG_ITEM_TAG_ID, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        if (!this.item.isEmpty() && !ItemStack.isSameItemSameComponents($$1, this.item)) {
            this.jukeboxSongPlayer.stop(this.level, this.getBlockState());
        }
        this.item = $$1;
        $$0.getLong(TICKS_SINCE_SONG_STARTED_TAG_ID).ifPresent($$12 -> JukeboxSong.fromStack($$0.lookup(), this.item).ifPresent($$1 -> this.jukeboxSongPlayer.setSongWithoutPlaying((Holder<JukeboxSong>)$$1, (long)$$12)));
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        if (!this.getTheItem().isEmpty()) {
            $$0.store(SONG_ITEM_TAG_ID, ItemStack.CODEC, this.getTheItem());
        }
        if (this.jukeboxSongPlayer.getSong() != null) {
            $$0.putLong(TICKS_SINCE_SONG_STARTED_TAG_ID, this.jukeboxSongPlayer.getTicksSinceSongStarted());
        }
    }

    @Override
    public ItemStack getTheItem() {
        return this.item;
    }

    @Override
    public ItemStack splitTheItem(int $$0) {
        ItemStack $$1 = this.item;
        this.setTheItem(ItemStack.EMPTY);
        return $$1;
    }

    @Override
    public void setTheItem(ItemStack $$0) {
        this.item = $$0;
        boolean $$1 = !this.item.isEmpty();
        Optional<Holder<JukeboxSong>> $$2 = JukeboxSong.fromStack(this.level.registryAccess(), this.item);
        this.notifyItemChangedInJukebox($$1);
        if ($$1 && $$2.isPresent()) {
            this.jukeboxSongPlayer.play(this.level, $$2.get());
        } else {
            this.jukeboxSongPlayer.stop(this.level, this.getBlockState());
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    @Override
    public boolean canPlaceItem(int $$0, ItemStack $$1) {
        return $$1.has(DataComponents.JUKEBOX_PLAYABLE) && this.getItem($$0).isEmpty();
    }

    @Override
    public boolean canTakeItem(Container $$0, int $$1, ItemStack $$2) {
        return $$0.hasAnyMatching(ItemStack::isEmpty);
    }

    @Override
    public void preRemoveSideEffects(BlockPos $$0, BlockState $$1) {
        this.popOutTheItem();
    }

    @VisibleForTesting
    public void setSongItemWithoutPlaying(ItemStack $$02) {
        this.item = $$02;
        JukeboxSong.fromStack(this.level.registryAccess(), $$02).ifPresent($$0 -> this.jukeboxSongPlayer.setSongWithoutPlaying((Holder<JukeboxSong>)$$0, 0L));
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();
    }

    @VisibleForTesting
    public void tryForcePlaySong() {
        JukeboxSong.fromStack(this.level.registryAccess(), this.getTheItem()).ifPresent($$0 -> this.jukeboxSongPlayer.play(this.level, (Holder<JukeboxSong>)$$0));
    }
}


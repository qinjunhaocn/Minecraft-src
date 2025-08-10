/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LecternBlockEntity
extends BlockEntity
implements Clearable,
MenuProvider {
    public static final int DATA_PAGE = 0;
    public static final int NUM_DATA = 1;
    public static final int SLOT_BOOK = 0;
    public static final int NUM_SLOTS = 1;
    private final Container bookAccess = new Container(){

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return LecternBlockEntity.this.book.isEmpty();
        }

        @Override
        public ItemStack getItem(int $$0) {
            return $$0 == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int $$0, int $$1) {
            if ($$0 == 0) {
                ItemStack $$2 = LecternBlockEntity.this.book.split($$1);
                if (LecternBlockEntity.this.book.isEmpty()) {
                    LecternBlockEntity.this.onBookItemRemove();
                }
                return $$2;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int $$0) {
            if ($$0 == 0) {
                ItemStack $$1 = LecternBlockEntity.this.book;
                LecternBlockEntity.this.book = ItemStack.EMPTY;
                LecternBlockEntity.this.onBookItemRemove();
                return $$1;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int $$0, ItemStack $$1) {
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            LecternBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player $$0) {
            return Container.stillValidBlockEntity(LecternBlockEntity.this, $$0) && LecternBlockEntity.this.hasBook();
        }

        @Override
        public boolean canPlaceItem(int $$0, ItemStack $$1) {
            return false;
        }

        @Override
        public void clearContent() {
        }
    };
    private final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int $$0) {
            return $$0 == 0 ? LecternBlockEntity.this.page : 0;
        }

        @Override
        public void set(int $$0, int $$1) {
            if ($$0 == 0) {
                LecternBlockEntity.this.setPage($$1);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    ItemStack book = ItemStack.EMPTY;
    int page;
    private int pageCount;

    public LecternBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.LECTERN, $$0, $$1);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return this.book.has(DataComponents.WRITABLE_BOOK_CONTENT) || this.book.has(DataComponents.WRITTEN_BOOK_CONTENT);
    }

    public void setBook(ItemStack $$0) {
        this.setBook($$0, null);
    }

    void onBookItemRemove() {
        this.page = 0;
        this.pageCount = 0;
        LecternBlock.resetBookState(null, this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
    }

    public void setBook(ItemStack $$0, @Nullable Player $$1) {
        this.book = this.resolveBook($$0, $$1);
        this.page = 0;
        this.pageCount = LecternBlockEntity.getPageCount(this.book);
        this.setChanged();
    }

    void setPage(int $$0) {
        int $$1 = Mth.clamp($$0, 0, this.pageCount - 1);
        if ($$1 != this.page) {
            this.page = $$1;
            this.setChanged();
            LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public int getPage() {
        return this.page;
    }

    public int getRedstoneSignal() {
        float $$0 = this.pageCount > 1 ? (float)this.getPage() / ((float)this.pageCount - 1.0f) : 1.0f;
        return Mth.floor($$0 * 14.0f) + (this.hasBook() ? 1 : 0);
    }

    private ItemStack resolveBook(ItemStack $$0, @Nullable Player $$1) {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$2 = (ServerLevel)level;
            WrittenBookContent.resolveForItem($$0, this.createCommandSourceStack($$1, $$2), $$1);
        }
        return $$0;
    }

    private CommandSourceStack createCommandSourceStack(@Nullable Player $$0, ServerLevel $$1) {
        Component $$5;
        String $$4;
        if ($$0 == null) {
            String $$2 = "Lectern";
            MutableComponent $$3 = Component.literal("Lectern");
        } else {
            $$4 = $$0.getName().getString();
            $$5 = $$0.getDisplayName();
        }
        Vec3 $$6 = Vec3.atCenterOf(this.worldPosition);
        return new CommandSourceStack(CommandSource.NULL, $$6, Vec2.ZERO, $$1, 2, $$4, $$5, $$1.getServer(), $$0);
    }

    @Override
    protected void loadAdditional(ValueInput $$02) {
        super.loadAdditional($$02);
        this.book = $$02.read("Book", ItemStack.CODEC).map($$0 -> this.resolveBook((ItemStack)$$0, null)).orElse(ItemStack.EMPTY);
        this.pageCount = LecternBlockEntity.getPageCount(this.book);
        this.page = Mth.clamp($$02.getIntOr("Page", 0), 0, this.pageCount - 1);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        if (!this.getBook().isEmpty()) {
            $$0.store("Book", ItemStack.CODEC, this.getBook());
            $$0.putInt("Page", this.page);
        }
    }

    @Override
    public void clearContent() {
        this.setBook(ItemStack.EMPTY);
    }

    @Override
    public void preRemoveSideEffects(BlockPos $$0, BlockState $$1) {
        if ($$1.getValue(LecternBlock.HAS_BOOK).booleanValue() && this.level != null) {
            Direction $$2 = $$1.getValue(LecternBlock.FACING);
            ItemStack $$3 = this.getBook().copy();
            float $$4 = 0.25f * (float)$$2.getStepX();
            float $$5 = 0.25f * (float)$$2.getStepZ();
            ItemEntity $$6 = new ItemEntity(this.level, (double)$$0.getX() + 0.5 + (double)$$4, $$0.getY() + 1, (double)$$0.getZ() + 0.5 + (double)$$5, $$3);
            $$6.setDefaultPickUpDelay();
            this.level.addFreshEntity($$6);
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        return new LecternMenu($$0, this.bookAccess, this.dataAccess);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.lectern");
    }

    private static int getPageCount(ItemStack $$0) {
        WrittenBookContent $$1 = $$0.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if ($$1 != null) {
            return $$1.pages().size();
        }
        WritableBookContent $$2 = $$0.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if ($$2 != null) {
            return $$2.pages().size();
        }
        return 0;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CrafterBlockEntity
extends RandomizableContainerBlockEntity
implements CraftingContainer {
    public static final int CONTAINER_WIDTH = 3;
    public static final int CONTAINER_HEIGHT = 3;
    public static final int CONTAINER_SIZE = 9;
    public static final int SLOT_DISABLED = 1;
    public static final int SLOT_ENABLED = 0;
    public static final int DATA_TRIGGERED = 9;
    public static final int NUM_DATA = 10;
    private static final int DEFAULT_CRAFTING_TICKS_REMAINING = 0;
    private static final int DEFAULT_TRIGGERED = 0;
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private int craftingTicksRemaining = 0;
    protected final ContainerData containerData = new ContainerData(this){
        private final int[] slotStates = new int[9];
        private int triggered = 0;

        @Override
        public int get(int $$0) {
            return $$0 == 9 ? this.triggered : this.slotStates[$$0];
        }

        @Override
        public void set(int $$0, int $$1) {
            if ($$0 == 9) {
                this.triggered = $$1;
            } else {
                this.slotStates[$$0] = $$1;
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    };

    public CrafterBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.CRAFTER, $$0, $$1);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.crafter");
    }

    @Override
    protected AbstractContainerMenu createMenu(int $$0, Inventory $$1) {
        return new CrafterMenu($$0, $$1, this, this.containerData);
    }

    public void setSlotState(int $$0, boolean $$1) {
        if (!this.slotCanBeDisabled($$0)) {
            return;
        }
        this.containerData.set($$0, $$1 ? 0 : 1);
        this.setChanged();
    }

    public boolean isSlotDisabled(int $$0) {
        if ($$0 >= 0 && $$0 < 9) {
            return this.containerData.get($$0) == 1;
        }
        return false;
    }

    @Override
    public boolean canPlaceItem(int $$0, ItemStack $$1) {
        if (this.containerData.get($$0) == 1) {
            return false;
        }
        ItemStack $$2 = this.items.get($$0);
        int $$3 = $$2.getCount();
        if ($$3 >= $$2.getMaxStackSize()) {
            return false;
        }
        if ($$2.isEmpty()) {
            return true;
        }
        return !this.smallerStackExist($$3, $$2, $$0);
    }

    private boolean smallerStackExist(int $$0, ItemStack $$1, int $$2) {
        for (int $$3 = $$2 + 1; $$3 < 9; ++$$3) {
            ItemStack $$4;
            if (this.isSlotDisabled($$3) || !($$4 = this.getItem($$3)).isEmpty() && ($$4.getCount() >= $$0 || !ItemStack.isSameItemSameComponents($$4, $$1))) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void loadAdditional(ValueInput $$02) {
        super.loadAdditional($$02);
        this.craftingTicksRemaining = $$02.getIntOr("crafting_ticks_remaining", 0);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable($$02)) {
            ContainerHelper.loadAllItems($$02, this.items);
        }
        for (int $$1 = 0; $$1 < 9; ++$$1) {
            this.containerData.set($$1, 0);
        }
        $$02.getIntArray("disabled_slots").ifPresent($$0 -> {
            for (int $$1 : $$0) {
                if (!this.slotCanBeDisabled($$1)) continue;
                this.containerData.set($$1, 1);
            }
        });
        this.containerData.set(9, $$02.getIntOr("triggered", 0));
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        $$0.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
        if (!this.trySaveLootTable($$0)) {
            ContainerHelper.saveAllItems($$0, this.items);
        }
        this.addDisabledSlots($$0);
        this.addTriggered($$0);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack $$0 : this.items) {
            if ($$0.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int $$0) {
        return this.items.get($$0);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        if (this.isSlotDisabled($$0)) {
            this.setSlotState($$0, true);
        }
        super.setItem($$0, $$1);
    }

    @Override
    public boolean stillValid(Player $$0) {
        return Container.stillValidBlockEntity(this, $$0);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> $$0) {
        this.items = $$0;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public void fillStackedContents(StackedItemContents $$0) {
        for (ItemStack $$1 : this.items) {
            $$0.accountSimpleStack($$1);
        }
    }

    private void addDisabledSlots(ValueOutput $$0) {
        IntArrayList $$1 = new IntArrayList();
        for (int $$2 = 0; $$2 < 9; ++$$2) {
            if (!this.isSlotDisabled($$2)) continue;
            $$1.add($$2);
        }
        $$0.a("disabled_slots", $$1.toIntArray());
    }

    private void addTriggered(ValueOutput $$0) {
        $$0.putInt("triggered", this.containerData.get(9));
    }

    public void setTriggered(boolean $$0) {
        this.containerData.set(9, $$0 ? 1 : 0);
    }

    @VisibleForTesting
    public boolean isTriggered() {
        return this.containerData.get(9) == 1;
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, CrafterBlockEntity $$3) {
        int $$4 = $$3.craftingTicksRemaining - 1;
        if ($$4 < 0) {
            return;
        }
        $$3.craftingTicksRemaining = $$4;
        if ($$4 == 0) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(CrafterBlock.CRAFTING, false), 3);
        }
    }

    public void setCraftingTicksRemaining(int $$0) {
        this.craftingTicksRemaining = $$0;
    }

    public int getRedstoneSignal() {
        int $$0 = 0;
        for (int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if ($$2.isEmpty() && !this.isSlotDisabled($$1)) continue;
            ++$$0;
        }
        return $$0;
    }

    private boolean slotCanBeDisabled(int $$0) {
        return $$0 > -1 && $$0 < 9 && this.items.get($$0).isEmpty();
    }

    public /* synthetic */ List getItems() {
        return this.getItems();
    }
}


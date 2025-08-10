/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

public class ChiseledBookShelfBlockEntity
extends BlockEntity
implements Container {
    public static final int MAX_BOOKS_IN_STORAGE = 6;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_LAST_INTERACTED_SLOT = -1;
    private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int lastInteractedSlot = -1;

    public ChiseledBookShelfBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.CHISELED_BOOKSHELF, $$0, $$1);
    }

    private void updateState(int $$0) {
        if ($$0 < 0 || $$0 >= 6) {
            LOGGER.error("Expected slot 0-5, got {}", (Object)$$0);
            return;
        }
        this.lastInteractedSlot = $$0;
        BlockState $$1 = this.getBlockState();
        for (int $$2 = 0; $$2 < ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); ++$$2) {
            boolean $$3 = !this.getItem($$2).isEmpty();
            BooleanProperty $$4 = ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get($$2);
            $$1 = (BlockState)$$1.setValue($$4, $$3);
        }
        Objects.requireNonNull(this.level).setBlock(this.worldPosition, $$1, 3);
        this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, GameEvent.Context.of($$1));
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.items.clear();
        ContainerHelper.loadAllItems($$0, this.items);
        this.lastInteractedSlot = $$0.getIntOr("last_interacted_slot", -1);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        ContainerHelper.saveAllItems($$0, this.items, true);
        $$0.putInt("last_interacted_slot", this.lastInteractedSlot);
    }

    public int count() {
        return (int)this.items.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int $$0) {
        return this.items.get($$0);
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        ItemStack $$2 = (ItemStack)Objects.requireNonNullElse((Object)this.items.get($$0), (Object)ItemStack.EMPTY);
        this.items.set($$0, ItemStack.EMPTY);
        if (!$$2.isEmpty()) {
            this.updateState($$0);
        }
        return $$2;
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        return this.removeItem($$0, 1);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        if ($$1.is(ItemTags.BOOKSHELF_BOOKS)) {
            this.items.set($$0, $$1);
            this.updateState($$0);
        } else if ($$1.isEmpty()) {
            this.removeItem($$0, 1);
        }
    }

    @Override
    public boolean canTakeItem(Container $$0, int $$1, ItemStack $$22) {
        return $$0.hasAnyMatching($$2 -> {
            if ($$2.isEmpty()) {
                return true;
            }
            return ItemStack.isSameItemSameComponents($$22, $$2) && $$2.getCount() + $$22.getCount() <= $$0.getMaxStackSize((ItemStack)$$2);
        });
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player $$0) {
        return Container.stillValidBlockEntity(this, $$0);
    }

    @Override
    public boolean canPlaceItem(int $$0, ItemStack $$1) {
        return $$1.is(ItemTags.BOOKSHELF_BOOKS) && this.getItem($$0).isEmpty() && $$1.getCount() == this.getMaxStackSize();
    }

    public int getLastInteractedSlot() {
        return this.lastInteractedSlot;
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter $$0) {
        super.applyImplicitComponents($$0);
        $$0.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
        super.collectImplicitComponents($$0);
        $$0.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    @Override
    public void removeComponentsFromTag(ValueOutput $$0) {
        $$0.discard("Items");
    }
}


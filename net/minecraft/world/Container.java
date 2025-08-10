/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface Container
extends Clearable,
Iterable<ItemStack> {
    public static final float DEFAULT_DISTANCE_BUFFER = 4.0f;

    public int getContainerSize();

    public boolean isEmpty();

    public ItemStack getItem(int var1);

    public ItemStack removeItem(int var1, int var2);

    public ItemStack removeItemNoUpdate(int var1);

    public void setItem(int var1, ItemStack var2);

    default public int getMaxStackSize() {
        return 99;
    }

    default public int getMaxStackSize(ItemStack $$0) {
        return Math.min(this.getMaxStackSize(), $$0.getMaxStackSize());
    }

    public void setChanged();

    public boolean stillValid(Player var1);

    default public void startOpen(Player $$0) {
    }

    default public void stopOpen(Player $$0) {
    }

    default public boolean canPlaceItem(int $$0, ItemStack $$1) {
        return true;
    }

    default public boolean canTakeItem(Container $$0, int $$1, ItemStack $$2) {
        return true;
    }

    default public int countItem(Item $$0) {
        int $$1 = 0;
        for (ItemStack $$2 : this) {
            if (!$$2.getItem().equals($$0)) continue;
            $$1 += $$2.getCount();
        }
        return $$1;
    }

    default public boolean hasAnyOf(Set<Item> $$0) {
        return this.hasAnyMatching($$1 -> !$$1.isEmpty() && $$0.contains($$1.getItem()));
    }

    default public boolean hasAnyMatching(Predicate<ItemStack> $$0) {
        for (ItemStack $$1 : this) {
            if (!$$0.test($$1)) continue;
            return true;
        }
        return false;
    }

    public static boolean stillValidBlockEntity(BlockEntity $$0, Player $$1) {
        return Container.stillValidBlockEntity($$0, $$1, 4.0f);
    }

    public static boolean stillValidBlockEntity(BlockEntity $$0, Player $$1, float $$2) {
        Level $$3 = $$0.getLevel();
        BlockPos $$4 = $$0.getBlockPos();
        if ($$3 == null) {
            return false;
        }
        if ($$3.getBlockEntity($$4) != $$0) {
            return false;
        }
        return $$1.canInteractWithBlock($$4, $$2);
    }

    @Override
    default public Iterator<ItemStack> iterator() {
        return new ContainerIterator(this);
    }

    public static class ContainerIterator
    implements Iterator<ItemStack> {
        private final Container container;
        private int index;
        private final int size;

        public ContainerIterator(Container $$0) {
            this.container = $$0;
            this.size = $$0.getContainerSize();
        }

        @Override
        public boolean hasNext() {
            return this.index < this.size;
        }

        @Override
        public ItemStack next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.container.getItem(this.index++);
        }

        @Override
        public /* synthetic */ Object next() {
            return this.next();
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface ContainerComponentManipulator<T> {
    public DataComponentType<T> type();

    public T empty();

    public T setContents(T var1, Stream<ItemStack> var2);

    public Stream<ItemStack> getContents(T var1);

    default public void setContents(ItemStack $$0, T $$1, Stream<ItemStack> $$2) {
        T $$3 = $$0.getOrDefault(this.type(), $$1);
        T $$4 = this.setContents($$3, $$2);
        $$0.set(this.type(), $$4);
    }

    default public void setContents(ItemStack $$0, Stream<ItemStack> $$1) {
        this.setContents($$0, this.empty(), $$1);
    }

    default public void modifyItems(ItemStack $$0, UnaryOperator<ItemStack> $$12) {
        T $$2 = $$0.get(this.type());
        if ($$2 != null) {
            UnaryOperator $$3 = $$1 -> {
                if ($$1.isEmpty()) {
                    return $$1;
                }
                ItemStack $$2 = (ItemStack)$$12.apply((ItemStack)$$1);
                $$2.limitSize($$2.getMaxStackSize());
                return $$2;
            };
            this.setContents($$0, this.getContents($$2).map($$3));
        }
    }
}


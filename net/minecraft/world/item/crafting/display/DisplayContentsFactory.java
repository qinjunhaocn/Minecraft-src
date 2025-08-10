/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting.display;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface DisplayContentsFactory<T> {

    public static interface ForRemainders<T>
    extends DisplayContentsFactory<T> {
        public T addRemainder(T var1, List<T> var2);
    }

    public static interface ForStacks<T>
    extends DisplayContentsFactory<T> {
        default public T forStack(Holder<Item> $$0) {
            return this.forStack(new ItemStack($$0));
        }

        default public T forStack(Item $$0) {
            return this.forStack(new ItemStack($$0));
        }

        public T forStack(ItemStack var1);
    }
}


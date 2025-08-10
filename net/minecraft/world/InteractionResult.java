/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

public sealed interface InteractionResult {
    public static final Success SUCCESS = new Success(SwingSource.CLIENT, ItemContext.DEFAULT);
    public static final Success SUCCESS_SERVER = new Success(SwingSource.SERVER, ItemContext.DEFAULT);
    public static final Success CONSUME = new Success(SwingSource.NONE, ItemContext.DEFAULT);
    public static final Fail FAIL = new Fail();
    public static final Pass PASS = new Pass();
    public static final TryEmptyHandInteraction TRY_WITH_EMPTY_HAND = new TryEmptyHandInteraction();

    default public boolean consumesAction() {
        return false;
    }

    public record Success(SwingSource swingSource, ItemContext itemContext) implements InteractionResult
    {
        @Override
        public boolean consumesAction() {
            return true;
        }

        public Success heldItemTransformedTo(ItemStack $$0) {
            return new Success(this.swingSource, new ItemContext(true, $$0));
        }

        public Success withoutItem() {
            return new Success(this.swingSource, ItemContext.NONE);
        }

        public boolean wasItemInteraction() {
            return this.itemContext.wasItemInteraction;
        }

        @Nullable
        public ItemStack heldItemTransformedTo() {
            return this.itemContext.heldItemTransformedTo;
        }
    }

    public static final class SwingSource
    extends Enum<SwingSource> {
        public static final /* enum */ SwingSource NONE = new SwingSource();
        public static final /* enum */ SwingSource CLIENT = new SwingSource();
        public static final /* enum */ SwingSource SERVER = new SwingSource();
        private static final /* synthetic */ SwingSource[] $VALUES;

        public static SwingSource[] values() {
            return (SwingSource[])$VALUES.clone();
        }

        public static SwingSource valueOf(String $$0) {
            return Enum.valueOf(SwingSource.class, $$0);
        }

        private static /* synthetic */ SwingSource[] a() {
            return new SwingSource[]{NONE, CLIENT, SERVER};
        }

        static {
            $VALUES = SwingSource.a();
        }
    }

    public static final class ItemContext
    extends Record {
        final boolean wasItemInteraction;
        @Nullable
        final ItemStack heldItemTransformedTo;
        static ItemContext NONE = new ItemContext(false, null);
        static ItemContext DEFAULT = new ItemContext(true, null);

        public ItemContext(boolean $$0, @Nullable ItemStack $$1) {
            this.wasItemInteraction = $$0;
            this.heldItemTransformedTo = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "wasItemInteraction", "heldItemTransformedTo"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "wasItemInteraction", "heldItemTransformedTo"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "wasItemInteraction", "heldItemTransformedTo"}, this, $$0);
        }

        public boolean wasItemInteraction() {
            return this.wasItemInteraction;
        }

        @Nullable
        public ItemStack heldItemTransformedTo() {
            return this.heldItemTransformedTo;
        }
    }

    public record Fail() implements InteractionResult
    {
    }

    public record Pass() implements InteractionResult
    {
    }

    public record TryEmptyHandInteraction() implements InteractionResult
    {
    }
}


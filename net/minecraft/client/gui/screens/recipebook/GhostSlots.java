/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.screens.recipebook;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class GhostSlots {
    private final Reference2ObjectMap<Slot, GhostSlot> ingredients = new Reference2ObjectArrayMap();
    private final SlotSelectTime slotSelectTime;

    public GhostSlots(SlotSelectTime $$0) {
        this.slotSelectTime = $$0;
    }

    public void clear() {
        this.ingredients.clear();
    }

    private void setSlot(Slot $$0, ContextMap $$1, SlotDisplay $$2, boolean $$3) {
        List<ItemStack> $$4 = $$2.resolveForStacks($$1);
        if (!$$4.isEmpty()) {
            this.ingredients.put((Object)$$0, (Object)new GhostSlot($$4, $$3));
        }
    }

    protected void setInput(Slot $$0, ContextMap $$1, SlotDisplay $$2) {
        this.setSlot($$0, $$1, $$2, false);
    }

    protected void setResult(Slot $$0, ContextMap $$1, SlotDisplay $$2) {
        this.setSlot($$0, $$1, $$2, true);
    }

    public void render(GuiGraphics $$0, Minecraft $$1, boolean $$2) {
        this.ingredients.forEach(($$3, $$4) -> {
            int $$5 = $$3.x;
            int $$6 = $$3.y;
            if ($$4.isResultSlot && $$2) {
                $$0.fill($$5 - 4, $$6 - 4, $$5 + 20, $$6 + 20, 0x30FF0000);
            } else {
                $$0.fill($$5, $$6, $$5 + 16, $$6 + 16, 0x30FF0000);
            }
            ItemStack $$7 = $$4.getItem(this.slotSelectTime.currentIndex());
            $$0.renderFakeItem($$7, $$5, $$6);
            $$0.fill($$5, $$6, $$5 + 16, $$6 + 16, 0x30FFFFFF);
            if ($$4.isResultSlot) {
                $$0.renderItemDecorations($$2.font, $$7, $$5, $$6);
            }
        });
    }

    public void renderTooltip(GuiGraphics $$0, Minecraft $$1, int $$2, int $$3, @Nullable Slot $$4) {
        if ($$4 == null) {
            return;
        }
        GhostSlot $$5 = (GhostSlot)((Object)this.ingredients.get((Object)$$4));
        if ($$5 != null) {
            ItemStack $$6 = $$5.getItem(this.slotSelectTime.currentIndex());
            $$0.setComponentTooltipForNextFrame($$1.font, Screen.getTooltipFromItem($$1, $$6), $$2, $$3, $$6.get(DataComponents.TOOLTIP_STYLE));
        }
    }

    static final class GhostSlot
    extends Record {
        private final List<ItemStack> items;
        final boolean isResultSlot;

        GhostSlot(List<ItemStack> $$0, boolean $$1) {
            this.items = $$0;
            this.isResultSlot = $$1;
        }

        public ItemStack getItem(int $$0) {
            int $$1 = this.items.size();
            if ($$1 == 0) {
                return ItemStack.EMPTY;
            }
            return this.items.get($$0 % $$1);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GhostSlot.class, "items;isResultSlot", "items", "isResultSlot"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GhostSlot.class, "items;isResultSlot", "items", "isResultSlot"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GhostSlot.class, "items;isResultSlot", "items", "isResultSlot"}, this, $$0);
        }

        public List<ItemStack> items() {
            return this.items;
        }

        public boolean isResultSlot() {
            return this.isResultSlot;
        }
    }
}


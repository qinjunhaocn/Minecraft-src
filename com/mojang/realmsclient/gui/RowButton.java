/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class RowButton {
    public final int width;
    public final int height;
    public final int xOffset;
    public final int yOffset;

    public RowButton(int $$0, int $$1, int $$2, int $$3) {
        this.width = $$0;
        this.height = $$1;
        this.xOffset = $$2;
        this.yOffset = $$3;
    }

    public void drawForRowAt(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        int $$5 = $$1 + this.xOffset;
        int $$6 = $$2 + this.yOffset;
        boolean $$7 = $$3 >= $$5 && $$3 <= $$5 + this.width && $$4 >= $$6 && $$4 <= $$6 + this.height;
        this.draw($$0, $$5, $$6, $$7);
    }

    protected abstract void draw(GuiGraphics var1, int var2, int var3, boolean var4);

    public int getRight() {
        return this.xOffset + this.width;
    }

    public int getBottom() {
        return this.yOffset + this.height;
    }

    public abstract void onClick(int var1);

    public static void drawButtonsInRow(GuiGraphics $$0, List<RowButton> $$1, AbstractSelectionList<?> $$2, int $$3, int $$4, int $$5, int $$6) {
        for (RowButton $$7 : $$1) {
            if ($$2.getRowWidth() <= $$7.getRight()) continue;
            $$7.drawForRowAt($$0, $$3, $$4, $$5, $$6);
        }
    }

    public static void rowButtonMouseClicked(AbstractSelectionList<?> $$0, ObjectSelectionList.Entry<?> $$1, List<RowButton> $$2, int $$3, double $$4, double $$5) {
        int $$6 = $$0.children().indexOf($$1);
        if ($$6 > -1) {
            $$0.setSelectedIndex($$6);
            int $$7 = $$0.getRowLeft();
            int $$8 = $$0.getRowTop($$6);
            int $$9 = (int)($$4 - (double)$$7);
            int $$10 = (int)($$5 - (double)$$8);
            for (RowButton $$11 : $$2) {
                if ($$9 < $$11.xOffset || $$9 > $$11.getRight() || $$10 < $$11.yOffset || $$10 > $$11.getBottom()) continue;
                $$11.onClick($$6);
            }
        }
    }
}


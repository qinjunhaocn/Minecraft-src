/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.FormattedCharSequence;

public class ClientTextTooltip
implements ClientTooltipComponent {
    private final FormattedCharSequence text;

    public ClientTextTooltip(FormattedCharSequence $$0) {
        this.text = $$0;
    }

    @Override
    public int getWidth(Font $$0) {
        return $$0.width(this.text);
    }

    @Override
    public int getHeight(Font $$0) {
        return 10;
    }

    @Override
    public void renderText(GuiGraphics $$0, Font $$1, int $$2, int $$3) {
        $$0.drawString($$1, this.text, $$2, $$3, -1, true);
    }
}


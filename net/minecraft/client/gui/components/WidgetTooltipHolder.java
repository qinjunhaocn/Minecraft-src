/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;

public class WidgetTooltipHolder {
    @Nullable
    private Tooltip tooltip;
    private Duration delay = Duration.ZERO;
    private long displayStartTime;
    private boolean wasDisplayed;

    public void setDelay(Duration $$0) {
        this.delay = $$0;
    }

    public void set(@Nullable Tooltip $$0) {
        this.tooltip = $$0;
    }

    @Nullable
    public Tooltip get() {
        return this.tooltip;
    }

    public void refreshTooltipForNextRenderPass(GuiGraphics $$0, int $$1, int $$2, boolean $$3, boolean $$4, ScreenRectangle $$5) {
        boolean $$7;
        if (this.tooltip == null) {
            this.wasDisplayed = false;
            return;
        }
        Minecraft $$6 = Minecraft.getInstance();
        boolean bl = $$7 = $$3 || $$4 && $$6.getLastInputType().isKeyboard();
        if ($$7 != this.wasDisplayed) {
            if ($$7) {
                this.displayStartTime = Util.getMillis();
            }
            this.wasDisplayed = $$7;
        }
        if ($$7 && Util.getMillis() - this.displayStartTime > this.delay.toMillis()) {
            $$0.setTooltipForNextFrame($$6.font, this.tooltip.toCharSequence($$6), this.createTooltipPositioner($$5, $$3, $$4), $$1, $$2, $$4);
        }
    }

    private ClientTooltipPositioner createTooltipPositioner(ScreenRectangle $$0, boolean $$1, boolean $$2) {
        if (!$$1 && $$2 && Minecraft.getInstance().getLastInputType().isKeyboard()) {
            return new BelowOrAboveWidgetTooltipPositioner($$0);
        }
        return new MenuTooltipPositioner($$0);
    }

    public void updateNarration(NarrationElementOutput $$0) {
        if (this.tooltip != null) {
            this.tooltip.updateNarration($$0);
        }
    }
}


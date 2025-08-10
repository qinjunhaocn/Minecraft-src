/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientActivePlayersTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface ClientTooltipComponent {
    public static ClientTooltipComponent create(FormattedCharSequence $$0) {
        return new ClientTextTooltip($$0);
    }

    public static ClientTooltipComponent create(TooltipComponent $$0) {
        TooltipComponent tooltipComponent = $$0;
        Objects.requireNonNull(tooltipComponent);
        TooltipComponent tooltipComponent2 = tooltipComponent;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BundleTooltip.class, ClientActivePlayersTooltip.ActivePlayersTooltip.class}, (Object)tooltipComponent2, (int)n)) {
            case 0 -> {
                BundleTooltip $$1 = (BundleTooltip)tooltipComponent2;
                yield new ClientBundleTooltip($$1.contents());
            }
            case 1 -> {
                ClientActivePlayersTooltip.ActivePlayersTooltip $$2 = (ClientActivePlayersTooltip.ActivePlayersTooltip)tooltipComponent2;
                yield new ClientActivePlayersTooltip($$2);
            }
            default -> throw new IllegalArgumentException("Unknown TooltipComponent");
        };
    }

    public int getHeight(Font var1);

    public int getWidth(Font var1);

    default public boolean showTooltipWithItemInHand() {
        return false;
    }

    default public void renderText(GuiGraphics $$0, Font $$1, int $$2, int $$3) {
    }

    default public void renderImage(Font $$0, int $$1, int $$2, int $$3, int $$4, GuiGraphics $$5) {
    }
}


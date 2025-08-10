/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector2i
 *  org.joml.Vector2ic
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class BelowOrAboveWidgetTooltipPositioner
implements ClientTooltipPositioner {
    private final ScreenRectangle screenRectangle;

    public BelowOrAboveWidgetTooltipPositioner(ScreenRectangle $$0) {
        this.screenRectangle = $$0;
    }

    @Override
    public Vector2ic positionTooltip(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        Vector2i $$6 = new Vector2i();
        $$6.x = this.screenRectangle.left() + 3;
        $$6.y = this.screenRectangle.bottom() + 3 + 1;
        if ($$6.y + $$5 + 3 > $$1) {
            $$6.y = this.screenRectangle.top() - $$5 - 3 - 1;
        }
        if ($$6.x + $$4 > $$0) {
            $$6.x = Math.max(this.screenRectangle.right() - $$4 - 3, 4);
        }
        return $$6;
    }
}


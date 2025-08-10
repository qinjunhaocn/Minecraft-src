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
import net.minecraft.util.Mth;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class MenuTooltipPositioner
implements ClientTooltipPositioner {
    private static final int MARGIN = 5;
    private static final int MOUSE_OFFSET_X = 12;
    public static final int MAX_OVERLAP_WITH_WIDGET = 3;
    public static final int MAX_DISTANCE_TO_WIDGET = 5;
    private final ScreenRectangle screenRectangle;

    public MenuTooltipPositioner(ScreenRectangle $$0) {
        this.screenRectangle = $$0;
    }

    @Override
    public Vector2ic positionTooltip(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$9;
        Vector2i $$6 = new Vector2i($$2 + 12, $$3);
        if ($$6.x + $$4 > $$0 - 5) {
            $$6.x = Math.max($$2 - 12 - $$4, 9);
        }
        $$6.y += 3;
        int $$7 = $$5 + 3 + 3;
        int $$8 = this.screenRectangle.bottom() + 3 + MenuTooltipPositioner.getOffset(0, 0, this.screenRectangle.height());
        $$6.y = $$8 + $$7 <= ($$9 = $$1 - 5) ? ($$6.y += MenuTooltipPositioner.getOffset($$6.y, this.screenRectangle.top(), this.screenRectangle.height())) : ($$6.y -= $$7 + MenuTooltipPositioner.getOffset($$6.y, this.screenRectangle.bottom(), this.screenRectangle.height()));
        return $$6;
    }

    private static int getOffset(int $$0, int $$1, int $$2) {
        int $$3 = Math.min(Math.abs($$0 - $$1), $$2);
        return Math.round(Mth.lerp((float)$$3 / (float)$$2, $$2 - 3, 5.0f));
    }
}


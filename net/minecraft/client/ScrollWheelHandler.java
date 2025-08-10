/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector2i
 */
package net.minecraft.client;

import org.joml.Vector2i;

public class ScrollWheelHandler {
    private double accumulatedScrollX;
    private double accumulatedScrollY;

    public Vector2i onMouseScroll(double $$0, double $$1) {
        if (this.accumulatedScrollX != 0.0 && Math.signum($$0) != Math.signum(this.accumulatedScrollX)) {
            this.accumulatedScrollX = 0.0;
        }
        if (this.accumulatedScrollY != 0.0 && Math.signum($$1) != Math.signum(this.accumulatedScrollY)) {
            this.accumulatedScrollY = 0.0;
        }
        this.accumulatedScrollX += $$0;
        this.accumulatedScrollY += $$1;
        int $$2 = (int)this.accumulatedScrollX;
        int $$3 = (int)this.accumulatedScrollY;
        if ($$2 == 0 && $$3 == 0) {
            return new Vector2i(0, 0);
        }
        this.accumulatedScrollX -= (double)$$2;
        this.accumulatedScrollY -= (double)$$3;
        return new Vector2i($$2, $$3);
    }

    public static int getNextScrollWheelSelection(double $$0, int $$1, int $$2) {
        int $$3 = (int)Math.signum($$0);
        $$1 -= $$3;
        for ($$1 = Math.max(-1, $$1); $$1 < 0; $$1 += $$2) {
        }
        while ($$1 >= $$2) {
            $$1 -= $$2;
        }
        return $$1;
    }
}


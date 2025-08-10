/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class SplashRenderer {
    public static final SplashRenderer CHRISTMAS = new SplashRenderer("Merry X-mas!");
    public static final SplashRenderer NEW_YEAR = new SplashRenderer("Happy new year!");
    public static final SplashRenderer HALLOWEEN = new SplashRenderer("OOoooOOOoooo! Spooky!");
    private static final int WIDTH_OFFSET = 123;
    private static final int HEIGH_OFFSET = 69;
    private final String splash;

    public SplashRenderer(String $$0) {
        this.splash = $$0;
    }

    public void render(GuiGraphics $$0, int $$1, Font $$2, float $$3) {
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)$$1 / 2.0f + 123.0f, 69.0f);
        $$0.pose().rotate(-0.34906584f);
        float $$4 = 1.8f - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
        $$4 = $$4 * 100.0f / (float)($$2.width(this.splash) + 32);
        $$0.pose().scale($$4, $$4);
        $$0.drawCenteredString($$2, this.splash, 0, -8, ARGB.color($$3, -256));
        $$0.pose().popMatrix();
    }
}


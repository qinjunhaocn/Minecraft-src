/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public class RealmsLabel
implements Renderable {
    private final Component text;
    private final int x;
    private final int y;
    private final int color;

    public RealmsLabel(Component $$0, int $$1, int $$2, int $$3) {
        this.text = $$0;
        this.x = $$1;
        this.y = $$2;
        this.color = $$3;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        $$0.drawCenteredString(Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
    }

    public Component getText() {
        return this.text;
    }
}


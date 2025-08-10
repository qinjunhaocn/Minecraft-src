/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public class CommonLayouts {
    private static final int LABEL_SPACING = 4;

    private CommonLayouts() {
    }

    public static Layout labeledElement(Font $$02, LayoutElement $$1, Component $$2) {
        return CommonLayouts.labeledElement($$02, $$1, $$2, $$0 -> {});
    }

    public static Layout labeledElement(Font $$0, LayoutElement $$1, Component $$2, Consumer<LayoutSettings> $$3) {
        LinearLayout $$4 = LinearLayout.vertical().spacing(4);
        $$4.addChild(new StringWidget($$2, $$0));
        $$4.addChild($$1, $$3);
        return $$4;
    }
}


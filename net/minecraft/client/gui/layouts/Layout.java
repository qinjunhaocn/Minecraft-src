/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;

public interface Layout
extends LayoutElement {
    public void visitChildren(Consumer<LayoutElement> var1);

    @Override
    default public void visitWidgets(Consumer<AbstractWidget> $$0) {
        this.visitChildren($$1 -> $$1.visitWidgets($$0));
    }

    default public void arrangeElements() {
        this.visitChildren($$0 -> {
            if ($$0 instanceof Layout) {
                Layout $$1 = (Layout)$$0;
                $$1.arrangeElements();
            }
        });
    }
}


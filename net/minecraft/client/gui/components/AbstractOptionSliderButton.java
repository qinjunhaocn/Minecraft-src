/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;

public abstract class AbstractOptionSliderButton
extends AbstractSliderButton {
    protected final Options options;

    protected AbstractOptionSliderButton(Options $$0, int $$1, int $$2, int $$3, int $$4, double $$5) {
        super($$1, $$2, $$3, $$4, CommonComponents.EMPTY, $$5);
        this.options = $$0;
    }
}


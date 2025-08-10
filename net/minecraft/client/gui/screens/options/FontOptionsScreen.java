/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class FontOptionsScreen
extends OptionsSubScreen {
    private static OptionInstance<?>[] a(Options $$0) {
        return new OptionInstance[]{$$0.forceUnicodeFont(), $$0.japaneseGlyphVariants()};
    }

    public FontOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("options.font.title"));
    }

    @Override
    protected void addOptions() {
        this.list.a(FontOptionsScreen.a(this.options));
    }
}


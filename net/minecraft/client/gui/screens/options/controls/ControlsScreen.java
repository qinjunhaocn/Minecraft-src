/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options.controls;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

public class ControlsScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("controls.title");

    private static OptionInstance<?>[] a(Options $$0) {
        return new OptionInstance[]{$$0.toggleCrouch(), $$0.toggleSprint(), $$0.autoJump(), $$0.operatorItemsTab()};
    }

    public ControlsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void addOptions() {
        this.list.addSmall(Button.builder(Component.translatable("options.mouse_settings"), $$0 -> this.minecraft.setScreen(new MouseSettingsScreen(this, this.options))).build(), Button.builder(Component.translatable("controls.keybinds"), $$0 -> this.minecraft.setScreen(new KeyBindsScreen(this, this.options))).build());
        this.list.a(ControlsScreen.a(this.options));
    }
}


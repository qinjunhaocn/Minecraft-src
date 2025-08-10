/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class MouseSettingsScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("options.mouse_settings.title");

    private static OptionInstance<?>[] a(Options $$0) {
        return new OptionInstance[]{$$0.sensitivity(), $$0.invertYMouse(), $$0.mouseWheelSensitivity(), $$0.discreteMouseScroll(), $$0.touchscreen()};
    }

    public MouseSettingsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void addOptions() {
        if (InputConstants.isRawMouseInputSupported()) {
            this.list.a((OptionInstance[])Stream.concat(Arrays.stream(MouseSettingsScreen.a(this.options)), Stream.of(this.options.rawMouseInput())).toArray(OptionInstance[]::new));
        } else {
            this.list.a(MouseSettingsScreen.a(this.options));
        }
    }
}


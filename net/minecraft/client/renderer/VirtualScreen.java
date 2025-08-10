/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public final class VirtualScreen
implements AutoCloseable {
    private final Minecraft minecraft;
    private final ScreenManager screenManager;

    public VirtualScreen(Minecraft $$0) {
        this.minecraft = $$0;
        this.screenManager = new ScreenManager(Monitor::new);
    }

    public Window newWindow(DisplayData $$0, @Nullable String $$1, String $$2) {
        return new Window(this.minecraft, this.screenManager, $$0, $$1, $$2);
    }

    @Override
    public void close() {
        this.screenManager.shutdown();
    }
}


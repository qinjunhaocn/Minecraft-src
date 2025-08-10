/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.tabs;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class TabManager {
    private final Consumer<AbstractWidget> addWidget;
    private final Consumer<AbstractWidget> removeWidget;
    private final Consumer<Tab> onSelected;
    private final Consumer<Tab> onDeselected;
    @Nullable
    private Tab currentTab;
    @Nullable
    private ScreenRectangle tabArea;

    public TabManager(Consumer<AbstractWidget> $$02, Consumer<AbstractWidget> $$1) {
        this($$02, $$1, $$0 -> {}, $$0 -> {});
    }

    public TabManager(Consumer<AbstractWidget> $$0, Consumer<AbstractWidget> $$1, Consumer<Tab> $$2, Consumer<Tab> $$3) {
        this.addWidget = $$0;
        this.removeWidget = $$1;
        this.onSelected = $$2;
        this.onDeselected = $$3;
    }

    public void setTabArea(ScreenRectangle $$0) {
        this.tabArea = $$0;
        Tab $$1 = this.getCurrentTab();
        if ($$1 != null) {
            $$1.doLayout($$0);
        }
    }

    public void setCurrentTab(Tab $$0, boolean $$1) {
        if (!Objects.equals(this.currentTab, $$0)) {
            if (this.currentTab != null) {
                this.currentTab.visitChildren(this.removeWidget);
            }
            Tab $$2 = this.currentTab;
            this.currentTab = $$0;
            $$0.visitChildren(this.addWidget);
            if (this.tabArea != null) {
                $$0.doLayout(this.tabArea);
            }
            if ($$1) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            this.onDeselected.accept($$2);
            this.onSelected.accept(this.currentTab);
        }
    }

    @Nullable
    public Tab getCurrentTab() {
        return this.currentTab;
    }
}


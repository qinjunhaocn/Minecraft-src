/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options.controls;

import com.mojang.blaze3d.platform.InputConstants;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class KeyBindsScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("controls.keybinds.title");
    @Nullable
    public KeyMapping selectedKey;
    public long lastKeySelection;
    private KeyBindsList keyBindsList;
    private Button resetButton;

    public KeyBindsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void addContents() {
        this.keyBindsList = this.layout.addToContents(new KeyBindsList(this, this.minecraft));
    }

    @Override
    protected void addOptions() {
    }

    @Override
    protected void addFooter() {
        this.resetButton = Button.builder(Component.translatable("controls.resetAll"), $$0 -> {
            for (KeyMapping $$1 : this.options.keyMappings) {
                $$1.setKey($$1.getDefaultKey());
            }
            this.keyBindsList.resetMappingAndUpdateButtons();
        }).build();
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        $$02.addChild(this.resetButton);
        $$02.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).build());
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.keyBindsList.updateSize(this.width, this.layout);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.selectedKey != null) {
            this.selectedKey.setKey(InputConstants.Type.MOUSE.getOrCreate($$2));
            this.selectedKey = null;
            this.keyBindsList.resetMappingAndUpdateButtons();
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.selectedKey != null) {
            if ($$0 == 256) {
                this.selectedKey.setKey(InputConstants.UNKNOWN);
            } else {
                this.selectedKey.setKey(InputConstants.getKey($$0, $$1));
            }
            this.selectedKey = null;
            this.lastKeySelection = Util.getMillis();
            this.keyBindsList.resetMappingAndUpdateButtons();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        boolean $$4 = false;
        for (KeyMapping $$5 : this.options.keyMappings) {
            if ($$5.isDefault()) continue;
            $$4 = true;
            break;
        }
        this.resetButton.active = $$4;
    }
}


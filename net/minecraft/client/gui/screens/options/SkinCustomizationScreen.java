/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import java.util.ArrayList;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinCustomizationScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("options.skinCustomisation.title");

    public SkinCustomizationScreen(Screen $$0, Options $$1) {
        super($$0, $$1, TITLE);
    }

    @Override
    protected void addOptions() {
        ArrayList<AbstractWidget> $$0 = new ArrayList<AbstractWidget>();
        for (PlayerModelPart $$12 : PlayerModelPart.values()) {
            $$0.add(CycleButton.onOffBuilder(this.options.isModelPartEnabled($$12)).create($$12.getName(), ($$1, $$2) -> this.options.setModelPart($$12, (boolean)$$2)));
        }
        $$0.add(this.options.mainHand().createButton(this.options));
        this.list.addSmall($$0);
    }
}


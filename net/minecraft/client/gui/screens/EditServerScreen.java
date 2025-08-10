/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class EditServerScreen
extends Screen {
    private static final Component NAME_LABEL = Component.translatable("addServer.enterName");
    private static final Component IP_LABEL = Component.translatable("addServer.enterIp");
    private Button addButton;
    private final BooleanConsumer callback;
    private final ServerData serverData;
    private EditBox ipEdit;
    private EditBox nameEdit;
    private final Screen lastScreen;

    public EditServerScreen(Screen $$0, BooleanConsumer $$1, ServerData $$2) {
        super(Component.translatable("addServer.title"));
        this.lastScreen = $$0;
        this.callback = $$1;
        this.serverData = $$2;
    }

    @Override
    protected void init() {
        this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, Component.translatable("addServer.enterName"));
        this.nameEdit.setValue(this.serverData.name);
        this.nameEdit.setResponder($$0 -> this.updateAddButtonStatus());
        this.addWidget(this.nameEdit);
        this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, Component.translatable("addServer.enterIp"));
        this.ipEdit.setMaxLength(128);
        this.ipEdit.setValue(this.serverData.ip);
        this.ipEdit.setResponder($$0 -> this.updateAddButtonStatus());
        this.addWidget(this.ipEdit);
        this.addRenderableWidget(CycleButton.builder(ServerData.ServerPackStatus::getName).a((ServerData.ServerPackStatus[])ServerData.ServerPackStatus.values()).withInitialValue(this.serverData.getResourcePackStatus()).create(this.width / 2 - 100, this.height / 4 + 72, 200, 20, Component.translatable("addServer.resourcePack"), ($$0, $$1) -> this.serverData.setResourcePackStatus((ServerData.ServerPackStatus)((Object)$$1))));
        this.addButton = this.addRenderableWidget(Button.builder(Component.translatable("addServer.add"), $$0 -> this.onAdd()).bounds(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.callback.accept(false)).bounds(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20).build());
        this.updateAddButtonStatus();
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameEdit);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.ipEdit.getValue();
        String $$4 = this.nameEdit.getValue();
        this.init($$0, $$1, $$2);
        this.ipEdit.setValue($$3);
        this.nameEdit.setValue($$4);
    }

    private void onAdd() {
        this.serverData.name = this.nameEdit.getValue();
        this.serverData.ip = this.ipEdit.getValue();
        this.callback.accept(true);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void updateAddButtonStatus() {
        this.addButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue()) && !this.nameEdit.getValue().isEmpty();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 17, -1);
        $$0.drawString(this.font, NAME_LABEL, this.width / 2 - 100 + 1, 53, -6250336);
        $$0.drawString(this.font, IP_LABEL, this.width / 2 - 100 + 1, 94, -6250336);
        this.nameEdit.render($$0, $$1, $$2, $$3);
        this.ipEdit.render($$0, $$1, $$2, $$3);
    }
}


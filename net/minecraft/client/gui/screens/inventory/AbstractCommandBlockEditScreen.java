/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.BaseCommandBlock;

public abstract class AbstractCommandBlockEditScreen
extends Screen {
    private static final Component SET_COMMAND_LABEL = Component.translatable("advMode.setCommand");
    private static final Component COMMAND_LABEL = Component.translatable("advMode.command");
    private static final Component PREVIOUS_OUTPUT_LABEL = Component.translatable("advMode.previousOutput");
    protected EditBox commandEdit;
    protected EditBox previousEdit;
    protected Button doneButton;
    protected Button cancelButton;
    protected CycleButton<Boolean> outputButton;
    CommandSuggestions commandSuggestions;

    public AbstractCommandBlockEditScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void tick() {
        if (!this.getCommandBlock().isValid()) {
            this.onClose();
        }
    }

    abstract BaseCommandBlock getCommandBlock();

    abstract int getPreviousY();

    @Override
    protected void init() {
        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).bounds(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20).build());
        this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).bounds(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20).build());
        boolean $$02 = this.getCommandBlock().isTrackOutput();
        this.outputButton = this.addRenderableWidget(CycleButton.booleanBuilder(Component.literal("O"), Component.literal("X")).withInitialValue($$02).displayOnlyValue().create(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, Component.translatable("advMode.trackOutput"), ($$0, $$1) -> {
            BaseCommandBlock $$2 = this.getCommandBlock();
            $$2.setTrackOutput((boolean)$$1);
            this.updatePreviousOutput((boolean)$$1);
        }));
        this.commandEdit = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, (Component)Component.translatable("advMode.command")){

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(AbstractCommandBlockEditScreen.this.commandSuggestions.getNarrationMessage());
            }
        };
        this.commandEdit.setMaxLength(32500);
        this.commandEdit.setResponder(this::onEdited);
        this.addWidget(this.commandEdit);
        this.previousEdit = new EditBox(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, Component.translatable("advMode.previousOutput"));
        this.previousEdit.setMaxLength(32500);
        this.previousEdit.setEditable(false);
        this.previousEdit.setValue("-");
        this.addWidget(this.previousEdit);
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.commandEdit, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
        this.commandSuggestions.setAllowSuggestions(true);
        this.commandSuggestions.updateCommandInfo();
        this.updatePreviousOutput($$02);
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.commandEdit);
    }

    @Override
    protected Component getUsageNarration() {
        if (this.commandSuggestions.isVisible()) {
            return this.commandSuggestions.getUsageNarration();
        }
        return super.getUsageNarration();
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.commandEdit.getValue();
        this.init($$0, $$1, $$2);
        this.commandEdit.setValue($$3);
        this.commandSuggestions.updateCommandInfo();
    }

    protected void updatePreviousOutput(boolean $$0) {
        this.previousEdit.setValue($$0 ? this.getCommandBlock().getLastOutput().getString() : "-");
    }

    protected void onDone() {
        BaseCommandBlock $$0 = this.getCommandBlock();
        this.populateAndSendPacket($$0);
        if (!$$0.isTrackOutput()) {
            $$0.setLastOutput(null);
        }
        this.minecraft.setScreen(null);
    }

    protected abstract void populateAndSendPacket(BaseCommandBlock var1);

    private void onEdited(String $$0) {
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.commandSuggestions.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if ($$0 == 257 || $$0 == 335) {
            this.onDone();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (this.commandSuggestions.mouseScrolled($$3)) {
            return true;
        }
        return super.mouseScrolled($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.commandSuggestions.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, SET_COMMAND_LABEL, this.width / 2, 20, -1);
        $$0.drawString(this.font, COMMAND_LABEL, this.width / 2 - 150 + 1, 40, -6250336);
        this.commandEdit.render($$0, $$1, $$2, $$3);
        int $$4 = 75;
        if (!this.previousEdit.getValue().isEmpty()) {
            $$0.drawString(this.font, PREVIOUS_OUTPUT_LABEL, this.width / 2 - 150 + 1, ($$4 += 5 * this.font.lineHeight + 1 + this.getPreviousY() - 135) + 4, -6250336);
            this.previousEdit.render($$0, $$1, $$2, $$3);
        }
        this.commandSuggestions.render($$0, $$1, $$2);
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderTransparentBackground($$0);
    }
}


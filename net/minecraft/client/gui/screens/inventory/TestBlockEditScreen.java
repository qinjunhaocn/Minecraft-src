/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetTestBlockPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.state.properties.TestBlockMode;

public class TestBlockEditScreen
extends Screen {
    private static final List<TestBlockMode> MODES = List.of((Object[])TestBlockMode.values());
    private static final Component TITLE = Component.translatable(Blocks.TEST_BLOCK.getDescriptionId());
    private static final Component MESSAGE_LABEL = Component.translatable("test_block.message");
    private final BlockPos position;
    private TestBlockMode mode;
    private String message;
    @Nullable
    private EditBox messageEdit;

    public TestBlockEditScreen(TestBlockEntity $$0) {
        super(TITLE);
        this.position = $$0.getBlockPos();
        this.mode = $$0.getMode();
        this.message = $$0.getMessage();
    }

    @Override
    public void init() {
        this.messageEdit = new EditBox(this.font, this.width / 2 - 152, 80, 240, 20, Component.translatable("test_block.message"));
        this.messageEdit.setMaxLength(128);
        this.messageEdit.setValue(this.message);
        this.addRenderableWidget(this.messageEdit);
        this.setInitialFocus(this.messageEdit);
        this.updateMode(this.mode);
        this.addRenderableWidget(CycleButton.builder(TestBlockMode::getDisplayName).withValues(MODES).displayOnlyValue().withInitialValue(this.mode).create(this.width / 2 - 4 - 150, 185, 50, 20, TITLE, ($$0, $$1) -> this.updateMode((TestBlockMode)$$1)));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onCancel()).bounds(this.width / 2 + 4, 210, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 10, -1);
        if (this.mode != TestBlockMode.START) {
            $$0.drawString(this.font, MESSAGE_LABEL, this.width / 2 - 153, 70, -6250336);
        }
        $$0.drawString(this.font, this.mode.getDetailedMessage(), this.width / 2 - 153, 174, -6250336);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void onDone() {
        this.message = this.messageEdit.getValue();
        this.minecraft.getConnection().send(new ServerboundSetTestBlockPacket(this.position, this.mode, this.message));
        this.onClose();
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    private void onCancel() {
        this.minecraft.setScreen(null);
    }

    private void updateMode(TestBlockMode $$0) {
        this.mode = $$0;
        this.messageEdit.visible = $$0 != TestBlockMode.START;
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderTransparentBackground($$0);
    }
}


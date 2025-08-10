/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class BookSignScreen
extends Screen {
    private static final Component EDIT_TITLE_LABEL = Component.translatable("book.editTitle");
    private static final Component FINALIZE_WARNING_LABEL = Component.translatable("book.finalizeWarning");
    private static final Component TITLE = Component.translatable("book.sign.title");
    private static final Component TITLE_EDIT_BOX = Component.translatable("book.sign.titlebox");
    private final BookEditScreen bookEditScreen;
    private final Player owner;
    private final List<String> pages;
    private final InteractionHand hand;
    private final Component ownerText;
    private EditBox titleBox;
    private String titleValue = "";

    public BookSignScreen(BookEditScreen $$0, Player $$1, InteractionHand $$2, List<String> $$3) {
        super(TITLE);
        this.bookEditScreen = $$0;
        this.owner = $$1;
        this.hand = $$2;
        this.pages = $$3;
        this.ownerText = Component.a("book.byAuthor", $$1.getName()).withStyle(ChatFormatting.DARK_GRAY);
    }

    @Override
    protected void init() {
        Button $$02 = Button.builder(Component.translatable("book.finalizeButton"), $$0 -> {
            this.saveChanges();
            this.minecraft.setScreen(null);
        }).bounds(this.width / 2 - 100, 196, 98, 20).build();
        $$02.active = false;
        this.titleBox = this.addRenderableWidget(new EditBox(this.minecraft.font, (this.width - 114) / 2 - 3, 50, 114, 20, TITLE_EDIT_BOX));
        this.titleBox.setMaxLength(15);
        this.titleBox.setBordered(false);
        this.titleBox.setCentered(true);
        this.titleBox.setTextColor(-16777216);
        this.titleBox.setTextShadow(false);
        this.titleBox.setResponder($$1 -> {
            $$0.active = !StringUtil.isBlank($$1);
        });
        this.titleBox.setValue(this.titleValue);
        this.addRenderableWidget($$02);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            this.titleValue = this.titleBox.getValue();
            this.minecraft.setScreen(this.bookEditScreen);
        }).bounds(this.width / 2 + 2, 196, 98, 20).build());
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.titleBox);
    }

    private void saveChanges() {
        int $$0 = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().getSelectedSlot() : 40;
        this.minecraft.getConnection().send(new ServerboundEditBookPacket($$0, this.pages, Optional.of(this.titleBox.getValue().trim())));
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.titleBox.isFocused() && !this.titleBox.getValue().isEmpty() && ($$0 == 257 || $$0 == 335)) {
            this.saveChanges();
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        int $$4 = (this.width - 192) / 2;
        int $$5 = 2;
        int $$6 = this.font.width(EDIT_TITLE_LABEL);
        $$0.drawString(this.font, EDIT_TITLE_LABEL, $$4 + 36 + (114 - $$6) / 2, 34, -16777216, false);
        int $$7 = this.font.width(this.ownerText);
        $$0.drawString(this.font, this.ownerText, $$4 + 36 + (114 - $$7) / 2, 60, -16777216, false);
        $$0.drawWordWrap(this.font, FINALIZE_WARNING_LABEL, $$4 + 36, 82, 114, -16777216, false);
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderTransparentBackground($$0);
        $$0.blit(RenderPipelines.GUI_TEXTURED, BookViewScreen.BOOK_LOCATION, (this.width - 192) / 2, 2, 0.0f, 0.0f, 192, 192, 256, 256);
    }
}


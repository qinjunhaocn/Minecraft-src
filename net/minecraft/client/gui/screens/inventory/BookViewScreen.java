/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.gui.screens.inventory;

import java.lang.runtime.SwitchBootstraps;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;

public class BookViewScreen
extends Screen {
    public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
    public static final int PAGE_TEXT_X_OFFSET = 36;
    public static final int PAGE_TEXT_Y_OFFSET = 30;
    private static final int BACKGROUND_TEXTURE_WIDTH = 256;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    private static final Component TITLE = Component.translatable("book.view.title");
    public static final BookAccess EMPTY_ACCESS = new BookAccess(List.of());
    public static final ResourceLocation BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/book.png");
    protected static final int TEXT_WIDTH = 114;
    protected static final int TEXT_HEIGHT = 128;
    protected static final int IMAGE_WIDTH = 192;
    protected static final int IMAGE_HEIGHT = 192;
    private BookAccess bookAccess;
    private int currentPage;
    private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();
    private int cachedPage = -1;
    private Component pageMsg = CommonComponents.EMPTY;
    private PageButton forwardButton;
    private PageButton backButton;
    private final boolean playTurnSound;

    public BookViewScreen(BookAccess $$0) {
        this($$0, true);
    }

    public BookViewScreen() {
        this(EMPTY_ACCESS, false);
    }

    private BookViewScreen(BookAccess $$0, boolean $$1) {
        super(TITLE);
        this.bookAccess = $$0;
        this.playTurnSound = $$1;
    }

    public void setBookAccess(BookAccess $$0) {
        this.bookAccess = $$0;
        this.currentPage = Mth.clamp(this.currentPage, 0, $$0.getPageCount());
        this.updateButtonVisibility();
        this.cachedPage = -1;
    }

    public boolean setPage(int $$0) {
        int $$1 = Mth.clamp($$0, 0, this.bookAccess.getPageCount() - 1);
        if ($$1 != this.currentPage) {
            this.currentPage = $$1;
            this.updateButtonVisibility();
            this.cachedPage = -1;
            return true;
        }
        return false;
    }

    protected boolean forcePage(int $$0) {
        return this.setPage($$0);
    }

    @Override
    protected void init() {
        this.createMenuControls();
        this.createPageControlButtons();
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.b(super.getNarrationMessage(), this.getPageNumberMessage(), this.bookAccess.getPage(this.currentPage));
    }

    private Component getPageNumberMessage() {
        return Component.a("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
    }

    protected void createMenuControls() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).bounds(this.width / 2 - 100, 196, 200, 20).build());
    }

    protected void createPageControlButtons() {
        int $$02 = (this.width - 192) / 2;
        int $$1 = 2;
        this.forwardButton = this.addRenderableWidget(new PageButton($$02 + 116, 159, true, $$0 -> this.pageForward(), this.playTurnSound));
        this.backButton = this.addRenderableWidget(new PageButton($$02 + 43, 159, false, $$0 -> this.pageBack(), this.playTurnSound));
        this.updateButtonVisibility();
    }

    private int getNumPages() {
        return this.bookAccess.getPageCount();
    }

    protected void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }
        this.updateButtonVisibility();
    }

    protected void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
        }
        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
        this.backButton.visible = this.currentPage > 0;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        switch ($$0) {
            case 266: {
                this.backButton.onPress();
                return true;
            }
            case 267: {
                this.forwardButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        int $$4 = (this.width - 192) / 2;
        int $$5 = 2;
        if (this.cachedPage != this.currentPage) {
            Component $$6 = this.bookAccess.getPage(this.currentPage);
            this.cachedPageComponents = this.font.split($$6, 114);
            this.pageMsg = this.getPageNumberMessage();
        }
        this.cachedPage = this.currentPage;
        int $$7 = this.font.width(this.pageMsg);
        $$0.drawString(this.font, this.pageMsg, $$4 - $$7 + 192 - 44, 18, -16777216, false);
        int $$8 = Math.min(128 / this.font.lineHeight, this.cachedPageComponents.size());
        for (int $$9 = 0; $$9 < $$8; ++$$9) {
            FormattedCharSequence $$10 = this.cachedPageComponents.get($$9);
            $$0.drawString(this.font, $$10, $$4 + 36, 32 + $$9 * this.font.lineHeight, -16777216, false);
        }
        Style $$11 = this.getClickedComponentStyleAt($$1, $$2);
        if ($$11 != null) {
            $$0.renderComponentHoverEffect(this.font, $$11, $$1, $$2);
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderTransparentBackground($$0);
        $$0.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, (this.width - 192) / 2, 2, 0.0f, 0.0f, 192, 192, 256, 256);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        Style $$3;
        if ($$2 == 0 && ($$3 = this.getClickedComponentStyleAt($$0, $$1)) != null && this.handleComponentClicked($$3)) {
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void handleClickEvent(Minecraft $$0, ClickEvent $$1) {
        LocalPlayer $$2 = Objects.requireNonNull($$0.player, "Player not available");
        ClickEvent clickEvent = $$1;
        Objects.requireNonNull(clickEvent);
        ClickEvent clickEvent2 = clickEvent;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.ChangePage.class, ClickEvent.RunCommand.class}, (Object)clickEvent2, (int)n)) {
            case 0: {
                ClickEvent.ChangePage changePage = (ClickEvent.ChangePage)clickEvent2;
                try {
                    int n2;
                    int $$3 = n2 = changePage.page();
                    this.forcePage($$3 - 1);
                    return;
                } catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
            }
            case 1: {
                String $$4;
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent2;
                {
                    String string;
                    $$4 = string = runCommand.command();
                    this.closeContainerOnServer();
                }
                BookViewScreen.clickCommandAction($$2, $$4, null);
                return;
            }
        }
        BookViewScreen.defaultHandleGameClickEvent($$1, $$0, this);
    }

    protected void closeContainerOnServer() {
    }

    @Nullable
    public Style getClickedComponentStyleAt(double $$0, double $$1) {
        if (this.cachedPageComponents.isEmpty()) {
            return null;
        }
        int $$2 = Mth.floor($$0 - (double)((this.width - 192) / 2) - 36.0);
        int $$3 = Mth.floor($$1 - 2.0 - 30.0);
        if ($$2 < 0 || $$3 < 0) {
            return null;
        }
        int $$4 = Math.min(128 / this.font.lineHeight, this.cachedPageComponents.size());
        if ($$2 <= 114 && $$3 < this.minecraft.font.lineHeight * $$4 + $$4) {
            int $$5 = $$3 / this.minecraft.font.lineHeight;
            if ($$5 >= 0 && $$5 < this.cachedPageComponents.size()) {
                FormattedCharSequence $$6 = this.cachedPageComponents.get($$5);
                return this.minecraft.font.getSplitter().componentStyleAtWidth($$6, $$2);
            }
            return null;
        }
        return null;
    }

    public record BookAccess(List<Component> pages) {
        public int getPageCount() {
            return this.pages.size();
        }

        public Component getPage(int $$0) {
            if ($$0 >= 0 && $$0 < this.getPageCount()) {
                return this.pages.get($$0);
            }
            return CommonComponents.EMPTY;
        }

        @Nullable
        public static BookAccess fromItem(ItemStack $$0) {
            boolean $$1 = Minecraft.getInstance().isTextFilteringEnabled();
            WrittenBookContent $$2 = $$0.get(DataComponents.WRITTEN_BOOK_CONTENT);
            if ($$2 != null) {
                return new BookAccess($$2.getPages($$1));
            }
            WritableBookContent $$3 = $$0.get(DataComponents.WRITABLE_BOOK_CONTENT);
            if ($$3 != null) {
                return new BookAccess($$3.getPages($$1).map(Component::literal).toList());
            }
            return null;
        }
    }
}


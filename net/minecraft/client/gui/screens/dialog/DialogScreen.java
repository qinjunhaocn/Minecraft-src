/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.gui.screens.dialog;

import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.dialog.DialogControlSet;
import net.minecraft.client.gui.screens.dialog.WaitingForResponseScreen;
import net.minecraft.client.gui.screens.dialog.body.DialogBodyHandlers;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.body.DialogBody;
import org.apache.commons.lang3.mutable.MutableObject;

public abstract class DialogScreen<T extends Dialog>
extends Screen {
    public static final Component DISCONNECT = Component.translatable("menu.custom_screen_info.disconnect");
    private static final int WARNING_BUTTON_SIZE = 20;
    private static final WidgetSprites WARNING_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("dialog/warning_button"), ResourceLocation.withDefaultNamespace("dialog/warning_button_disabled"), ResourceLocation.withDefaultNamespace("dialog/warning_button_highlighted"));
    private final T dialog;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    @Nullable
    private final Screen previousScreen;
    @Nullable
    private ScrollableLayout bodyScroll;
    private Button warningButton;
    private final DialogConnectionAccess connectionAccess;
    private Supplier<Optional<ClickEvent>> onClose = DialogControlSet.EMPTY_ACTION;

    public DialogScreen(@Nullable Screen $$0, T $$1, DialogConnectionAccess $$2) {
        super($$1.common().title());
        this.dialog = $$1;
        this.previousScreen = $$0;
        this.connectionAccess = $$2;
    }

    @Override
    protected final void init() {
        super.init();
        this.warningButton = this.createWarningButton();
        this.warningButton.setTabOrderGroup(-10);
        DialogControlSet $$02 = new DialogControlSet(this);
        LinearLayout $$1 = LinearLayout.vertical().spacing(10);
        $$1.defaultCellSetting().alignHorizontallyCenter();
        this.layout.addToHeader(this.createTitleWithWarningButton());
        for (DialogBody $$2 : this.dialog.common().body()) {
            LayoutElement $$3 = DialogBodyHandlers.createBodyElement(this, $$2);
            if ($$3 == null) continue;
            $$1.addChild($$3);
        }
        for (Input $$4 : this.dialog.common().inputs()) {
            $$02.addInput($$4, $$1::addChild);
        }
        this.populateBodyElements($$1, $$02, this.dialog, this.connectionAccess);
        this.bodyScroll = new ScrollableLayout(this.minecraft, $$1, this.layout.getContentHeight());
        this.layout.addToContents(this.bodyScroll);
        this.updateHeaderAndFooter(this.layout, $$02, this.dialog, this.connectionAccess);
        this.onClose = $$02.bindAction(this.dialog.onCancel());
        this.layout.visitWidgets($$0 -> {
            if ($$0 != this.warningButton) {
                this.addRenderableWidget($$0);
            }
        });
        this.addRenderableWidget(this.warningButton);
        this.repositionElements();
    }

    protected void populateBodyElements(LinearLayout $$0, DialogControlSet $$1, T $$2, DialogConnectionAccess $$3) {
    }

    protected void updateHeaderAndFooter(HeaderAndFooterLayout $$0, DialogControlSet $$1, T $$2, DialogConnectionAccess $$3) {
    }

    @Override
    protected void repositionElements() {
        this.bodyScroll.setMaxHeight(this.layout.getContentHeight());
        this.layout.arrangeElements();
        this.makeSureWarningButtonIsInBounds();
    }

    protected LayoutElement createTitleWithWarningButton() {
        LinearLayout $$0 = LinearLayout.horizontal().spacing(10);
        $$0.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
        $$0.addChild(new StringWidget(this.title, this.font));
        $$0.addChild(this.warningButton);
        return $$0;
    }

    protected void makeSureWarningButtonIsInBounds() {
        int $$0 = this.warningButton.getX();
        int $$1 = this.warningButton.getY();
        if ($$0 < 0 || $$1 < 0 || $$0 > this.width - 20 || $$1 > this.height - 20) {
            this.warningButton.setX(Math.max(0, this.width - 40));
            this.warningButton.setY(Math.min(5, this.height));
        }
    }

    private Button createWarningButton() {
        ImageButton $$02 = new ImageButton(0, 0, 20, 20, WARNING_BUTTON_SPRITES, $$0 -> this.minecraft.setScreen(WarningScreen.create(this.minecraft, this)), Component.translatable("menu.custom_screen_info.button_narration"));
        $$02.setTooltip(Tooltip.create(Component.translatable("menu.custom_screen_info.tooltip")));
        return $$02;
    }

    @Override
    public boolean isPauseScreen() {
        return this.dialog.common().pause();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.dialog.common().canCloseWithEscape();
    }

    @Override
    public void onClose() {
        this.runAction(this.onClose.get(), DialogAction.CLOSE);
    }

    public void runAction(Optional<ClickEvent> $$0) {
        this.runAction($$0, this.dialog.common().afterAction());
    }

    public void runAction(Optional<ClickEvent> $$0, DialogAction $$1) {
        Screen $$2;
        switch ($$1) {
            default: {
                throw new MatchException(null, null);
            }
            case NONE: {
                Screen screen = this;
                break;
            }
            case CLOSE: {
                Screen screen = this.previousScreen;
                break;
            }
            case WAIT_FOR_RESPONSE: {
                Screen screen = $$2 = new WaitingForResponseScreen(this.previousScreen);
            }
        }
        if ($$0.isPresent()) {
            this.handleDialogClickEvent($$0.get(), $$2);
        } else {
            this.minecraft.setScreen($$2);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void handleDialogClickEvent(ClickEvent $$0, @Nullable Screen $$1) {
        ClickEvent clickEvent = $$0;
        Objects.requireNonNull(clickEvent);
        ClickEvent clickEvent2 = clickEvent;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.RunCommand.class, ClickEvent.ShowDialog.class, ClickEvent.Custom.class}, (Object)clickEvent2, (int)n)) {
            case 0: {
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent2;
                try {
                    String string;
                    String $$2 = string = runCommand.command();
                    this.connectionAccess.runCommand(Commands.trimOptionalPrefix($$2), $$1);
                    return;
                } catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
            }
            case 1: {
                ClickEvent.ShowDialog $$3 = (ClickEvent.ShowDialog)clickEvent2;
                this.connectionAccess.openDialog($$3.dialog(), $$1);
                return;
            }
            case 2: {
                ClickEvent.Custom $$4 = (ClickEvent.Custom)clickEvent2;
                this.connectionAccess.sendCustomAction($$4.id(), $$4.payload());
                this.minecraft.setScreen($$1);
                return;
            }
        }
        DialogScreen.defaultHandleClickEvent($$0, this.minecraft, $$1);
    }

    @Nullable
    public Screen previousScreen() {
        return this.previousScreen;
    }

    protected static LayoutElement packControlsIntoColumns(List<? extends LayoutElement> $$0, int $$1) {
        GridLayout $$2 = new GridLayout();
        $$2.defaultCellSetting().alignHorizontallyCenter();
        $$2.columnSpacing(2).rowSpacing(2);
        int $$3 = $$0.size();
        int $$4 = $$3 / $$1;
        int $$5 = $$4 * $$1;
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            $$2.addChild($$0.get($$6), $$6 / $$1, $$6 % $$1);
        }
        if ($$3 != $$5) {
            LinearLayout $$7 = LinearLayout.horizontal().spacing(2);
            $$7.defaultCellSetting().alignHorizontallyCenter();
            for (int $$8 = $$5; $$8 < $$3; ++$$8) {
                $$7.addChild($$0.get($$8));
            }
            $$2.addChild($$7, $$4, 0, 1, $$1);
        }
        return $$2;
    }

    public static class WarningScreen
    extends ConfirmScreen {
        private final MutableObject<Screen> returnScreen;

        public static Screen create(Minecraft $$0, Screen $$1) {
            return new WarningScreen($$0, new MutableObject<Screen>($$1));
        }

        private WarningScreen(Minecraft $$0, MutableObject<Screen> $$1) {
            super($$2 -> {
                if ($$2) {
                    PauseScreen.disconnectFromWorld($$0, DISCONNECT);
                } else {
                    $$0.setScreen((Screen)$$1.getValue());
                }
            }, Component.translatable("menu.custom_screen_info.title"), Component.translatable("menu.custom_screen_info.contents"), CommonComponents.disconnectButtonLabel($$0.isLocalServer()), CommonComponents.GUI_BACK);
            this.returnScreen = $$1;
        }

        @Nullable
        public Screen returnScreen() {
            return this.returnScreen.getValue();
        }

        public void updateReturnScreen(@Nullable Screen $$0) {
            this.returnScreen.setValue($$0);
        }
    }
}


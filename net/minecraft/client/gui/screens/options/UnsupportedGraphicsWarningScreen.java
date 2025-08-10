/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

public class UnsupportedGraphicsWarningScreen
extends Screen {
    private static final int BUTTON_PADDING = 20;
    private static final int BUTTON_MARGIN = 5;
    private static final int BUTTON_HEIGHT = 20;
    private final Component narrationMessage;
    private final List<Component> message;
    private final ImmutableList<ButtonOption> buttonOptions;
    private MultiLineLabel messageLines = MultiLineLabel.EMPTY;
    private int contentTop;
    private int buttonWidth;

    protected UnsupportedGraphicsWarningScreen(Component $$0, List<Component> $$1, ImmutableList<ButtonOption> $$2) {
        super($$0);
        this.message = $$1;
        this.narrationMessage = CommonComponents.a($$0, ComponentUtils.formatList($$1, CommonComponents.EMPTY));
        this.buttonOptions = $$2;
    }

    @Override
    public Component getNarrationMessage() {
        return this.narrationMessage;
    }

    @Override
    public void init() {
        for (ButtonOption $$0 : this.buttonOptions) {
            this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width($$0.message) + 20);
        }
        int $$1 = 5 + this.buttonWidth + 5;
        int $$2 = $$1 * this.buttonOptions.size();
        this.messageLines = MultiLineLabel.a(this.font, $$2, this.message.toArray(new Component[0]));
        int $$3 = this.messageLines.getLineCount() * this.font.lineHeight;
        this.contentTop = (int)((double)this.height / 2.0 - (double)$$3 / 2.0);
        int $$4 = this.contentTop + $$3 + this.font.lineHeight * 2;
        int $$5 = (int)((double)this.width / 2.0 - (double)$$2 / 2.0);
        for (ButtonOption $$6 : this.buttonOptions) {
            this.addRenderableWidget(Button.builder($$6.message, $$6.onPress).bounds($$5, $$4, this.buttonWidth, 20).build());
            $$5 += $$1;
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, this.contentTop - this.font.lineHeight * 2, -1);
        this.messageLines.renderCentered($$0, this.width / 2, this.contentTop);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static final class ButtonOption {
        final Component message;
        final Button.OnPress onPress;

        public ButtonOption(Component $$0, Button.OnPress $$1) {
            this.message = $$0;
            this.onPress = $$1;
        }
    }
}


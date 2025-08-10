/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.telemetry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.DoubleConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TelemetryEventWidget
extends AbstractTextAreaWidget {
    private static final int HEADER_HORIZONTAL_PADDING = 32;
    private static final String TELEMETRY_REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
    private static final String TELEMETRY_OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
    private static final String TELEMETRY_OPTIONAL_DISABLED_TRANSLATION_KEY = "telemetry.event.optional.disabled";
    private static final Component PROPERTY_TITLE = Component.translatable("telemetry_info.property_title").withStyle(ChatFormatting.UNDERLINE);
    private final Font font;
    private Content content;
    @Nullable
    private DoubleConsumer onScrolledListener;

    public TelemetryEventWidget(int $$0, int $$1, int $$2, int $$3, Font $$4) {
        super($$0, $$1, $$2, $$3, Component.empty());
        this.font = $$4;
        this.content = this.buildContent(Minecraft.getInstance().telemetryOptInExtra());
    }

    public void onOptInChanged(boolean $$0) {
        this.content = this.buildContent($$0);
        this.refreshScrollAmount();
    }

    public void updateLayout() {
        this.content = this.buildContent(Minecraft.getInstance().telemetryOptInExtra());
        this.refreshScrollAmount();
    }

    private Content buildContent(boolean $$0) {
        ContentBuilder $$1 = new ContentBuilder(this.containerWidth());
        ArrayList<TelemetryEventType> $$2 = new ArrayList<TelemetryEventType>(TelemetryEventType.values());
        $$2.sort(Comparator.comparing(TelemetryEventType::isOptIn));
        for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
            TelemetryEventType $$4 = (TelemetryEventType)$$2.get($$3);
            boolean $$5 = $$4.isOptIn() && !$$0;
            this.addEventType($$1, $$4, $$5);
            if ($$3 >= $$2.size() - 1) continue;
            $$1.addSpacer(this.font.lineHeight);
        }
        return $$1.build();
    }

    public void setOnScrolledListener(@Nullable DoubleConsumer $$0) {
        this.onScrolledListener = $$0;
    }

    @Override
    public void setScrollAmount(double $$0) {
        super.setScrollAmount($$0);
        if (this.onScrolledListener != null) {
            this.onScrolledListener.accept(this.scrollAmount());
        }
    }

    @Override
    protected int getInnerHeight() {
        return this.content.container().getHeight();
    }

    @Override
    protected double scrollRate() {
        return this.font.lineHeight;
    }

    @Override
    protected void renderContents(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        int $$42 = this.getInnerTop();
        int $$5 = this.getInnerLeft();
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)$$5, (float)$$42);
        this.content.container().visitWidgets($$4 -> $$4.render($$0, $$1, $$2, $$3));
        $$0.pose().popMatrix();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.content.narration());
    }

    private Component grayOutIfDisabled(Component $$0, boolean $$1) {
        if ($$1) {
            return $$0.copy().withStyle(ChatFormatting.GRAY);
        }
        return $$0;
    }

    private void addEventType(ContentBuilder $$0, TelemetryEventType $$1, boolean $$2) {
        String $$3 = $$1.isOptIn() ? ($$2 ? TELEMETRY_OPTIONAL_DISABLED_TRANSLATION_KEY : TELEMETRY_OPTIONAL_TRANSLATION_KEY) : TELEMETRY_REQUIRED_TRANSLATION_KEY;
        $$0.addHeader(this.font, this.grayOutIfDisabled(Component.a($$3, $$1.title()), $$2));
        $$0.addHeader(this.font, $$1.description().withStyle(ChatFormatting.GRAY));
        $$0.addSpacer(this.font.lineHeight / 2);
        $$0.addLine(this.font, this.grayOutIfDisabled(PROPERTY_TITLE, $$2), 2);
        this.addEventTypeProperties($$1, $$0, $$2);
    }

    private void addEventTypeProperties(TelemetryEventType $$0, ContentBuilder $$1, boolean $$2) {
        for (TelemetryProperty<?> $$3 : $$0.properties()) {
            $$1.addLine(this.font, this.grayOutIfDisabled($$3.title(), $$2));
        }
    }

    private int containerWidth() {
        return this.width - this.totalInnerPadding();
    }

    record Content(Layout container, Component narration) {
    }

    static class ContentBuilder {
        private final int width;
        private final LinearLayout layout;
        private final MutableComponent narration = Component.empty();

        public ContentBuilder(int $$0) {
            this.width = $$0;
            this.layout = LinearLayout.vertical();
            this.layout.defaultCellSetting().alignHorizontallyLeft();
            this.layout.addChild(SpacerElement.width($$0));
        }

        public void addLine(Font $$0, Component $$1) {
            this.addLine($$0, $$1, 0);
        }

        public void addLine(Font $$0, Component $$12, int $$2) {
            this.layout.addChild(new MultiLineTextWidget($$12, $$0).setMaxWidth(this.width), $$1 -> $$1.paddingBottom($$2));
            this.narration.append($$12).append("\n");
        }

        public void addHeader(Font $$02, Component $$1) {
            this.layout.addChild(new MultiLineTextWidget($$1, $$02).setMaxWidth(this.width - 64).setCentered(true), $$0 -> $$0.alignHorizontallyCenter().paddingHorizontal(32));
            this.narration.append($$1).append("\n");
        }

        public void addSpacer(int $$0) {
            this.layout.addChild(SpacerElement.height($$0));
        }

        public Content build() {
            this.layout.arrangeElements();
            return new Content(this.layout, this.narration);
        }
    }
}


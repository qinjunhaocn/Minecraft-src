/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

public class RealmsSelectWorldTemplateScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
    private static final Component SELECT_BUTTON_NAME = Component.translatable("mco.template.button.select");
    private static final Component TRAILER_BUTTON_NAME = Component.translatable("mco.template.button.trailer");
    private static final Component PUBLISHER_BUTTON_NAME = Component.translatable("mco.template.button.publisher");
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_SPACING = 10;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    final Consumer<WorldTemplate> callback;
    WorldTemplateList worldTemplateList;
    private final RealmsServer.WorldType worldType;
    private Button selectButton;
    private Button trailerButton;
    private Button publisherButton;
    @Nullable
    WorldTemplate selectedTemplate = null;
    @Nullable
    String currentLink;
    @Nullable
    private Component[] warning;
    @Nullable
    List<TextRenderingUtils.Line> noTemplatesMessage;

    public RealmsSelectWorldTemplateScreen(Component $$0, Consumer<WorldTemplate> $$1, RealmsServer.WorldType $$2) {
        this($$0, $$1, $$2, null);
    }

    public RealmsSelectWorldTemplateScreen(Component $$0, Consumer<WorldTemplate> $$1, RealmsServer.WorldType $$2, @Nullable WorldTemplatePaginatedList $$3) {
        super($$0);
        this.callback = $$1;
        this.worldType = $$2;
        if ($$3 == null) {
            this.worldTemplateList = new WorldTemplateList();
            this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
        } else {
            this.worldTemplateList = new WorldTemplateList(Lists.newArrayList($$3.templates));
            this.fetchTemplatesAsync($$3);
        }
    }

    public void a(Component ... $$0) {
        this.warning = $$0;
    }

    @Override
    public void init() {
        this.layout.addTitleHeader(this.title, this.font);
        this.worldTemplateList = this.layout.addToContents(new WorldTemplateList(this.worldTemplateList.getTemplates()));
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.horizontal().spacing(10));
        $$02.defaultCellSetting().alignHorizontallyCenter();
        this.trailerButton = $$02.addChild(Button.builder(TRAILER_BUTTON_NAME, $$0 -> this.onTrailer()).width(100).build());
        this.selectButton = $$02.addChild(Button.builder(SELECT_BUTTON_NAME, $$0 -> this.selectTemplate()).width(100).build());
        $$02.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).width(100).build());
        this.publisherButton = $$02.addChild(Button.builder(PUBLISHER_BUTTON_NAME, $$0 -> this.onPublish()).width(100).build());
        this.updateButtonStates();
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.worldTemplateList.setSize(this.width, this.height - this.layout.getFooterHeight() - this.getHeaderHeight());
        this.layout.arrangeElements();
    }

    @Override
    public Component getNarrationMessage() {
        ArrayList<Component> $$0 = Lists.newArrayListWithCapacity(2);
        $$0.add(this.title);
        if (this.warning != null) {
            $$0.addAll(Arrays.asList(this.warning));
        }
        return CommonComponents.joinLines($$0);
    }

    void updateButtonStates() {
        this.publisherButton.visible = this.selectedTemplate != null && !this.selectedTemplate.link.isEmpty();
        this.trailerButton.visible = this.selectedTemplate != null && !this.selectedTemplate.trailer.isEmpty();
        this.selectButton.active = this.selectedTemplate != null;
    }

    @Override
    public void onClose() {
        this.callback.accept(null);
    }

    private void selectTemplate() {
        if (this.selectedTemplate != null) {
            this.callback.accept(this.selectedTemplate);
        }
    }

    private void onTrailer() {
        if (this.selectedTemplate != null && !this.selectedTemplate.trailer.isBlank()) {
            ConfirmLinkScreen.confirmLinkNow((Screen)this, this.selectedTemplate.trailer);
        }
    }

    private void onPublish() {
        if (this.selectedTemplate != null && !this.selectedTemplate.link.isBlank()) {
            ConfirmLinkScreen.confirmLinkNow((Screen)this, this.selectedTemplate.link);
        }
    }

    private void fetchTemplatesAsync(final WorldTemplatePaginatedList $$0) {
        new Thread("realms-template-fetcher"){

            @Override
            public void run() {
                WorldTemplatePaginatedList $$02 = $$0;
                RealmsClient $$1 = RealmsClient.getOrCreate();
                while ($$02 != null) {
                    Either<WorldTemplatePaginatedList, Exception> $$2 = RealmsSelectWorldTemplateScreen.this.fetchTemplates($$02, $$1);
                    $$02 = RealmsSelectWorldTemplateScreen.this.minecraft.submit(() -> {
                        if ($$2.right().isPresent()) {
                            LOGGER.error("Couldn't fetch templates", (Throwable)$$2.right().get());
                            if (RealmsSelectWorldTemplateScreen.this.worldTemplateList.isEmpty()) {
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.a(I18n.a("mco.template.select.failure", new Object[0]), new TextRenderingUtils.LineSegment[0]);
                            }
                            return null;
                        }
                        WorldTemplatePaginatedList $$1 = (WorldTemplatePaginatedList)$$2.left().get();
                        for (WorldTemplate $$2 : $$1.templates) {
                            RealmsSelectWorldTemplateScreen.this.worldTemplateList.addEntry($$2);
                        }
                        if ($$1.templates.isEmpty()) {
                            if (RealmsSelectWorldTemplateScreen.this.worldTemplateList.isEmpty()) {
                                String $$3 = I18n.a("mco.template.select.none", "%link");
                                TextRenderingUtils.LineSegment $$4 = TextRenderingUtils.LineSegment.link(I18n.a("mco.template.select.none.linkTitle", new Object[0]), CommonLinks.REALMS_CONTENT_CREATION.toString());
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.a($$3, $$4);
                            }
                            return null;
                        }
                        return $$1;
                    }).join();
                }
            }
        }.start();
    }

    Either<WorldTemplatePaginatedList, Exception> fetchTemplates(WorldTemplatePaginatedList $$0, RealmsClient $$1) {
        try {
            return Either.left((Object)$$1.fetchWorldTemplates($$0.page + 1, $$0.size, this.worldType));
        } catch (RealmsServiceException $$2) {
            return Either.right((Object)$$2);
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.currentLink = null;
        if (this.noTemplatesMessage != null) {
            this.renderMultilineMessage($$0, $$1, $$2, this.noTemplatesMessage);
        }
        if (this.warning != null) {
            for (int $$4 = 0; $$4 < this.warning.length; ++$$4) {
                Component $$5 = this.warning[$$4];
                $$0.drawCenteredString(this.font, $$5, this.width / 2, RealmsSelectWorldTemplateScreen.row(-1 + $$4), -6250336);
            }
        }
    }

    private void renderMultilineMessage(GuiGraphics $$02, int $$1, int $$2, List<TextRenderingUtils.Line> $$3) {
        for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            TextRenderingUtils.Line $$5 = $$3.get($$4);
            int $$6 = RealmsSelectWorldTemplateScreen.row(4 + $$4);
            int $$7 = $$5.segments.stream().mapToInt($$0 -> this.font.width($$0.renderedText())).sum();
            int $$8 = this.width / 2 - $$7 / 2;
            for (TextRenderingUtils.LineSegment $$9 : $$5.segments) {
                int $$10 = $$9.isLink() ? -13408581 : -1;
                String $$11 = $$9.renderedText();
                $$02.drawString(this.font, $$11, $$8, $$6, $$10);
                int $$12 = $$8 + this.font.width($$11);
                if ($$9.isLink() && $$1 > $$8 && $$1 < $$12 && $$2 > $$6 - 3 && $$2 < $$6 + 8) {
                    $$02.setTooltipForNextFrame(Component.literal($$9.getLinkUrl()), $$1, $$2);
                    this.currentLink = $$9.getLinkUrl();
                }
                $$8 = $$12;
            }
        }
    }

    int getHeaderHeight() {
        return this.warning != null ? RealmsSelectWorldTemplateScreen.row(1) : 33;
    }

    class WorldTemplateList
    extends ObjectSelectionList<Entry> {
        public WorldTemplateList() {
            this(Collections.emptyList());
        }

        public WorldTemplateList(Iterable<WorldTemplate> $$0) {
            super(Minecraft.getInstance(), RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height - 33 - RealmsSelectWorldTemplateScreen.this.getHeaderHeight(), RealmsSelectWorldTemplateScreen.this.getHeaderHeight(), 46);
            $$0.forEach(this::addEntry);
        }

        public void addEntry(WorldTemplate $$0) {
            this.addEntry(new Entry($$0));
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
                ConfirmLinkScreen.confirmLinkNow((Screen)RealmsSelectWorldTemplateScreen.this, RealmsSelectWorldTemplateScreen.this.currentLink);
                return true;
            }
            return super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsSelectWorldTemplateScreen.this.selectedTemplate = $$0 == null ? null : $$0.template;
            RealmsSelectWorldTemplateScreen.this.updateButtonStates();
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        public boolean isEmpty() {
            return this.getItemCount() == 0;
        }

        public List<WorldTemplate> getTemplates() {
            return this.children().stream().map($$0 -> $$0.template).collect(Collectors.toList());
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private static final WidgetSprites WEBSITE_LINK_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("icon/link"), ResourceLocation.withDefaultNamespace("icon/link_highlighted"));
        private static final WidgetSprites TRAILER_LINK_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("icon/video_link"), ResourceLocation.withDefaultNamespace("icon/video_link_highlighted"));
        private static final Component PUBLISHER_LINK_TOOLTIP = Component.translatable("mco.template.info.tooltip");
        private static final Component TRAILER_LINK_TOOLTIP = Component.translatable("mco.template.trailer.tooltip");
        public final WorldTemplate template;
        private long lastClickTime;
        @Nullable
        private ImageButton websiteButton;
        @Nullable
        private ImageButton trailerButton;

        public Entry(WorldTemplate $$0) {
            this.template = $$0;
            if (!$$0.link.isBlank()) {
                this.websiteButton = new ImageButton(15, 15, WEBSITE_LINK_SPRITES, ConfirmLinkScreen.confirmLink((Screen)RealmsSelectWorldTemplateScreen.this, $$0.link), PUBLISHER_LINK_TOOLTIP);
                this.websiteButton.setTooltip(Tooltip.create(PUBLISHER_LINK_TOOLTIP));
            }
            if (!$$0.trailer.isBlank()) {
                this.trailerButton = new ImageButton(15, 15, TRAILER_LINK_SPRITES, ConfirmLinkScreen.confirmLink((Screen)RealmsSelectWorldTemplateScreen.this, $$0.trailer), TRAILER_LINK_TOOLTIP);
                this.trailerButton.setTooltip(Tooltip.create(TRAILER_LINK_TOOLTIP));
            }
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.template;
            RealmsSelectWorldTemplateScreen.this.updateButtonStates();
            if (Util.getMillis() - this.lastClickTime < 250L && this.isFocused()) {
                RealmsSelectWorldTemplateScreen.this.callback.accept(this.template);
            }
            this.lastClickTime = Util.getMillis();
            if (this.websiteButton != null) {
                this.websiteButton.mouseClicked($$0, $$1, $$2);
            }
            if (this.trailerButton != null) {
                this.trailerButton.mouseClicked($$0, $$1, $$2);
            }
            return super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            $$0.blit(RenderPipelines.GUI_TEXTURED, RealmsTextureManager.worldTemplate(this.template.id, this.template.image), $$3 + 1, $$2 + 1 + 1, 0.0f, 0.0f, 38, 38, 38, 38);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, $$3, $$2 + 1, 40, 40);
            int $$10 = 5;
            int $$11 = RealmsSelectWorldTemplateScreen.this.font.width(this.template.version);
            if (this.websiteButton != null) {
                this.websiteButton.setPosition($$3 + $$4 - $$11 - this.websiteButton.getWidth() - 10, $$2);
                this.websiteButton.render($$0, $$6, $$7, $$9);
            }
            if (this.trailerButton != null) {
                this.trailerButton.setPosition($$3 + $$4 - $$11 - this.trailerButton.getWidth() * 2 - 15, $$2);
                this.trailerButton.render($$0, $$6, $$7, $$9);
            }
            int $$12 = $$3 + 45 + 20;
            int $$13 = $$2 + 5;
            $$0.drawString(RealmsSelectWorldTemplateScreen.this.font, this.template.name, $$12, $$13, -1);
            $$0.drawString(RealmsSelectWorldTemplateScreen.this.font, this.template.version, $$3 + $$4 - $$11 - 5, $$13, -9671572);
            $$0.drawString(RealmsSelectWorldTemplateScreen.this.font, this.template.author, $$12, $$13 + ((RealmsSelectWorldTemplateScreen)RealmsSelectWorldTemplateScreen.this).font.lineHeight + 5, -6250336);
            if (!this.template.recommendedPlayers.isBlank()) {
                $$0.drawString(RealmsSelectWorldTemplateScreen.this.font, this.template.recommendedPlayers, $$12, $$2 + $$5 - ((RealmsSelectWorldTemplateScreen)RealmsSelectWorldTemplateScreen.this).font.lineHeight / 2 - 5, -11776948);
            }
        }

        @Override
        public Component getNarration() {
            Component $$0 = CommonComponents.b(Component.literal(this.template.name), Component.a("mco.template.select.narrate.authors", this.template.author), Component.literal(this.template.recommendedPlayers), Component.a("mco.template.select.narrate.version", this.template.version));
            return Component.a("narrator.select", $$0);
        }
    }
}


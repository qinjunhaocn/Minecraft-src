/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.FontOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class LanguageSelectScreen
extends OptionsSubScreen {
    private static final Component WARNING_LABEL = Component.translatable("options.languageAccuracyWarning").withColor(-4539718);
    private static final int FOOTER_HEIGHT = 53;
    private LanguageSelectionList languageSelectionList;
    final LanguageManager languageManager;

    public LanguageSelectScreen(Screen $$0, Options $$1, LanguageManager $$2) {
        super($$0, $$1, Component.translatable("options.language.title"));
        this.languageManager = $$2;
        this.layout.setFooterHeight(53);
    }

    @Override
    protected void addContents() {
        this.languageSelectionList = this.layout.addToContents(new LanguageSelectionList(this.minecraft));
    }

    @Override
    protected void addOptions() {
    }

    @Override
    protected void addFooter() {
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.vertical()).spacing(8);
        $$02.defaultCellSetting().alignHorizontallyCenter();
        $$02.addChild(new StringWidget(WARNING_LABEL, this.font));
        LinearLayout $$1 = $$02.addChild(LinearLayout.horizontal().spacing(8));
        $$1.addChild(Button.builder(Component.translatable("options.font"), $$0 -> this.minecraft.setScreen(new FontOptionsScreen(this, this.options))).build());
        $$1.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).build());
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        this.languageSelectionList.updateSize(this.width, this.layout);
    }

    void onDone() {
        LanguageSelectionList.Entry $$0 = (LanguageSelectionList.Entry)this.languageSelectionList.getSelected();
        if ($$0 != null && !$$0.code.equals(this.languageManager.getSelected())) {
            this.languageManager.setSelected($$0.code);
            this.options.languageCode = $$0.code;
            this.minecraft.reloadResourcePacks();
        }
        this.minecraft.setScreen(this.lastScreen);
    }

    class LanguageSelectionList
    extends ObjectSelectionList<Entry> {
        public LanguageSelectionList(Minecraft $$0) {
            super($$0, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height - 33 - 53, 33, 18);
            String $$12 = LanguageSelectScreen.this.languageManager.getSelected();
            LanguageSelectScreen.this.languageManager.getLanguages().forEach(($$1, $$2) -> {
                Entry $$3 = new Entry((String)$$1, (LanguageInfo)((Object)$$2));
                this.addEntry($$3);
                if ($$12.equals($$1)) {
                    this.setSelected($$3);
                }
            });
            if (this.getSelected() != null) {
                this.centerScrollOn((Entry)this.getSelected());
            }
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            final String code;
            private final Component language;
            private long lastClickTime;

            public Entry(String $$1, LanguageInfo $$2) {
                this.code = $$1;
                this.language = $$2.toComponent();
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                $$0.drawCenteredString(LanguageSelectScreen.this.font, this.language, LanguageSelectionList.this.width / 2, $$2 + $$5 / 2 - ((LanguageSelectScreen)LanguageSelectScreen.this).font.lineHeight / 2, -1);
            }

            @Override
            public boolean keyPressed(int $$0, int $$1, int $$2) {
                if (CommonInputs.selected($$0)) {
                    this.select();
                    LanguageSelectScreen.this.onDone();
                    return true;
                }
                return super.keyPressed($$0, $$1, $$2);
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                this.select();
                if (Util.getMillis() - this.lastClickTime < 250L) {
                    LanguageSelectScreen.this.onDone();
                }
                this.lastClickTime = Util.getMillis();
                return super.mouseClicked($$0, $$1, $$2);
            }

            private void select() {
                LanguageSelectionList.this.setSelected(this);
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", this.language);
            }
        }
    }
}


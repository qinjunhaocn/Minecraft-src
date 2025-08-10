/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

public class KeyBindsList
extends ContainerObjectSelectionList<Entry> {
    private static final int ITEM_HEIGHT = 20;
    final KeyBindsScreen keyBindsScreen;
    private int maxNameWidth;

    public KeyBindsList(KeyBindsScreen $$0, Minecraft $$1) {
        super($$1, $$0.width, $$0.layout.getContentHeight(), $$0.layout.getHeaderHeight(), 20);
        this.keyBindsScreen = $$0;
        Object[] $$2 = ArrayUtils.clone($$1.options.keyMappings);
        Arrays.sort($$2);
        String $$3 = null;
        for (Object $$4 : $$2) {
            MutableComponent $$6;
            int $$7;
            String $$5 = ((KeyMapping)$$4).getCategory();
            if (!$$5.equals($$3)) {
                $$3 = $$5;
                this.addEntry(new CategoryEntry(Component.translatable($$5)));
            }
            if (($$7 = $$1.font.width($$6 = Component.translatable(((KeyMapping)$$4).getName()))) > this.maxNameWidth) {
                this.maxNameWidth = $$7;
            }
            this.addEntry(new KeyEntry((KeyMapping)$$4, $$6));
        }
    }

    public void resetMappingAndUpdateButtons() {
        KeyMapping.resetMapping();
        this.refreshEntries();
    }

    public void refreshEntries() {
        this.children().forEach(Entry::refreshEntry);
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    public class CategoryEntry
    extends Entry {
        final Component name;
        private final int width;

        public CategoryEntry(Component $$1) {
            this.name = $$1;
            this.width = ((KeyBindsList)KeyBindsList.this).minecraft.font.width(this.name);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            $$0.drawString(((KeyBindsList)KeyBindsList.this).minecraft.font, this.name, KeyBindsList.this.width / 2 - this.width / 2, $$2 + $$5 - ((KeyBindsList)KeyBindsList.this).minecraft.font.lineHeight - 1, -1);
        }

        @Override
        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
            return null;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry(){

                @Override
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput $$0) {
                    $$0.add(NarratedElementType.TITLE, CategoryEntry.this.name);
                }
            });
        }

        @Override
        protected void refreshEntry() {
        }
    }

    public class KeyEntry
    extends Entry {
        private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
        private static final int PADDING = 10;
        private final KeyMapping key;
        private final Component name;
        private final Button changeButton;
        private final Button resetButton;
        private boolean hasCollision = false;

        KeyEntry(KeyMapping $$12, Component $$22) {
            this.key = $$12;
            this.name = $$22;
            this.changeButton = Button.builder($$22, $$1 -> {
                KeyBindsList.this.keyBindsScreen.selectedKey = $$12;
                KeyBindsList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 75, 20).createNarration($$2 -> {
                if ($$12.isUnbound()) {
                    return Component.a("narrator.controls.unbound", $$22);
                }
                return Component.a("narrator.controls.bound", $$22, $$2.get());
            }).build();
            this.resetButton = Button.builder(RESET_BUTTON_TITLE, $$1 -> {
                $$12.setKey($$12.getDefaultKey());
                KeyBindsList.this.resetMappingAndUpdateButtons();
            }).bounds(0, 0, 50, 20).createNarration($$1 -> Component.a("narrator.controls.reset", $$22)).build();
            this.refreshEntry();
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int $$10 = KeyBindsList.this.scrollBarX() - this.resetButton.getWidth() - 10;
            int $$11 = $$2 - 2;
            this.resetButton.setPosition($$10, $$11);
            this.resetButton.render($$0, $$6, $$7, $$9);
            int $$12 = $$10 - 5 - this.changeButton.getWidth();
            this.changeButton.setPosition($$12, $$11);
            this.changeButton.render($$0, $$6, $$7, $$9);
            $$0.drawString(((KeyBindsList)KeyBindsList.this).minecraft.font, this.name, $$3, $$2 + $$5 / 2 - ((KeyBindsList)KeyBindsList.this).minecraft.font.lineHeight / 2, -1);
            if (this.hasCollision) {
                int $$13 = 3;
                int $$14 = this.changeButton.getX() - 6;
                $$0.fill($$14, $$2 - 1, $$14 + 3, $$2 + $$5, -65536);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.changeButton, this.resetButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.changeButton, this.resetButton);
        }

        @Override
        protected void refreshEntry() {
            this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            this.resetButton.active = !this.key.isDefault();
            this.hasCollision = false;
            MutableComponent $$0 = Component.empty();
            if (!this.key.isUnbound()) {
                for (KeyMapping $$1 : ((KeyBindsList)KeyBindsList.this).minecraft.options.keyMappings) {
                    if ($$1 == this.key || !this.key.same($$1)) continue;
                    if (this.hasCollision) {
                        $$0.append(", ");
                    }
                    this.hasCollision = true;
                    $$0.append(Component.translatable($$1.getName()));
                }
            }
            if (this.hasCollision) {
                this.changeButton.setMessage(Component.literal("[ ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.RED));
                this.changeButton.setTooltip(Tooltip.create(Component.a("controls.keybinds.duplicateKeybinds", $$0)));
            } else {
                this.changeButton.setTooltip(null);
            }
            if (KeyBindsList.this.keyBindsScreen.selectedKey == this.key) {
                this.changeButton.setMessage(Component.literal("> ").append(this.changeButton.getMessage().copy().a(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    public static abstract class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
        abstract void refreshEntry();
    }
}


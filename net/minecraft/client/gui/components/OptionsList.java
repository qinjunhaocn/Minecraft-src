/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;

public class OptionsList
extends ContainerObjectSelectionList<Entry> {
    private static final int BIG_BUTTON_WIDTH = 310;
    private static final int DEFAULT_ITEM_HEIGHT = 25;
    private final OptionsSubScreen screen;

    public OptionsList(Minecraft $$0, int $$1, OptionsSubScreen $$2) {
        super($$0, $$1, $$2.layout.getContentHeight(), $$2.layout.getHeaderHeight(), 25);
        this.centerListVertically = false;
        this.screen = $$2;
    }

    public void addBig(OptionInstance<?> $$0) {
        this.addEntry(OptionEntry.big(this.minecraft.options, $$0, this.screen));
    }

    public void a(OptionInstance<?> ... $$0) {
        for (int $$1 = 0; $$1 < $$0.length; $$1 += 2) {
            OptionInstance<?> $$2 = $$1 < $$0.length - 1 ? $$0[$$1 + 1] : null;
            this.addEntry(OptionEntry.small(this.minecraft.options, $$0[$$1], $$2, this.screen));
        }
    }

    public void addSmall(List<AbstractWidget> $$0) {
        for (int $$1 = 0; $$1 < $$0.size(); $$1 += 2) {
            this.addSmall($$0.get($$1), $$1 < $$0.size() - 1 ? $$0.get($$1 + 1) : null);
        }
    }

    public void addSmall(AbstractWidget $$0, @Nullable AbstractWidget $$1) {
        this.addEntry(Entry.small($$0, $$1, this.screen));
    }

    @Override
    public int getRowWidth() {
        return 310;
    }

    @Nullable
    public AbstractWidget findOption(OptionInstance<?> $$0) {
        for (Entry $$1 : this.children()) {
            if (!($$1 instanceof OptionEntry)) continue;
            OptionEntry $$2 = (OptionEntry)$$1;
            AbstractWidget $$3 = $$2.options.get($$0);
            if ($$3 == null) continue;
            return $$3;
        }
        return null;
    }

    public void applyUnsavedChanges() {
        for (Entry $$0 : this.children()) {
            if (!($$0 instanceof OptionEntry)) continue;
            OptionEntry $$1 = (OptionEntry)$$0;
            for (AbstractWidget $$2 : $$1.options.values()) {
                if (!($$2 instanceof OptionInstance.OptionInstanceSliderButton)) continue;
                OptionInstance.OptionInstanceSliderButton $$3 = (OptionInstance.OptionInstanceSliderButton)$$2;
                $$3.applyUnsavedValue();
            }
        }
    }

    public Optional<GuiEventListener> getMouseOver(double $$0, double $$1) {
        for (Entry $$2 : this.children()) {
            for (GuiEventListener guiEventListener : $$2.children()) {
                if (!guiEventListener.isMouseOver($$0, $$1)) continue;
                return Optional.of(guiEventListener);
            }
        }
        return Optional.empty();
    }

    protected static class OptionEntry
    extends Entry {
        final Map<OptionInstance<?>, AbstractWidget> options;

        private OptionEntry(Map<OptionInstance<?>, AbstractWidget> $$0, OptionsSubScreen $$1) {
            super(ImmutableList.copyOf($$0.values()), $$1);
            this.options = $$0;
        }

        public static OptionEntry big(Options $$0, OptionInstance<?> $$1, OptionsSubScreen $$2) {
            return new OptionEntry(ImmutableMap.of($$1, $$1.createButton($$0, 0, 0, 310)), $$2);
        }

        public static OptionEntry small(Options $$0, OptionInstance<?> $$1, @Nullable OptionInstance<?> $$2, OptionsSubScreen $$3) {
            AbstractWidget $$4 = $$1.createButton($$0);
            if ($$2 == null) {
                return new OptionEntry(ImmutableMap.of($$1, $$4), $$3);
            }
            return new OptionEntry(ImmutableMap.of($$1, $$4, $$2, $$2.createButton($$0)), $$3);
        }
    }

    protected static class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
        private final List<AbstractWidget> children;
        private final Screen screen;
        private static final int X_OFFSET = 160;

        Entry(List<AbstractWidget> $$0, Screen $$1) {
            this.children = ImmutableList.copyOf($$0);
            this.screen = $$1;
        }

        public static Entry big(List<AbstractWidget> $$0, Screen $$1) {
            return new Entry($$0, $$1);
        }

        public static Entry small(AbstractWidget $$0, @Nullable AbstractWidget $$1, Screen $$2) {
            if ($$1 == null) {
                return new Entry(ImmutableList.of($$0), $$2);
            }
            return new Entry(ImmutableList.of($$0, $$1), $$2);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int $$10 = 0;
            int $$11 = this.screen.width / 2 - 155;
            for (AbstractWidget $$12 : this.children) {
                $$12.setPosition($$11 + $$10, $$2);
                $$12.render($$0, $$6, $$7, $$9);
                $$10 += 160;
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}


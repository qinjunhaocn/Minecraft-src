/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SwitchGrid;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;

public class ExperimentsScreen
extends Screen {
    private static final Component TITLE = Component.translatable("selectWorld.experiments");
    private static final Component INFO = Component.translatable("selectWorld.experiments.info").withStyle(ChatFormatting.RED);
    private static final int MAIN_CONTENT_WIDTH = 310;
    private static final int SCROLL_AREA_MIN_HEIGHT = 130;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen parent;
    private final PackRepository packRepository;
    private final Consumer<PackRepository> output;
    private final Object2BooleanMap<Pack> packs = new Object2BooleanLinkedOpenHashMap();
    @Nullable
    private ScrollableLayout scrollArea;

    public ExperimentsScreen(Screen $$0, PackRepository $$1, Consumer<PackRepository> $$2) {
        super(TITLE);
        this.parent = $$0;
        this.packRepository = $$1;
        this.output = $$2;
        for (Pack $$3 : $$1.getAvailablePacks()) {
            if ($$3.getPackSource() != PackSource.FEATURE) continue;
            this.packs.put((Object)$$3, $$1.getSelectedPacks().contains($$3));
        }
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        LinearLayout $$02 = this.layout.addToContents(LinearLayout.vertical());
        $$02.addChild(new MultiLineTextWidget(INFO, this.font).setMaxWidth(310), $$0 -> $$0.paddingBottom(15));
        SwitchGrid.Builder $$13 = SwitchGrid.builder(299).withInfoUnderneath(2, true).withRowSpacing(4);
        this.packs.forEach(($$12, $$2) -> $$13.addSwitch(ExperimentsScreen.getHumanReadableTitle($$12), () -> this.packs.getBoolean($$12), $$1 -> this.packs.put($$12, $$1.booleanValue())).withInfo($$12.getDescription()));
        Layout $$22 = $$13.build().layout();
        this.scrollArea = new ScrollableLayout(this.minecraft, $$22, 130);
        this.scrollArea.setMinWidth(310);
        $$02.addChild(this.scrollArea);
        LinearLayout $$3 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        $$3.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).build());
        $$3.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    private static Component getHumanReadableTitle(Pack $$0) {
        String $$1 = "dataPack." + $$0.getId() + ".name";
        return I18n.exists($$1) ? Component.translatable($$1) : $$0.getTitle();
    }

    @Override
    protected void repositionElements() {
        this.scrollArea.setMaxHeight(130);
        this.layout.arrangeElements();
        int $$0 = this.height - this.layout.getFooterHeight() - this.scrollArea.getRectangle().bottom();
        this.scrollArea.setMaxHeight(this.scrollArea.getHeight() + $$0);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(super.getNarrationMessage(), INFO);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void onDone() {
        ArrayList<Pack> $$0 = new ArrayList<Pack>(this.packRepository.getSelectedPacks());
        ArrayList $$1 = new ArrayList();
        this.packs.forEach(($$2, $$3) -> {
            $$0.remove($$2);
            if ($$3.booleanValue()) {
                $$1.add($$2);
            }
        });
        $$0.addAll(Lists.reverse($$1));
        this.packRepository.setSelected($$0.stream().map(Pack::getId).toList());
        this.output.accept(this.packRepository);
    }
}


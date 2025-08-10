/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.Collator
 */
package net.minecraft.client.gui.screens;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class CreateBuffetWorldScreen
extends Screen {
    private static final Component BIOME_SELECT_INFO = Component.translatable("createWorld.customize.buffet.biome").withColor(-8355712);
    private static final int SPACING = 8;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen parent;
    private final Consumer<Holder<Biome>> applySettings;
    final Registry<Biome> biomes;
    private BiomeList list;
    Holder<Biome> biome;
    private Button doneButton;

    public CreateBuffetWorldScreen(Screen $$0, WorldCreationContext $$1, Consumer<Holder<Biome>> $$2) {
        super(Component.translatable("createWorld.customize.buffet.title"));
        this.parent = $$0;
        this.applySettings = $$2;
        this.biomes = $$1.worldgenLoadContext().lookupOrThrow(Registries.BIOME);
        Holder $$3 = (Holder)this.biomes.get(Biomes.PLAINS).or(() -> this.biomes.listElements().findAny()).orElseThrow();
        this.biome = $$1.selectedDimensions().overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse($$3);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    protected void init() {
        LinearLayout $$02 = this.layout.addToHeader(LinearLayout.vertical().spacing(8));
        $$02.defaultCellSetting().alignHorizontallyCenter();
        $$02.addChild(new StringWidget(this.getTitle(), this.font));
        $$02.addChild(new StringWidget(BIOME_SELECT_INFO, this.font));
        this.list = this.layout.addToContents(new BiomeList());
        LinearLayout $$1 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.doneButton = $$1.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.applySettings.accept(this.biome);
            this.onClose();
        }).build());
        $$1.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).build());
        this.list.setSelected((BiomeList.Entry)this.list.children().stream().filter($$0 -> Objects.equals($$0.biome, this.biome)).findFirst().orElse(null));
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.list.updateSize(this.width, this.layout);
    }

    void updateButtonValidity() {
        this.doneButton.active = this.list.getSelected() != null;
    }

    class BiomeList
    extends ObjectSelectionList<Entry> {
        BiomeList() {
            super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height - 77, 40, 16);
            Collator $$02 = Collator.getInstance((Locale)Locale.getDefault());
            CreateBuffetWorldScreen.this.biomes.listElements().map($$0 -> new Entry((Holder.Reference<Biome>)$$0)).sorted(Comparator.comparing($$0 -> $$0.name.getString(), $$02)).forEach($$1 -> this.addEntry($$1));
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            if ($$0 != null) {
                CreateBuffetWorldScreen.this.biome = $$0.biome;
            }
            CreateBuffetWorldScreen.this.updateButtonValidity();
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            final Holder.Reference<Biome> biome;
            final Component name;

            public Entry(Holder.Reference<Biome> $$0) {
                this.biome = $$0;
                ResourceLocation $$1 = $$0.key().location();
                String $$2 = $$1.toLanguageKey("biome");
                this.name = Language.getInstance().has($$2) ? Component.translatable($$2) : Component.literal($$1.toString());
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", this.name);
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                $$0.drawString(CreateBuffetWorldScreen.this.font, this.name, $$3 + 5, $$2 + 2, -1);
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                BiomeList.this.setSelected(this);
                return super.mouseClicked($$0, $$1, $$2);
            }
        }
    }
}


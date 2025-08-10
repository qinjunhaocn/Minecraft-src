/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class PresetFlatWorldScreen
extends Screen {
    static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int SLOT_BG_SIZE = 18;
    private static final int SLOT_STAT_HEIGHT = 20;
    private static final int SLOT_BG_X = 1;
    private static final int SLOT_BG_Y = 1;
    private static final int SLOT_FG_X = 2;
    private static final int SLOT_FG_Y = 2;
    private static final ResourceKey<Biome> DEFAULT_BIOME = Biomes.PLAINS;
    public static final Component UNKNOWN_PRESET = Component.translatable("flat_world_preset.unknown");
    private final CreateFlatWorldScreen parent;
    private Component shareText;
    private Component listText;
    private PresetsList list;
    private Button selectButton;
    EditBox export;
    FlatLevelGeneratorSettings settings;

    public PresetFlatWorldScreen(CreateFlatWorldScreen $$0) {
        super(Component.translatable("createWorld.customize.presets.title"));
        this.parent = $$0;
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private static FlatLayerInfo getLayerInfoFromString(HolderGetter<Block> $$0, String $$1, int $$2) {
        void $$13;
        int $$8;
        String $$7;
        List<String> $$3 = Splitter.on('*').limit(2).splitToList($$1);
        if ($$3.size() == 2) {
            String $$4 = $$3.get(1);
            try {
                int $$5 = Math.max(Integer.parseInt($$3.get(0)), 0);
            } catch (NumberFormatException $$6) {
                LOGGER.error("Error while parsing flat world string", $$6);
                return null;
            }
        } else {
            $$7 = $$3.get(0);
            $$8 = 1;
        }
        int $$9 = Math.min($$2 + $$8, DimensionType.Y_SIZE);
        int $$10 = $$9 - $$2;
        try {
            Optional<Holder.Reference<Block>> $$11 = $$0.get(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse($$7)));
        } catch (Exception $$12) {
            LOGGER.error("Error while parsing flat world string", $$12);
            return null;
        }
        if ($$13.isEmpty()) {
            LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)$$7);
            return null;
        }
        return new FlatLayerInfo($$10, (Block)((Holder.Reference)$$13.get()).value());
    }

    private static List<FlatLayerInfo> getLayersInfoFromString(HolderGetter<Block> $$0, String $$1) {
        ArrayList<FlatLayerInfo> $$2 = Lists.newArrayList();
        String[] $$3 = $$1.split(",");
        int $$4 = 0;
        for (String $$5 : $$3) {
            FlatLayerInfo $$6 = PresetFlatWorldScreen.getLayerInfoFromString($$0, $$5, $$4);
            if ($$6 == null) {
                return Collections.emptyList();
            }
            int $$7 = DimensionType.Y_SIZE - $$4;
            if ($$7 <= 0) continue;
            $$2.add($$6.heightLimited($$7));
            $$4 += $$6.getHeight();
        }
        return $$2;
    }

    public static FlatLevelGeneratorSettings fromString(HolderGetter<Block> $$02, HolderGetter<Biome> $$1, HolderGetter<StructureSet> $$2, HolderGetter<PlacedFeature> $$3, String $$4, FlatLevelGeneratorSettings $$5) {
        Holder.Reference<Biome> $$8;
        Iterator<String> $$6 = Splitter.on(';').split($$4).iterator();
        if (!$$6.hasNext()) {
            return FlatLevelGeneratorSettings.getDefault($$1, $$2, $$3);
        }
        List<FlatLayerInfo> $$7 = PresetFlatWorldScreen.getLayersInfoFromString($$02, $$6.next());
        if ($$7.isEmpty()) {
            return FlatLevelGeneratorSettings.getDefault($$1, $$2, $$3);
        }
        Holder<Biome> $$9 = $$8 = $$1.getOrThrow(DEFAULT_BIOME);
        if ($$6.hasNext()) {
            String $$10 = $$6.next();
            $$9 = Optional.ofNullable(ResourceLocation.tryParse($$10)).map($$0 -> ResourceKey.create(Registries.BIOME, $$0)).flatMap($$1::get).orElseGet(() -> {
                LOGGER.warn("Invalid biome: {}", (Object)$$10);
                return $$8;
            });
        }
        return $$5.withBiomeAndLayers($$7, $$5.structureOverrides(), $$9);
    }

    static String save(FlatLevelGeneratorSettings $$0) {
        StringBuilder $$1 = new StringBuilder();
        for (int $$2 = 0; $$2 < $$0.getLayersInfo().size(); ++$$2) {
            if ($$2 > 0) {
                $$1.append(",");
            }
            $$1.append($$0.getLayersInfo().get($$2));
        }
        $$1.append(";");
        $$1.append($$0.getBiome().unwrapKey().map(ResourceKey::location).orElseThrow(() -> new IllegalStateException("Biome not registered")));
        return $$1.toString();
    }

    @Override
    protected void init() {
        this.shareText = Component.translatable("createWorld.customize.presets.share");
        this.listText = Component.translatable("createWorld.customize.presets.list");
        this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
        this.export.setMaxLength(1230);
        WorldCreationContext $$02 = this.parent.parent.getUiState().getSettings();
        RegistryAccess.Frozen $$1 = $$02.worldgenLoadContext();
        FeatureFlagSet $$2 = $$02.dataConfiguration().enabledFeatures();
        HolderLookup.RegistryLookup $$3 = $$1.lookupOrThrow(Registries.BIOME);
        HolderLookup.RegistryLookup $$42 = $$1.lookupOrThrow(Registries.STRUCTURE_SET);
        HolderLookup.RegistryLookup $$5 = $$1.lookupOrThrow(Registries.PLACED_FEATURE);
        HolderLookup.RegistryLookup $$6 = $$1.lookupOrThrow(Registries.BLOCK).filterFeatures($$2);
        this.export.setValue(PresetFlatWorldScreen.save(this.parent.settings()));
        this.settings = this.parent.settings();
        this.addWidget(this.export);
        this.list = this.addRenderableWidget(new PresetsList($$1, $$2));
        this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("createWorld.customize.presets.select"), $$4 -> {
            FlatLevelGeneratorSettings $$5 = PresetFlatWorldScreen.fromString($$6, $$3, $$42, $$5, this.export.getValue(), this.settings);
            this.parent.setConfig($$5);
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.parent)).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.updateButtonValidity(this.list.getSelected() != null);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        return this.list.mouseScrolled($$0, $$1, $$2, $$3);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.export.getValue();
        this.init($$0, $$1, $$2);
        this.export.setValue($$3);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 8, -1);
        $$0.drawString(this.font, this.shareText, 51, 30, -6250336);
        $$0.drawString(this.font, this.listText, 51, 68, -6250336);
        this.export.render($$0, $$1, $$2, $$3);
    }

    public void updateButtonValidity(boolean $$0) {
        this.selectButton.active = $$0 || this.export.getValue().length() > 1;
    }

    class PresetsList
    extends ObjectSelectionList<Entry> {
        public PresetsList(RegistryAccess $$02, FeatureFlagSet $$12) {
            super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height - 117, 80, 24);
            for (Holder<FlatLevelGeneratorPreset> $$2 : $$02.lookupOrThrow(Registries.FLAT_LEVEL_GENERATOR_PRESET).getTagOrEmpty(FlatLevelGeneratorPresetTags.VISIBLE)) {
                Set $$3 = $$2.value().settings().getLayersInfo().stream().map($$0 -> $$0.getBlockState().getBlock()).filter($$1 -> !$$1.isEnabled($$12)).collect(Collectors.toSet());
                if (!$$3.isEmpty()) {
                    LOGGER.info("Discarding flat world preset {} since it contains experimental blocks {}", (Object)$$2.unwrapKey().map($$0 -> $$0.location().toString()).orElse("<unknown>"), (Object)$$3);
                    continue;
                }
                this.addEntry(new Entry($$2));
            }
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            PresetFlatWorldScreen.this.updateButtonValidity($$0 != null);
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (super.keyPressed($$0, $$1, $$2)) {
                return true;
            }
            if (CommonInputs.selected($$0) && this.getSelected() != null) {
                ((Entry)this.getSelected()).select();
            }
            return false;
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private static final ResourceLocation STATS_ICON_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/stats_icons.png");
            private final FlatLevelGeneratorPreset preset;
            private final Component name;

            public Entry(Holder<FlatLevelGeneratorPreset> $$1) {
                this.preset = $$1.value();
                this.name = $$1.unwrapKey().map($$0 -> Component.translatable($$0.location().toLanguageKey("flat_world_preset"))).orElse(UNKNOWN_PRESET);
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                this.blitSlot($$0, $$3, $$2, this.preset.displayItem().value());
                $$0.drawString(PresetFlatWorldScreen.this.font, this.name, $$3 + 18 + 5, $$2 + 6, -1);
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                this.select();
                return super.mouseClicked($$0, $$1, $$2);
            }

            void select() {
                PresetsList.this.setSelected(this);
                PresetFlatWorldScreen.this.settings = this.preset.settings();
                PresetFlatWorldScreen.this.export.setValue(PresetFlatWorldScreen.save(PresetFlatWorldScreen.this.settings));
                PresetFlatWorldScreen.this.export.moveCursorToStart(false);
            }

            private void blitSlot(GuiGraphics $$0, int $$1, int $$2, Item $$3) {
                this.blitSlotBg($$0, $$1 + 1, $$2 + 1);
                $$0.renderFakeItem(new ItemStack($$3), $$1 + 2, $$2 + 2);
            }

            private void blitSlotBg(GuiGraphics $$0, int $$1, int $$2) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, $$1, $$2, 18, 18);
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", this.name);
            }
        }
    }
}


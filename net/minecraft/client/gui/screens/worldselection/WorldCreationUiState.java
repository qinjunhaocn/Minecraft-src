/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.worldselection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class WorldCreationUiState {
    private static final Component DEFAULT_WORLD_NAME = Component.translatable("selectWorld.newWorld");
    private final List<Consumer<WorldCreationUiState>> listeners = new ArrayList<Consumer<WorldCreationUiState>>();
    private String name = DEFAULT_WORLD_NAME.getString();
    private SelectedGameMode gameMode = SelectedGameMode.SURVIVAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    @Nullable
    private Boolean allowCommands;
    private String seed;
    private boolean generateStructures;
    private boolean bonusChest;
    private final Path savesFolder;
    private String targetFolder;
    private WorldCreationContext settings;
    private WorldTypeEntry worldType;
    private final List<WorldTypeEntry> normalPresetList = new ArrayList<WorldTypeEntry>();
    private final List<WorldTypeEntry> altPresetList = new ArrayList<WorldTypeEntry>();
    private GameRules gameRules;

    public WorldCreationUiState(Path $$02, WorldCreationContext $$1, Optional<ResourceKey<WorldPreset>> $$2, OptionalLong $$3) {
        this.savesFolder = $$02;
        this.settings = $$1;
        this.worldType = new WorldTypeEntry(WorldCreationUiState.findPreset($$1, $$2).orElse(null));
        this.updatePresetLists();
        this.seed = $$3.isPresent() ? Long.toString($$3.getAsLong()) : "";
        this.generateStructures = $$1.options().generateStructures();
        this.bonusChest = $$1.options().generateBonusChest();
        this.targetFolder = this.findResultFolder(this.name);
        this.gameMode = $$1.initialWorldCreationOptions().selectedGameMode();
        this.gameRules = new GameRules($$1.dataConfiguration().enabledFeatures());
        $$1.initialWorldCreationOptions().disabledGameRules().forEach($$0 -> ((GameRules.BooleanValue)this.gameRules.getRule($$0)).set(false, null));
        Optional.ofNullable($$1.initialWorldCreationOptions().flatLevelPreset()).flatMap($$12 -> $$1.worldgenLoadContext().lookup(Registries.FLAT_LEVEL_GENERATOR_PRESET).flatMap($$1 -> $$1.get($$12))).map($$0 -> ((FlatLevelGeneratorPreset)((Object)((Object)$$0.value()))).settings()).ifPresent($$0 -> this.updateDimensions(PresetEditor.flatWorldConfigurator($$0)));
    }

    public void addListener(Consumer<WorldCreationUiState> $$0) {
        this.listeners.add($$0);
    }

    public void onChanged() {
        boolean $$12;
        boolean $$0 = this.isBonusChest();
        if ($$0 != this.settings.options().generateBonusChest()) {
            this.settings = this.settings.withOptions($$1 -> $$1.withBonusChest($$0));
        }
        if (($$12 = this.isGenerateStructures()) != this.settings.options().generateStructures()) {
            this.settings = this.settings.withOptions($$1 -> $$1.withStructures($$12));
        }
        for (Consumer<WorldCreationUiState> $$2 : this.listeners) {
            $$2.accept(this);
        }
    }

    public void setName(String $$0) {
        this.name = $$0;
        this.targetFolder = this.findResultFolder($$0);
        this.onChanged();
    }

    private String findResultFolder(String $$0) {
        String $$1 = $$0.trim();
        try {
            return FileUtil.findAvailableName(this.savesFolder, !$$1.isEmpty() ? $$1 : DEFAULT_WORLD_NAME.getString(), "");
        } catch (Exception exception) {
            try {
                return FileUtil.findAvailableName(this.savesFolder, "World", "");
            } catch (IOException $$2) {
                throw new RuntimeException("Could not create save folder", $$2);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getTargetFolder() {
        return this.targetFolder;
    }

    public void setGameMode(SelectedGameMode $$0) {
        this.gameMode = $$0;
        this.onChanged();
    }

    public SelectedGameMode getGameMode() {
        if (this.isDebug()) {
            return SelectedGameMode.DEBUG;
        }
        return this.gameMode;
    }

    public void setDifficulty(Difficulty $$0) {
        this.difficulty = $$0;
        this.onChanged();
    }

    public Difficulty getDifficulty() {
        if (this.isHardcore()) {
            return Difficulty.HARD;
        }
        return this.difficulty;
    }

    public boolean isHardcore() {
        return this.getGameMode() == SelectedGameMode.HARDCORE;
    }

    public void setAllowCommands(boolean $$0) {
        this.allowCommands = $$0;
        this.onChanged();
    }

    public boolean isAllowCommands() {
        if (this.isDebug()) {
            return true;
        }
        if (this.isHardcore()) {
            return false;
        }
        if (this.allowCommands == null) {
            return this.getGameMode() == SelectedGameMode.CREATIVE;
        }
        return this.allowCommands;
    }

    public void setSeed(String $$02) {
        this.seed = $$02;
        this.settings = this.settings.withOptions($$0 -> $$0.withSeed(WorldOptions.parseSeed(this.getSeed())));
        this.onChanged();
    }

    public String getSeed() {
        return this.seed;
    }

    public void setGenerateStructures(boolean $$0) {
        this.generateStructures = $$0;
        this.onChanged();
    }

    public boolean isGenerateStructures() {
        if (this.isDebug()) {
            return false;
        }
        return this.generateStructures;
    }

    public void setBonusChest(boolean $$0) {
        this.bonusChest = $$0;
        this.onChanged();
    }

    public boolean isBonusChest() {
        if (this.isDebug() || this.isHardcore()) {
            return false;
        }
        return this.bonusChest;
    }

    public void setSettings(WorldCreationContext $$0) {
        this.settings = $$0;
        this.updatePresetLists();
        this.onChanged();
    }

    public WorldCreationContext getSettings() {
        return this.settings;
    }

    public void updateDimensions(WorldCreationContext.DimensionsUpdater $$0) {
        this.settings = this.settings.withDimensions($$0);
        this.onChanged();
    }

    protected boolean tryUpdateDataConfiguration(WorldDataConfiguration $$0) {
        WorldDataConfiguration $$1 = this.settings.dataConfiguration();
        if ($$1.dataPacks().getEnabled().equals($$0.dataPacks().getEnabled()) && $$1.enabledFeatures().equals($$0.enabledFeatures())) {
            this.settings = new WorldCreationContext(this.settings.options(), this.settings.datapackDimensions(), this.settings.selectedDimensions(), this.settings.worldgenRegistries(), this.settings.dataPackResources(), $$0, this.settings.initialWorldCreationOptions());
            return true;
        }
        return false;
    }

    public boolean isDebug() {
        return this.settings.selectedDimensions().isDebug();
    }

    public void setWorldType(WorldTypeEntry $$0) {
        this.worldType = $$0;
        Holder<WorldPreset> $$12 = $$0.preset();
        if ($$12 != null) {
            this.updateDimensions(($$1, $$2) -> ((WorldPreset)$$12.value()).createWorldDimensions());
        }
    }

    public WorldTypeEntry getWorldType() {
        return this.worldType;
    }

    @Nullable
    public PresetEditor getPresetEditor() {
        Holder<WorldPreset> $$0 = this.getWorldType().preset();
        return $$0 != null ? PresetEditor.EDITORS.get($$0.unwrapKey()) : null;
    }

    public List<WorldTypeEntry> getNormalPresetList() {
        return this.normalPresetList;
    }

    public List<WorldTypeEntry> getAltPresetList() {
        return this.altPresetList;
    }

    private void updatePresetLists() {
        HolderLookup.RegistryLookup $$0 = this.getSettings().worldgenLoadContext().lookupOrThrow(Registries.WORLD_PRESET);
        this.normalPresetList.clear();
        this.normalPresetList.addAll(WorldCreationUiState.getNonEmptyList((Registry<WorldPreset>)$$0, WorldPresetTags.NORMAL).orElseGet(() -> WorldCreationUiState.lambda$updatePresetLists$9((Registry)$$0)));
        this.altPresetList.clear();
        this.altPresetList.addAll((Collection<WorldTypeEntry>)WorldCreationUiState.getNonEmptyList((Registry<WorldPreset>)$$0, WorldPresetTags.EXTENDED).orElse(this.normalPresetList));
        Holder<WorldPreset> $$1 = this.worldType.preset();
        if ($$1 != null) {
            boolean $$3;
            WorldTypeEntry $$2 = WorldCreationUiState.findPreset(this.getSettings(), $$1.unwrapKey()).map(WorldTypeEntry::new).orElse((WorldTypeEntry)((Object)this.normalPresetList.getFirst()));
            boolean bl = $$3 = PresetEditor.EDITORS.get($$1.unwrapKey()) != null;
            if ($$3) {
                this.worldType = $$2;
            } else {
                this.setWorldType($$2);
            }
        }
    }

    private static Optional<Holder<WorldPreset>> findPreset(WorldCreationContext $$0, Optional<ResourceKey<WorldPreset>> $$12) {
        return $$12.flatMap($$1 -> $$0.worldgenLoadContext().lookupOrThrow(Registries.WORLD_PRESET).get((ResourceKey)$$1));
    }

    private static Optional<List<WorldTypeEntry>> getNonEmptyList(Registry<WorldPreset> $$02, TagKey<WorldPreset> $$1) {
        return $$02.get($$1).map($$0 -> $$0.stream().map(WorldTypeEntry::new).toList()).filter($$0 -> !$$0.isEmpty());
    }

    public void setGameRules(GameRules $$0) {
        this.gameRules = $$0;
        this.onChanged();
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    private static /* synthetic */ List lambda$updatePresetLists$9(Registry $$0) {
        return $$0.listElements().map(WorldTypeEntry::new).toList();
    }

    public static final class SelectedGameMode
    extends Enum<SelectedGameMode> {
        public static final /* enum */ SelectedGameMode SURVIVAL = new SelectedGameMode("survival", GameType.SURVIVAL);
        public static final /* enum */ SelectedGameMode HARDCORE = new SelectedGameMode("hardcore", GameType.SURVIVAL);
        public static final /* enum */ SelectedGameMode CREATIVE = new SelectedGameMode("creative", GameType.CREATIVE);
        public static final /* enum */ SelectedGameMode DEBUG = new SelectedGameMode("spectator", GameType.SPECTATOR);
        public final GameType gameType;
        public final Component displayName;
        private final Component info;
        private static final /* synthetic */ SelectedGameMode[] $VALUES;

        public static SelectedGameMode[] values() {
            return (SelectedGameMode[])$VALUES.clone();
        }

        public static SelectedGameMode valueOf(String $$0) {
            return Enum.valueOf(SelectedGameMode.class, $$0);
        }

        private SelectedGameMode(String $$0, GameType $$1) {
            this.gameType = $$1;
            this.displayName = Component.translatable("selectWorld.gameMode." + $$0);
            this.info = Component.translatable("selectWorld.gameMode." + $$0 + ".info");
        }

        public Component getInfo() {
            return this.info;
        }

        private static /* synthetic */ SelectedGameMode[] b() {
            return new SelectedGameMode[]{SURVIVAL, HARDCORE, CREATIVE, DEBUG};
        }

        static {
            $VALUES = SelectedGameMode.b();
        }
    }

    public record WorldTypeEntry(@Nullable Holder<WorldPreset> preset) {
        private static final Component CUSTOM_WORLD_DESCRIPTION = Component.translatable("generator.custom");

        public Component describePreset() {
            return Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).map($$0 -> Component.translatable($$0.location().toLanguageKey("generator"))).orElse(CUSTOM_WORLD_DESCRIPTION);
        }

        public boolean isAmplified() {
            return Optional.ofNullable(this.preset).flatMap(Holder::unwrapKey).filter($$0 -> $$0.equals(WorldPresets.AMPLIFIED)).isPresent();
        }

        @Nullable
        public Holder<WorldPreset> preset() {
            return this.preset;
        }
    }
}


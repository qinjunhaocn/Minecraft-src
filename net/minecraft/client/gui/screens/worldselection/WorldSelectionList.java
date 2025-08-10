/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class WorldSelectionList
extends ObjectSelectionList<Entry> {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
    static final ResourceLocation ERROR_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/error_highlighted");
    static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("world_list/error");
    static final ResourceLocation MARKED_JOIN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/marked_join_highlighted");
    static final ResourceLocation MARKED_JOIN_SPRITE = ResourceLocation.withDefaultNamespace("world_list/marked_join");
    static final ResourceLocation WARNING_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/warning_highlighted");
    static final ResourceLocation WARNING_SPRITE = ResourceLocation.withDefaultNamespace("world_list/warning");
    static final ResourceLocation JOIN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/join_highlighted");
    static final ResourceLocation JOIN_SPRITE = ResourceLocation.withDefaultNamespace("world_list/join");
    static final Logger LOGGER = LogUtils.getLogger();
    static final Component FROM_NEWER_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED);
    static final Component FROM_NEWER_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED);
    static final Component SNAPSHOT_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD);
    static final Component SNAPSHOT_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD);
    static final Component WORLD_LOCKED_TOOLTIP = Component.translatable("selectWorld.locked").withStyle(ChatFormatting.RED);
    static final Component WORLD_REQUIRES_CONVERSION = Component.translatable("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED);
    static final Component INCOMPATIBLE_VERSION_TOOLTIP = Component.translatable("selectWorld.incompatible.tooltip").withStyle(ChatFormatting.RED);
    static final Component WORLD_EXPERIMENTAL = Component.translatable("selectWorld.experimental");
    private final SelectWorldScreen screen;
    private CompletableFuture<List<LevelSummary>> pendingLevels;
    @Nullable
    private List<LevelSummary> currentlyDisplayedLevels;
    private String filter;
    private final LoadingHeader loadingHeader;

    public WorldSelectionList(SelectWorldScreen $$0, Minecraft $$1, int $$2, int $$3, int $$4, int $$5, String $$6, @Nullable WorldSelectionList $$7) {
        super($$1, $$2, $$3, $$4, $$5);
        this.screen = $$0;
        this.loadingHeader = new LoadingHeader($$1);
        this.filter = $$6;
        this.pendingLevels = $$7 != null ? $$7.pendingLevels : this.loadLevels();
        this.handleNewLevels(this.pollLevelsIgnoreErrors());
    }

    @Override
    protected void clearEntries() {
        this.children().forEach(Entry::close);
        super.clearEntries();
    }

    @Nullable
    private List<LevelSummary> pollLevelsIgnoreErrors() {
        try {
            return this.pendingLevels.getNow(null);
        } catch (CancellationException | CompletionException $$0) {
            return null;
        }
    }

    void reloadWorldList() {
        this.pendingLevels = this.loadLevels();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        Optional<WorldListEntry> $$3;
        if (CommonInputs.selected($$0) && ($$3 = this.getSelectedOpt()).isPresent()) {
            if ($$3.get().canJoin()) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                $$3.get().joinWorld();
            }
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        List<LevelSummary> $$4 = this.pollLevelsIgnoreErrors();
        if ($$4 != this.currentlyDisplayedLevels) {
            this.handleNewLevels($$4);
        }
        super.renderWidget($$0, $$1, $$2, $$3);
    }

    private void handleNewLevels(@Nullable List<LevelSummary> $$0) {
        if ($$0 == null) {
            this.fillLoadingLevels();
        } else {
            this.fillLevels(this.filter, $$0);
        }
        this.currentlyDisplayedLevels = $$0;
    }

    public void updateFilter(String $$0) {
        if (this.currentlyDisplayedLevels != null && !$$0.equals(this.filter)) {
            this.fillLevels($$0, this.currentlyDisplayedLevels);
        }
        this.filter = $$0;
    }

    /*
     * WARNING - void declaration
     */
    private CompletableFuture<List<LevelSummary>> loadLevels() {
        void $$2;
        try {
            LevelStorageSource.LevelCandidates $$02 = this.minecraft.getLevelSource().findLevelCandidates();
        } catch (LevelStorageException $$1) {
            LOGGER.error("Couldn't load level list", $$1);
            this.handleLevelLoadFailure($$1.getMessageComponent());
            return CompletableFuture.completedFuture(List.of());
        }
        if ($$2.isEmpty()) {
            CreateWorldScreen.openFresh(this.minecraft, null);
            return CompletableFuture.completedFuture(List.of());
        }
        return this.minecraft.getLevelSource().loadLevelSummaries((LevelStorageSource.LevelCandidates)$$2).exceptionally($$0 -> {
            this.minecraft.delayCrash(CrashReport.forThrowable($$0, "Couldn't load level list"));
            return List.of();
        });
    }

    private void fillLevels(String $$0, List<LevelSummary> $$1) {
        this.clearEntries();
        $$0 = $$0.toLowerCase(Locale.ROOT);
        for (LevelSummary $$2 : $$1) {
            if (!this.filterAccepts($$0, $$2)) continue;
            this.addEntry(new WorldListEntry(this, $$2));
        }
        this.notifyListUpdated();
    }

    private boolean filterAccepts(String $$0, LevelSummary $$1) {
        return $$1.getLevelName().toLowerCase(Locale.ROOT).contains($$0) || $$1.getLevelId().toLowerCase(Locale.ROOT).contains($$0);
    }

    private void fillLoadingLevels() {
        this.clearEntries();
        this.addEntry(this.loadingHeader);
        this.notifyListUpdated();
    }

    private void notifyListUpdated() {
        this.refreshScrollAmount();
        this.screen.triggerImmediateNarration(true);
    }

    private void handleLevelLoadFailure(Component $$0) {
        this.minecraft.setScreen(new ErrorScreen(Component.translatable("selectWorld.unable_to_load"), $$0));
    }

    @Override
    public int getRowWidth() {
        return 270;
    }

    @Override
    public void setSelected(@Nullable Entry $$0) {
        LevelSummary levelSummary;
        super.setSelected($$0);
        if ($$0 instanceof WorldListEntry) {
            WorldListEntry $$1 = (WorldListEntry)$$0;
            levelSummary = $$1.summary;
        } else {
            levelSummary = null;
        }
        this.screen.updateButtonStatus(levelSummary);
    }

    public Optional<WorldListEntry> getSelectedOpt() {
        Entry $$0 = (Entry)this.getSelected();
        if ($$0 instanceof WorldListEntry) {
            WorldListEntry $$1 = (WorldListEntry)$$0;
            return Optional.of($$1);
        }
        return Optional.empty();
    }

    public SelectWorldScreen getScreen() {
        return this.screen;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        if (this.children().contains(this.loadingHeader)) {
            this.loadingHeader.updateNarration($$0);
            return;
        }
        super.updateWidgetNarration($$0);
    }

    public static class LoadingHeader
    extends Entry {
        private static final Component LOADING_LABEL = Component.translatable("selectWorld.loading_list");
        private final Minecraft minecraft;

        public LoadingHeader(Minecraft $$0) {
            this.minecraft = $$0;
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int $$10 = (this.minecraft.screen.width - this.minecraft.font.width(LOADING_LABEL)) / 2;
            int $$11 = $$2 + ($$5 - this.minecraft.font.lineHeight) / 2;
            $$0.drawString(this.minecraft.font, LOADING_LABEL, $$10, $$11, -1);
            String $$12 = LoadingDotsText.get(Util.getMillis());
            int $$13 = (this.minecraft.screen.width - this.minecraft.font.width($$12)) / 2;
            int $$14 = $$11 + this.minecraft.font.lineHeight;
            $$0.drawString(this.minecraft.font, $$12, $$13, $$14, -8355712);
        }

        @Override
        public Component getNarration() {
            return LOADING_LABEL;
        }
    }

    public final class WorldListEntry
    extends Entry {
        private static final int ICON_WIDTH = 32;
        private static final int ICON_HEIGHT = 32;
        private final Minecraft minecraft;
        private final SelectWorldScreen screen;
        final LevelSummary summary;
        private final FaviconTexture icon;
        @Nullable
        private Path iconFile;
        private long lastClickTime;

        public WorldListEntry(WorldSelectionList $$1, LevelSummary $$2) {
            this.minecraft = $$1.minecraft;
            this.screen = $$1.getScreen();
            this.summary = $$2;
            this.icon = FaviconTexture.forWorld(this.minecraft.getTextureManager(), $$2.getLevelId());
            this.iconFile = $$2.getIcon();
            this.validateIconFile();
            this.loadIcon();
        }

        private void validateIconFile() {
            if (this.iconFile == null) {
                return;
            }
            try {
                BasicFileAttributes $$0 = Files.readAttributes(this.iconFile, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                if ($$0.isSymbolicLink()) {
                    List<ForbiddenSymlinkInfo> $$1 = this.minecraft.directoryValidator().validateSymlink(this.iconFile);
                    if (!$$1.isEmpty()) {
                        LOGGER.warn("{}", (Object)ContentValidationException.getMessage(this.iconFile, $$1));
                        this.iconFile = null;
                    } else {
                        $$0 = Files.readAttributes(this.iconFile, BasicFileAttributes.class, new LinkOption[0]);
                    }
                }
                if (!$$0.isRegularFile()) {
                    this.iconFile = null;
                }
            } catch (NoSuchFileException $$2) {
                this.iconFile = null;
            } catch (IOException $$3) {
                LOGGER.error("could not validate symlink", $$3);
                this.iconFile = null;
            }
        }

        @Override
        public Component getNarration() {
            MutableComponent $$0 = Component.a("narrator.select.world_info", new Object[]{this.summary.getLevelName(), Component.translationArg(new Date(this.summary.getLastPlayed())), this.summary.getInfo()});
            if (this.summary.isLocked()) {
                $$0 = CommonComponents.a($$0, WORLD_LOCKED_TOOLTIP);
            }
            if (this.summary.isExperimental()) {
                $$0 = CommonComponents.a($$0, WORLD_EXPERIMENTAL);
            }
            return Component.a("narrator.select", $$0);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            Object $$10 = this.summary.getLevelName();
            Object $$11 = this.summary.getLevelId();
            long $$12 = this.summary.getLastPlayed();
            if ($$12 != -1L) {
                $$11 = (String)$$11 + " (" + DATE_FORMAT.format(Instant.ofEpochMilli($$12)) + ")";
            }
            if (StringUtils.isEmpty((CharSequence)$$10)) {
                $$10 = I18n.a("selectWorld.world", new Object[0]) + " " + ($$1 + 1);
            }
            Component $$13 = this.summary.getInfo();
            $$0.drawString(this.minecraft.font, (String)$$10, $$3 + 32 + 3, $$2 + 1, -1);
            $$0.drawString(this.minecraft.font, (String)$$11, $$3 + 32 + 3, $$2 + this.minecraft.font.lineHeight + 3, -8355712);
            $$0.drawString(this.minecraft.font, $$13, $$3 + 32 + 3, $$2 + this.minecraft.font.lineHeight + this.minecraft.font.lineHeight + 3, -8355712);
            $$0.blit(RenderPipelines.GUI_TEXTURED, this.icon.textureLocation(), $$3, $$2, 0.0f, 0.0f, 32, 32, 32, 32);
            if (this.minecraft.options.touchscreen().get().booleanValue() || $$8) {
                ResourceLocation $$19;
                $$0.fill($$3, $$2, $$3 + 32, $$2 + 32, -1601138544);
                int $$14 = $$6 - $$3;
                boolean $$15 = $$14 < 32;
                ResourceLocation $$16 = $$15 ? JOIN_HIGHLIGHTED_SPRITE : JOIN_SPRITE;
                ResourceLocation $$17 = $$15 ? WARNING_HIGHLIGHTED_SPRITE : WARNING_SPRITE;
                ResourceLocation $$18 = $$15 ? ERROR_HIGHLIGHTED_SPRITE : ERROR_SPRITE;
                ResourceLocation resourceLocation = $$19 = $$15 ? MARKED_JOIN_HIGHLIGHTED_SPRITE : MARKED_JOIN_SPRITE;
                if (this.summary instanceof LevelSummary.SymlinkLevelSummary || this.summary instanceof LevelSummary.CorruptedLevelSummary) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$18, $$3, $$2, 32, 32);
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$19, $$3, $$2, 32, 32);
                    return;
                }
                if (this.summary.isLocked()) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$18, $$3, $$2, 32, 32);
                    if ($$15) {
                        $$0.setTooltipForNextFrame(this.minecraft.font.split(WORLD_LOCKED_TOOLTIP, 175), $$6, $$7);
                    }
                } else if (this.summary.requiresManualConversion()) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$18, $$3, $$2, 32, 32);
                    if ($$15) {
                        $$0.setTooltipForNextFrame(this.minecraft.font.split(WORLD_REQUIRES_CONVERSION, 175), $$6, $$7);
                    }
                } else if (!this.summary.isCompatible()) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$18, $$3, $$2, 32, 32);
                    if ($$15) {
                        $$0.setTooltipForNextFrame(this.minecraft.font.split(INCOMPATIBLE_VERSION_TOOLTIP, 175), $$6, $$7);
                    }
                } else if (this.summary.shouldBackup()) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$19, $$3, $$2, 32, 32);
                    if (this.summary.isDowngrade()) {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$18, $$3, $$2, 32, 32);
                        if ($$15) {
                            $$0.setTooltipForNextFrame(ImmutableList.of(FROM_NEWER_TOOLTIP_1.getVisualOrderText(), FROM_NEWER_TOOLTIP_2.getVisualOrderText()), $$6, $$7);
                        }
                    } else if (!SharedConstants.getCurrentVersion().stable()) {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$17, $$3, $$2, 32, 32);
                        if ($$15) {
                            $$0.setTooltipForNextFrame(ImmutableList.of(SNAPSHOT_TOOLTIP_1.getVisualOrderText(), SNAPSHOT_TOOLTIP_2.getVisualOrderText()), $$6, $$7);
                        }
                    }
                } else {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$16, $$3, $$2, 32, 32);
                }
            }
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if (!this.summary.primaryActionActive()) {
                return true;
            }
            WorldSelectionList.this.setSelected(this);
            if ($$0 - (double)WorldSelectionList.this.getRowLeft() <= 32.0 || Util.getMillis() - this.lastClickTime < 250L) {
                if (this.canJoin()) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    this.joinWorld();
                }
                return true;
            }
            this.lastClickTime = Util.getMillis();
            return super.mouseClicked($$0, $$1, $$2);
        }

        public boolean canJoin() {
            return this.summary.primaryActionActive();
        }

        public void joinWorld() {
            if (!this.summary.primaryActionActive()) {
                return;
            }
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
                this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(this.screen)));
                return;
            }
            this.minecraft.createWorldOpenFlows().openWorld(this.summary.getLevelId(), () -> {
                WorldSelectionList.this.reloadWorldList();
                this.minecraft.setScreen(this.screen);
            });
        }

        public void deleteWorld() {
            this.minecraft.setScreen(new ConfirmScreen($$0 -> {
                if ($$0) {
                    this.minecraft.setScreen(new ProgressScreen(true));
                    this.doDeleteWorld();
                }
                this.minecraft.setScreen(this.screen);
            }, Component.translatable("selectWorld.deleteQuestion"), Component.a("selectWorld.deleteWarning", this.summary.getLevelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
        }

        public void doDeleteWorld() {
            LevelStorageSource $$0 = this.minecraft.getLevelSource();
            String $$1 = this.summary.getLevelId();
            try (LevelStorageSource.LevelStorageAccess $$2 = $$0.createAccess($$1);){
                $$2.deleteLevel();
            } catch (IOException $$3) {
                SystemToast.onWorldDeleteFailure(this.minecraft, $$1);
                LOGGER.error("Failed to delete world {}", (Object)$$1, (Object)$$3);
            }
            WorldSelectionList.this.reloadWorldList();
        }

        /*
         * WARNING - void declaration
         */
        public void editWorld() {
            void $$7;
            void $$4;
            this.queueLoadScreen();
            String $$0 = this.summary.getLevelId();
            try {
                LevelStorageSource.LevelStorageAccess $$1 = this.minecraft.getLevelSource().validateAndCreateAccess($$0);
            } catch (IOException $$2) {
                SystemToast.onWorldAccessFailure(this.minecraft, $$0);
                LOGGER.error("Failed to access level {}", (Object)$$0, (Object)$$2);
                WorldSelectionList.this.reloadWorldList();
                return;
            } catch (ContentValidationException $$3) {
                LOGGER.warn("{}", (Object)$$3.getMessage());
                this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(this.screen)));
                return;
            }
            try {
                EditWorldScreen $$5 = EditWorldScreen.create(this.minecraft, (LevelStorageSource.LevelStorageAccess)$$4, arg_0 -> this.lambda$editWorld$4((LevelStorageSource.LevelStorageAccess)$$4, arg_0));
            } catch (IOException | NbtException | ReportedNbtException $$6) {
                $$4.safeClose();
                SystemToast.onWorldAccessFailure(this.minecraft, $$0);
                LOGGER.error("Failed to load world data {}", (Object)$$0, (Object)$$6);
                WorldSelectionList.this.reloadWorldList();
                return;
            }
            this.minecraft.setScreen((Screen)$$7);
        }

        public void recreateWorld() {
            this.queueLoadScreen();
            try (LevelStorageSource.LevelStorageAccess $$0 = this.minecraft.getLevelSource().validateAndCreateAccess(this.summary.getLevelId());){
                Pair<LevelSettings, WorldCreationContext> $$1 = this.minecraft.createWorldOpenFlows().recreateWorldData($$0);
                LevelSettings $$2 = (LevelSettings)$$1.getFirst();
                WorldCreationContext $$32 = (WorldCreationContext)((Object)$$1.getSecond());
                Path $$4 = CreateWorldScreen.createTempDataPackDirFromExistingWorld($$0.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
                $$32.validate();
                if ($$32.options().isOldCustomizedWorld()) {
                    this.minecraft.setScreen(new ConfirmScreen($$3 -> this.minecraft.setScreen($$3 ? CreateWorldScreen.createFromExisting(this.minecraft, this.screen, $$2, $$32, $$4) : this.screen), Component.translatable("selectWorld.recreate.customized.title"), Component.translatable("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
                } else {
                    this.minecraft.setScreen(CreateWorldScreen.createFromExisting(this.minecraft, this.screen, $$2, $$32, $$4));
                }
            } catch (ContentValidationException $$5) {
                LOGGER.warn("{}", (Object)$$5.getMessage());
                this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(this.screen)));
            } catch (Exception $$6) {
                LOGGER.error("Unable to recreate world", $$6);
                this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.translatable("selectWorld.recreate.error.title"), Component.translatable("selectWorld.recreate.error.text")));
            }
        }

        private void queueLoadScreen() {
            this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        }

        private void loadIcon() {
            boolean $$0;
            boolean bl = $$0 = this.iconFile != null && Files.isRegularFile(this.iconFile, new LinkOption[0]);
            if ($$0) {
                try (InputStream $$1 = Files.newInputStream(this.iconFile, new OpenOption[0]);){
                    this.icon.upload(NativeImage.read($$1));
                } catch (Throwable $$2) {
                    LOGGER.error("Invalid icon for world {}", (Object)this.summary.getLevelId(), (Object)$$2);
                    this.iconFile = null;
                }
            } else {
                this.icon.clear();
            }
        }

        @Override
        public void close() {
            this.icon.close();
        }

        public String getLevelName() {
            return this.summary.getLevelName();
        }

        private /* synthetic */ void lambda$editWorld$4(LevelStorageSource.LevelStorageAccess $$0, boolean $$1) {
            $$0.safeClose();
            if ($$1) {
                WorldSelectionList.this.reloadWorldList();
            }
            this.minecraft.setScreen(this.screen);
        }
    }

    public static abstract class Entry
    extends ObjectSelectionList.Entry<Entry>
    implements AutoCloseable {
        @Override
        public void close() {
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackDetector;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class PackSelectionScreen
extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component AVAILABLE_TITLE = Component.translatable("pack.available.title");
    private static final Component SELECTED_TITLE = Component.translatable("pack.selected.title");
    private static final Component OPEN_PACK_FOLDER_TITLE = Component.translatable("pack.openFolder");
    private static final int LIST_WIDTH = 200;
    private static final Component DRAG_AND_DROP = Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY);
    private static final Component DIRECTORY_BUTTON_TOOLTIP = Component.translatable("pack.folderInfo");
    private static final int RELOAD_COOLDOWN = 20;
    private static final ResourceLocation DEFAULT_ICON = ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final PackSelectionModel model;
    @Nullable
    private Watcher watcher;
    private long ticksToReload;
    private TransferableSelectionList availablePackList;
    private TransferableSelectionList selectedPackList;
    private final Path packDir;
    private Button doneButton;
    private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

    public PackSelectionScreen(PackRepository $$0, Consumer<PackRepository> $$1, Path $$2, Component $$3) {
        super($$3);
        this.model = new PackSelectionModel(this::populateLists, this::getPackIcon, $$0, $$1);
        this.packDir = $$2;
        this.watcher = Watcher.create($$2);
    }

    @Override
    public void onClose() {
        this.model.commit();
        this.closeWatcher();
    }

    private void closeWatcher() {
        if (this.watcher != null) {
            try {
                this.watcher.close();
                this.watcher = null;
            } catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void init() {
        LinearLayout $$02 = this.layout.addToHeader(LinearLayout.vertical().spacing(5));
        $$02.defaultCellSetting().alignHorizontallyCenter();
        $$02.addChild(new StringWidget(this.getTitle(), this.font));
        $$02.addChild(new StringWidget(DRAG_AND_DROP, this.font));
        this.availablePackList = this.addRenderableWidget(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, AVAILABLE_TITLE));
        this.selectedPackList = this.addRenderableWidget(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, SELECTED_TITLE));
        LinearLayout $$12 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        $$12.addChild(Button.builder(OPEN_PACK_FOLDER_TITLE, $$0 -> Util.getPlatform().openPath(this.packDir)).tooltip(Tooltip.create(DIRECTORY_BUTTON_TOOLTIP)).build());
        this.doneButton = $$12.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).build());
        this.reload();
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.availablePackList.updateSize(200, this.layout);
        this.availablePackList.setX(this.width / 2 - 15 - 200);
        this.selectedPackList.updateSize(200, this.layout);
        this.selectedPackList.setX(this.width / 2 + 15);
    }

    @Override
    public void tick() {
        if (this.watcher != null) {
            try {
                if (this.watcher.pollForChanges()) {
                    this.ticksToReload = 20L;
                }
            } catch (IOException $$0) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.packDir);
                this.closeWatcher();
            }
        }
        if (this.ticksToReload > 0L && --this.ticksToReload == 0L) {
            this.reload();
        }
    }

    private void populateLists() {
        this.updateList(this.selectedPackList, this.model.getSelected());
        this.updateList(this.availablePackList, this.model.getUnselected());
        this.doneButton.active = !this.selectedPackList.children().isEmpty();
    }

    private void updateList(TransferableSelectionList $$0, Stream<PackSelectionModel.Entry> $$1) {
        $$0.children().clear();
        TransferableSelectionList.PackEntry $$22 = (TransferableSelectionList.PackEntry)$$0.getSelected();
        String $$3 = $$22 == null ? "" : $$22.getPackId();
        $$0.setSelected(null);
        $$1.forEach($$2 -> {
            TransferableSelectionList.PackEntry $$3 = new TransferableSelectionList.PackEntry(this.minecraft, $$0, (PackSelectionModel.Entry)$$2);
            $$0.children().add($$3);
            if ($$2.getId().equals($$3)) {
                $$0.setSelected($$3);
            }
        });
    }

    public void updateFocus(TransferableSelectionList $$0) {
        TransferableSelectionList $$1 = this.selectedPackList == $$0 ? this.availablePackList : this.selectedPackList;
        this.changeFocus(ComponentPath.a($$1.getFirstElement(), new ContainerEventHandler[]{$$1, this}));
    }

    public void clearSelected() {
        this.selectedPackList.setSelected(null);
        this.availablePackList.setSelected(null);
    }

    private void reload() {
        this.model.findNewPacks();
        this.populateLists();
        this.ticksToReload = 0L;
        this.packIcons.clear();
    }

    protected static void copyPacks(Minecraft $$0, List<Path> $$1, Path $$22) {
        MutableBoolean $$3 = new MutableBoolean();
        $$1.forEach($$2 -> {
            try (Stream<Path> $$32 = Files.walk($$2, new FileVisitOption[0]);){
                $$32.forEach($$3 -> {
                    try {
                        Util.copyBetweenDirs($$2.getParent(), $$22, $$3);
                    } catch (IOException $$4) {
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", $$3, $$22, $$4);
                        $$3.setTrue();
                    }
                });
            } catch (IOException $$4) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", $$2, (Object)$$22);
                $$3.setTrue();
            }
        });
        if ($$3.isTrue()) {
            SystemToast.onPackCopyFailure($$0, $$22.toString());
        }
    }

    @Override
    public void onFilesDrop(List<Path> $$0) {
        String $$12 = PackSelectionScreen.extractPackNames($$0).collect(Collectors.joining(", "));
        this.minecraft.setScreen(new ConfirmScreen($$1 -> {
            if ($$1) {
                ArrayList<Path> $$2 = new ArrayList<Path>($$0.size());
                HashSet<Path> $$3 = new HashSet<Path>($$0);
                PackDetector<Path> $$4 = new PackDetector<Path>(this, this.minecraft.directoryValidator()){

                    @Override
                    protected Path createZipPack(Path $$0) {
                        return $$0;
                    }

                    @Override
                    protected Path createDirectoryPack(Path $$0) {
                        return $$0;
                    }

                    @Override
                    protected /* synthetic */ Object createDirectoryPack(Path path) throws IOException {
                        return this.createDirectoryPack(path);
                    }

                    @Override
                    protected /* synthetic */ Object createZipPack(Path path) throws IOException {
                        return this.createZipPack(path);
                    }
                };
                ArrayList<ForbiddenSymlinkInfo> $$5 = new ArrayList<ForbiddenSymlinkInfo>();
                for (Path $$6 : $$0) {
                    try {
                        Path $$7 = (Path)$$4.detectPackResources($$6, $$5);
                        if ($$7 == null) {
                            LOGGER.warn("Path {} does not seem like pack", (Object)$$6);
                            continue;
                        }
                        $$2.add($$7);
                        $$3.remove($$7);
                    } catch (IOException $$8) {
                        LOGGER.warn("Failed to check {} for packs", (Object)$$6, (Object)$$8);
                    }
                }
                if (!$$5.isEmpty()) {
                    this.minecraft.setScreen(NoticeWithLinkScreen.createPackSymlinkWarningScreen(() -> this.minecraft.setScreen(this)));
                    return;
                }
                if (!$$2.isEmpty()) {
                    PackSelectionScreen.copyPacks(this.minecraft, $$2, this.packDir);
                    this.reload();
                }
                if (!$$3.isEmpty()) {
                    String $$9 = PackSelectionScreen.extractPackNames($$3).collect(Collectors.joining(", "));
                    this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this), Component.translatable("pack.dropRejected.title"), Component.a("pack.dropRejected.message", $$9)));
                    return;
                }
            }
            this.minecraft.setScreen(this);
        }, Component.translatable("pack.dropConfirm"), Component.literal($$12)));
    }

    private static Stream<String> extractPackNames(Collection<Path> $$0) {
        return $$0.stream().map(Path::getFileName).map(Path::toString);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private ResourceLocation loadPackIcon(TextureManager $$0, Pack $$1) {
        try (PackResources $$2 = $$1.open();){
            ResourceLocation resourceLocation;
            block16: {
                IoSupplier<InputStream> $$3 = $$2.a("pack.png");
                if ($$3 == null) {
                    ResourceLocation resourceLocation2 = DEFAULT_ICON;
                    return resourceLocation2;
                }
                String $$4 = $$1.getId();
                ResourceLocation $$5 = ResourceLocation.withDefaultNamespace("pack/" + Util.sanitizeName($$4, ResourceLocation::b) + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars($$4)) + "/icon");
                InputStream $$6 = $$3.get();
                try {
                    NativeImage $$7 = NativeImage.read($$6);
                    $$0.register($$5, new DynamicTexture($$5::toString, $$7));
                    resourceLocation = $$5;
                    if ($$6 == null) break block16;
                } catch (Throwable throwable) {
                    if ($$6 != null) {
                        try {
                            $$6.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                $$6.close();
            }
            return resourceLocation;
        } catch (Exception $$8) {
            LOGGER.warn("Failed to load icon from pack {}", (Object)$$1.getId(), (Object)$$8);
            return DEFAULT_ICON;
        }
    }

    private ResourceLocation getPackIcon(Pack $$0) {
        return this.packIcons.computeIfAbsent($$0.getId(), $$1 -> this.loadPackIcon(this.minecraft.getTextureManager(), $$0));
    }

    static class Watcher
    implements AutoCloseable {
        private final WatchService watcher;
        private final Path packPath;

        public Watcher(Path $$0) throws IOException {
            this.packPath = $$0;
            this.watcher = $$0.getFileSystem().newWatchService();
            try {
                this.watchDir($$0);
                try (DirectoryStream<Path> $$1 = Files.newDirectoryStream($$0);){
                    for (Path $$2 : $$1) {
                        if (!Files.isDirectory($$2, LinkOption.NOFOLLOW_LINKS)) continue;
                        this.watchDir($$2);
                    }
                }
            } catch (Exception $$3) {
                this.watcher.close();
                throw $$3;
            }
        }

        @Nullable
        public static Watcher create(Path $$0) {
            try {
                return new Watcher($$0);
            } catch (IOException $$1) {
                LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)$$0, (Object)$$1);
                return null;
            }
        }

        private void watchDir(Path $$0) throws IOException {
            $$0.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean pollForChanges() throws IOException {
            WatchKey $$1;
            boolean $$0 = false;
            while (($$1 = this.watcher.poll()) != null) {
                List<WatchEvent<?>> $$2 = $$1.pollEvents();
                for (WatchEvent<?> $$3 : $$2) {
                    Path $$4;
                    $$0 = true;
                    if ($$1.watchable() != this.packPath || $$3.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory($$4 = this.packPath.resolve((Path)$$3.context()), LinkOption.NOFOLLOW_LINKS)) continue;
                    this.watchDir($$4);
                }
                $$1.reset();
            }
            return $$0;
        }

        @Override
        public void close() throws IOException {
            this.watcher.close();
        }
    }
}


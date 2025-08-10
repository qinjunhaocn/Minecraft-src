/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.MatchException
 *  java.lang.Record
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.gui.screens;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import java.lang.runtime.SwitchBootstraps;
import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.slf4j.Logger;

public abstract class Screen
extends AbstractContainerEventHandler
implements Renderable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component USAGE_NARRATION = Component.translatable("narrator.screen.usage");
    public static final ResourceLocation MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/menu_background.png");
    public static final ResourceLocation HEADER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/header_separator.png");
    public static final ResourceLocation FOOTER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/footer_separator.png");
    private static final ResourceLocation INWORLD_MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_background.png");
    public static final ResourceLocation INWORLD_HEADER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/inworld_header_separator.png");
    public static final ResourceLocation INWORLD_FOOTER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/inworld_footer_separator.png");
    protected static final float FADE_IN_TIME = 2000.0f;
    protected final Component title;
    private final List<GuiEventListener> children = Lists.newArrayList();
    private final List<NarratableEntry> narratables = Lists.newArrayList();
    @Nullable
    protected Minecraft minecraft;
    private boolean initialized;
    public int width;
    public int height;
    private final List<Renderable> renderables = Lists.newArrayList();
    protected Font font;
    private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME;
    private static final long NARRATE_DELAY_NARRATOR_ENABLED;
    private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
    private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
    private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
    private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
    private long narrationSuppressTime = Long.MIN_VALUE;
    private long nextNarrationTime = Long.MAX_VALUE;
    @Nullable
    protected CycleButton<NarratorStatus> narratorButton;
    @Nullable
    private NarratableEntry lastNarratable;
    protected final Executor screenExecutor = $$0 -> this.minecraft.execute(() -> {
        if (this.minecraft.screen == this) {
            $$0.run();
        }
    });

    protected Screen(Component $$02) {
        this.title = $$02;
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getNarrationMessage() {
        return this.getTitle();
    }

    public final void renderWithTooltip(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        $$0.nextStratum();
        this.renderBackground($$0, $$1, $$2, $$3);
        $$0.nextStratum();
        this.render($$0, $$1, $$2, $$3);
        $$0.renderDeferredTooltip();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        for (Renderable $$4 : this.renderables) {
            $$4.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        FocusNavigationEvent.TabNavigation $$3;
        if ($$0 == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        switch ($$0) {
            case 263: {
                Record record = this.createArrowEvent(ScreenDirection.LEFT);
                break;
            }
            case 262: {
                Record record = this.createArrowEvent(ScreenDirection.RIGHT);
                break;
            }
            case 265: {
                Record record = this.createArrowEvent(ScreenDirection.UP);
                break;
            }
            case 264: {
                Record record = this.createArrowEvent(ScreenDirection.DOWN);
                break;
            }
            case 258: {
                Record record = this.createTabEvent();
                break;
            }
            default: {
                Record record = $$3 = null;
            }
        }
        if ($$3 != null) {
            ComponentPath $$4 = super.nextFocusPath($$3);
            if ($$4 == null && $$3 instanceof FocusNavigationEvent.TabNavigation) {
                this.clearFocus();
                $$4 = super.nextFocusPath($$3);
            }
            if ($$4 != null) {
                this.changeFocus($$4);
            }
        }
        return false;
    }

    private FocusNavigationEvent.TabNavigation createTabEvent() {
        boolean $$0 = !Screen.hasShiftDown();
        return new FocusNavigationEvent.TabNavigation($$0);
    }

    private FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection $$0) {
        return new FocusNavigationEvent.ArrowNavigation($$0);
    }

    protected void setInitialFocus() {
        FocusNavigationEvent.TabNavigation $$0;
        ComponentPath $$1;
        if (this.minecraft.getLastInputType().isKeyboard() && ($$1 = super.nextFocusPath($$0 = new FocusNavigationEvent.TabNavigation(true))) != null) {
            this.changeFocus($$1);
        }
    }

    protected void setInitialFocus(GuiEventListener $$0) {
        ComponentPath $$1 = ComponentPath.path(this, $$0.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
        if ($$1 != null) {
            this.changeFocus($$1);
        }
    }

    public void clearFocus() {
        ComponentPath $$0 = this.getCurrentFocusPath();
        if ($$0 != null) {
            $$0.applyFocus(false);
        }
    }

    @VisibleForTesting
    protected void changeFocus(ComponentPath $$0) {
        this.clearFocus();
        $$0.applyFocus(true);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        this.minecraft.setScreen(null);
    }

    protected <T extends GuiEventListener & Renderable> T addRenderableWidget(T $$0) {
        this.renderables.add($$0);
        return this.addWidget($$0);
    }

    protected <T extends Renderable> T addRenderableOnly(T $$0) {
        this.renderables.add($$0);
        return $$0;
    }

    protected <T extends GuiEventListener & NarratableEntry> T addWidget(T $$0) {
        this.children.add($$0);
        this.narratables.add($$0);
        return $$0;
    }

    protected void removeWidget(GuiEventListener $$0) {
        if ($$0 instanceof Renderable) {
            this.renderables.remove((Renderable)((Object)$$0));
        }
        if ($$0 instanceof NarratableEntry) {
            this.narratables.remove((NarratableEntry)((Object)$$0));
        }
        this.children.remove($$0);
    }

    protected void clearWidgets() {
        this.renderables.clear();
        this.children.clear();
        this.narratables.clear();
    }

    public static List<Component> getTooltipFromItem(Minecraft $$0, ItemStack $$1) {
        return $$1.getTooltipLines(Item.TooltipContext.of($$0.level), $$0.player, $$0.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
    }

    protected void insertText(String $$0, boolean $$1) {
    }

    public boolean handleComponentClicked(Style $$0) {
        ClickEvent $$1 = $$0.getClickEvent();
        if (Screen.hasShiftDown()) {
            if ($$0.getInsertion() != null) {
                this.insertText($$0.getInsertion(), false);
            }
        } else if ($$1 != null) {
            this.handleClickEvent(this.minecraft, $$1);
            return true;
        }
        return false;
    }

    protected void handleClickEvent(Minecraft $$0, ClickEvent $$1) {
        Screen.defaultHandleGameClickEvent($$1, $$0, this);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected static void defaultHandleGameClickEvent(ClickEvent $$0, Minecraft $$1, @Nullable Screen $$2) {
        LocalPlayer $$3 = Objects.requireNonNull($$1.player, "Player not available");
        ClickEvent clickEvent = $$0;
        Objects.requireNonNull(clickEvent);
        ClickEvent clickEvent2 = clickEvent;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.RunCommand.class, ClickEvent.ShowDialog.class, ClickEvent.Custom.class}, (Object)clickEvent2, (int)n)) {
            case 0: {
                String $$4;
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent2;
                try {
                    String string;
                    $$4 = string = runCommand.command();
                } catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
                Screen.clickCommandAction($$3, $$4, $$2);
                return;
            }
            case 1: {
                ClickEvent.ShowDialog $$5 = (ClickEvent.ShowDialog)clickEvent2;
                $$3.connection.showDialog($$5.dialog(), $$2);
                return;
            }
            case 2: {
                ClickEvent.Custom $$6 = (ClickEvent.Custom)clickEvent2;
                $$3.connection.send(new ServerboundCustomClickActionPacket($$6.id(), $$6.payload()));
                if ($$1.screen == $$2) return;
                $$1.setScreen($$2);
                return;
            }
        }
        Screen.defaultHandleClickEvent($$0, $$1, $$2);
    }

    /*
     * Loose catch block
     */
    protected static void defaultHandleClickEvent(ClickEvent $$0, Minecraft $$1, @Nullable Screen $$2) {
        block12: {
            boolean $$7;
            ClickEvent clickEvent = $$0;
            Objects.requireNonNull(clickEvent);
            ClickEvent clickEvent2 = clickEvent;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.OpenUrl.class, ClickEvent.OpenFile.class, ClickEvent.SuggestCommand.class, ClickEvent.CopyToClipboard.class}, (Object)clickEvent2, (int)n)) {
                case 0: {
                    URI uRI;
                    ClickEvent.OpenUrl openUrl = (ClickEvent.OpenUrl)clickEvent2;
                    URI $$3 = uRI = openUrl.uri();
                    Screen.clickUrlAction($$1, $$2, $$3);
                    boolean bl = false;
                    break;
                }
                case 1: {
                    ClickEvent.OpenFile $$4 = (ClickEvent.OpenFile)clickEvent2;
                    Util.getPlatform().openFile($$4.file());
                    boolean bl = true;
                    break;
                }
                case 2: {
                    Object object;
                    ClickEvent.SuggestCommand suggestCommand = (ClickEvent.SuggestCommand)clickEvent2;
                    Object $$5 = object = suggestCommand.command();
                    if ($$2 != null) {
                        $$2.insertText((String)$$5, true);
                    }
                    boolean bl = true;
                    break;
                }
                case 3: {
                    String string;
                    Object object = (ClickEvent.CopyToClipboard)clickEvent2;
                    String $$6 = string = ((ClickEvent.CopyToClipboard)object).value();
                    $$1.keyboardHandler.setClipboard($$6);
                    boolean bl = true;
                    break;
                }
                default: {
                    LOGGER.error("Don't know how to handle {}", (Object)$$0);
                    boolean bl = $$7 = true;
                }
            }
            if ($$7 && $$1.screen != $$2) {
                $$1.setScreen($$2);
            }
            break block12;
            catch (Throwable throwable) {
                throw new MatchException(throwable.toString(), throwable);
            }
        }
    }

    protected static boolean clickUrlAction(Minecraft $$0, @Nullable Screen $$1, URI $$2) {
        if (!$$0.options.chatLinks().get().booleanValue()) {
            return false;
        }
        if ($$0.options.chatLinksPrompt().get().booleanValue()) {
            $$0.setScreen(new ConfirmLinkScreen($$3 -> {
                if ($$3) {
                    Util.getPlatform().openUri($$2);
                }
                $$0.setScreen($$1);
            }, $$2.toString(), false));
        } else {
            Util.getPlatform().openUri($$2);
        }
        return true;
    }

    protected static void clickCommandAction(LocalPlayer $$0, String $$1, @Nullable Screen $$2) {
        $$0.connection.sendUnattendedCommand(Commands.trimOptionalPrefix($$1), $$2);
    }

    public final void init(Minecraft $$0, int $$1, int $$2) {
        this.minecraft = $$0;
        this.font = $$0.font;
        this.width = $$1;
        this.height = $$2;
        if (!this.initialized) {
            this.init();
            this.setInitialFocus();
        } else {
            this.repositionElements();
        }
        this.initialized = true;
        this.triggerImmediateNarration(false);
        this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
    }

    protected void rebuildWidgets() {
        this.clearWidgets();
        this.clearFocus();
        this.init();
        this.setInitialFocus();
    }

    protected void fadeWidgets(float $$0) {
        for (GuiEventListener guiEventListener : this.children()) {
            if (!(guiEventListener instanceof AbstractWidget)) continue;
            AbstractWidget $$2 = (AbstractWidget)guiEventListener;
            $$2.setAlpha($$0);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    protected void init() {
    }

    public void tick() {
    }

    public void removed() {
    }

    public void added() {
    }

    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.minecraft.level == null) {
            this.renderPanorama($$0, $$3);
        }
        this.renderBlurredBackground($$0);
        this.renderMenuBackground($$0);
    }

    protected void renderBlurredBackground(GuiGraphics $$0) {
        float $$1 = this.minecraft.options.getMenuBackgroundBlurriness();
        if ($$1 >= 1.0f) {
            $$0.blurBeforeThisStratum();
        }
    }

    protected void renderPanorama(GuiGraphics $$0, float $$1) {
        this.minecraft.gameRenderer.getPanorama().render($$0, this.width, this.height, true);
    }

    protected void renderMenuBackground(GuiGraphics $$0) {
        this.renderMenuBackground($$0, 0, 0, this.width, this.height);
    }

    protected void renderMenuBackground(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        Screen.renderMenuBackgroundTexture($$0, this.minecraft.level == null ? MENU_BACKGROUND : INWORLD_MENU_BACKGROUND, $$1, $$2, 0.0f, 0.0f, $$3, $$4);
    }

    public static void renderMenuBackgroundTexture(GuiGraphics $$0, ResourceLocation $$1, int $$2, int $$3, float $$4, float $$5, int $$6, int $$7) {
        int $$8 = 32;
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$1, $$2, $$3, $$4, $$5, $$6, $$7, 32, 32);
    }

    public void renderTransparentBackground(GuiGraphics $$0) {
        $$0.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
    }

    public boolean isPauseScreen() {
        return true;
    }

    public static boolean hasControlDown() {
        if (Minecraft.ON_OSX) {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
        }
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
    }

    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
    }

    public static boolean isCut(int $$0) {
        return $$0 == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int $$0) {
        return $$0 == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int $$0) {
        return $$0 == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int $$0) {
        return $$0 == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    protected void repositionElements() {
        this.rebuildWidgets();
    }

    public void resize(Minecraft $$0, int $$1, int $$2) {
        this.width = $$1;
        this.height = $$2;
        this.repositionElements();
    }

    public void fillCrashDetails(CrashReport $$0) {
        CrashReportCategory $$1 = $$0.addCategory("Affected screen", 1);
        $$1.setDetail("Screen name", () -> this.getClass().getCanonicalName());
    }

    protected boolean a(String $$0, char $$1, int $$2) {
        int $$3 = $$0.indexOf(58);
        int $$4 = $$0.indexOf(47);
        if ($$1 == ':') {
            return ($$4 == -1 || $$2 <= $$4) && $$3 == -1;
        }
        if ($$1 == '/') {
            return $$2 > $$3;
        }
        return $$1 == '_' || $$1 == '-' || $$1 >= 'a' && $$1 <= 'z' || $$1 >= '0' && $$1 <= '9' || $$1 == '.';
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return true;
    }

    public void onFilesDrop(List<Path> $$0) {
    }

    private void scheduleNarration(long $$0, boolean $$1) {
        this.nextNarrationTime = Util.getMillis() + $$0;
        if ($$1) {
            this.narrationSuppressTime = Long.MIN_VALUE;
        }
    }

    private void suppressNarration(long $$0) {
        this.narrationSuppressTime = Util.getMillis() + $$0;
    }

    public void afterMouseMove() {
        this.scheduleNarration(750L, false);
    }

    public void afterMouseAction() {
        this.scheduleNarration(200L, true);
    }

    public void afterKeyboardAction() {
        this.scheduleNarration(200L, true);
    }

    private boolean shouldRunNarration() {
        return this.minecraft.getNarrator().isActive();
    }

    public void handleDelayedNarration() {
        long $$0;
        if (this.shouldRunNarration() && ($$0 = Util.getMillis()) > this.nextNarrationTime && $$0 > this.narrationSuppressTime) {
            this.runNarration(true);
            this.nextNarrationTime = Long.MAX_VALUE;
        }
    }

    public void triggerImmediateNarration(boolean $$0) {
        if (this.shouldRunNarration()) {
            this.runNarration($$0);
        }
    }

    private void runNarration(boolean $$0) {
        this.narrationState.update(this::updateNarrationState);
        String $$1 = this.narrationState.collectNarrationText(!$$0);
        if (!$$1.isEmpty()) {
            this.minecraft.getNarrator().saySystemNow($$1);
        }
    }

    protected boolean shouldNarrateNavigation() {
        return true;
    }

    protected void updateNarrationState(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.getNarrationMessage());
        if (this.shouldNarrateNavigation()) {
            $$0.add(NarratedElementType.USAGE, USAGE_NARRATION);
        }
        this.updateNarratedWidget($$0);
    }

    protected void updateNarratedWidget(NarrationElementOutput $$02) {
        List $$1 = this.narratables.stream().flatMap($$0 -> $$0.getNarratables().stream()).filter(NarratableEntry::isActive).sorted(Comparator.comparingInt(TabOrderedElement::getTabOrderGroup)).toList();
        NarratableSearchResult $$2 = Screen.findNarratableWidget($$1, this.lastNarratable);
        if ($$2 != null) {
            if ($$2.priority.isTerminal()) {
                this.lastNarratable = $$2.entry;
            }
            if ($$1.size() > 1) {
                $$02.add(NarratedElementType.POSITION, Component.a("narrator.position.screen", $$2.index + 1, $$1.size()));
                if ($$2.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                    $$02.add(NarratedElementType.USAGE, this.getUsageNarration());
                }
            }
            $$2.entry.updateNarration($$02.nest());
        }
    }

    protected Component getUsageNarration() {
        return Component.translatable("narration.component_list.usage");
    }

    @Nullable
    public static NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> $$0, @Nullable NarratableEntry $$1) {
        NarratableSearchResult $$2 = null;
        NarratableSearchResult $$3 = null;
        int $$5 = $$0.size();
        for (int $$4 = 0; $$4 < $$5; ++$$4) {
            NarratableEntry $$6 = $$0.get($$4);
            NarratableEntry.NarrationPriority $$7 = $$6.narrationPriority();
            if ($$7.isTerminal()) {
                if ($$6 == $$1) {
                    $$3 = new NarratableSearchResult($$6, $$4, $$7);
                    continue;
                }
                return new NarratableSearchResult($$6, $$4, $$7);
            }
            if ($$7.compareTo($$2 != null ? $$2.priority : NarratableEntry.NarrationPriority.NONE) <= 0) continue;
            $$2 = new NarratableSearchResult($$6, $$4, $$7);
        }
        return $$2 != null ? $$2 : $$3;
    }

    public void updateNarratorStatus(boolean $$0) {
        if ($$0) {
            this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
        }
        if (this.narratorButton != null) {
            this.narratorButton.setValue(this.minecraft.options.narrator().get());
        }
    }

    public Font getFont() {
        return this.font;
    }

    public boolean showsActiveEffects() {
        return false;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(0, 0, this.width, this.height);
    }

    @Nullable
    public Music getBackgroundMusic() {
        return null;
    }

    static {
        NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
    }

    public static class NarratableSearchResult {
        public final NarratableEntry entry;
        public final int index;
        public final NarratableEntry.NarrationPriority priority;

        public NarratableSearchResult(NarratableEntry $$0, int $$1, NarratableEntry.NarrationPriority $$2) {
            this.entry = $$0;
            this.index = $$1;
            this.priority = $$2;
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class GameModeSwitcherScreen
extends Screen {
    static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("gamemode_switcher/slot");
    static final ResourceLocation SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("gamemode_switcher/selection");
    private static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/gamemode_switcher.png");
    private static final int SPRITE_SHEET_WIDTH = 128;
    private static final int SPRITE_SHEET_HEIGHT = 128;
    private static final int SLOT_AREA = 26;
    private static final int SLOT_PADDING = 5;
    private static final int SLOT_AREA_PADDED = 31;
    private static final int HELP_TIPS_OFFSET_Y = 5;
    private static final int ALL_SLOTS_WIDTH = GameModeIcon.values().length * 31 - 5;
    private static final Component SELECT_KEY = Component.a("debug.gamemodes.select_next", Component.translatable("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA));
    private final GameModeIcon previousHovered;
    private GameModeIcon currentlyHovered;
    private int firstMouseX;
    private int firstMouseY;
    private boolean setFirstMousePos;
    private final List<GameModeSlot> slots = Lists.newArrayList();

    public GameModeSwitcherScreen() {
        super(GameNarrator.NO_TITLE);
        this.currentlyHovered = this.previousHovered = GameModeIcon.getFromGameType(this.getDefaultSelected());
    }

    private GameType getDefaultSelected() {
        MultiPlayerGameMode $$0 = Minecraft.getInstance().gameMode;
        GameType $$1 = $$0.getPreviousPlayerMode();
        if ($$1 != null) {
            return $$1;
        }
        return $$0.getPlayerMode() == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
    }

    @Override
    protected void init() {
        super.init();
        this.currentlyHovered = this.previousHovered;
        for (int $$0 = 0; $$0 < GameModeIcon.VALUES.length; ++$$0) {
            GameModeIcon $$1 = GameModeIcon.VALUES[$$0];
            this.slots.add(new GameModeSlot($$1, this.width / 2 - ALL_SLOTS_WIDTH / 2 + $$0 * 31, this.height / 2 - 31));
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.checkToClose()) {
            return;
        }
        $$0.drawCenteredString(this.font, this.currentlyHovered.name, this.width / 2, this.height / 2 - 31 - 20, -1);
        $$0.drawCenteredString(this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, -1);
        if (!this.setFirstMousePos) {
            this.firstMouseX = $$1;
            this.firstMouseY = $$2;
            this.setFirstMousePos = true;
        }
        boolean $$4 = this.firstMouseX == $$1 && this.firstMouseY == $$2;
        for (GameModeSlot $$5 : this.slots) {
            $$5.render($$0, $$1, $$2, $$3);
            $$5.setSelected(this.currentlyHovered == $$5.icon);
            if ($$4 || !$$5.isHoveredOrFocused()) continue;
            this.currentlyHovered = $$5.icon;
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        int $$4 = this.width / 2 - 62;
        int $$5 = this.height / 2 - 31 - 27;
        $$0.blit(RenderPipelines.GUI_TEXTURED, GAMEMODE_SWITCHER_LOCATION, $$4, $$5, 0.0f, 0.0f, 125, 75, 128, 128);
    }

    private void switchToHoveredGameMode() {
        GameModeSwitcherScreen.switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
    }

    private static void switchToHoveredGameMode(Minecraft $$0, GameModeIcon $$1) {
        if ($$0.gameMode == null || $$0.player == null) {
            return;
        }
        GameModeIcon $$2 = GameModeIcon.getFromGameType($$0.gameMode.getPlayerMode());
        if ($$0.player.hasPermissions(2) && $$1 != $$2) {
            $$0.player.connection.send(new ServerboundChangeGameModePacket($$1.mode));
        }
    }

    private boolean checkToClose() {
        if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
            this.switchToHoveredGameMode();
            this.minecraft.setScreen(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 293) {
            this.setFirstMousePos = false;
            this.currentlyHovered = this.currentlyHovered.getNext();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    static final class GameModeIcon
    extends Enum<GameModeIcon> {
        public static final /* enum */ GameModeIcon CREATIVE = new GameModeIcon(Component.translatable("gameMode.creative"), GameType.CREATIVE, new ItemStack(Blocks.GRASS_BLOCK));
        public static final /* enum */ GameModeIcon SURVIVAL = new GameModeIcon(Component.translatable("gameMode.survival"), GameType.SURVIVAL, new ItemStack(Items.IRON_SWORD));
        public static final /* enum */ GameModeIcon ADVENTURE = new GameModeIcon(Component.translatable("gameMode.adventure"), GameType.ADVENTURE, new ItemStack(Items.MAP));
        public static final /* enum */ GameModeIcon SPECTATOR = new GameModeIcon(Component.translatable("gameMode.spectator"), GameType.SPECTATOR, new ItemStack(Items.ENDER_EYE));
        static final GameModeIcon[] VALUES;
        private static final int ICON_AREA = 16;
        private static final int ICON_TOP_LEFT = 5;
        final Component name;
        final GameType mode;
        private final ItemStack renderStack;
        private static final /* synthetic */ GameModeIcon[] $VALUES;

        public static GameModeIcon[] values() {
            return (GameModeIcon[])$VALUES.clone();
        }

        public static GameModeIcon valueOf(String $$0) {
            return Enum.valueOf(GameModeIcon.class, $$0);
        }

        private GameModeIcon(Component $$0, GameType $$1, ItemStack $$2) {
            this.name = $$0;
            this.mode = $$1;
            this.renderStack = $$2;
        }

        void drawIcon(GuiGraphics $$0, int $$1, int $$2) {
            $$0.renderItem(this.renderStack, $$1, $$2);
        }

        GameModeIcon getNext() {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> SURVIVAL;
                case 1 -> ADVENTURE;
                case 2 -> SPECTATOR;
                case 3 -> CREATIVE;
            };
        }

        static GameModeIcon getFromGameType(GameType $$0) {
            return switch ($$0) {
                default -> throw new MatchException(null, null);
                case GameType.SPECTATOR -> SPECTATOR;
                case GameType.SURVIVAL -> SURVIVAL;
                case GameType.CREATIVE -> CREATIVE;
                case GameType.ADVENTURE -> ADVENTURE;
            };
        }

        private static /* synthetic */ GameModeIcon[] b() {
            return new GameModeIcon[]{CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR};
        }

        static {
            $VALUES = GameModeIcon.b();
            VALUES = GameModeIcon.values();
        }
    }

    public static class GameModeSlot
    extends AbstractWidget {
        final GameModeIcon icon;
        private boolean isSelected;

        public GameModeSlot(GameModeIcon $$0, int $$1, int $$2) {
            super($$1, $$2, 26, 26, $$0.name);
            this.icon = $$0;
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            this.drawSlot($$0);
            if (this.isSelected) {
                this.drawSelection($$0);
            }
            this.icon.drawIcon($$0, this.getX() + 5, this.getY() + 5);
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput $$0) {
            this.defaultButtonNarrationText($$0);
        }

        @Override
        public boolean isHoveredOrFocused() {
            return super.isHoveredOrFocused() || this.isSelected;
        }

        public void setSelected(boolean $$0) {
            this.isSelected = $$0;
        }

        private void drawSlot(GuiGraphics $$0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, this.getX(), this.getY(), 26, 26);
        }

        private void drawSelection(GuiGraphics $$0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTION_SPRITE, this.getX(), this.getY(), 26, 26);
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class DeathScreen
extends Screen {
    private static final ResourceLocation DRAFT_REPORT_SPRITE = ResourceLocation.withDefaultNamespace("icon/draft_report");
    private int delayTicker;
    private final Component causeOfDeath;
    private final boolean hardcore;
    private Component deathScore;
    private final List<Button> exitButtons = Lists.newArrayList();
    @Nullable
    private Button exitToTitleButton;

    public DeathScreen(@Nullable Component $$0, boolean $$1) {
        super(Component.translatable($$1 ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.causeOfDeath = $$0;
        this.hardcore = $$1;
    }

    @Override
    protected void init() {
        this.delayTicker = 0;
        this.exitButtons.clear();
        MutableComponent $$02 = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
        this.exitButtons.add(this.addRenderableWidget(Button.builder($$02, $$0 -> {
            this.minecraft.player.respawn();
            $$0.active = false;
        }).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
        this.exitToTitleButton = this.addRenderableWidget(Button.builder(Component.translatable("deathScreen.titleScreen"), $$0 -> this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::handleExitToTitleScreen, true)).bounds(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build());
        this.exitButtons.add(this.exitToTitleButton);
        this.setButtonsActive(false);
        this.deathScore = Component.a("deathScreen.score.value", Component.literal(Integer.toString(this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void handleExitToTitleScreen() {
        if (this.hardcore) {
            this.exitToTitleScreen();
            return;
        }
        TitleConfirmScreen $$02 = new TitleConfirmScreen($$0 -> {
            if ($$0) {
                this.exitToTitleScreen();
            } else {
                this.minecraft.player.respawn();
                this.minecraft.setScreen(null);
            }
        }, Component.translatable("deathScreen.quit.confirm"), CommonComponents.EMPTY, Component.translatable("deathScreen.titleScreen"), Component.translatable("deathScreen.respawn"));
        this.minecraft.setScreen($$02);
        $$02.setDelay(20);
    }

    private void exitToTitleScreen() {
        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
        }
        this.minecraft.disconnectWithSavingScreen();
        this.minecraft.setScreen(new TitleScreen());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.pose().pushMatrix();
        $$0.pose().scale(2.0f, 2.0f);
        $$0.drawCenteredString(this.font, this.title, this.width / 2 / 2, 30, -1);
        $$0.pose().popMatrix();
        if (this.causeOfDeath != null) {
            $$0.drawCenteredString(this.font, this.causeOfDeath, this.width / 2, 85, -1);
        }
        $$0.drawCenteredString(this.font, this.deathScore, this.width / 2, 100, -1);
        if (this.causeOfDeath != null && $$2 > 85 && $$2 < 85 + this.font.lineHeight) {
            Style $$4 = this.getClickedComponentStyleAt($$1);
            $$0.renderComponentHoverEffect(this.font, $$4, $$1, $$2);
        }
        if (this.exitToTitleButton != null && this.minecraft.getReportingContext().hasDraftReport()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_SPRITE, this.exitToTitleButton.getX() + this.exitToTitleButton.getWidth() - 17, this.exitToTitleButton.getY() + 3, 15, 15);
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        DeathScreen.renderDeathBackground($$0, this.width, this.height);
    }

    static void renderDeathBackground(GuiGraphics $$0, int $$1, int $$2) {
        $$0.fillGradient(0, 0, $$1, $$2, 0x60500000, -1602211792);
    }

    @Nullable
    private Style getClickedComponentStyleAt(int $$0) {
        if (this.causeOfDeath == null) {
            return null;
        }
        int $$1 = this.minecraft.font.width(this.causeOfDeath);
        int $$2 = this.width / 2 - $$1 / 2;
        int $$3 = this.width / 2 + $$1 / 2;
        if ($$0 < $$2 || $$0 > $$3) {
            return null;
        }
        return this.minecraft.font.getSplitter().componentStyleAtWidth(this.causeOfDeath, $$0 - $$2);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        ClickEvent clickEvent;
        Style $$3;
        if (this.causeOfDeath != null && $$1 > 85.0 && $$1 < (double)(85 + this.font.lineHeight) && ($$3 = this.getClickedComponentStyleAt((int)$$0)) != null && (clickEvent = $$3.getClickEvent()) instanceof ClickEvent.OpenUrl) {
            ClickEvent.OpenUrl $$4 = (ClickEvent.OpenUrl)clickEvent;
            return DeathScreen.clickUrlAction(this.minecraft, this, $$4.uri());
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.delayTicker;
        if (this.delayTicker == 20) {
            this.setButtonsActive(true);
        }
    }

    private void setButtonsActive(boolean $$0) {
        for (Button $$1 : this.exitButtons) {
            $$1.active = $$0;
        }
    }

    public static class TitleConfirmScreen
    extends ConfirmScreen {
        public TitleConfirmScreen(BooleanConsumer $$0, Component $$1, Component $$2, Component $$3, Component $$4) {
            super($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            DeathScreen.renderDeathBackground($$0, this.width, this.height);
        }
    }
}


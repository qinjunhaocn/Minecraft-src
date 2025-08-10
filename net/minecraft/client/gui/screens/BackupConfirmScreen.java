/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BackupConfirmScreen
extends Screen {
    private static final Component SKIP_AND_JOIN = Component.translatable("selectWorld.backupJoinSkipButton");
    public static final Component BACKUP_AND_JOIN = Component.translatable("selectWorld.backupJoinConfirmButton");
    private final Runnable onCancel;
    protected final Listener onProceed;
    private final Component description;
    private final boolean promptForCacheErase;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    final Component confirmation;
    protected int id;
    private Checkbox eraseCache;

    public BackupConfirmScreen(Runnable $$0, Listener $$1, Component $$2, Component $$3, boolean $$4) {
        this($$0, $$1, $$2, $$3, BACKUP_AND_JOIN, $$4);
    }

    public BackupConfirmScreen(Runnable $$0, Listener $$1, Component $$2, Component $$3, Component $$4, boolean $$5) {
        super($$2);
        this.onCancel = $$0;
        this.onProceed = $$1;
        this.description = $$3;
        this.promptForCacheErase = $$5;
        this.confirmation = $$4;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, this.description, this.width - 50);
        int $$02 = (this.message.getLineCount() + 1) * this.font.lineHeight;
        this.eraseCache = Checkbox.builder(Component.translatable("selectWorld.backupEraseCache"), this.font).pos(this.width / 2 - 155 + 80, 76 + $$02).build();
        if (this.promptForCacheErase) {
            this.addRenderableWidget(this.eraseCache);
        }
        this.addRenderableWidget(Button.builder(this.confirmation, $$0 -> this.onProceed.proceed(true, this.eraseCache.selected())).bounds(this.width / 2 - 155, 100 + $$02, 150, 20).build());
        this.addRenderableWidget(Button.builder(SKIP_AND_JOIN, $$0 -> this.onProceed.proceed(false, this.eraseCache.selected())).bounds(this.width / 2 - 155 + 160, 100 + $$02, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onCancel.run()).bounds(this.width / 2 - 155 + 80, 124 + $$02, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 50, -1);
        this.message.renderCentered($$0, this.width / 2, 70);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.onCancel.run();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    public static interface Listener {
        public void proceed(boolean var1, boolean var2);
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.time.Duration;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RepeatedNarrator;
import org.slf4j.Logger;

public class RealmsLongRunningMcoTaskScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
    private final List<LongRunningTask> queuedTasks;
    private final Screen lastScreen;
    protected final LinearLayout layout = LinearLayout.vertical();
    private volatile Component title;
    @Nullable
    private LoadingDotsWidget loadingDotsWidget;

    public RealmsLongRunningMcoTaskScreen(Screen $$0, LongRunningTask ... $$1) {
        super(GameNarrator.NO_TITLE);
        this.lastScreen = $$0;
        this.queuedTasks = List.of((Object[])$$1);
        if (this.queuedTasks.isEmpty()) {
            throw new IllegalArgumentException("No tasks added");
        }
        this.title = this.queuedTasks.get(0).getTitle();
        Runnable $$2 = () -> {
            for (LongRunningTask $$1 : $$1) {
                this.setTitle($$1.getTitle());
                if ($$1.aborted()) break;
                $$1.run();
                if (!$$1.aborted()) continue;
                return;
            }
        };
        Thread $$3 = new Thread($$2, "Realms-long-running-task");
        $$3.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        $$3.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.loadingDotsWidget != null) {
            REPEATED_NARRATOR.narrate(this.minecraft.getNarrator(), this.loadingDotsWidget.getMessage());
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.cancel();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void init() {
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        this.layout.addChild(RealmsLongRunningMcoTaskScreen.realmsLogo());
        this.loadingDotsWidget = new LoadingDotsWidget(this.font, this.title);
        this.layout.addChild(this.loadingDotsWidget, $$0 -> $$0.paddingTop(10).paddingBottom(30));
        this.layout.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.cancel()).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    protected void cancel() {
        for (LongRunningTask $$0 : this.queuedTasks) {
            $$0.abortTask();
        }
        this.minecraft.setScreen(this.lastScreen);
    }

    public void setTitle(Component $$0) {
        if (this.loadingDotsWidget != null) {
            this.loadingDotsWidget.setMessage($$0);
        }
        this.title = $$0;
    }
}


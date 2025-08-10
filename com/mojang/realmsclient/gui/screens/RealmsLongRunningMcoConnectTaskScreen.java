/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsJoinInformation;
import com.mojang.realmsclient.dto.ServiceQuality;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RealmsLongRunningMcoConnectTaskScreen
extends RealmsLongRunningMcoTaskScreen {
    private final LongRunningTask task;
    private final RealmsJoinInformation serverAddress;
    private final LinearLayout footer = LinearLayout.vertical();

    public RealmsLongRunningMcoConnectTaskScreen(Screen $$0, RealmsJoinInformation $$1, LongRunningTask $$2) {
        super($$0, $$2);
        this.task = $$2;
        this.serverAddress = $$1;
    }

    @Override
    public void init() {
        super.init();
        if (this.serverAddress.regionData() == null || this.serverAddress.regionData().region() == null) {
            return;
        }
        LinearLayout $$02 = LinearLayout.horizontal().spacing(10);
        StringWidget $$12 = new StringWidget(Component.a("mco.connect.region", Component.translatable(this.serverAddress.regionData().region().translationKey)), this.font);
        $$02.addChild($$12);
        ResourceLocation $$2 = this.serverAddress.regionData().serviceQuality() != null ? this.serverAddress.regionData().serviceQuality().getIcon() : ServiceQuality.UNKNOWN.getIcon();
        $$02.addChild(ImageWidget.sprite(10, 8, $$2), LayoutSettings::alignVerticallyTop);
        this.footer.addChild($$02, $$0 -> $$0.paddingTop(40));
        this.footer.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        int $$0 = this.layout.getY() + this.layout.getHeight();
        ScreenRectangle $$1 = new ScreenRectangle(0, $$0, this.width, this.height - $$0);
        this.footer.arrangeElements();
        FrameLayout.alignInRectangle(this.footer, $$1, 0.5f, 0.0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.task.tick();
    }

    @Override
    protected void cancel() {
        this.task.abortTask();
        super.cancel();
    }
}


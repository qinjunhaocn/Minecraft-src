/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen
extends RealmsScreen {
    private final Screen nextScreen;
    private final ErrorMessage lines;
    private MultiLineLabel line2Split = MultiLineLabel.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException $$0, Screen $$1) {
        super(GameNarrator.NO_TITLE);
        this.nextScreen = $$1;
        this.lines = RealmsGenericErrorScreen.errorMessage($$0);
    }

    public RealmsGenericErrorScreen(Component $$0, Screen $$1) {
        super(GameNarrator.NO_TITLE);
        this.nextScreen = $$1;
        this.lines = RealmsGenericErrorScreen.errorMessage($$0);
    }

    public RealmsGenericErrorScreen(Component $$0, Component $$1, Screen $$2) {
        super(GameNarrator.NO_TITLE);
        this.nextScreen = $$2;
        this.lines = RealmsGenericErrorScreen.errorMessage($$0, $$1);
    }

    private static ErrorMessage errorMessage(RealmsServiceException $$0) {
        RealmsError $$1 = $$0.realmsError;
        return RealmsGenericErrorScreen.errorMessage(Component.a("mco.errorMessage.realmsService.realmsError", $$1.errorCode()), $$1.errorMessage());
    }

    private static ErrorMessage errorMessage(Component $$0) {
        return RealmsGenericErrorScreen.errorMessage(Component.translatable("mco.errorMessage.generic"), $$0);
    }

    private static ErrorMessage errorMessage(Component $$0, Component $$1) {
        return new ErrorMessage($$0, $$1);
    }

    @Override
    public void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, $$0 -> this.onClose()).bounds(this.width / 2 - 100, this.height - 52, 200, 20).build());
        this.line2Split = MultiLineLabel.create(this.font, this.lines.detail, this.width * 3 / 4);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.nextScreen);
    }

    @Override
    public Component getNarrationMessage() {
        return Component.empty().append(this.lines.title).append(": ").append(this.lines.detail);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.lines.title, this.width / 2, 80, -1);
        this.line2Split.renderCentered($$0, this.width / 2, 100, this.minecraft.font.lineHeight, -2142128);
    }

    static final class ErrorMessage
    extends Record {
        final Component title;
        final Component detail;

        ErrorMessage(Component $$0, Component $$1) {
            this.title = $$0;
            this.detail = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorMessage.class, "title;detail", "title", "detail"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorMessage.class, "title;detail", "title", "detail"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorMessage.class, "title;detail", "title", "detail"}, this, $$0);
        }

        public Component title() {
            return this.title;
        }

        public Component detail() {
            return this.detail;
        }
    }
}


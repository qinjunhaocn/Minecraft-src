/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector3f;

public abstract class AbstractSignEditScreen
extends Screen {
    protected final SignBlockEntity sign;
    private SignText text;
    private final String[] messages;
    private final boolean isFrontText;
    protected final WoodType woodType;
    private int frame;
    private int line;
    @Nullable
    private TextFieldHelper signField;

    public AbstractSignEditScreen(SignBlockEntity $$0, boolean $$1, boolean $$2) {
        this($$0, $$1, $$2, Component.translatable("sign.edit"));
    }

    public AbstractSignEditScreen(SignBlockEntity $$0, boolean $$12, boolean $$2, Component $$3) {
        super($$3);
        this.sign = $$0;
        this.text = $$0.getText($$12);
        this.isFrontText = $$12;
        this.woodType = SignBlock.getWoodType($$0.getBlockState().getBlock());
        this.messages = (String[])IntStream.range(0, 4).mapToObj($$1 -> this.text.getMessage($$1, $$2)).map(Component::getString).toArray(String[]::new);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
        this.signField = new TextFieldHelper(() -> this.messages[this.line], this::setMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), $$0 -> this.minecraft.font.width((String)$$0) <= this.sign.getMaxTextLineWidth());
    }

    @Override
    public void tick() {
        ++this.frame;
        if (!this.isValid()) {
            this.onDone();
        }
    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && !this.sign.isRemoved() && !this.sign.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 265) {
            this.line = this.line - 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
        if ($$0 == 264 || $$0 == 257 || $$0 == 335) {
            this.line = this.line + 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
        if (this.signField.keyPressed($$0)) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean a(char $$0, int $$1) {
        this.signField.a($$0);
        return true;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 40, -1);
        this.renderSign($$0);
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderTransparentBackground($$0);
    }

    @Override
    public void onClose() {
        this.onDone();
    }

    @Override
    public void removed() {
        ClientPacketListener $$0 = this.minecraft.getConnection();
        if ($$0 != null) {
            $$0.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.isFrontText, this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected abstract void renderSignBackground(GuiGraphics var1);

    protected abstract Vector3f getSignTextScale();

    protected abstract float getSignYOffset();

    private void renderSign(GuiGraphics $$0) {
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)this.width / 2.0f, this.getSignYOffset());
        $$0.pose().pushMatrix();
        this.renderSignBackground($$0);
        $$0.pose().popMatrix();
        this.renderSignText($$0);
        $$0.pose().popMatrix();
    }

    private void renderSignText(GuiGraphics $$0) {
        Vector3f $$1 = this.getSignTextScale();
        $$0.pose().scale($$1.x(), $$1.y());
        int $$2 = this.text.hasGlowingText() ? this.text.getColor().getTextColor() : AbstractSignRenderer.getDarkColor(this.text);
        boolean $$3 = this.frame / 6 % 2 == 0;
        int $$4 = this.signField.getCursorPos();
        int $$5 = this.signField.getSelectionPos();
        int $$6 = 4 * this.sign.getTextLineHeight() / 2;
        int $$7 = this.line * this.sign.getTextLineHeight() - $$6;
        for (int $$8 = 0; $$8 < this.messages.length; ++$$8) {
            String $$9 = this.messages[$$8];
            if ($$9 == null) continue;
            if (this.font.isBidirectional()) {
                $$9 = this.font.bidirectionalShaping($$9);
            }
            int $$10 = -this.font.width($$9) / 2;
            $$0.drawString(this.font, $$9, $$10, $$8 * this.sign.getTextLineHeight() - $$6, $$2, false);
            if ($$8 != this.line || $$4 < 0 || !$$3) continue;
            int $$11 = this.font.width($$9.substring(0, Math.max(Math.min($$4, $$9.length()), 0)));
            int $$12 = $$11 - this.font.width($$9) / 2;
            if ($$4 < $$9.length()) continue;
            $$0.drawString(this.font, "_", $$12, $$7, $$2, false);
        }
        for (int $$13 = 0; $$13 < this.messages.length; ++$$13) {
            String $$14 = this.messages[$$13];
            if ($$14 == null || $$13 != this.line || $$4 < 0) continue;
            int $$15 = this.font.width($$14.substring(0, Math.max(Math.min($$4, $$14.length()), 0)));
            int $$16 = $$15 - this.font.width($$14) / 2;
            if ($$3 && $$4 < $$14.length()) {
                $$0.fill($$16, $$7 - 1, $$16 + 1, $$7 + this.sign.getTextLineHeight(), ARGB.opaque($$2));
            }
            if ($$5 == $$4) continue;
            int $$17 = Math.min($$4, $$5);
            int $$18 = Math.max($$4, $$5);
            int $$19 = this.font.width($$14.substring(0, $$17)) - this.font.width($$14) / 2;
            int $$20 = this.font.width($$14.substring(0, $$18)) - this.font.width($$14) / 2;
            int $$21 = Math.min($$19, $$20);
            int $$22 = Math.max($$19, $$20);
            $$0.textHighlight($$21, $$7, $$22, $$7 + this.sign.getTextLineHeight());
        }
    }

    private void setMessage(String $$0) {
        this.messages[this.line] = $$0;
        this.text = this.text.setMessage(this.line, Component.literal($$0));
        this.sign.setText(this.text, this.isFrontText);
    }

    private void onDone() {
        this.minecraft.setScreen(null);
    }
}


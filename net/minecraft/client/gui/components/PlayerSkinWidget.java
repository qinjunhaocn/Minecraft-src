/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;

public class PlayerSkinWidget
extends AbstractWidget {
    private static final float MODEL_HEIGHT = 2.125f;
    private static final float FIT_SCALE = 0.97f;
    private static final float ROTATION_SENSITIVITY = 2.5f;
    private static final float DEFAULT_ROTATION_X = -5.0f;
    private static final float DEFAULT_ROTATION_Y = 30.0f;
    private static final float ROTATION_X_LIMIT = 50.0f;
    private final PlayerModel wideModel;
    private final PlayerModel slimModel;
    private final Supplier<PlayerSkin> skin;
    private float rotationX = -5.0f;
    private float rotationY = 30.0f;

    public PlayerSkinWidget(int $$0, int $$1, EntityModelSet $$2, Supplier<PlayerSkin> $$3) {
        super(0, 0, $$0, $$1, CommonComponents.EMPTY);
        this.wideModel = new PlayerModel($$2.bakeLayer(ModelLayers.PLAYER), false);
        this.slimModel = new PlayerModel($$2.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        this.skin = $$3;
    }

    @Override
    protected void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        float $$4 = 0.97f * (float)this.getHeight() / 2.125f;
        float $$5 = -1.0625f;
        PlayerSkin $$6 = this.skin.get();
        PlayerModel $$7 = $$6.model() == PlayerSkin.Model.SLIM ? this.slimModel : this.wideModel;
        $$0.submitSkinRenderState($$7, $$6.texture(), $$4, this.rotationX, this.rotationY, -1.0625f, this.getX(), this.getY(), this.getRight(), this.getBottom());
    }

    @Override
    protected void onDrag(double $$0, double $$1, double $$2, double $$3) {
        this.rotationX = Mth.clamp(this.rotationX - (float)$$3 * 2.5f, -50.0f, 50.0f);
        this.rotationY += (float)$$2 * 2.5f;
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        return null;
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.joml.Vector3f;

public class SignEditScreen
extends AbstractSignEditScreen {
    public static final float MAGIC_SCALE_NUMBER = 62.500004f;
    public static final float MAGIC_TEXT_SCALE = 0.9765628f;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628f, 0.9765628f, 0.9765628f);
    @Nullable
    private Model signModel;

    public SignEditScreen(SignBlockEntity $$0, boolean $$1, boolean $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void init() {
        super.init();
        boolean $$0 = this.sign.getBlockState().getBlock() instanceof StandingSignBlock;
        this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType, $$0);
    }

    @Override
    protected float getSignYOffset() {
        return 90.0f;
    }

    @Override
    protected void renderSignBackground(GuiGraphics $$0) {
        if (this.signModel == null) {
            return;
        }
        int $$1 = this.width / 2;
        int $$2 = $$1 - 48;
        int $$3 = 66;
        int $$4 = $$1 + 48;
        int $$5 = 168;
        $$0.submitSignRenderState(this.signModel, 62.500004f, this.woodType, $$2, 66, $$4, 168);
    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }
}


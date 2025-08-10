/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.joml.Vector3f;

public class HangingSignEditScreen
extends AbstractSignEditScreen {
    public static final float MAGIC_BACKGROUND_SCALE = 4.5f;
    private static final Vector3f TEXT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 16;
    private final ResourceLocation texture;

    public HangingSignEditScreen(SignBlockEntity $$0, boolean $$1, boolean $$2) {
        super($$0, $$1, $$2, Component.translatable("hanging_sign.edit"));
        this.texture = ResourceLocation.withDefaultNamespace("textures/gui/hanging_signs/" + this.woodType.name() + ".png");
    }

    @Override
    protected float getSignYOffset() {
        return 125.0f;
    }

    @Override
    protected void renderSignBackground(GuiGraphics $$0) {
        $$0.pose().translate(0.0f, -13.0f);
        $$0.pose().scale(4.5f, 4.5f);
        $$0.blit(RenderPipelines.GUI_TEXTURED, this.texture, -8, -8, 0.0f, 0.0f, 16, 16, 16, 16);
    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }
}


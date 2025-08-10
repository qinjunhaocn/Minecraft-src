/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.OversizedItemRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class OversizedItemRenderer
extends PictureInPictureRenderer<OversizedItemRenderState> {
    private boolean usedOnThisFrame;
    @Nullable
    private Object modelOnTextureIdentity;

    public OversizedItemRenderer(MultiBufferSource.BufferSource $$0) {
        super($$0);
    }

    public boolean usedOnThisFrame() {
        return this.usedOnThisFrame;
    }

    public void resetUsedOnThisFrame() {
        this.usedOnThisFrame = false;
    }

    public void invalidateTexture() {
        this.modelOnTextureIdentity = null;
    }

    @Override
    public Class<OversizedItemRenderState> getRenderStateClass() {
        return OversizedItemRenderState.class;
    }

    @Override
    protected void renderToTexture(OversizedItemRenderState $$0, PoseStack $$1) {
        boolean $$9;
        $$1.scale(1.0f, -1.0f, -1.0f);
        GuiItemRenderState $$2 = $$0.guiItemRenderState();
        ScreenRectangle $$3 = $$2.oversizedItemBounds();
        Objects.requireNonNull($$3);
        float $$4 = (float)($$3.left() + $$3.right()) / 2.0f;
        float $$5 = (float)($$3.top() + $$3.bottom()) / 2.0f;
        float $$6 = (float)$$2.x() + 8.0f;
        float $$7 = (float)$$2.y() + 8.0f;
        $$1.translate(($$6 - $$4) / 16.0f, ($$5 - $$7) / 16.0f, 0.0f);
        TrackingItemStackRenderState $$8 = $$2.itemStackRenderState();
        boolean bl = $$9 = !$$8.usesBlockLight();
        if ($$9) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        } else {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        }
        $$8.render($$1, this.bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
        this.modelOnTextureIdentity = $$8.getModelIdentity();
    }

    @Override
    public void blitTexture(OversizedItemRenderState $$0, GuiRenderState $$1) {
        super.blitTexture($$0, $$1);
        this.usedOnThisFrame = true;
    }

    @Override
    public boolean textureIsReadyToBlit(OversizedItemRenderState $$0) {
        TrackingItemStackRenderState $$1 = $$0.guiItemRenderState().itemStackRenderState();
        return !$$1.isAnimated() && $$1.getModelIdentity().equals(this.modelOnTextureIdentity);
    }

    @Override
    protected float getTranslateY(int $$0, int $$1) {
        return (float)$$0 / 2.0f;
    }

    @Override
    protected String getTextureLabel() {
        return "oversized_item";
    }
}


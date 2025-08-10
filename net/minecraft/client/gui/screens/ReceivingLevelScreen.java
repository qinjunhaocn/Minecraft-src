/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class ReceivingLevelScreen
extends Screen {
    private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
    private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 30000L;
    private final long createdAt;
    private final BooleanSupplier levelReceived;
    private final Reason reason;
    @Nullable
    private TextureAtlasSprite cachedNetherPortalSprite;

    public ReceivingLevelScreen(BooleanSupplier $$0, Reason $$1) {
        super(GameNarrator.NO_TITLE);
        this.levelReceived = $$0;
        this.reason = $$1;
        this.createdAt = Util.getMillis();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, -1);
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        switch (this.reason.ordinal()) {
            case 2: {
                this.renderPanorama($$0, $$3);
                this.renderBlurredBackground($$0);
                this.renderMenuBackground($$0);
                break;
            }
            case 0: {
                $$0.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, this.getNetherPortalSprite(), 0, 0, $$0.guiWidth(), $$0.guiHeight());
                break;
            }
            case 1: {
                TextureManager $$4 = Minecraft.getInstance().getTextureManager();
                TextureSetup $$5 = TextureSetup.doubleTexture($$4.getTexture(TheEndPortalRenderer.END_SKY_LOCATION).getTextureView(), $$4.getTexture(TheEndPortalRenderer.END_PORTAL_LOCATION).getTextureView());
                $$0.fill(RenderPipelines.END_PORTAL, $$5, 0, 0, this.width, this.height);
            }
        }
    }

    private TextureAtlasSprite getNetherPortalSprite() {
        if (this.cachedNetherPortalSprite != null) {
            return this.cachedNetherPortalSprite;
        }
        this.cachedNetherPortalSprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
        return this.cachedNetherPortalSprite;
    }

    @Override
    public void tick() {
        if (this.levelReceived.getAsBoolean() || Util.getMillis() > this.createdAt + 30000L) {
            this.onClose();
        }
    }

    @Override
    public void onClose() {
        this.minecraft.getNarrator().saySystemNow(Component.translatable("narrator.ready_to_play"));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static final class Reason
    extends Enum<Reason> {
        public static final /* enum */ Reason NETHER_PORTAL = new Reason();
        public static final /* enum */ Reason END_PORTAL = new Reason();
        public static final /* enum */ Reason OTHER = new Reason();
        private static final /* synthetic */ Reason[] $VALUES;

        public static Reason[] values() {
            return (Reason[])$VALUES.clone();
        }

        public static Reason valueOf(String $$0) {
            return Enum.valueOf(Reason.class, $$0);
        }

        private static /* synthetic */ Reason[] a() {
            return new Reason[]{NETHER_PORTAL, END_PORTAL, OTHER};
        }

        static {
            $VALUES = Reason.a();
        }
    }
}


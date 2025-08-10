/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;

public class LogoRenderer {
    public static final ResourceLocation MINECRAFT_LOGO = ResourceLocation.withDefaultNamespace("textures/gui/title/minecraft.png");
    public static final ResourceLocation EASTER_EGG_LOGO = ResourceLocation.withDefaultNamespace("textures/gui/title/minceraft.png");
    public static final ResourceLocation MINECRAFT_EDITION = ResourceLocation.withDefaultNamespace("textures/gui/title/edition.png");
    public static final int LOGO_WIDTH = 256;
    public static final int LOGO_HEIGHT = 44;
    private static final int LOGO_TEXTURE_WIDTH = 256;
    private static final int LOGO_TEXTURE_HEIGHT = 64;
    private static final int EDITION_WIDTH = 128;
    private static final int EDITION_HEIGHT = 14;
    private static final int EDITION_TEXTURE_WIDTH = 128;
    private static final int EDITION_TEXTURE_HEIGHT = 16;
    public static final int DEFAULT_HEIGHT_OFFSET = 30;
    private static final int EDITION_LOGO_OVERLAP = 7;
    private final boolean showEasterEgg = (double)RandomSource.create().nextFloat() < 1.0E-4;
    private final boolean keepLogoThroughFade;

    public LogoRenderer(boolean $$0) {
        this.keepLogoThroughFade = $$0;
    }

    public void renderLogo(GuiGraphics $$0, int $$1, float $$2) {
        this.renderLogo($$0, $$1, $$2, 30);
    }

    public void renderLogo(GuiGraphics $$0, int $$1, float $$2, int $$3) {
        int $$4 = $$1 / 2 - 128;
        float $$5 = this.keepLogoThroughFade ? 1.0f : $$2;
        int $$6 = ARGB.white($$5);
        $$0.blit(RenderPipelines.GUI_TEXTURED, this.showEasterEgg ? EASTER_EGG_LOGO : MINECRAFT_LOGO, $$4, $$3, 0.0f, 0.0f, 256, 44, 256, 64, $$6);
        int $$7 = $$1 / 2 - 64;
        int $$8 = $$3 + 44 - 7;
        $$0.blit(RenderPipelines.GUI_TEXTURED, MINECRAFT_EDITION, $$7, $$8, 0.0f, 0.0f, 128, 14, 128, 16, $$6);
    }

    public boolean keepLogoThroughFade() {
        return this.keepLogoThroughFade;
    }
}


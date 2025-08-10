/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fStack
 *  org.joml.Matrix3x2fc
 *  org.joml.Quaternionf
 *  org.joml.Vector2ic
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.ColoredRectangleRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.pip.GuiBannerResultRenderState;
import net.minecraft.client.gui.render.state.pip.GuiBookModelRenderState;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.gui.render.state.pip.GuiProfilerChartRenderState;
import net.minecraft.client.gui.render.state.pip.GuiSignRenderState;
import net.minecraft.client.gui.render.state.pip.GuiSkinRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.joml.Quaternionf;
import org.joml.Vector2ic;
import org.joml.Vector3f;

public class GuiGraphics {
    private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
    private final Minecraft minecraft;
    private final Matrix3x2fStack pose;
    private final ScissorStack scissorStack = new ScissorStack();
    private final GuiSpriteManager sprites;
    private final GuiRenderState guiRenderState;
    @Nullable
    private Runnable deferredTooltip;

    private GuiGraphics(Minecraft $$0, Matrix3x2fStack $$1, GuiRenderState $$2) {
        this.minecraft = $$0;
        this.pose = $$1;
        this.sprites = $$0.getGuiSprites();
        this.guiRenderState = $$2;
    }

    public GuiGraphics(Minecraft $$0, GuiRenderState $$1) {
        this($$0, new Matrix3x2fStack(16), $$1);
    }

    public int guiWidth() {
        return this.minecraft.getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return this.minecraft.getWindow().getGuiScaledHeight();
    }

    public void nextStratum() {
        this.guiRenderState.nextStratum();
    }

    public void blurBeforeThisStratum() {
        this.guiRenderState.blurBeforeThisStratum();
    }

    public Matrix3x2fStack pose() {
        return this.pose;
    }

    public void hLine(int $$0, int $$1, int $$2, int $$3) {
        if ($$1 < $$0) {
            int $$4 = $$0;
            $$0 = $$1;
            $$1 = $$4;
        }
        this.fill($$0, $$2, $$1 + 1, $$2 + 1, $$3);
    }

    public void vLine(int $$0, int $$1, int $$2, int $$3) {
        if ($$2 < $$1) {
            int $$4 = $$1;
            $$1 = $$2;
            $$2 = $$4;
        }
        this.fill($$0, $$1 + 1, $$0 + 1, $$2, $$3);
    }

    public void enableScissor(int $$0, int $$1, int $$2, int $$3) {
        ScreenRectangle $$4 = new ScreenRectangle($$0, $$1, $$2 - $$0, $$3 - $$1).transformAxisAligned((Matrix3x2f)this.pose);
        this.scissorStack.push($$4);
    }

    public void disableScissor() {
        this.scissorStack.pop();
    }

    public boolean containsPointInScissor(int $$0, int $$1) {
        return this.scissorStack.containsPoint($$0, $$1);
    }

    public void fill(int $$0, int $$1, int $$2, int $$3, int $$4) {
        this.fill(RenderPipelines.GUI, $$0, $$1, $$2, $$3, $$4);
    }

    public void fill(RenderPipeline $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        if ($$1 < $$3) {
            int $$6 = $$1;
            $$1 = $$3;
            $$3 = $$6;
        }
        if ($$2 < $$4) {
            int $$7 = $$2;
            $$2 = $$4;
            $$4 = $$7;
        }
        this.submitColoredRectangle($$0, TextureSetup.noTexture(), $$1, $$2, $$3, $$4, $$5, null);
    }

    public void fillGradient(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        this.submitColoredRectangle(RenderPipelines.GUI, TextureSetup.noTexture(), $$0, $$1, $$2, $$3, $$4, $$5);
    }

    public void fill(RenderPipeline $$0, TextureSetup $$1, int $$2, int $$3, int $$4, int $$5) {
        this.submitColoredRectangle($$0, $$1, $$2, $$3, $$4, $$5, -1, null);
    }

    private void submitColoredRectangle(RenderPipeline $$0, TextureSetup $$1, int $$2, int $$3, int $$4, int $$5, int $$6, @Nullable Integer $$7) {
        this.guiRenderState.submitGuiElement(new ColoredRectangleRenderState($$0, $$1, new Matrix3x2f((Matrix3x2fc)this.pose), $$2, $$3, $$4, $$5, $$6, $$7 != null ? $$7 : $$6, this.scissorStack.peek()));
    }

    public void textHighlight(int $$0, int $$1, int $$2, int $$3) {
        this.fill(RenderPipelines.GUI_INVERT, $$0, $$1, $$2, $$3, -1);
        this.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, $$0, $$1, $$2, $$3, -16776961);
    }

    public void drawCenteredString(Font $$0, String $$1, int $$2, int $$3, int $$4) {
        this.drawString($$0, $$1, $$2 - $$0.width($$1) / 2, $$3, $$4);
    }

    public void drawCenteredString(Font $$0, Component $$1, int $$2, int $$3, int $$4) {
        FormattedCharSequence $$5 = $$1.getVisualOrderText();
        this.drawString($$0, $$5, $$2 - $$0.width($$5) / 2, $$3, $$4);
    }

    public void drawCenteredString(Font $$0, FormattedCharSequence $$1, int $$2, int $$3, int $$4) {
        this.drawString($$0, $$1, $$2 - $$0.width($$1) / 2, $$3, $$4);
    }

    public void drawString(Font $$0, @Nullable String $$1, int $$2, int $$3, int $$4) {
        this.drawString($$0, $$1, $$2, $$3, $$4, true);
    }

    public void drawString(Font $$0, @Nullable String $$1, int $$2, int $$3, int $$4, boolean $$5) {
        if ($$1 == null) {
            return;
        }
        this.drawString($$0, Language.getInstance().getVisualOrder(FormattedText.of($$1)), $$2, $$3, $$4, $$5);
    }

    public void drawString(Font $$0, FormattedCharSequence $$1, int $$2, int $$3, int $$4) {
        this.drawString($$0, $$1, $$2, $$3, $$4, true);
    }

    public void drawString(Font $$0, FormattedCharSequence $$1, int $$2, int $$3, int $$4, boolean $$5) {
        if (ARGB.alpha($$4) == 0) {
            return;
        }
        this.guiRenderState.submitText(new GuiTextRenderState($$0, $$1, new Matrix3x2f((Matrix3x2fc)this.pose), $$2, $$3, $$4, 0, $$5, this.scissorStack.peek()));
    }

    public void drawString(Font $$0, Component $$1, int $$2, int $$3, int $$4) {
        this.drawString($$0, $$1, $$2, $$3, $$4, true);
    }

    public void drawString(Font $$0, Component $$1, int $$2, int $$3, int $$4, boolean $$5) {
        this.drawString($$0, $$1.getVisualOrderText(), $$2, $$3, $$4, $$5);
    }

    public void drawWordWrap(Font $$0, FormattedText $$1, int $$2, int $$3, int $$4, int $$5) {
        this.drawWordWrap($$0, $$1, $$2, $$3, $$4, $$5, true);
    }

    public void drawWordWrap(Font $$0, FormattedText $$1, int $$2, int $$3, int $$4, int $$5, boolean $$6) {
        for (FormattedCharSequence $$7 : $$0.split($$1, $$4)) {
            this.drawString($$0, $$7, $$2, $$3, $$5, $$6);
            $$3 += $$0.lineHeight;
        }
    }

    public void drawStringWithBackdrop(Font $$0, Component $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$6 = this.minecraft.options.getBackgroundColor(0.0f);
        if ($$6 != 0) {
            int $$7 = 2;
            this.fill($$2 - 2, $$3 - 2, $$2 + $$4 + 2, $$3 + $$0.lineHeight + 2, ARGB.multiply($$6, $$5));
        }
        this.drawString($$0, $$1, $$2, $$3, $$5, true);
    }

    public void renderOutline(int $$0, int $$1, int $$2, int $$3, int $$4) {
        this.fill($$0, $$1, $$0 + $$2, $$1 + 1, $$4);
        this.fill($$0, $$1 + $$3 - 1, $$0 + $$2, $$1 + $$3, $$4);
        this.fill($$0, $$1 + 1, $$0 + 1, $$1 + $$3 - 1, $$4);
        this.fill($$0 + $$2 - 1, $$1 + 1, $$0 + $$2, $$1 + $$3 - 1, $$4);
    }

    public void blitSprite(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, int $$5) {
        this.blitSprite($$0, $$1, $$2, $$3, $$4, $$5, -1);
    }

    public void blitSprite(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, int $$5, float $$6) {
        this.blitSprite($$0, $$1, $$2, $$3, $$4, $$5, ARGB.color($$6, -1));
    }

    public void blitSprite(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        TextureAtlasSprite $$7 = this.sprites.getSprite($$1);
        GuiSpriteScaling $$8 = this.sprites.getSpriteScaling($$7);
        if ($$8 instanceof GuiSpriteScaling.Stretch) {
            this.blitSprite($$0, $$7, $$2, $$3, $$4, $$5, $$6);
        } else if ($$8 instanceof GuiSpriteScaling.Tile) {
            GuiSpriteScaling.Tile $$9 = (GuiSpriteScaling.Tile)$$8;
            this.blitTiledSprite($$0, $$7, $$2, $$3, $$4, $$5, 0, 0, $$9.width(), $$9.height(), $$9.width(), $$9.height(), $$6);
        } else if ($$8 instanceof GuiSpriteScaling.NineSlice) {
            GuiSpriteScaling.NineSlice $$10 = (GuiSpriteScaling.NineSlice)$$8;
            this.blitNineSlicedSprite($$0, $$7, $$10, $$2, $$3, $$4, $$5, $$6);
        }
    }

    public void blitSprite(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9) {
        this.blitSprite($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, -1);
    }

    public void blitSprite(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10) {
        TextureAtlasSprite $$11 = this.sprites.getSprite($$1);
        GuiSpriteScaling $$12 = this.sprites.getSpriteScaling($$11);
        if ($$12 instanceof GuiSpriteScaling.Stretch) {
            this.blitSprite($$0, $$11, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10);
        } else {
            this.enableScissor($$6, $$7, $$6 + $$8, $$7 + $$9);
            this.blitSprite($$0, $$1, $$6 - $$4, $$7 - $$5, $$2, $$3, $$10);
            this.disableScissor();
        }
    }

    public void blitSprite(RenderPipeline $$0, TextureAtlasSprite $$1, int $$2, int $$3, int $$4, int $$5) {
        this.blitSprite($$0, $$1, $$2, $$3, $$4, $$5, -1);
    }

    public void blitSprite(RenderPipeline $$0, TextureAtlasSprite $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        if ($$4 == 0 || $$5 == 0) {
            return;
        }
        this.innerBlit($$0, $$1.atlasLocation(), $$2, $$2 + $$4, $$3, $$3 + $$5, $$1.getU0(), $$1.getU1(), $$1.getV0(), $$1.getV1(), $$6);
    }

    private void blitSprite(RenderPipeline $$0, TextureAtlasSprite $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10) {
        if ($$8 == 0 || $$9 == 0) {
            return;
        }
        this.innerBlit($$0, $$1.atlasLocation(), $$6, $$6 + $$8, $$7, $$7 + $$9, $$1.getU((float)$$4 / (float)$$2), $$1.getU((float)($$4 + $$8) / (float)$$2), $$1.getV((float)$$5 / (float)$$3), $$1.getV((float)($$5 + $$9) / (float)$$3), $$10);
    }

    private void blitNineSlicedSprite(RenderPipeline $$0, TextureAtlasSprite $$1, GuiSpriteScaling.NineSlice $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        GuiSpriteScaling.NineSlice.Border $$8 = $$2.border();
        int $$9 = Math.min($$8.left(), $$5 / 2);
        int $$10 = Math.min($$8.right(), $$5 / 2);
        int $$11 = Math.min($$8.top(), $$6 / 2);
        int $$12 = Math.min($$8.bottom(), $$6 / 2);
        if ($$5 == $$2.width() && $$6 == $$2.height()) {
            this.blitSprite($$0, $$1, $$2.width(), $$2.height(), 0, 0, $$3, $$4, $$5, $$6, $$7);
            return;
        }
        if ($$6 == $$2.height()) {
            this.blitSprite($$0, $$1, $$2.width(), $$2.height(), 0, 0, $$3, $$4, $$9, $$6, $$7);
            this.blitNineSliceInnerSegment($$0, $$2, $$1, $$3 + $$9, $$4, $$5 - $$10 - $$9, $$6, $$9, 0, $$2.width() - $$10 - $$9, $$2.height(), $$2.width(), $$2.height(), $$7);
            this.blitSprite($$0, $$1, $$2.width(), $$2.height(), $$2.width() - $$10, 0, $$3 + $$5 - $$10, $$4, $$10, $$6, $$7);
            return;
        }
        if ($$5 == $$2.width()) {
            this.blitSprite($$0, $$1, $$2.width(), $$2.height(), 0, 0, $$3, $$4, $$5, $$11, $$7);
            this.blitNineSliceInnerSegment($$0, $$2, $$1, $$3, $$4 + $$11, $$5, $$6 - $$12 - $$11, 0, $$11, $$2.width(), $$2.height() - $$12 - $$11, $$2.width(), $$2.height(), $$7);
            this.blitSprite($$0, $$1, $$2.width(), $$2.height(), 0, $$2.height() - $$12, $$3, $$4 + $$6 - $$12, $$5, $$12, $$7);
            return;
        }
        this.blitSprite($$0, $$1, $$2.width(), $$2.height(), 0, 0, $$3, $$4, $$9, $$11, $$7);
        this.blitNineSliceInnerSegment($$0, $$2, $$1, $$3 + $$9, $$4, $$5 - $$10 - $$9, $$11, $$9, 0, $$2.width() - $$10 - $$9, $$11, $$2.width(), $$2.height(), $$7);
        this.blitSprite($$0, $$1, $$2.width(), $$2.height(), $$2.width() - $$10, 0, $$3 + $$5 - $$10, $$4, $$10, $$11, $$7);
        this.blitSprite($$0, $$1, $$2.width(), $$2.height(), 0, $$2.height() - $$12, $$3, $$4 + $$6 - $$12, $$9, $$12, $$7);
        this.blitNineSliceInnerSegment($$0, $$2, $$1, $$3 + $$9, $$4 + $$6 - $$12, $$5 - $$10 - $$9, $$12, $$9, $$2.height() - $$12, $$2.width() - $$10 - $$9, $$12, $$2.width(), $$2.height(), $$7);
        this.blitSprite($$0, $$1, $$2.width(), $$2.height(), $$2.width() - $$10, $$2.height() - $$12, $$3 + $$5 - $$10, $$4 + $$6 - $$12, $$10, $$12, $$7);
        this.blitNineSliceInnerSegment($$0, $$2, $$1, $$3, $$4 + $$11, $$9, $$6 - $$12 - $$11, 0, $$11, $$9, $$2.height() - $$12 - $$11, $$2.width(), $$2.height(), $$7);
        this.blitNineSliceInnerSegment($$0, $$2, $$1, $$3 + $$9, $$4 + $$11, $$5 - $$10 - $$9, $$6 - $$12 - $$11, $$9, $$11, $$2.width() - $$10 - $$9, $$2.height() - $$12 - $$11, $$2.width(), $$2.height(), $$7);
        this.blitNineSliceInnerSegment($$0, $$2, $$1, $$3 + $$5 - $$10, $$4 + $$11, $$10, $$6 - $$12 - $$11, $$2.width() - $$10, $$11, $$10, $$2.height() - $$12 - $$11, $$2.width(), $$2.height(), $$7);
    }

    private void blitNineSliceInnerSegment(RenderPipeline $$0, GuiSpriteScaling.NineSlice $$1, TextureAtlasSprite $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10, int $$11, int $$12, int $$13) {
        if ($$5 <= 0 || $$6 <= 0) {
            return;
        }
        if ($$1.stretchInner()) {
            this.innerBlit($$0, $$2.atlasLocation(), $$3, $$3 + $$5, $$4, $$4 + $$6, $$2.getU((float)$$7 / (float)$$11), $$2.getU((float)($$7 + $$9) / (float)$$11), $$2.getV((float)$$8 / (float)$$12), $$2.getV((float)($$8 + $$10) / (float)$$12), $$13);
        } else {
            this.blitTiledSprite($$0, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$12, $$13);
        }
    }

    private void blitTiledSprite(RenderPipeline $$0, TextureAtlasSprite $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10, int $$11, int $$12) {
        if ($$4 <= 0 || $$5 <= 0) {
            return;
        }
        if ($$8 <= 0 || $$9 <= 0) {
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + $$8 + "x" + $$9);
        }
        for (int $$13 = 0; $$13 < $$4; $$13 += $$8) {
            int $$14 = Math.min($$8, $$4 - $$13);
            for (int $$15 = 0; $$15 < $$5; $$15 += $$9) {
                int $$16 = Math.min($$9, $$5 - $$15);
                this.blitSprite($$0, $$1, $$10, $$11, $$6, $$7, $$2 + $$13, $$3 + $$15, $$14, $$16, $$12);
            }
        }
    }

    public void blit(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, float $$4, float $$5, int $$6, int $$7, int $$8, int $$9, int $$10) {
        this.blit($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$6, $$7, $$8, $$9, $$10);
    }

    public void blit(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, float $$4, float $$5, int $$6, int $$7, int $$8, int $$9) {
        this.blit($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$6, $$7, $$8, $$9);
    }

    public void blit(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, float $$4, float $$5, int $$6, int $$7, int $$8, int $$9, int $$10, int $$11) {
        this.blit($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, -1);
    }

    public void blit(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, float $$4, float $$5, int $$6, int $$7, int $$8, int $$9, int $$10, int $$11, int $$12) {
        this.innerBlit($$0, $$1, $$2, $$2 + $$6, $$3, $$3 + $$7, ($$4 + 0.0f) / (float)$$10, ($$4 + (float)$$8) / (float)$$10, ($$5 + 0.0f) / (float)$$11, ($$5 + (float)$$9) / (float)$$11, $$12);
    }

    public void blit(ResourceLocation $$0, int $$1, int $$2, int $$3, int $$4, float $$5, float $$6, float $$7, float $$8) {
        this.innerBlit(RenderPipelines.GUI_TEXTURED, $$0, $$1, $$3, $$2, $$4, $$5, $$6, $$7, $$8, -1);
    }

    private void innerBlit(RenderPipeline $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, int $$5, float $$6, float $$7, float $$8, float $$9, int $$10) {
        GpuTextureView $$11 = this.minecraft.getTextureManager().getTexture($$1).getTextureView();
        this.submitBlit($$0, $$11, $$2, $$4, $$3, $$5, $$6, $$7, $$8, $$9, $$10);
    }

    private void submitBlit(RenderPipeline $$0, GpuTextureView $$1, int $$2, int $$3, int $$4, int $$5, float $$6, float $$7, float $$8, float $$9, int $$10) {
        this.guiRenderState.submitGuiElement(new BlitRenderState($$0, TextureSetup.singleTexture($$1), new Matrix3x2f((Matrix3x2fc)this.pose), $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, this.scissorStack.peek()));
    }

    public void renderItem(ItemStack $$0, int $$1, int $$2) {
        this.renderItem(this.minecraft.player, this.minecraft.level, $$0, $$1, $$2, 0);
    }

    public void renderItem(ItemStack $$0, int $$1, int $$2, int $$3) {
        this.renderItem(this.minecraft.player, this.minecraft.level, $$0, $$1, $$2, $$3);
    }

    public void renderFakeItem(ItemStack $$0, int $$1, int $$2) {
        this.renderFakeItem($$0, $$1, $$2, 0);
    }

    public void renderFakeItem(ItemStack $$0, int $$1, int $$2, int $$3) {
        this.renderItem(null, this.minecraft.level, $$0, $$1, $$2, $$3);
    }

    public void renderItem(LivingEntity $$0, ItemStack $$1, int $$2, int $$3, int $$4) {
        this.renderItem($$0, $$0.level(), $$1, $$2, $$3, $$4);
    }

    private void renderItem(@Nullable LivingEntity $$0, @Nullable Level $$1, ItemStack $$2, int $$3, int $$4, int $$5) {
        if ($$2.isEmpty()) {
            return;
        }
        TrackingItemStackRenderState $$6 = new TrackingItemStackRenderState();
        this.minecraft.getItemModelResolver().updateForTopItem($$6, $$2, ItemDisplayContext.GUI, $$1, $$0, $$5);
        try {
            this.guiRenderState.submitItem(new GuiItemRenderState($$2.getItem().getName().toString(), new Matrix3x2f((Matrix3x2fc)this.pose), $$6, $$3, $$4, this.scissorStack.peek()));
        } catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Rendering item");
            CrashReportCategory $$9 = $$8.addCategory("Item being rendered");
            $$9.setDetail("Item Type", () -> String.valueOf($$2.getItem()));
            $$9.setDetail("Item Components", () -> String.valueOf($$2.getComponents()));
            $$9.setDetail("Item Foil", () -> String.valueOf($$2.hasFoil()));
            throw new ReportedException($$8);
        }
    }

    public void renderItemDecorations(Font $$0, ItemStack $$1, int $$2, int $$3) {
        this.renderItemDecorations($$0, $$1, $$2, $$3, null);
    }

    public void renderItemDecorations(Font $$0, ItemStack $$1, int $$2, int $$3, @Nullable String $$4) {
        if ($$1.isEmpty()) {
            return;
        }
        this.pose.pushMatrix();
        this.renderItemBar($$1, $$2, $$3);
        this.renderItemCooldown($$1, $$2, $$3);
        this.renderItemCount($$0, $$1, $$2, $$3, $$4);
        this.pose.popMatrix();
    }

    public void setTooltipForNextFrame(Component $$0, int $$1, int $$2) {
        this.setTooltipForNextFrame(List.of((Object)$$0.getVisualOrderText()), $$1, $$2);
    }

    public void setTooltipForNextFrame(List<FormattedCharSequence> $$0, int $$1, int $$2) {
        this.setTooltipForNextFrame(this.minecraft.font, $$0, DefaultTooltipPositioner.INSTANCE, $$1, $$2, false);
    }

    public void setTooltipForNextFrame(Font $$0, ItemStack $$1, int $$2, int $$3) {
        this.setTooltipForNextFrame($$0, Screen.getTooltipFromItem(this.minecraft, $$1), $$1.getTooltipImage(), $$2, $$3, $$1.get(DataComponents.TOOLTIP_STYLE));
    }

    public void setTooltipForNextFrame(Font $$0, List<Component> $$1, Optional<TooltipComponent> $$2, int $$3, int $$4) {
        this.setTooltipForNextFrame($$0, $$1, $$2, $$3, $$4, null);
    }

    public void setTooltipForNextFrame(Font $$0, List<Component> $$12, Optional<TooltipComponent> $$2, int $$3, int $$4, @Nullable ResourceLocation $$5) {
        List<ClientTooltipComponent> $$6 = $$12.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Util.toMutableList());
        $$2.ifPresent($$1 -> $$6.add($$6.isEmpty() ? 0 : 1, ClientTooltipComponent.create($$1)));
        this.setTooltipForNextFrameInternal($$0, $$6, $$3, $$4, DefaultTooltipPositioner.INSTANCE, $$5, false);
    }

    public void setTooltipForNextFrame(Font $$0, Component $$1, int $$2, int $$3) {
        this.setTooltipForNextFrame($$0, $$1, $$2, $$3, null);
    }

    public void setTooltipForNextFrame(Font $$0, Component $$1, int $$2, int $$3, @Nullable ResourceLocation $$4) {
        this.setTooltipForNextFrame($$0, List.of((Object)$$1.getVisualOrderText()), $$2, $$3, $$4);
    }

    public void setComponentTooltipForNextFrame(Font $$0, List<Component> $$1, int $$2, int $$3) {
        this.setComponentTooltipForNextFrame($$0, $$1, $$2, $$3, null);
    }

    public void setComponentTooltipForNextFrame(Font $$0, List<Component> $$1, int $$2, int $$3, @Nullable ResourceLocation $$4) {
        this.setTooltipForNextFrameInternal($$0, $$1.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList(), $$2, $$3, DefaultTooltipPositioner.INSTANCE, $$4, false);
    }

    public void setTooltipForNextFrame(Font $$0, List<? extends FormattedCharSequence> $$1, int $$2, int $$3) {
        this.setTooltipForNextFrame($$0, $$1, $$2, $$3, null);
    }

    public void setTooltipForNextFrame(Font $$0, List<? extends FormattedCharSequence> $$1, int $$2, int $$3, @Nullable ResourceLocation $$4) {
        this.setTooltipForNextFrameInternal($$0, $$1.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), $$2, $$3, DefaultTooltipPositioner.INSTANCE, $$4, false);
    }

    public void setTooltipForNextFrame(Font $$0, List<FormattedCharSequence> $$1, ClientTooltipPositioner $$2, int $$3, int $$4, boolean $$5) {
        this.setTooltipForNextFrameInternal($$0, $$1.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), $$3, $$4, $$2, null, $$5);
    }

    private void setTooltipForNextFrameInternal(Font $$0, List<ClientTooltipComponent> $$1, int $$2, int $$3, ClientTooltipPositioner $$4, @Nullable ResourceLocation $$5, boolean $$6) {
        if ($$1.isEmpty()) {
            return;
        }
        if (this.deferredTooltip == null || $$6) {
            this.deferredTooltip = () -> this.renderTooltip($$0, $$1, $$2, $$3, $$4, $$5);
        }
    }

    public void renderTooltip(Font $$0, List<ClientTooltipComponent> $$1, int $$2, int $$3, ClientTooltipPositioner $$4, @Nullable ResourceLocation $$5) {
        int $$6 = 0;
        int $$7 = $$1.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent $$8 : $$1) {
            int $$9 = $$8.getWidth($$0);
            if ($$9 > $$6) {
                $$6 = $$9;
            }
            $$7 += $$8.getHeight($$0);
        }
        int $$10 = $$6;
        int $$11 = $$7;
        Vector2ic $$12 = $$4.positionTooltip(this.guiWidth(), this.guiHeight(), $$2, $$3, $$10, $$11);
        int $$13 = $$12.x();
        int $$14 = $$12.y();
        this.pose.pushMatrix();
        TooltipRenderUtil.renderTooltipBackground(this, $$13, $$14, $$10, $$11, $$5);
        int $$15 = $$14;
        for (int $$16 = 0; $$16 < $$1.size(); ++$$16) {
            ClientTooltipComponent $$17 = $$1.get($$16);
            $$17.renderText(this, $$0, $$13, $$15);
            $$15 += $$17.getHeight($$0) + ($$16 == 0 ? 2 : 0);
        }
        $$15 = $$14;
        for (int $$18 = 0; $$18 < $$1.size(); ++$$18) {
            ClientTooltipComponent $$19 = $$1.get($$18);
            $$19.renderImage($$0, $$13, $$15, $$10, $$11, this);
            $$15 += $$19.getHeight($$0) + ($$18 == 0 ? 2 : 0);
        }
        this.pose.popMatrix();
    }

    public void renderDeferredTooltip() {
        if (this.deferredTooltip != null) {
            this.nextStratum();
            this.deferredTooltip.run();
            this.deferredTooltip = null;
        }
    }

    private void renderItemBar(ItemStack $$0, int $$1, int $$2) {
        if ($$0.isBarVisible()) {
            int $$3 = $$1 + 2;
            int $$4 = $$2 + 13;
            this.fill(RenderPipelines.GUI, $$3, $$4, $$3 + 13, $$4 + 2, -16777216);
            this.fill(RenderPipelines.GUI, $$3, $$4, $$3 + $$0.getBarWidth(), $$4 + 1, ARGB.opaque($$0.getBarColor()));
        }
    }

    private void renderItemCount(Font $$0, ItemStack $$1, int $$2, int $$3, @Nullable String $$4) {
        if ($$1.getCount() != 1 || $$4 != null) {
            String $$5 = $$4 == null ? String.valueOf($$1.getCount()) : $$4;
            this.drawString($$0, $$5, $$2 + 19 - 2 - $$0.width($$5), $$3 + 6 + 3, -1, true);
        }
    }

    private void renderItemCooldown(ItemStack $$0, int $$1, int $$2) {
        float $$4;
        LocalPlayer $$3 = this.minecraft.player;
        float f = $$4 = $$3 == null ? 0.0f : $$3.getCooldowns().getCooldownPercent($$0, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        if ($$4 > 0.0f) {
            int $$5 = $$2 + Mth.floor(16.0f * (1.0f - $$4));
            int $$6 = $$5 + Mth.ceil(16.0f * $$4);
            this.fill(RenderPipelines.GUI, $$1, $$5, $$1 + 16, $$6, Integer.MAX_VALUE);
        }
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void renderComponentHoverEffect(Font $$0, @Nullable Style $$1, int $$2, int $$3) {
        if ($$1 == null) return;
        if ($$1.getHoverEvent() == null) {
            return;
        }
        HoverEvent hoverEvent = $$1.getHoverEvent();
        Objects.requireNonNull(hoverEvent);
        HoverEvent hoverEvent2 = hoverEvent;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{HoverEvent.ShowItem.class, HoverEvent.ShowEntity.class, HoverEvent.ShowText.class}, (Object)hoverEvent2, (int)n)) {
            case 0: {
                HoverEvent.ShowItem showItem = (HoverEvent.ShowItem)hoverEvent2;
                try {
                    ItemStack itemStack;
                    ItemStack $$4 = itemStack = showItem.item();
                    this.setTooltipForNextFrame($$0, $$4, $$2, $$3);
                    return;
                } catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
            }
            case 1: {
                HoverEvent.ShowEntity showEntity = (HoverEvent.ShowEntity)hoverEvent2;
                {
                    HoverEvent.EntityTooltipInfo entityTooltipInfo;
                    HoverEvent.EntityTooltipInfo $$5 = entityTooltipInfo = showEntity.entity();
                    if (!this.minecraft.options.advancedItemTooltips) return;
                    this.setComponentTooltipForNextFrame($$0, $$5.getTooltipLines(), $$2, $$3);
                    return;
                }
            }
            case 2: {
                HoverEvent.ShowText showText = (HoverEvent.ShowText)hoverEvent2;
                {
                    Component component;
                    Component $$6 = component = showText.value();
                    this.setTooltipForNextFrame($$0, $$0.split($$6, Math.max(this.guiWidth() / 2, 200)), $$2, $$3);
                    return;
                }
            }
        }
    }

    public void submitMapRenderState(MapRenderState $$0) {
        Minecraft $$1 = Minecraft.getInstance();
        TextureManager $$2 = $$1.getTextureManager();
        GpuTextureView $$3 = $$2.getTexture($$0.texture).getTextureView();
        this.submitBlit(RenderPipelines.GUI_TEXTURED, $$3, 0, 0, 128, 128, 0.0f, 1.0f, 0.0f, 1.0f, -1);
        for (MapRenderState.MapDecorationRenderState $$4 : $$0.decorations) {
            if (!$$4.renderOnFrame) continue;
            this.pose.pushMatrix();
            this.pose.translate((float)$$4.x / 2.0f + 64.0f, (float)$$4.y / 2.0f + 64.0f);
            this.pose.rotate((float)Math.PI / 180 * (float)$$4.rot * 360.0f / 16.0f);
            this.pose.scale(4.0f, 4.0f);
            this.pose.translate(-0.125f, 0.125f);
            TextureAtlasSprite $$5 = $$4.atlasSprite;
            if ($$5 != null) {
                GpuTextureView $$6 = $$2.getTexture($$5.atlasLocation()).getTextureView();
                this.submitBlit(RenderPipelines.GUI_TEXTURED, $$6, -1, -1, 1, 1, $$5.getU0(), $$5.getU1(), $$5.getV1(), $$5.getV0(), -1);
            }
            this.pose.popMatrix();
            if ($$4.name == null) continue;
            Font $$7 = $$1.font;
            float $$8 = $$7.width($$4.name);
            float f = 25.0f / $$8;
            Objects.requireNonNull($$7);
            float $$9 = Mth.clamp(f, 0.0f, 6.0f / 9.0f);
            this.pose.pushMatrix();
            this.pose.translate((float)$$4.x / 2.0f + 64.0f - $$8 * $$9 / 2.0f, (float)$$4.y / 2.0f + 64.0f + 4.0f);
            this.pose.scale($$9, $$9);
            this.guiRenderState.submitText(new GuiTextRenderState($$7, $$4.name.getVisualOrderText(), new Matrix3x2f((Matrix3x2fc)this.pose), 0, 0, -1, Integer.MIN_VALUE, false, this.scissorStack.peek()));
            this.pose.popMatrix();
        }
    }

    public void submitEntityRenderState(EntityRenderState $$0, float $$1, Vector3f $$2, Quaternionf $$3, @Nullable Quaternionf $$4, int $$5, int $$6, int $$7, int $$8) {
        this.guiRenderState.submitPicturesInPictureState(new GuiEntityRenderState($$0, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$1, this.scissorStack.peek()));
    }

    public void submitSkinRenderState(PlayerModel $$0, ResourceLocation $$1, float $$2, float $$3, float $$4, float $$5, int $$6, int $$7, int $$8, int $$9) {
        this.guiRenderState.submitPicturesInPictureState(new GuiSkinRenderState($$0, $$1, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$2, this.scissorStack.peek()));
    }

    public void submitBookModelRenderState(BookModel $$0, ResourceLocation $$1, float $$2, float $$3, float $$4, int $$5, int $$6, int $$7, int $$8) {
        this.guiRenderState.submitPicturesInPictureState(new GuiBookModelRenderState($$0, $$1, $$3, $$4, $$5, $$6, $$7, $$8, $$2, this.scissorStack.peek()));
    }

    public void submitBannerPatternRenderState(ModelPart $$0, DyeColor $$1, BannerPatternLayers $$2, int $$3, int $$4, int $$5, int $$6) {
        this.guiRenderState.submitPicturesInPictureState(new GuiBannerResultRenderState($$0, $$1, $$2, $$3, $$4, $$5, $$6, this.scissorStack.peek()));
    }

    public void submitSignRenderState(Model $$0, float $$1, WoodType $$2, int $$3, int $$4, int $$5, int $$6) {
        this.guiRenderState.submitPicturesInPictureState(new GuiSignRenderState($$0, $$2, $$3, $$4, $$5, $$6, $$1, this.scissorStack.peek()));
    }

    public void submitProfilerChartRenderState(List<ResultField> $$0, int $$1, int $$2, int $$3, int $$4) {
        this.guiRenderState.submitPicturesInPictureState(new GuiProfilerChartRenderState($$0, $$1, $$2, $$3, $$4, this.scissorStack.peek()));
    }

    static class ScissorStack {
        private final Deque<ScreenRectangle> stack = new ArrayDeque<ScreenRectangle>();

        ScissorStack() {
        }

        public ScreenRectangle push(ScreenRectangle $$0) {
            ScreenRectangle $$1 = this.stack.peekLast();
            if ($$1 != null) {
                ScreenRectangle $$2 = (ScreenRectangle)((Object)Objects.requireNonNullElse((Object)((Object)$$0.intersection($$1)), (Object)((Object)ScreenRectangle.empty())));
                this.stack.addLast($$2);
                return $$2;
            }
            this.stack.addLast($$0);
            return $$0;
        }

        @Nullable
        public ScreenRectangle pop() {
            if (this.stack.isEmpty()) {
                throw new IllegalStateException("Scissor stack underflow");
            }
            this.stack.removeLast();
            return this.stack.peekLast();
        }

        @Nullable
        public ScreenRectangle peek() {
            return this.stack.peekLast();
        }

        public boolean containsPoint(int $$0, int $$1) {
            if (this.stack.isEmpty()) {
                return true;
            }
            return this.stack.peek().containsPoint($$0, $$1);
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class LoomScreen
extends AbstractContainerScreen<LoomMenu> {
    private static final ResourceLocation BANNER_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/banner");
    private static final ResourceLocation DYE_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/dye");
    private static final ResourceLocation PATTERN_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/banner_pattern");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/scroller_disabled");
    private static final ResourceLocation PATTERN_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/pattern_selected");
    private static final ResourceLocation PATTERN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/pattern_highlighted");
    private static final ResourceLocation PATTERN_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/pattern");
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/error");
    private static final ResourceLocation BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/loom.png");
    private static final int PATTERN_COLUMNS = 4;
    private static final int PATTERN_ROWS = 4;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int PATTERN_IMAGE_SIZE = 14;
    private static final int SCROLLER_FULL_HEIGHT = 56;
    private static final int PATTERNS_X = 60;
    private static final int PATTERNS_Y = 13;
    private static final float BANNER_PATTERN_TEXTURE_SIZE = 64.0f;
    private static final float BANNER_PATTERN_WIDTH = 21.0f;
    private static final float BANNER_PATTERN_HEIGHT = 40.0f;
    private ModelPart flag;
    @Nullable
    private BannerPatternLayers resultBannerPatterns;
    private ItemStack bannerStack = ItemStack.EMPTY;
    private ItemStack dyeStack = ItemStack.EMPTY;
    private ItemStack patternStack = ItemStack.EMPTY;
    private boolean displayPatterns;
    private boolean hasMaxPatterns;
    private float scrollOffs;
    private boolean scrolling;
    private int startRow;

    public LoomScreen(LoomMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        $$0.registerUpdateListener(this::containerChanged);
        this.titleLabelY -= 2;
    }

    @Override
    protected void init() {
        super.init();
        this.flag = this.minecraft.getEntityModels().bakeLayer(ModelLayers.STANDING_BANNER_FLAG).getChild("flag");
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    private int totalRowCount() {
        return Mth.positiveCeilDiv(((LoomMenu)this.menu).getSelectablePatterns().size(), 4);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        $$0.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        Slot $$6 = ((LoomMenu)this.menu).getBannerSlot();
        Slot $$7 = ((LoomMenu)this.menu).getDyeSlot();
        Slot $$8 = ((LoomMenu)this.menu).getPatternSlot();
        Slot $$9 = ((LoomMenu)this.menu).getResultSlot();
        if (!$$6.hasItem()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BANNER_SLOT_SPRITE, $$4 + $$6.x, $$5 + $$6.y, 16, 16);
        }
        if (!$$7.hasItem()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DYE_SLOT_SPRITE, $$4 + $$7.x, $$5 + $$7.y, 16, 16);
        }
        if (!$$8.hasItem()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, PATTERN_SLOT_SPRITE, $$4 + $$8.x, $$5 + $$8.y, 16, 16);
        }
        int $$10 = (int)(41.0f * this.scrollOffs);
        ResourceLocation $$11 = this.displayPatterns ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$11, $$4 + 119, $$5 + 13 + $$10, 12, 15);
        if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
            DyeColor $$12 = ((BannerItem)$$9.getItem().getItem()).getColor();
            int $$13 = $$4 + 141;
            int $$14 = $$5 + 8;
            $$0.submitBannerPatternRenderState(this.flag, $$12, this.resultBannerPatterns, $$13, $$14, $$13 + 20, $$14 + 40);
        } else if (this.hasMaxPatterns) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, $$4 + $$9.x - 5, $$5 + $$9.y - 5, 26, 26);
        }
        if (this.displayPatterns) {
            int $$15 = $$4 + 60;
            int $$16 = $$5 + 13;
            List<Holder<BannerPattern>> $$17 = ((LoomMenu)this.menu).getSelectablePatterns();
            block0: for (int $$18 = 0; $$18 < 4; ++$$18) {
                for (int $$19 = 0; $$19 < 4; ++$$19) {
                    ResourceLocation $$27;
                    boolean $$24;
                    int $$20 = $$18 + this.startRow;
                    int $$21 = $$20 * 4 + $$19;
                    if ($$21 >= $$17.size()) break block0;
                    int $$22 = $$15 + $$19 * 14;
                    int $$23 = $$16 + $$18 * 14;
                    boolean bl = $$24 = $$2 >= $$22 && $$3 >= $$23 && $$2 < $$22 + 14 && $$3 < $$23 + 14;
                    if ($$21 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
                        ResourceLocation $$25 = PATTERN_SELECTED_SPRITE;
                    } else if ($$24) {
                        ResourceLocation $$26 = PATTERN_HIGHLIGHTED_SPRITE;
                    } else {
                        $$27 = PATTERN_SPRITE;
                    }
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$27, $$22, $$23, 14, 14);
                    TextureAtlasSprite $$28 = Sheets.getBannerMaterial($$17.get($$21)).sprite();
                    this.renderBannerOnButton($$0, $$22, $$23, $$28);
                }
            }
        }
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
    }

    private void renderBannerOnButton(GuiGraphics $$0, int $$1, int $$2, TextureAtlasSprite $$3) {
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)($$1 + 4), (float)($$2 + 2));
        float $$4 = $$3.getU0();
        float $$5 = $$4 + ($$3.getU1() - $$3.getU0()) * 21.0f / 64.0f;
        float $$6 = $$3.getV1() - $$3.getV0();
        float $$7 = $$3.getV0() + $$6 / 64.0f;
        float $$8 = $$7 + $$6 * 40.0f / 64.0f;
        int $$9 = 5;
        int $$10 = 10;
        $$0.fill(0, 0, 5, 10, DyeColor.GRAY.getTextureDiffuseColor());
        $$0.blit($$3.atlasLocation(), 0, 0, 5, 10, $$4, $$5, $$7, $$8);
        $$0.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        this.scrolling = false;
        if (this.displayPatterns) {
            int $$3 = this.leftPos + 60;
            int $$4 = this.topPos + 13;
            for (int $$5 = 0; $$5 < 4; ++$$5) {
                for (int $$6 = 0; $$6 < 4; ++$$6) {
                    double $$7 = $$0 - (double)($$3 + $$6 * 14);
                    double $$8 = $$1 - (double)($$4 + $$5 * 14);
                    int $$9 = $$5 + this.startRow;
                    int $$10 = $$9 * 4 + $$6;
                    if (!($$7 >= 0.0) || !($$8 >= 0.0) || !($$7 < 14.0) || !($$8 < 14.0) || !((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, $$10)) continue;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                    this.minecraft.gameMode.handleInventoryButtonClick(((LoomMenu)this.menu).containerId, $$10);
                    return true;
                }
            }
            $$3 = this.leftPos + 119;
            $$4 = this.topPos + 9;
            if ($$0 >= (double)$$3 && $$0 < (double)($$3 + 12) && $$1 >= (double)$$4 && $$1 < (double)($$4 + 56)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        int $$5 = this.totalRowCount() - 4;
        if (this.scrolling && this.displayPatterns && $$5 > 0) {
            int $$6 = this.topPos + 13;
            int $$7 = $$6 + 56;
            this.scrollOffs = ((float)$$1 - (float)$$6 - 7.5f) / ((float)($$7 - $$6) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startRow = Math.max((int)((double)(this.scrollOffs * (float)$$5) + 0.5), 0);
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (super.mouseScrolled($$0, $$1, $$2, $$3)) {
            return true;
        }
        int $$4 = this.totalRowCount() - 4;
        if (this.displayPatterns && $$4 > 0) {
            float $$5 = (float)$$3 / (float)$$4;
            this.scrollOffs = Mth.clamp(this.scrollOffs - $$5, 0.0f, 1.0f);
            this.startRow = Math.max((int)(this.scrollOffs * (float)$$4 + 0.5f), 0);
        }
        return true;
    }

    @Override
    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        return $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
    }

    private void containerChanged() {
        ItemStack $$0 = ((LoomMenu)this.menu).getResultSlot().getItem();
        this.resultBannerPatterns = $$0.isEmpty() ? null : $$0.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        ItemStack $$1 = ((LoomMenu)this.menu).getBannerSlot().getItem();
        ItemStack $$2 = ((LoomMenu)this.menu).getDyeSlot().getItem();
        ItemStack $$3 = ((LoomMenu)this.menu).getPatternSlot().getItem();
        BannerPatternLayers $$4 = $$1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        boolean bl = this.hasMaxPatterns = $$4.layers().size() >= 6;
        if (this.hasMaxPatterns) {
            this.resultBannerPatterns = null;
        }
        if (!(ItemStack.matches($$1, this.bannerStack) && ItemStack.matches($$2, this.dyeStack) && ItemStack.matches($$3, this.patternStack))) {
            boolean bl2 = this.displayPatterns = !$$1.isEmpty() && !$$2.isEmpty() && !this.hasMaxPatterns && !((LoomMenu)this.menu).getSelectablePatterns().isEmpty();
        }
        if (this.startRow >= this.totalRowCount()) {
            this.startRow = 0;
            this.scrollOffs = 0.0f;
        }
        this.bannerStack = $$1.copy();
        this.dyeStack = $$2.copy();
        this.patternStack = $$3.copy();
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class AdvancementTab {
    private final Minecraft minecraft;
    private final AdvancementsScreen screen;
    private final AdvancementTabType type;
    private final int index;
    private final AdvancementNode rootNode;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final Component title;
    private final AdvancementWidget root;
    private final Map<AdvancementHolder, AdvancementWidget> widgets = Maps.newLinkedHashMap();
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public AdvancementTab(Minecraft $$0, AdvancementsScreen $$1, AdvancementTabType $$2, int $$3, AdvancementNode $$4, DisplayInfo $$5) {
        this.minecraft = $$0;
        this.screen = $$1;
        this.type = $$2;
        this.index = $$3;
        this.rootNode = $$4;
        this.display = $$5;
        this.icon = $$5.getIcon();
        this.title = $$5.getTitle();
        this.root = new AdvancementWidget(this, $$0, $$4, $$5);
        this.addWidget(this.root, $$4.holder());
    }

    public AdvancementTabType getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public AdvancementNode getRootNode() {
        return this.rootNode;
    }

    public Component getTitle() {
        return this.title;
    }

    public DisplayInfo getDisplay() {
        return this.display;
    }

    public void drawTab(GuiGraphics $$0, int $$1, int $$2, boolean $$3) {
        this.type.draw($$0, $$1, $$2, $$3, this.index);
    }

    public void drawIcon(GuiGraphics $$0, int $$1, int $$2) {
        this.type.drawIcon($$0, $$1, $$2, this.index, this.icon);
    }

    public void drawContents(GuiGraphics $$0, int $$1, int $$2) {
        if (!this.centered) {
            this.scrollX = 117 - (this.maxX + this.minX) / 2;
            this.scrollY = 56 - (this.maxY + this.minY) / 2;
            this.centered = true;
        }
        $$0.enableScissor($$1, $$2, $$1 + 234, $$2 + 113);
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)$$1, (float)$$2);
        ResourceLocation $$3 = this.display.getBackground().map(ClientAsset::texturePath).orElse(TextureManager.INTENTIONAL_MISSING_TEXTURE);
        int $$4 = Mth.floor(this.scrollX);
        int $$5 = Mth.floor(this.scrollY);
        int $$6 = $$4 % 16;
        int $$7 = $$5 % 16;
        for (int $$8 = -1; $$8 <= 15; ++$$8) {
            for (int $$9 = -1; $$9 <= 8; ++$$9) {
                $$0.blit(RenderPipelines.GUI_TEXTURED, $$3, $$6 + 16 * $$8, $$7 + 16 * $$9, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.root.drawConnectivity($$0, $$4, $$5, true);
        this.root.drawConnectivity($$0, $$4, $$5, false);
        this.root.draw($$0, $$4, $$5);
        $$0.pose().popMatrix();
        $$0.disableScissor();
    }

    public void drawTooltips(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        $$0.fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0f) << 24);
        boolean $$5 = false;
        int $$6 = Mth.floor(this.scrollX);
        int $$7 = Mth.floor(this.scrollY);
        if ($$1 > 0 && $$1 < 234 && $$2 > 0 && $$2 < 113) {
            for (AdvancementWidget $$8 : this.widgets.values()) {
                if (!$$8.isMouseOver($$6, $$7, $$1, $$2)) continue;
                $$5 = true;
                $$8.drawHover($$0, $$6, $$7, this.fade, $$3, $$4);
                break;
            }
        }
        this.fade = $$5 ? Mth.clamp(this.fade + 0.02f, 0.0f, 0.3f) : Mth.clamp(this.fade - 0.04f, 0.0f, 1.0f);
    }

    public boolean isMouseOver(int $$0, int $$1, double $$2, double $$3) {
        return this.type.isMouseOver($$0, $$1, this.index, $$2, $$3);
    }

    @Nullable
    public static AdvancementTab create(Minecraft $$0, AdvancementsScreen $$1, int $$2, AdvancementNode $$3) {
        Optional<DisplayInfo> $$4 = $$3.advancement().display();
        if ($$4.isEmpty()) {
            return null;
        }
        for (AdvancementTabType $$5 : AdvancementTabType.values()) {
            if ($$2 >= $$5.getMax()) {
                $$2 -= $$5.getMax();
                continue;
            }
            return new AdvancementTab($$0, $$1, $$5, $$2, $$3, $$4.get());
        }
        return null;
    }

    public void scroll(double $$0, double $$1) {
        if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + $$0, (double)(-(this.maxX - 234)), 0.0);
        }
        if (this.maxY - this.minY > 113) {
            this.scrollY = Mth.clamp(this.scrollY + $$1, (double)(-(this.maxY - 113)), 0.0);
        }
    }

    public void addAdvancement(AdvancementNode $$0) {
        Optional<DisplayInfo> $$1 = $$0.advancement().display();
        if ($$1.isEmpty()) {
            return;
        }
        AdvancementWidget $$2 = new AdvancementWidget(this, this.minecraft, $$0, $$1.get());
        this.addWidget($$2, $$0.holder());
    }

    private void addWidget(AdvancementWidget $$0, AdvancementHolder $$1) {
        this.widgets.put($$1, $$0);
        int $$2 = $$0.getX();
        int $$3 = $$2 + 28;
        int $$4 = $$0.getY();
        int $$5 = $$4 + 27;
        this.minX = Math.min(this.minX, $$2);
        this.maxX = Math.max(this.maxX, $$3);
        this.minY = Math.min(this.minY, $$4);
        this.maxY = Math.max(this.maxY, $$5);
        for (AdvancementWidget $$6 : this.widgets.values()) {
            $$6.attachToParent();
        }
    }

    @Nullable
    public AdvancementWidget getWidget(AdvancementHolder $$0) {
        return this.widgets.get((Object)$$0);
    }

    public AdvancementsScreen getScreen() {
        return this.screen;
    }
}


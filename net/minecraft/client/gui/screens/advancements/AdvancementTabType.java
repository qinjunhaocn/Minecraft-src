/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

final class AdvancementTabType
extends Enum<AdvancementTabType> {
    public static final /* enum */ AdvancementTabType ABOVE = new AdvancementTabType(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_above_left_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_above_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_above_right_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_above_left"), ResourceLocation.withDefaultNamespace("advancements/tab_above_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_above_right")), 28, 32, 8);
    public static final /* enum */ AdvancementTabType BELOW = new AdvancementTabType(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_below_left_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_below_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_below_right_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_below_left"), ResourceLocation.withDefaultNamespace("advancements/tab_below_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_below_right")), 28, 32, 8);
    public static final /* enum */ AdvancementTabType LEFT = new AdvancementTabType(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_left_top_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_left_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_left_bottom_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_left_top"), ResourceLocation.withDefaultNamespace("advancements/tab_left_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_left_bottom")), 32, 28, 5);
    public static final /* enum */ AdvancementTabType RIGHT = new AdvancementTabType(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_right_top_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_right_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_right_bottom_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_right_top"), ResourceLocation.withDefaultNamespace("advancements/tab_right_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_right_bottom")), 32, 28, 5);
    private final Sprites selectedSprites;
    private final Sprites unselectedSprites;
    private final int width;
    private final int height;
    private final int max;
    private static final /* synthetic */ AdvancementTabType[] $VALUES;

    public static AdvancementTabType[] values() {
        return (AdvancementTabType[])$VALUES.clone();
    }

    public static AdvancementTabType valueOf(String $$0) {
        return Enum.valueOf(AdvancementTabType.class, $$0);
    }

    private AdvancementTabType(Sprites $$0, Sprites $$1, int $$2, int $$3, int $$4) {
        this.selectedSprites = $$0;
        this.unselectedSprites = $$1;
        this.width = $$2;
        this.height = $$3;
        this.max = $$4;
    }

    public int getMax() {
        return this.max;
    }

    public void draw(GuiGraphics $$0, int $$1, int $$2, boolean $$3, int $$4) {
        ResourceLocation $$8;
        Sprites $$5;
        Sprites sprites = $$5 = $$3 ? this.selectedSprites : this.unselectedSprites;
        if ($$4 == 0) {
            ResourceLocation $$6 = $$5.first();
        } else if ($$4 == this.max - 1) {
            ResourceLocation $$7 = $$5.last();
        } else {
            $$8 = $$5.middle();
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$8, $$1 + this.getX($$4), $$2 + this.getY($$4), this.width, this.height);
    }

    public void drawIcon(GuiGraphics $$0, int $$1, int $$2, int $$3, ItemStack $$4) {
        int $$5 = $$1 + this.getX($$3);
        int $$6 = $$2 + this.getY($$3);
        switch (this.ordinal()) {
            case 0: {
                $$5 += 6;
                $$6 += 9;
                break;
            }
            case 1: {
                $$5 += 6;
                $$6 += 6;
                break;
            }
            case 2: {
                $$5 += 10;
                $$6 += 5;
                break;
            }
            case 3: {
                $$5 += 6;
                $$6 += 5;
            }
        }
        $$0.renderFakeItem($$4, $$5, $$6);
    }

    public int getX(int $$0) {
        switch (this.ordinal()) {
            case 0: {
                return (this.width + 4) * $$0;
            }
            case 1: {
                return (this.width + 4) * $$0;
            }
            case 2: {
                return -this.width + 4;
            }
            case 3: {
                return 248;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + String.valueOf((Object)this));
    }

    public int getY(int $$0) {
        switch (this.ordinal()) {
            case 0: {
                return -this.height + 4;
            }
            case 1: {
                return 136;
            }
            case 2: {
                return this.height * $$0;
            }
            case 3: {
                return this.height * $$0;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + String.valueOf((Object)this));
    }

    public boolean isMouseOver(int $$0, int $$1, int $$2, double $$3, double $$4) {
        int $$5 = $$0 + this.getX($$2);
        int $$6 = $$1 + this.getY($$2);
        return $$3 > (double)$$5 && $$3 < (double)($$5 + this.width) && $$4 > (double)$$6 && $$4 < (double)($$6 + this.height);
    }

    private static /* synthetic */ AdvancementTabType[] b() {
        return new AdvancementTabType[]{ABOVE, BELOW, LEFT, RIGHT};
    }

    static {
        $VALUES = AdvancementTabType.b();
    }

    record Sprites(ResourceLocation first, ResourceLocation middle, ResourceLocation last) {
    }
}


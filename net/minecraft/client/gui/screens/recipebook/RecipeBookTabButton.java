/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class RecipeBookTabButton
extends StateSwitchingButton {
    private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/tab"), ResourceLocation.withDefaultNamespace("recipe_book/tab_selected"));
    private final RecipeBookComponent.TabInfo tabInfo;
    private static final float ANIMATION_TIME = 15.0f;
    private float animationTime;

    public RecipeBookTabButton(RecipeBookComponent.TabInfo $$0) {
        super(0, 0, 35, 27, false);
        this.tabInfo = $$0;
        this.initTextureValues(SPRITES);
    }

    public void startAnimation(ClientRecipeBook $$0, boolean $$1) {
        RecipeCollection.CraftableStatus $$2 = $$1 ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY;
        List<RecipeCollection> $$3 = $$0.getCollection(this.tabInfo.category());
        for (RecipeCollection $$4 : $$3) {
            for (RecipeDisplayEntry $$5 : $$4.getSelectedRecipes($$2)) {
                if (!$$0.willHighlight($$5.id())) continue;
                this.animationTime = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.sprites == null) {
            return;
        }
        if (this.animationTime > 0.0f) {
            float $$4 = 1.0f + 0.1f * (float)Math.sin(this.animationTime / 15.0f * (float)Math.PI);
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
            $$0.pose().scale(1.0f, $$4);
            $$0.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
        }
        ResourceLocation $$5 = this.sprites.get(true, this.isStateTriggered);
        int $$6 = this.getX();
        if (this.isStateTriggered) {
            $$6 -= 2;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$5, $$6, this.getY(), this.width, this.height);
        this.renderIcon($$0);
        if (this.animationTime > 0.0f) {
            $$0.pose().popMatrix();
            this.animationTime -= $$3;
        }
    }

    private void renderIcon(GuiGraphics $$0) {
        int $$1;
        int n = $$1 = this.isStateTriggered ? -2 : 0;
        if (this.tabInfo.secondaryIcon().isPresent()) {
            $$0.renderFakeItem(this.tabInfo.primaryIcon(), this.getX() + 3 + $$1, this.getY() + 5);
            $$0.renderFakeItem(this.tabInfo.secondaryIcon().get(), this.getX() + 14 + $$1, this.getY() + 5);
        } else {
            $$0.renderFakeItem(this.tabInfo.primaryIcon(), this.getX() + 9 + $$1, this.getY() + 5);
        }
    }

    public ExtendedRecipeBookCategory getCategory() {
        return this.tabInfo.category();
    }

    public boolean updateVisibility(ClientRecipeBook $$0) {
        List<RecipeCollection> $$1 = $$0.getCollection(this.tabInfo.category());
        this.visible = false;
        for (RecipeCollection $$2 : $$1) {
            if (!$$2.hasAnySelected()) continue;
            this.visible = true;
            break;
        }
        return this.visible;
    }
}


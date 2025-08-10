/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class InventoryScreen
extends AbstractRecipeBookScreen<InventoryMenu> {
    private float xMouse;
    private float yMouse;
    private boolean buttonClicked;
    private final EffectsInInventory effects;

    public InventoryScreen(Player $$0) {
        super($$0.inventoryMenu, new CraftingRecipeBookComponent($$0.inventoryMenu), $$0.getInventory(), Component.translatable("container.crafting"));
        this.titleLabelX = 97;
        this.effects = new EffectsInInventory(this);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.minecraft.player.hasInfiniteMaterials()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()));
        }
    }

    @Override
    protected void init() {
        if (this.minecraft.player.hasInfiniteMaterials()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()));
            return;
        }
        super.init();
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 104, this.height / 2 - 22);
    }

    @Override
    protected void onRecipeBookButtonClick() {
        this.buttonClicked = true;
    }

    @Override
    protected void renderLabels(GuiGraphics $$0, int $$1, int $$2) {
        $$0.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.effects.renderEffects($$0, $$1, $$2);
        super.render($$0, $$1, $$2, $$3);
        this.effects.renderTooltip($$0, $$1, $$2);
        this.xMouse = $$1;
        this.yMouse = $$2;
    }

    @Override
    public boolean showsActiveEffects() {
        return this.effects.canSeeEffects();
    }

    @Override
    protected boolean isBiggerResultSlot() {
        return false;
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        $$0.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        InventoryScreen.renderEntityInInventoryFollowsMouse($$0, $$4 + 26, $$5 + 8, $$4 + 75, $$5 + 78, 30, 0.0625f, this.xMouse, this.yMouse, this.minecraft.player);
    }

    public static void renderEntityInInventoryFollowsMouse(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, float $$6, float $$7, float $$8, LivingEntity $$9) {
        float $$10 = (float)($$1 + $$3) / 2.0f;
        float $$11 = (float)($$2 + $$4) / 2.0f;
        $$0.enableScissor($$1, $$2, $$3, $$4);
        float $$12 = (float)Math.atan(($$10 - $$7) / 40.0f);
        float $$13 = (float)Math.atan(($$11 - $$8) / 40.0f);
        Quaternionf $$14 = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf $$15 = new Quaternionf().rotateX($$13 * 20.0f * ((float)Math.PI / 180));
        $$14.mul((Quaternionfc)$$15);
        float $$16 = $$9.yBodyRot;
        float $$17 = $$9.getYRot();
        float $$18 = $$9.getXRot();
        float $$19 = $$9.yHeadRotO;
        float $$20 = $$9.yHeadRot;
        $$9.yBodyRot = 180.0f + $$12 * 20.0f;
        $$9.setYRot(180.0f + $$12 * 40.0f);
        $$9.setXRot(-$$13 * 20.0f);
        $$9.yHeadRot = $$9.getYRot();
        $$9.yHeadRotO = $$9.getYRot();
        float $$21 = $$9.getScale();
        Vector3f $$22 = new Vector3f(0.0f, $$9.getBbHeight() / 2.0f + $$6 * $$21, 0.0f);
        float $$23 = (float)$$5 / $$21;
        InventoryScreen.renderEntityInInventory($$0, $$1, $$2, $$3, $$4, $$23, $$22, $$14, $$15, $$9);
        $$9.yBodyRot = $$16;
        $$9.setYRot($$17);
        $$9.setXRot($$18);
        $$9.yHeadRotO = $$19;
        $$9.yHeadRot = $$20;
        $$0.disableScissor();
    }

    public static void renderEntityInInventory(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, float $$5, Vector3f $$6, Quaternionf $$7, @Nullable Quaternionf $$8, LivingEntity $$9) {
        EntityRenderDispatcher $$10 = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<?, LivingEntity> $$11 = $$10.getRenderer($$9);
        LivingEntity $$12 = $$11.createRenderState($$9, 1.0f);
        ((EntityRenderState)((Object)$$12)).hitboxesRenderState = null;
        $$0.submitEntityRenderState((EntityRenderState)((Object)$$12), $$5, $$6, $$7, $$8, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        }
        return super.mouseReleased($$0, $$1, $$2);
    }
}


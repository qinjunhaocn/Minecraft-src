/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconScreen
extends AbstractContainerScreen<BeaconMenu> {
    private static final ResourceLocation BEACON_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/beacon.png");
    static final ResourceLocation BUTTON_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button_disabled");
    static final ResourceLocation BUTTON_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button_selected");
    static final ResourceLocation BUTTON_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button_highlighted");
    static final ResourceLocation BUTTON_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/button");
    static final ResourceLocation CONFIRM_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/confirm");
    static final ResourceLocation CANCEL_SPRITE = ResourceLocation.withDefaultNamespace("container/beacon/cancel");
    private static final Component PRIMARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.primary");
    private static final Component SECONDARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.secondary");
    private final List<BeaconButton> beaconButtons = Lists.newArrayList();
    @Nullable
    Holder<MobEffect> primary;
    @Nullable
    Holder<MobEffect> secondary;

    public BeaconScreen(final BeaconMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        this.imageWidth = 230;
        this.imageHeight = 219;
        $$0.addSlotListener(new ContainerListener(){

            @Override
            public void slotChanged(AbstractContainerMenu $$02, int $$1, ItemStack $$2) {
            }

            @Override
            public void dataChanged(AbstractContainerMenu $$02, int $$1, int $$2) {
                BeaconScreen.this.primary = $$0.getPrimaryEffect();
                BeaconScreen.this.secondary = $$0.getSecondaryEffect();
            }
        });
    }

    private <T extends AbstractWidget> void addBeaconButton(T $$0) {
        this.addRenderableWidget($$0);
        this.beaconButtons.add((BeaconButton)((Object)$$0));
    }

    @Override
    protected void init() {
        super.init();
        this.beaconButtons.clear();
        this.addBeaconButton(new BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
        this.addBeaconButton(new BeaconCancelButton(this.leftPos + 190, this.topPos + 107));
        for (int $$0 = 0; $$0 <= 2; ++$$0) {
            int $$1 = BeaconBlockEntity.BEACON_EFFECTS.get($$0).size();
            int $$2 = $$1 * 22 + ($$1 - 1) * 2;
            for (int $$3 = 0; $$3 < $$1; ++$$3) {
                Holder<MobEffect> $$4 = BeaconBlockEntity.BEACON_EFFECTS.get($$0).get($$3);
                BeaconPowerButton $$5 = new BeaconPowerButton(this.leftPos + 76 + $$3 * 24 - $$2 / 2, this.topPos + 22 + $$0 * 25, $$4, true, $$0);
                $$5.active = false;
                this.addBeaconButton($$5);
            }
        }
        int $$6 = 3;
        int $$7 = BeaconBlockEntity.BEACON_EFFECTS.get(3).size() + 1;
        int $$8 = $$7 * 22 + ($$7 - 1) * 2;
        for (int $$9 = 0; $$9 < $$7 - 1; ++$$9) {
            Holder<MobEffect> $$10 = BeaconBlockEntity.BEACON_EFFECTS.get(3).get($$9);
            BeaconPowerButton $$11 = new BeaconPowerButton(this.leftPos + 167 + $$9 * 24 - $$8 / 2, this.topPos + 47, $$10, false, 3);
            $$11.active = false;
            this.addBeaconButton($$11);
        }
        Holder<MobEffect> $$12 = BeaconBlockEntity.BEACON_EFFECTS.get(0).get(0);
        BeaconUpgradePowerButton $$13 = new BeaconUpgradePowerButton(this.leftPos + 167 + ($$7 - 1) * 24 - $$8 / 2, this.topPos + 47, $$12);
        $$13.visible = false;
        this.addBeaconButton($$13);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.updateButtons();
    }

    void updateButtons() {
        int $$0 = ((BeaconMenu)this.menu).getLevels();
        this.beaconButtons.forEach($$1 -> $$1.updateStatus($$0));
    }

    @Override
    protected void renderLabels(GuiGraphics $$0, int $$1, int $$2) {
        $$0.drawCenteredString(this.font, PRIMARY_EFFECT_LABEL, 62, 10, -2039584);
        $$0.drawCenteredString(this.font, SECONDARY_EFFECT_LABEL, 169, 10, -2039584);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        $$0.blit(RenderPipelines.GUI_TEXTURED, BEACON_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        $$0.renderItem(new ItemStack(Items.NETHERITE_INGOT), $$4 + 20, $$5 + 109);
        $$0.renderItem(new ItemStack(Items.EMERALD), $$4 + 41, $$5 + 109);
        $$0.renderItem(new ItemStack(Items.DIAMOND), $$4 + 41 + 22, $$5 + 109);
        $$0.renderItem(new ItemStack(Items.GOLD_INGOT), $$4 + 42 + 44, $$5 + 109);
        $$0.renderItem(new ItemStack(Items.IRON_INGOT), $$4 + 42 + 66, $$5 + 109);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    static interface BeaconButton {
        public void updateStatus(int var1);
    }

    class BeaconConfirmButton
    extends BeaconSpriteScreenButton {
        public BeaconConfirmButton(int $$0, int $$1) {
            super($$0, $$1, CONFIRM_SPRITE, CommonComponents.GUI_DONE);
        }

        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket(Optional.ofNullable(BeaconScreen.this.primary), Optional.ofNullable(BeaconScreen.this.secondary)));
            ((BeaconScreen)BeaconScreen.this).minecraft.player.closeContainer();
        }

        @Override
        public void updateStatus(int $$0) {
            this.active = ((BeaconMenu)BeaconScreen.this.menu).hasPayment() && BeaconScreen.this.primary != null;
        }
    }

    class BeaconCancelButton
    extends BeaconSpriteScreenButton {
        public BeaconCancelButton(int $$0, int $$1) {
            super($$0, $$1, CANCEL_SPRITE, CommonComponents.GUI_CANCEL);
        }

        @Override
        public void onPress() {
            ((BeaconScreen)BeaconScreen.this).minecraft.player.closeContainer();
        }

        @Override
        public void updateStatus(int $$0) {
        }
    }

    class BeaconPowerButton
    extends BeaconScreenButton {
        private final boolean isPrimary;
        protected final int tier;
        private Holder<MobEffect> effect;
        private ResourceLocation sprite;

        public BeaconPowerButton(int $$0, int $$1, Holder<MobEffect> $$2, boolean $$3, int $$4) {
            super($$0, $$1);
            this.isPrimary = $$3;
            this.tier = $$4;
            this.setEffect($$2);
        }

        protected void setEffect(Holder<MobEffect> $$0) {
            this.effect = $$0;
            this.sprite = Gui.getMobEffectSprite($$0);
            this.setTooltip(Tooltip.create(this.createEffectDescription($$0), null));
        }

        protected MutableComponent createEffectDescription(Holder<MobEffect> $$0) {
            return Component.translatable($$0.value().getDescriptionId());
        }

        @Override
        public void onPress() {
            if (this.isSelected()) {
                return;
            }
            if (this.isPrimary) {
                BeaconScreen.this.primary = this.effect;
            } else {
                BeaconScreen.this.secondary = this.effect;
            }
            BeaconScreen.this.updateButtons();
        }

        @Override
        protected void renderIcon(GuiGraphics $$0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
        }

        @Override
        public void updateStatus(int $$0) {
            this.active = this.tier < $$0;
            this.setSelected(this.effect.equals(this.isPrimary ? BeaconScreen.this.primary : BeaconScreen.this.secondary));
        }

        @Override
        protected MutableComponent createNarrationMessage() {
            return this.createEffectDescription(this.effect);
        }
    }

    class BeaconUpgradePowerButton
    extends BeaconPowerButton {
        public BeaconUpgradePowerButton(int $$0, int $$1, Holder<MobEffect> $$2) {
            super($$0, $$1, $$2, false, 3);
        }

        @Override
        protected MutableComponent createEffectDescription(Holder<MobEffect> $$0) {
            return Component.translatable($$0.value().getDescriptionId()).append(" II");
        }

        @Override
        public void updateStatus(int $$0) {
            if (BeaconScreen.this.primary != null) {
                this.visible = true;
                this.setEffect(BeaconScreen.this.primary);
                super.updateStatus($$0);
            } else {
                this.visible = false;
            }
        }
    }

    static abstract class BeaconSpriteScreenButton
    extends BeaconScreenButton {
        private final ResourceLocation sprite;

        protected BeaconSpriteScreenButton(int $$0, int $$1, ResourceLocation $$2, Component $$3) {
            super($$0, $$1, $$3);
            this.sprite = $$2;
        }

        @Override
        protected void renderIcon(GuiGraphics $$0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
        }
    }

    static abstract class BeaconScreenButton
    extends AbstractButton
    implements BeaconButton {
        private boolean selected;

        protected BeaconScreenButton(int $$0, int $$1) {
            super($$0, $$1, 22, 22, CommonComponents.EMPTY);
        }

        protected BeaconScreenButton(int $$0, int $$1, Component $$2) {
            super($$0, $$1, 22, 22, $$2);
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            ResourceLocation $$7;
            if (!this.active) {
                ResourceLocation $$4 = BUTTON_DISABLED_SPRITE;
            } else if (this.selected) {
                ResourceLocation $$5 = BUTTON_SELECTED_SPRITE;
            } else if (this.isHoveredOrFocused()) {
                ResourceLocation $$6 = BUTTON_HIGHLIGHTED_SPRITE;
            } else {
                $$7 = BUTTON_SPRITE;
            }
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$7, this.getX(), this.getY(), this.width, this.height);
            this.renderIcon($$0);
        }

        protected abstract void renderIcon(GuiGraphics var1);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean $$0) {
            this.selected = $$0;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput $$0) {
            this.defaultButtonNarrationText($$0);
        }
    }
}


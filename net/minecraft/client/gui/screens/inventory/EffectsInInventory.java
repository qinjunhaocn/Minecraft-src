/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;

public class EffectsInInventory {
    private static final ResourceLocation EFFECT_BACKGROUND_LARGE_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_large");
    private static final ResourceLocation EFFECT_BACKGROUND_SMALL_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_small");
    private final AbstractContainerScreen<?> screen;
    private final Minecraft minecraft;
    @Nullable
    private MobEffectInstance hoveredEffect;

    public EffectsInInventory(AbstractContainerScreen<?> $$0) {
        this.screen = $$0;
        this.minecraft = Minecraft.getInstance();
    }

    public boolean canSeeEffects() {
        int $$0 = this.screen.leftPos + this.screen.imageWidth + 2;
        int $$1 = this.screen.width - $$0;
        return $$1 >= 32;
    }

    public void renderEffects(GuiGraphics $$0, int $$1, int $$2) {
        this.hoveredEffect = null;
        int $$3 = this.screen.leftPos + this.screen.imageWidth + 2;
        int $$4 = this.screen.width - $$3;
        Collection<MobEffectInstance> $$5 = this.minecraft.player.getActiveEffects();
        if ($$5.isEmpty() || $$4 < 32) {
            return;
        }
        boolean $$6 = $$4 >= 120;
        int $$7 = 33;
        if ($$5.size() > 5) {
            $$7 = 132 / ($$5.size() - 1);
        }
        List<MobEffectInstance> $$8 = Ordering.natural().sortedCopy($$5);
        this.renderBackgrounds($$0, $$3, $$7, $$8, $$6);
        this.renderIcons($$0, $$3, $$7, $$8, $$6);
        if ($$6) {
            this.renderLabels($$0, $$3, $$7, $$8);
        } else if ($$1 >= $$3 && $$1 <= $$3 + 33) {
            int $$9 = this.screen.topPos;
            for (MobEffectInstance $$10 : $$8) {
                if ($$2 >= $$9 && $$2 <= $$9 + $$7) {
                    this.hoveredEffect = $$10;
                }
                $$9 += $$7;
            }
        }
    }

    public void renderTooltip(GuiGraphics $$0, int $$1, int $$2) {
        if (this.hoveredEffect != null) {
            List $$3 = List.of((Object)this.getEffectName(this.hoveredEffect), (Object)MobEffectUtil.formatDuration(this.hoveredEffect, 1.0f, this.minecraft.level.tickRateManager().tickrate()));
            $$0.setTooltipForNextFrame(this.screen.getFont(), $$3, Optional.empty(), $$1, $$2);
        }
    }

    private void renderBackgrounds(GuiGraphics $$0, int $$1, int $$2, Iterable<MobEffectInstance> $$3, boolean $$4) {
        int $$5 = this.screen.topPos;
        for (MobEffectInstance $$6 : $$3) {
            if ($$4) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_LARGE_SPRITE, $$1, $$5, 120, 32);
            } else {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SMALL_SPRITE, $$1, $$5, 32, 32);
            }
            $$5 += $$2;
        }
    }

    private void renderIcons(GuiGraphics $$0, int $$1, int $$2, Iterable<MobEffectInstance> $$3, boolean $$4) {
        int $$5 = this.screen.topPos;
        for (MobEffectInstance $$6 : $$3) {
            Holder<MobEffect> $$7 = $$6.getEffect();
            ResourceLocation $$8 = Gui.getMobEffectSprite($$7);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$8, $$1 + ($$4 ? 6 : 7), $$5 + 7, 18, 18);
            $$5 += $$2;
        }
    }

    private void renderLabels(GuiGraphics $$0, int $$1, int $$2, Iterable<MobEffectInstance> $$3) {
        int $$4 = this.screen.topPos;
        for (MobEffectInstance $$5 : $$3) {
            Component $$6 = this.getEffectName($$5);
            $$0.drawString(this.screen.getFont(), $$6, $$1 + 10 + 18, $$4 + 6, -1);
            Component $$7 = MobEffectUtil.formatDuration($$5, 1.0f, this.minecraft.level.tickRateManager().tickrate());
            $$0.drawString(this.screen.getFont(), $$7, $$1 + 10 + 18, $$4 + 6 + 10, -8421505);
            $$4 += $$2;
        }
    }

    private Component getEffectName(MobEffectInstance $$0) {
        MutableComponent $$1 = $$0.getEffect().value().getDisplayName().copy();
        if ($$0.getAmplifier() >= 1 && $$0.getAmplifier() <= 9) {
            $$1.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + ($$0.getAmplifier() + 1)));
        }
        return $$1;
    }
}


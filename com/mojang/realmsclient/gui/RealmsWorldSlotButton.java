/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class RealmsWorldSlotButton
extends Button {
    private static final ResourceLocation SLOT_FRAME_SPRITE = ResourceLocation.withDefaultNamespace("widget/slot_frame");
    public static final ResourceLocation EMPTY_SLOT_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/empty_frame.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_0.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_2.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_3.png");
    private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
    private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
    static final Component MINIGAME = Component.translatable("mco.worldSlot.minigame");
    private static final int WORLD_NAME_MAX_WIDTH = 64;
    private static final String DOTS = "...";
    private final int slotIndex;
    private State state;

    public RealmsWorldSlotButton(int $$0, int $$1, int $$2, int $$3, int $$4, RealmsServer $$5, Button.OnPress $$6) {
        super($$0, $$1, $$2, $$3, CommonComponents.EMPTY, $$6, DEFAULT_NARRATION);
        this.slotIndex = $$4;
        this.state = this.setServerData($$5);
    }

    public State getState() {
        return this.state;
    }

    public State setServerData(RealmsServer $$0) {
        this.state = new State($$0, this.slotIndex);
        this.setTooltipAndNarration(this.state, $$0.minigameName);
        return this.state;
    }

    private void setTooltipAndNarration(State $$0, @Nullable String $$1) {
        Component $$2;
        switch ($$0.action.ordinal()) {
            case 1: {
                Component component;
                if ($$0.minigame) {
                    component = SWITCH_TO_MINIGAME_SLOT_TOOLTIP;
                    break;
                }
                component = SWITCH_TO_WORLD_SLOT_TOOLTIP;
                break;
            }
            default: {
                Component component = $$2 = null;
            }
        }
        if ($$2 != null) {
            this.setTooltip(Tooltip.create($$2));
        }
        MutableComponent $$3 = Component.literal($$0.slotName);
        if ($$0.minigame && $$1 != null) {
            $$3 = $$3.append(CommonComponents.SPACE).append($$1);
        }
        this.setMessage($$3);
    }

    static Action getAction(RealmsServer $$0, boolean $$1, boolean $$2) {
        if (!($$2 || $$1 && $$0.expired)) {
            return Action.SWITCH_SLOT;
        }
        return Action.NOTHING;
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        Object $$16;
        Font $$15;
        ResourceLocation $$13;
        int $$4 = this.getX();
        int $$5 = this.getY();
        boolean $$6 = this.isHoveredOrFocused();
        if (this.state.minigame) {
            ResourceLocation $$7 = RealmsTextureManager.worldTemplate(String.valueOf(this.state.imageId), this.state.image);
        } else if (this.state.empty) {
            ResourceLocation $$8 = EMPTY_SLOT_LOCATION;
        } else if (this.state.image != null && this.state.imageId != -1L) {
            ResourceLocation $$9 = RealmsTextureManager.worldTemplate(String.valueOf(this.state.imageId), this.state.image);
        } else if (this.slotIndex == 1) {
            ResourceLocation $$10 = DEFAULT_WORLD_SLOT_1;
        } else if (this.slotIndex == 2) {
            ResourceLocation $$11 = DEFAULT_WORLD_SLOT_2;
        } else if (this.slotIndex == 3) {
            ResourceLocation $$12 = DEFAULT_WORLD_SLOT_3;
        } else {
            $$13 = EMPTY_SLOT_LOCATION;
        }
        int $$14 = -1;
        if (!this.state.activeSlot) {
            $$14 = ARGB.colorFromFloat(1.0f, 0.56f, 0.56f, 0.56f);
        }
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$13, $$4 + 1, $$5 + 1, 0.0f, 0.0f, this.width - 2, this.height - 2, 74, 74, 74, 74, $$14);
        if ($$6 && this.state.action != Action.NOTHING) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, $$4, $$5, this.width, this.height);
        } else if (this.state.activeSlot) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, $$4, $$5, this.width, this.height, ARGB.colorFromFloat(1.0f, 0.8f, 0.8f, 0.8f));
        } else {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_FRAME_SPRITE, $$4, $$5, this.width, this.height, ARGB.colorFromFloat(1.0f, 0.56f, 0.56f, 0.56f));
        }
        if (this.state.hardcore) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, RealmsMainScreen.HARDCORE_MODE_SPRITE, $$4 + 3, $$5 + 4, 9, 8);
        }
        if (($$15 = Minecraft.getInstance().font).width((String)($$16 = this.state.slotName)) > 64) {
            $$16 = $$15.plainSubstrByWidth((String)$$16, 64 - $$15.width(DOTS)) + DOTS;
        }
        $$0.drawCenteredString($$15, (String)$$16, $$4 + this.width / 2, $$5 + this.height - 14, -1);
        if (this.state.activeSlot) {
            $$0.drawCenteredString($$15, RealmsMainScreen.getVersionComponent(this.state.slotVersion, this.state.compatibility.isCompatible()), $$4 + this.width / 2, $$5 + this.height + 2, -1);
        }
    }

    public static class State {
        final String slotName;
        final String slotVersion;
        final RealmsServer.Compatibility compatibility;
        final long imageId;
        @Nullable
        final String image;
        public final boolean empty;
        public final boolean minigame;
        public final Action action;
        public final boolean hardcore;
        public final boolean activeSlot;

        public State(RealmsServer $$0, int $$1) {
            boolean bl = this.minigame = $$1 == 4;
            if (this.minigame) {
                this.slotName = MINIGAME.getString();
                this.imageId = $$0.minigameId;
                this.image = $$0.minigameImage;
                this.empty = $$0.minigameId == -1;
                this.slotVersion = "";
                this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
                this.hardcore = false;
                this.activeSlot = $$0.isMinigameActive();
            } else {
                RealmsSlot $$2 = $$0.slots.get($$1);
                this.slotName = $$2.options.getSlotName($$1);
                this.imageId = $$2.options.templateId;
                this.image = $$2.options.templateImage;
                this.empty = $$2.options.empty;
                this.slotVersion = $$2.options.version;
                this.compatibility = $$2.options.compatibility;
                this.hardcore = $$2.isHardcore();
                this.activeSlot = $$0.activeSlot == $$1 && !$$0.isMinigameActive();
            }
            this.action = RealmsWorldSlotButton.getAction($$0, this.minigame, this.activeSlot);
        }
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action NOTHING = new Action();
        public static final /* enum */ Action SWITCH_SLOT = new Action();
        private static final /* synthetic */ Action[] $VALUES;

        public static Action[] values() {
            return (Action[])$VALUES.clone();
        }

        public static Action valueOf(String $$0) {
            return Enum.valueOf(Action.class, $$0);
        }

        private static /* synthetic */ Action[] a() {
            return new Action[]{NOTHING, SWITCH_SLOT};
        }

        static {
            $VALUES = Action.a();
        }
    }
}


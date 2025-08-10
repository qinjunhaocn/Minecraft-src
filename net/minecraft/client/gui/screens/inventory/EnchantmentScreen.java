/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentScreen
extends AbstractContainerScreen<EnchantmentMenu> {
    private static final ResourceLocation[] ENABLED_LEVEL_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3")};
    private static final ResourceLocation[] DISABLED_LEVEL_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1_disabled"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2_disabled"), ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3_disabled")};
    private static final ResourceLocation ENCHANTMENT_SLOT_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final ResourceLocation ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
    private static final ResourceLocation ENCHANTMENT_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot");
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation ENCHANTING_BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enchanting_table_book.png");
    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void init() {
        super.init();
        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        int $$3 = (this.width - this.imageWidth) / 2;
        int $$4 = (this.height - this.imageHeight) / 2;
        for (int $$5 = 0; $$5 < 3; ++$$5) {
            double $$6 = $$0 - (double)($$3 + 60);
            double $$7 = $$1 - (double)($$4 + 14 + 19 * $$5);
            if (!($$6 >= 0.0) || !($$7 >= 0.0) || !($$6 < 108.0) || !($$7 < 19.0) || !((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, $$5)) continue;
            this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, $$5);
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        $$0.blit(RenderPipelines.GUI_TEXTURED, ENCHANTING_TABLE_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        this.renderBook($$0, $$4, $$5);
        EnchantmentNames.getInstance().initSeed(((EnchantmentMenu)this.menu).getEnchantmentSeed());
        int $$6 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int $$7 = 0; $$7 < 3; ++$$7) {
            int $$8 = $$4 + 60;
            int $$9 = $$8 + 20;
            int $$10 = ((EnchantmentMenu)this.menu).costs[$$7];
            if ($$10 == 0) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, $$8, $$5 + 14 + 19 * $$7, 108, 19);
                continue;
            }
            String $$11 = "" + $$10;
            int $$12 = 86 - this.font.width($$11);
            FormattedText $$13 = EnchantmentNames.getInstance().getRandomName(this.font, $$12);
            int $$14 = -9937334;
            if (!($$6 >= $$7 + 1 && this.minecraft.player.experienceLevel >= $$10 || this.minecraft.player.hasInfiniteMaterials())) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, $$8, $$5 + 14 + 19 * $$7, 108, 19);
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DISABLED_LEVEL_SPRITES[$$7], $$8 + 1, $$5 + 15 + 19 * $$7, 16, 16);
                $$0.drawWordWrap(this.font, $$13, $$9, $$5 + 16 + 19 * $$7, $$12, ARGB.opaque(($$14 & 0xFEFEFE) >> 1), false);
                $$14 = -12550384;
            } else {
                int $$15 = $$2 - ($$4 + 60);
                int $$16 = $$3 - ($$5 + 14 + 19 * $$7);
                if ($$15 >= 0 && $$16 >= 0 && $$15 < 108 && $$16 < 19) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE, $$8, $$5 + 14 + 19 * $$7, 108, 19);
                    $$14 = -128;
                } else {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_SPRITE, $$8, $$5 + 14 + 19 * $$7, 108, 19);
                }
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ENABLED_LEVEL_SPRITES[$$7], $$8 + 1, $$5 + 15 + 19 * $$7, 16, 16);
                $$0.drawWordWrap(this.font, $$13, $$9, $$5 + 16 + 19 * $$7, $$12, $$14, false);
                $$14 = -8323296;
            }
            $$0.drawString(this.font, $$11, $$9 + 86 - this.font.width($$11), $$5 + 16 + 19 * $$7 + 7, $$14);
        }
    }

    private void renderBook(GuiGraphics $$0, int $$1, int $$2) {
        float $$3 = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float $$4 = Mth.lerp($$3, this.oOpen, this.open);
        float $$5 = Mth.lerp($$3, this.oFlip, this.flip);
        int $$6 = $$1 + 14;
        int $$7 = $$2 + 14;
        int $$8 = $$6 + 38;
        int $$9 = $$7 + 31;
        $$0.submitBookModelRenderState(this.bookModel, ENCHANTING_BOOK_LOCATION, 40.0f, $$4, $$5, $$6, $$7, $$8, $$9);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        float $$4 = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        super.render($$0, $$1, $$2, $$4);
        this.renderTooltip($$0, $$1, $$2);
        boolean $$5 = this.minecraft.player.hasInfiniteMaterials();
        int $$6 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int $$7 = 0; $$7 < 3; ++$$7) {
            int $$8 = ((EnchantmentMenu)this.menu).costs[$$7];
            Optional $$9 = this.minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(((EnchantmentMenu)this.menu).enchantClue[$$7]);
            if ($$9.isEmpty()) continue;
            int $$10 = ((EnchantmentMenu)this.menu).levelClue[$$7];
            int $$11 = $$7 + 1;
            if (!this.isHovering(60, 14 + 19 * $$7, 108, 17, $$1, $$2) || $$8 <= 0 || $$10 < 0 || $$9 == null) continue;
            ArrayList<Component> $$12 = Lists.newArrayList();
            $$12.add(Component.a("container.enchant.clue", Enchantment.getFullname($$9.get(), $$10)).withStyle(ChatFormatting.WHITE));
            if (!$$5) {
                $$12.add(CommonComponents.EMPTY);
                if (this.minecraft.player.experienceLevel < $$8) {
                    $$12.add(Component.a("container.enchant.level.requirement", ((EnchantmentMenu)this.menu).costs[$$7]).withStyle(ChatFormatting.RED));
                } else {
                    MutableComponent $$16;
                    MutableComponent $$14;
                    if ($$11 == 1) {
                        MutableComponent $$13 = Component.translatable("container.enchant.lapis.one");
                    } else {
                        $$14 = Component.a("container.enchant.lapis.many", $$11);
                    }
                    $$12.add($$14.withStyle($$6 >= $$11 ? ChatFormatting.GRAY : ChatFormatting.RED));
                    if ($$11 == 1) {
                        MutableComponent $$15 = Component.translatable("container.enchant.level.one");
                    } else {
                        $$16 = Component.a("container.enchant.level.many", $$11);
                    }
                    $$12.add($$16.withStyle(ChatFormatting.GRAY));
                }
            }
            $$0.setComponentTooltipForNextFrame(this.font, $$12, $$1, $$2);
            break;
        }
    }

    public void tickBook() {
        ItemStack $$0 = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
        if (!ItemStack.matches($$0, this.last)) {
            this.last = $$0;
            do {
                this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.flip <= this.flipT + 1.0f && this.flip >= this.flipT - 1.0f);
        }
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean $$1 = false;
        for (int $$2 = 0; $$2 < 3; ++$$2) {
            if (((EnchantmentMenu)this.menu).costs[$$2] == 0) continue;
            $$1 = true;
        }
        this.open = $$1 ? (this.open += 0.2f) : (this.open -= 0.2f);
        this.open = Mth.clamp(this.open, 0.0f, 1.0f);
        float $$3 = (this.flipT - this.flip) * 0.4f;
        float $$4 = 0.2f;
        $$3 = Mth.clamp($$3, -0.2f, 0.2f);
        this.flipA += ($$3 - this.flipA) * 0.9f;
        this.flip += this.flipA;
    }
}


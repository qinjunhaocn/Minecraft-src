/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class SmithingTemplateItem
extends Item {
    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    private static final Component INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.ingredients"))).withStyle(TITLE_FORMAT);
    private static final Component APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.applies_to"))).withStyle(TITLE_FORMAT);
    private static final Component SMITHING_TEMPLATE_SUFFIX = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template"))).withStyle(TITLE_FORMAT);
    private static final Component ARMOR_TRIM_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component ARMOR_TRIM_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component ARMOR_TRIM_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.base_slot_description")));
    private static final Component ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.additions_slot_description")));
    private static final Component NETHERITE_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component NETHERITE_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final Component NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.base_slot_description")));
    private static final Component NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.additions_slot_description")));
    private static final ResourceLocation EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace("container/slot/helmet");
    private static final ResourceLocation EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("container/slot/chestplate");
    private static final ResourceLocation EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("container/slot/leggings");
    private static final ResourceLocation EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("container/slot/boots");
    private static final ResourceLocation EMPTY_SLOT_HOE = ResourceLocation.withDefaultNamespace("container/slot/hoe");
    private static final ResourceLocation EMPTY_SLOT_AXE = ResourceLocation.withDefaultNamespace("container/slot/axe");
    private static final ResourceLocation EMPTY_SLOT_SWORD = ResourceLocation.withDefaultNamespace("container/slot/sword");
    private static final ResourceLocation EMPTY_SLOT_SHOVEL = ResourceLocation.withDefaultNamespace("container/slot/shovel");
    private static final ResourceLocation EMPTY_SLOT_PICKAXE = ResourceLocation.withDefaultNamespace("container/slot/pickaxe");
    private static final ResourceLocation EMPTY_SLOT_INGOT = ResourceLocation.withDefaultNamespace("container/slot/ingot");
    private static final ResourceLocation EMPTY_SLOT_REDSTONE_DUST = ResourceLocation.withDefaultNamespace("container/slot/redstone_dust");
    private static final ResourceLocation EMPTY_SLOT_QUARTZ = ResourceLocation.withDefaultNamespace("container/slot/quartz");
    private static final ResourceLocation EMPTY_SLOT_EMERALD = ResourceLocation.withDefaultNamespace("container/slot/emerald");
    private static final ResourceLocation EMPTY_SLOT_DIAMOND = ResourceLocation.withDefaultNamespace("container/slot/diamond");
    private static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("container/slot/lapis_lazuli");
    private static final ResourceLocation EMPTY_SLOT_AMETHYST_SHARD = ResourceLocation.withDefaultNamespace("container/slot/amethyst_shard");
    private final Component appliesTo;
    private final Component ingredients;
    private final Component baseSlotDescription;
    private final Component additionsSlotDescription;
    private final List<ResourceLocation> baseSlotEmptyIcons;
    private final List<ResourceLocation> additionalSlotEmptyIcons;

    public SmithingTemplateItem(Component $$0, Component $$1, Component $$2, Component $$3, List<ResourceLocation> $$4, List<ResourceLocation> $$5, Item.Properties $$6) {
        super($$6);
        this.appliesTo = $$0;
        this.ingredients = $$1;
        this.baseSlotDescription = $$2;
        this.additionsSlotDescription = $$3;
        this.baseSlotEmptyIcons = $$4;
        this.additionalSlotEmptyIcons = $$5;
    }

    public static SmithingTemplateItem createArmorTrimTemplate(Item.Properties $$0) {
        return new SmithingTemplateItem(ARMOR_TRIM_APPLIES_TO, ARMOR_TRIM_INGREDIENTS, ARMOR_TRIM_BASE_SLOT_DESCRIPTION, ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION, SmithingTemplateItem.createTrimmableArmorIconList(), SmithingTemplateItem.createTrimmableMaterialIconList(), $$0);
    }

    public static SmithingTemplateItem createNetheriteUpgradeTemplate(Item.Properties $$0) {
        return new SmithingTemplateItem(NETHERITE_UPGRADE_APPLIES_TO, NETHERITE_UPGRADE_INGREDIENTS, NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION, NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION, SmithingTemplateItem.createNetheriteUpgradeIconList(), SmithingTemplateItem.createNetheriteUpgradeMaterialList(), $$0);
    }

    private static List<ResourceLocation> createTrimmableArmorIconList() {
        return List.of((Object)EMPTY_SLOT_HELMET, (Object)EMPTY_SLOT_CHESTPLATE, (Object)EMPTY_SLOT_LEGGINGS, (Object)EMPTY_SLOT_BOOTS);
    }

    private static List<ResourceLocation> createTrimmableMaterialIconList() {
        return List.of((Object)EMPTY_SLOT_INGOT, (Object)EMPTY_SLOT_REDSTONE_DUST, (Object)EMPTY_SLOT_LAPIS_LAZULI, (Object)EMPTY_SLOT_QUARTZ, (Object)EMPTY_SLOT_DIAMOND, (Object)EMPTY_SLOT_EMERALD, (Object)EMPTY_SLOT_AMETHYST_SHARD);
    }

    private static List<ResourceLocation> createNetheriteUpgradeIconList() {
        return List.of((Object)EMPTY_SLOT_HELMET, (Object)EMPTY_SLOT_SWORD, (Object)EMPTY_SLOT_CHESTPLATE, (Object)EMPTY_SLOT_PICKAXE, (Object)EMPTY_SLOT_LEGGINGS, (Object)EMPTY_SLOT_AXE, (Object)EMPTY_SLOT_BOOTS, (Object)EMPTY_SLOT_HOE, (Object)EMPTY_SLOT_SHOVEL);
    }

    private static List<ResourceLocation> createNetheriteUpgradeMaterialList() {
        return List.of((Object)EMPTY_SLOT_INGOT);
    }

    @Override
    public void appendHoverText(ItemStack $$0, Item.TooltipContext $$1, TooltipDisplay $$2, Consumer<Component> $$3, TooltipFlag $$4) {
        $$3.accept(SMITHING_TEMPLATE_SUFFIX);
        $$3.accept(CommonComponents.EMPTY);
        $$3.accept(APPLIES_TO_TITLE);
        $$3.accept(CommonComponents.space().append(this.appliesTo));
        $$3.accept(INGREDIENTS_TITLE);
        $$3.accept(CommonComponents.space().append(this.ingredients));
    }

    public Component getBaseSlotDescription() {
        return this.baseSlotDescription;
    }

    public Component getAdditionSlotDescription() {
        return this.additionsSlotDescription;
    }

    public List<ResourceLocation> getBaseSlotEmptyIcons() {
        return this.baseSlotEmptyIcons;
    }

    public List<ResourceLocation> getAdditionalSlotEmptyIcons() {
        return this.additionalSlotEmptyIcons;
    }
}


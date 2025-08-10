/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record ToolMaterial(TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems) {
    public static final ToolMaterial WOOD = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 59, 2.0f, 0.0f, 15, ItemTags.WOODEN_TOOL_MATERIALS);
    public static final ToolMaterial STONE = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 4.0f, 1.0f, 5, ItemTags.STONE_TOOL_MATERIALS);
    public static final ToolMaterial IRON = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6.0f, 2.0f, 14, ItemTags.IRON_TOOL_MATERIALS);
    public static final ToolMaterial DIAMOND = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0f, 3.0f, 10, ItemTags.DIAMOND_TOOL_MATERIALS);
    public static final ToolMaterial GOLD = new ToolMaterial(BlockTags.INCORRECT_FOR_GOLD_TOOL, 32, 12.0f, 0.0f, 22, ItemTags.GOLD_TOOL_MATERIALS);
    public static final ToolMaterial NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0f, 4.0f, 15, ItemTags.NETHERITE_TOOL_MATERIALS);

    private Item.Properties applyCommonProperties(Item.Properties $$0) {
        return $$0.durability(this.durability).repairable(this.repairItems).enchantable(this.enchantmentValue);
    }

    public Item.Properties applyToolProperties(Item.Properties $$0, TagKey<Block> $$1, float $$2, float $$3, float $$4) {
        HolderGetter<Block> $$5 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return this.applyCommonProperties($$0).component(DataComponents.TOOL, new Tool(List.of((Object)((Object)Tool.Rule.deniesDrops($$5.getOrThrow(this.incorrectBlocksForDrops))), (Object)((Object)Tool.Rule.minesAndDrops($$5.getOrThrow($$1), this.speed))), 1.0f, 1, true)).attributes(this.createToolAttributes($$2, $$3)).component(DataComponents.WEAPON, new Weapon(2, $$4));
    }

    private ItemAttributeModifiers createToolAttributes(float $$0, float $$1) {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, $$0 + this.attackDamageBonus, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, $$1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    public Item.Properties applySwordProperties(Item.Properties $$0, float $$1, float $$2) {
        HolderGetter<Block> $$3 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return this.applyCommonProperties($$0).component(DataComponents.TOOL, new Tool(List.of((Object)((Object)Tool.Rule.minesAndDrops(HolderSet.a(Blocks.COBWEB.builtInRegistryHolder()), 15.0f)), (Object)((Object)Tool.Rule.overrideSpeed($$3.getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE)), (Object)((Object)Tool.Rule.overrideSpeed($$3.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5f))), 1.0f, 2, false)).attributes(this.createSwordAttributes($$1, $$2)).component(DataComponents.WEAPON, new Weapon(1));
    }

    private ItemAttributeModifiers createSwordAttributes(float $$0, float $$1) {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, $$0 + this.attackDamageBonus, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, $$1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }
}


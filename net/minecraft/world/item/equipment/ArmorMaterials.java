/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.equipment;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

public interface ArmorMaterials {
    public static final ArmorMaterial LEATHER = new ArmorMaterial(5, ArmorMaterials.makeDefense(1, 2, 3, 1, 3), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, ItemTags.REPAIRS_LEATHER_ARMOR, EquipmentAssets.LEATHER);
    public static final ArmorMaterial CHAINMAIL = new ArmorMaterial(15, ArmorMaterials.makeDefense(1, 4, 5, 2, 4), 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, ItemTags.REPAIRS_CHAIN_ARMOR, EquipmentAssets.CHAINMAIL);
    public static final ArmorMaterial IRON = new ArmorMaterial(15, ArmorMaterials.makeDefense(2, 5, 6, 2, 5), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0f, 0.0f, ItemTags.REPAIRS_IRON_ARMOR, EquipmentAssets.IRON);
    public static final ArmorMaterial GOLD = new ArmorMaterial(7, ArmorMaterials.makeDefense(1, 3, 5, 2, 7), 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0f, 0.0f, ItemTags.REPAIRS_GOLD_ARMOR, EquipmentAssets.GOLD);
    public static final ArmorMaterial DIAMOND = new ArmorMaterial(33, ArmorMaterials.makeDefense(3, 6, 8, 3, 11), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0f, 0.0f, ItemTags.REPAIRS_DIAMOND_ARMOR, EquipmentAssets.DIAMOND);
    public static final ArmorMaterial TURTLE_SCUTE = new ArmorMaterial(25, ArmorMaterials.makeDefense(2, 5, 6, 2, 5), 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0f, 0.0f, ItemTags.REPAIRS_TURTLE_HELMET, EquipmentAssets.TURTLE_SCUTE);
    public static final ArmorMaterial NETHERITE = new ArmorMaterial(37, ArmorMaterials.makeDefense(3, 6, 8, 3, 11), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentAssets.NETHERITE);
    public static final ArmorMaterial ARMADILLO_SCUTE = new ArmorMaterial(4, ArmorMaterials.makeDefense(3, 6, 8, 3, 11), 10, SoundEvents.ARMOR_EQUIP_WOLF, 0.0f, 0.0f, ItemTags.REPAIRS_WOLF_ARMOR, EquipmentAssets.ARMADILLO_SCUTE);

    private static Map<ArmorType, Integer> makeDefense(int $$0, int $$1, int $$2, int $$3, int $$4) {
        return Maps.newEnumMap(Map.of((Object)ArmorType.BOOTS, (Object)$$0, (Object)ArmorType.LEGGINGS, (Object)$$1, (Object)ArmorType.CHESTPLATE, (Object)$$2, (Object)ArmorType.HELMET, (Object)$$3, (Object)ArmorType.BODY, (Object)$$4));
    }
}


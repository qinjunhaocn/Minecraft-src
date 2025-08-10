/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 */
package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;

public class EnchantmentHelper {
    public static int getItemEnchantmentLevel(Holder<Enchantment> $$0, ItemStack $$1) {
        ItemEnchantments $$2 = $$1.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return $$2.getLevel($$0);
    }

    public static ItemEnchantments updateEnchantments(ItemStack $$0, Consumer<ItemEnchantments.Mutable> $$1) {
        DataComponentType<ItemEnchantments> $$2 = EnchantmentHelper.getComponentType($$0);
        ItemEnchantments $$3 = $$0.get($$2);
        if ($$3 == null) {
            return ItemEnchantments.EMPTY;
        }
        ItemEnchantments.Mutable $$4 = new ItemEnchantments.Mutable($$3);
        $$1.accept($$4);
        ItemEnchantments $$5 = $$4.toImmutable();
        $$0.set($$2, $$5);
        return $$5;
    }

    public static boolean canStoreEnchantments(ItemStack $$0) {
        return $$0.has(EnchantmentHelper.getComponentType($$0));
    }

    public static void setEnchantments(ItemStack $$0, ItemEnchantments $$1) {
        $$0.set(EnchantmentHelper.getComponentType($$0), $$1);
    }

    public static ItemEnchantments getEnchantmentsForCrafting(ItemStack $$0) {
        return $$0.getOrDefault(EnchantmentHelper.getComponentType($$0), ItemEnchantments.EMPTY);
    }

    private static DataComponentType<ItemEnchantments> getComponentType(ItemStack $$0) {
        return $$0.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
    }

    public static boolean hasAnyEnchantments(ItemStack $$0) {
        return !$$0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty() || !$$0.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty();
    }

    public static int processDurabilityChange(ServerLevel $$0, ItemStack $$1, int $$2) {
        MutableFloat $$32 = new MutableFloat($$2);
        EnchantmentHelper.runIterationOnItem($$1, ($$3, $$4) -> ((Enchantment)((Object)((Object)$$3.value()))).modifyDurabilityChange($$0, $$4, $$1, $$32));
        return $$32.intValue();
    }

    public static int processAmmoUse(ServerLevel $$0, ItemStack $$1, ItemStack $$2, int $$32) {
        MutableFloat $$42 = new MutableFloat($$32);
        EnchantmentHelper.runIterationOnItem($$1, ($$3, $$4) -> ((Enchantment)((Object)((Object)$$3.value()))).modifyAmmoCount($$0, $$4, $$2, $$42));
        return $$42.intValue();
    }

    public static int processBlockExperience(ServerLevel $$0, ItemStack $$1, int $$2) {
        MutableFloat $$32 = new MutableFloat($$2);
        EnchantmentHelper.runIterationOnItem($$1, ($$3, $$4) -> ((Enchantment)((Object)((Object)$$3.value()))).modifyBlockExperience($$0, $$4, $$1, $$32));
        return $$32.intValue();
    }

    public static int processMobExperience(ServerLevel $$0, @Nullable Entity $$1, Entity $$2, int $$32) {
        if ($$1 instanceof LivingEntity) {
            LivingEntity $$42 = (LivingEntity)$$1;
            MutableFloat $$52 = new MutableFloat($$32);
            EnchantmentHelper.runIterationOnEquipment($$42, ($$3, $$4, $$5) -> ((Enchantment)((Object)((Object)$$3.value()))).modifyMobExperience($$0, $$4, $$5.itemStack(), $$2, $$52));
            return $$52.intValue();
        }
        return $$32;
    }

    public static ItemStack createBook(EnchantmentInstance $$0) {
        ItemStack $$1 = new ItemStack(Items.ENCHANTED_BOOK);
        $$1.enchant($$0.enchantment(), $$0.level());
        return $$1;
    }

    private static void runIterationOnItem(ItemStack $$0, EnchantmentVisitor $$1) {
        ItemEnchantments $$2 = $$0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> $$3 : $$2.entrySet()) {
            $$1.accept((Holder)$$3.getKey(), $$3.getIntValue());
        }
    }

    private static void runIterationOnItem(ItemStack $$0, EquipmentSlot $$1, LivingEntity $$2, EnchantmentInSlotVisitor $$3) {
        if ($$0.isEmpty()) {
            return;
        }
        ItemEnchantments $$4 = $$0.get(DataComponents.ENCHANTMENTS);
        if ($$4 == null || $$4.isEmpty()) {
            return;
        }
        EnchantedItemInUse $$5 = new EnchantedItemInUse($$0, $$1, $$2);
        for (Object2IntMap.Entry<Holder<Enchantment>> $$6 : $$4.entrySet()) {
            Holder $$7 = (Holder)$$6.getKey();
            if (!((Enchantment)((Object)$$7.value())).matchingSlot($$1)) continue;
            $$3.accept($$7, $$6.getIntValue(), $$5);
        }
    }

    private static void runIterationOnEquipment(LivingEntity $$0, EnchantmentInSlotVisitor $$1) {
        for (EquipmentSlot $$2 : EquipmentSlot.VALUES) {
            EnchantmentHelper.runIterationOnItem($$0.getItemBySlot($$2), $$2, $$0, $$1);
        }
    }

    public static boolean isImmuneToDamage(ServerLevel $$0, LivingEntity $$1, DamageSource $$2) {
        MutableBoolean $$3 = new MutableBoolean();
        EnchantmentHelper.runIterationOnEquipment($$1, ($$4, $$5, $$6) -> $$3.setValue($$3.isTrue() || ((Enchantment)((Object)((Object)$$4.value()))).isImmuneToDamage($$0, $$5, $$1, $$2)));
        return $$3.isTrue();
    }

    public static float getDamageProtection(ServerLevel $$0, LivingEntity $$1, DamageSource $$2) {
        MutableFloat $$3 = new MutableFloat(0.0f);
        EnchantmentHelper.runIterationOnEquipment($$1, ($$4, $$5, $$6) -> ((Enchantment)((Object)((Object)$$4.value()))).modifyDamageProtection($$0, $$5, $$6.itemStack(), $$1, $$2, $$3));
        return $$3.floatValue();
    }

    public static float modifyDamage(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
        MutableFloat $$52 = new MutableFloat($$4);
        EnchantmentHelper.runIterationOnItem($$1, ($$5, $$6) -> ((Enchantment)((Object)((Object)$$5.value()))).modifyDamage($$0, $$6, $$1, $$2, $$3, $$52));
        return $$52.floatValue();
    }

    public static float modifyFallBasedDamage(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
        MutableFloat $$52 = new MutableFloat($$4);
        EnchantmentHelper.runIterationOnItem($$1, ($$5, $$6) -> ((Enchantment)((Object)((Object)$$5.value()))).modifyFallBasedDamage($$0, $$6, $$1, $$2, $$3, $$52));
        return $$52.floatValue();
    }

    public static float modifyArmorEffectiveness(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
        MutableFloat $$52 = new MutableFloat($$4);
        EnchantmentHelper.runIterationOnItem($$1, ($$5, $$6) -> ((Enchantment)((Object)((Object)$$5.value()))).modifyArmorEffectivness($$0, $$6, $$1, $$2, $$3, $$52));
        return $$52.floatValue();
    }

    public static float modifyKnockback(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
        MutableFloat $$52 = new MutableFloat($$4);
        EnchantmentHelper.runIterationOnItem($$1, ($$5, $$6) -> ((Enchantment)((Object)((Object)$$5.value()))).modifyKnockback($$0, $$6, $$1, $$2, $$3, $$52));
        return $$52.floatValue();
    }

    public static void doPostAttackEffects(ServerLevel $$0, Entity $$1, DamageSource $$2) {
        Entity entity = $$2.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)entity;
            EnchantmentHelper.doPostAttackEffectsWithItemSource($$0, $$1, $$2, $$3.getWeaponItem());
        } else {
            EnchantmentHelper.doPostAttackEffectsWithItemSource($$0, $$1, $$2, null);
        }
    }

    public static void doPostAttackEffectsWithItemSource(ServerLevel $$0, Entity $$1, DamageSource $$2, @Nullable ItemStack $$3) {
        EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak($$0, $$1, $$2, $$3, null);
    }

    public static void doPostAttackEffectsWithItemSourceOnBreak(ServerLevel $$0, Entity $$1, DamageSource $$2, @Nullable ItemStack $$32, @Nullable Consumer<Item> $$42) {
        if ($$1 instanceof LivingEntity) {
            LivingEntity $$52 = (LivingEntity)$$1;
            EnchantmentHelper.runIterationOnEquipment($$52, ($$3, $$4, $$5) -> ((Enchantment)((Object)((Object)$$3.value()))).doPostAttack($$0, $$4, $$5, EnchantmentTarget.VICTIM, $$1, $$2));
        }
        if ($$32 != null) {
            Entity entity = $$2.getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity $$6 = (LivingEntity)entity;
                EnchantmentHelper.runIterationOnItem($$32, EquipmentSlot.MAINHAND, $$6, ($$3, $$4, $$5) -> ((Enchantment)((Object)((Object)$$3.value()))).doPostAttack($$0, $$4, $$5, EnchantmentTarget.ATTACKER, $$1, $$2));
            } else if ($$42 != null) {
                EnchantedItemInUse $$7 = new EnchantedItemInUse($$32, null, null, $$42);
                EnchantmentHelper.runIterationOnItem($$32, ($$4, $$5) -> ((Enchantment)((Object)((Object)$$4.value()))).doPostAttack($$0, $$5, $$7, EnchantmentTarget.ATTACKER, $$1, $$2));
            }
        }
    }

    public static void runLocationChangedEffects(ServerLevel $$0, LivingEntity $$1) {
        EnchantmentHelper.runIterationOnEquipment($$1, ($$2, $$3, $$4) -> ((Enchantment)((Object)((Object)$$2.value()))).runLocationChangedEffects($$0, $$3, $$4, $$1));
    }

    public static void runLocationChangedEffects(ServerLevel $$0, ItemStack $$1, LivingEntity $$22, EquipmentSlot $$32) {
        EnchantmentHelper.runIterationOnItem($$1, $$32, $$22, ($$2, $$3, $$4) -> ((Enchantment)((Object)((Object)$$2.value()))).runLocationChangedEffects($$0, $$3, $$4, $$22));
    }

    public static void stopLocationBasedEffects(LivingEntity $$0) {
        EnchantmentHelper.runIterationOnEquipment($$0, ($$1, $$2, $$3) -> ((Enchantment)((Object)((Object)$$1.value()))).stopLocationBasedEffects($$2, $$3, $$0));
    }

    public static void stopLocationBasedEffects(ItemStack $$0, LivingEntity $$12, EquipmentSlot $$22) {
        EnchantmentHelper.runIterationOnItem($$0, $$22, $$12, ($$1, $$2, $$3) -> ((Enchantment)((Object)((Object)$$1.value()))).stopLocationBasedEffects($$2, $$3, $$12));
    }

    public static void tickEffects(ServerLevel $$0, LivingEntity $$1) {
        EnchantmentHelper.runIterationOnEquipment($$1, ($$2, $$3, $$4) -> ((Enchantment)((Object)((Object)$$2.value()))).tick($$0, $$3, $$4, $$1));
    }

    public static int getEnchantmentLevel(Holder<Enchantment> $$0, LivingEntity $$1) {
        Collection<ItemStack> $$2 = $$0.value().getSlotItems($$1).values();
        int $$3 = 0;
        for (ItemStack $$4 : $$2) {
            int $$5 = EnchantmentHelper.getItemEnchantmentLevel($$0, $$4);
            if ($$5 <= $$3) continue;
            $$3 = $$5;
        }
        return $$3;
    }

    public static int processProjectileCount(ServerLevel $$0, ItemStack $$1, Entity $$2, int $$3) {
        MutableFloat $$42 = new MutableFloat($$3);
        EnchantmentHelper.runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)((Object)((Object)$$4.value()))).modifyProjectileCount($$0, $$5, $$1, $$2, $$42));
        return Math.max(0, $$42.intValue());
    }

    public static float processProjectileSpread(ServerLevel $$0, ItemStack $$1, Entity $$2, float $$3) {
        MutableFloat $$42 = new MutableFloat($$3);
        EnchantmentHelper.runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)((Object)((Object)$$4.value()))).modifyProjectileSpread($$0, $$5, $$1, $$2, $$42));
        return Math.max(0.0f, $$42.floatValue());
    }

    public static int getPiercingCount(ServerLevel $$0, ItemStack $$1, ItemStack $$2) {
        MutableFloat $$32 = new MutableFloat(0.0f);
        EnchantmentHelper.runIterationOnItem($$1, ($$3, $$4) -> ((Enchantment)((Object)((Object)$$3.value()))).modifyPiercingCount($$0, $$4, $$2, $$32));
        return Math.max(0, $$32.intValue());
    }

    public static void onProjectileSpawned(ServerLevel $$0, ItemStack $$1, Projectile $$2, Consumer<Item> $$32) {
        LivingEntity $$42;
        Entity entity = $$2.getOwner();
        LivingEntity $$5 = entity instanceof LivingEntity ? ($$42 = (LivingEntity)entity) : null;
        EnchantedItemInUse $$6 = new EnchantedItemInUse($$1, null, $$5, $$32);
        EnchantmentHelper.runIterationOnItem($$1, ($$3, $$4) -> ((Enchantment)((Object)((Object)$$3.value()))).onProjectileSpawned($$0, $$4, $$6, $$2));
    }

    public static void onHitBlock(ServerLevel $$0, ItemStack $$1, @Nullable LivingEntity $$2, Entity $$3, @Nullable EquipmentSlot $$4, Vec3 $$52, BlockState $$62, Consumer<Item> $$7) {
        EnchantedItemInUse $$8 = new EnchantedItemInUse($$1, $$4, $$2, $$7);
        EnchantmentHelper.runIterationOnItem($$1, ($$5, $$6) -> ((Enchantment)((Object)((Object)$$5.value()))).onHitBlock($$0, $$6, $$8, $$3, $$52, $$62));
    }

    public static int modifyDurabilityToRepairFromXp(ServerLevel $$0, ItemStack $$1, int $$2) {
        MutableFloat $$32 = new MutableFloat($$2);
        EnchantmentHelper.runIterationOnItem($$1, ($$3, $$4) -> ((Enchantment)((Object)((Object)$$3.value()))).modifyDurabilityToRepairFromXp($$0, $$4, $$1, $$32));
        return Math.max(0, $$32.intValue());
    }

    public static float processEquipmentDropChance(ServerLevel $$0, LivingEntity $$1, DamageSource $$2, float $$3) {
        MutableFloat $$4 = new MutableFloat($$3);
        RandomSource $$52 = $$1.getRandom();
        EnchantmentHelper.runIterationOnEquipment($$1, ($$5, $$6, $$7) -> {
            LootContext $$8 = Enchantment.damageContext($$0, $$6, $$1, $$2);
            ((Enchantment)((Object)((Object)$$5.value()))).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach($$4 -> {
                if ($$4.enchanted() == EnchantmentTarget.VICTIM && $$4.affected() == EnchantmentTarget.VICTIM && $$4.matches($$8)) {
                    $$4.setValue(((EnchantmentValueEffect)$$4.effect()).process($$6, $$52, $$4.floatValue()));
                }
            });
        });
        Entity $$62 = $$2.getEntity();
        if ($$62 instanceof LivingEntity) {
            LivingEntity $$72 = (LivingEntity)$$62;
            EnchantmentHelper.runIterationOnEquipment($$72, ($$5, $$6, $$7) -> {
                LootContext $$8 = Enchantment.damageContext($$0, $$6, $$1, $$2);
                ((Enchantment)((Object)((Object)$$5.value()))).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach($$4 -> {
                    if ($$4.enchanted() == EnchantmentTarget.ATTACKER && $$4.affected() == EnchantmentTarget.VICTIM && $$4.matches($$8)) {
                        $$4.setValue(((EnchantmentValueEffect)$$4.effect()).process($$6, $$52, $$4.floatValue()));
                    }
                });
            });
        }
        return $$4.floatValue();
    }

    public static void forEachModifier(ItemStack $$0, EquipmentSlotGroup $$1, BiConsumer<Holder<Attribute>, AttributeModifier> $$22) {
        EnchantmentHelper.runIterationOnItem($$0, ($$2, $$3) -> ((Enchantment)((Object)((Object)$$2.value()))).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach($$4 -> {
            if (((Enchantment)((Object)((Object)((Object)$$2.value())))).definition().slots().contains($$1)) {
                $$22.accept($$4.attribute(), $$4.getModifier($$3, $$1));
            }
        }));
    }

    public static void forEachModifier(ItemStack $$0, EquipmentSlot $$1, BiConsumer<Holder<Attribute>, AttributeModifier> $$22) {
        EnchantmentHelper.runIterationOnItem($$0, ($$2, $$3) -> ((Enchantment)((Object)((Object)$$2.value()))).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach($$4 -> {
            if (((Enchantment)((Object)((Object)((Object)$$2.value())))).matchingSlot($$1)) {
                $$22.accept($$4.attribute(), $$4.getModifier($$3, $$1));
            }
        }));
    }

    public static int getFishingLuckBonus(ServerLevel $$0, ItemStack $$1, Entity $$2) {
        MutableFloat $$3 = new MutableFloat(0.0f);
        EnchantmentHelper.runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)((Object)((Object)$$4.value()))).modifyFishingLuckBonus($$0, $$5, $$1, $$2, $$3));
        return Math.max(0, $$3.intValue());
    }

    public static float getFishingTimeReduction(ServerLevel $$0, ItemStack $$1, Entity $$2) {
        MutableFloat $$3 = new MutableFloat(0.0f);
        EnchantmentHelper.runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)((Object)((Object)$$4.value()))).modifyFishingTimeReduction($$0, $$5, $$1, $$2, $$3));
        return Math.max(0.0f, $$3.floatValue());
    }

    public static int getTridentReturnToOwnerAcceleration(ServerLevel $$0, ItemStack $$1, Entity $$2) {
        MutableFloat $$3 = new MutableFloat(0.0f);
        EnchantmentHelper.runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)((Object)((Object)$$4.value()))).modifyTridentReturnToOwnerAcceleration($$0, $$5, $$1, $$2, $$3));
        return Math.max(0, $$3.intValue());
    }

    public static float modifyCrossbowChargingTime(ItemStack $$0, LivingEntity $$1, float $$22) {
        MutableFloat $$32 = new MutableFloat($$22);
        EnchantmentHelper.runIterationOnItem($$0, ($$2, $$3) -> ((Enchantment)((Object)((Object)$$2.value()))).modifyCrossbowChargeTime($$1.getRandom(), $$3, $$32));
        return Math.max(0.0f, $$32.floatValue());
    }

    public static float getTridentSpinAttackStrength(ItemStack $$0, LivingEntity $$1) {
        MutableFloat $$22 = new MutableFloat(0.0f);
        EnchantmentHelper.runIterationOnItem($$0, ($$2, $$3) -> ((Enchantment)((Object)((Object)$$2.value()))).modifyTridentSpinAttackStrength($$1.getRandom(), $$3, $$22));
        return $$22.floatValue();
    }

    public static boolean hasTag(ItemStack $$0, TagKey<Enchantment> $$1) {
        ItemEnchantments $$2 = $$0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> $$3 : $$2.entrySet()) {
            Holder $$4 = (Holder)$$3.getKey();
            if (!$$4.is($$1)) continue;
            return true;
        }
        return false;
    }

    public static boolean has(ItemStack $$0, DataComponentType<?> $$1) {
        MutableBoolean $$22 = new MutableBoolean(false);
        EnchantmentHelper.runIterationOnItem($$0, ($$2, $$3) -> {
            if (((Enchantment)((Object)((Object)$$2.value()))).effects().has($$1)) {
                $$22.setTrue();
            }
        });
        return $$22.booleanValue();
    }

    public static <T> Optional<T> pickHighestLevel(ItemStack $$0, DataComponentType<List<T>> $$1) {
        Pair<List<T>, Integer> $$2 = EnchantmentHelper.getHighestLevel($$0, $$1);
        if ($$2 != null) {
            List $$3 = (List)$$2.getFirst();
            int $$4 = (Integer)$$2.getSecond();
            return Optional.of($$3.get(Math.min($$4, $$3.size()) - 1));
        }
        return Optional.empty();
    }

    @Nullable
    public static <T> Pair<T, Integer> getHighestLevel(ItemStack $$0, DataComponentType<T> $$1) {
        MutableObject $$22 = new MutableObject();
        EnchantmentHelper.runIterationOnItem($$0, ($$2, $$3) -> {
            Object $$4;
            if (($$22.getValue() == null || (Integer)((Pair)$$22.getValue()).getSecond() < $$3) && ($$4 = ((Enchantment)((Object)((Object)$$2.value()))).effects().get($$1)) != null) {
                $$22.setValue(Pair.of($$4, (Object)$$3));
            }
        });
        return (Pair)$$22.getValue();
    }

    public static Optional<EnchantedItemInUse> getRandomItemWith(DataComponentType<?> $$0, LivingEntity $$1, Predicate<ItemStack> $$2) {
        ArrayList<EnchantedItemInUse> $$3 = new ArrayList<EnchantedItemInUse>();
        for (EquipmentSlot $$4 : EquipmentSlot.VALUES) {
            ItemStack $$5 = $$1.getItemBySlot($$4);
            if (!$$2.test($$5)) continue;
            ItemEnchantments $$6 = $$5.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            for (Object2IntMap.Entry<Holder<Enchantment>> $$7 : $$6.entrySet()) {
                Holder $$8 = (Holder)$$7.getKey();
                if (!((Enchantment)((Object)$$8.value())).effects().has($$0) || !((Enchantment)((Object)$$8.value())).matchingSlot($$4)) continue;
                $$3.add(new EnchantedItemInUse($$5, $$4, $$1));
            }
        }
        return Util.getRandomSafe($$3, $$1.getRandom());
    }

    public static int getEnchantmentCost(RandomSource $$0, int $$1, int $$2, ItemStack $$3) {
        Enchantable $$4 = $$3.get(DataComponents.ENCHANTABLE);
        if ($$4 == null) {
            return 0;
        }
        if ($$2 > 15) {
            $$2 = 15;
        }
        int $$5 = $$0.nextInt(8) + 1 + ($$2 >> 1) + $$0.nextInt($$2 + 1);
        if ($$1 == 0) {
            return Math.max($$5 / 3, 1);
        }
        if ($$1 == 1) {
            return $$5 * 2 / 3 + 1;
        }
        return Math.max($$5, $$2 * 2);
    }

    public static ItemStack enchantItem(RandomSource $$0, ItemStack $$1, int $$2, RegistryAccess $$3, Optional<? extends HolderSet<Enchantment>> $$4) {
        return EnchantmentHelper.enchantItem($$0, $$1, $$2, $$4.map(HolderSet::stream).orElseGet(() -> $$3.lookupOrThrow(Registries.ENCHANTMENT).listElements().map($$0 -> $$0)));
    }

    public static ItemStack enchantItem(RandomSource $$0, ItemStack $$1, int $$2, Stream<Holder<Enchantment>> $$3) {
        List<EnchantmentInstance> $$4 = EnchantmentHelper.selectEnchantment($$0, $$1, $$2, $$3);
        if ($$1.is(Items.BOOK)) {
            $$1 = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentInstance $$5 : $$4) {
            $$1.enchant($$5.enchantment(), $$5.level());
        }
        return $$1;
    }

    public static List<EnchantmentInstance> selectEnchantment(RandomSource $$0, ItemStack $$1, int $$2, Stream<Holder<Enchantment>> $$3) {
        ArrayList<EnchantmentInstance> $$4 = Lists.newArrayList();
        Enchantable $$5 = $$1.get(DataComponents.ENCHANTABLE);
        if ($$5 == null) {
            return $$4;
        }
        $$2 += 1 + $$0.nextInt($$5.value() / 4 + 1) + $$0.nextInt($$5.value() / 4 + 1);
        float $$6 = ($$0.nextFloat() + $$0.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentInstance> $$7 = EnchantmentHelper.getAvailableEnchantmentResults($$2 = Mth.clamp(Math.round((float)$$2 + (float)$$2 * $$6), 1, Integer.MAX_VALUE), $$1, $$3);
        if (!$$7.isEmpty()) {
            WeightedRandom.getRandomItem($$0, $$7, EnchantmentInstance::weight).ifPresent($$4::add);
            while ($$0.nextInt(50) <= $$2) {
                if (!$$4.isEmpty()) {
                    EnchantmentHelper.filterCompatibleEnchantments($$7, Util.lastOf($$4));
                }
                if ($$7.isEmpty()) break;
                WeightedRandom.getRandomItem($$0, $$7, EnchantmentInstance::weight).ifPresent($$4::add);
                $$2 /= 2;
            }
        }
        return $$4;
    }

    public static void filterCompatibleEnchantments(List<EnchantmentInstance> $$0, EnchantmentInstance $$12) {
        $$0.removeIf($$1 -> !Enchantment.areCompatible($$12.enchantment(), $$1.enchantment()));
    }

    public static boolean isEnchantmentCompatible(Collection<Holder<Enchantment>> $$0, Holder<Enchantment> $$1) {
        for (Holder<Enchantment> $$2 : $$0) {
            if (Enchantment.areCompatible($$2, $$1)) continue;
            return false;
        }
        return true;
    }

    public static List<EnchantmentInstance> getAvailableEnchantmentResults(int $$0, ItemStack $$1, Stream<Holder<Enchantment>> $$22) {
        ArrayList<EnchantmentInstance> $$3 = Lists.newArrayList();
        boolean $$4 = $$1.is(Items.BOOK);
        $$22.filter($$2 -> ((Enchantment)((Object)((Object)$$2.value()))).isPrimaryItem($$1) || $$4).forEach($$2 -> {
            Enchantment $$3 = (Enchantment)((Object)((Object)$$2.value()));
            for (int $$4 = $$3.getMaxLevel(); $$4 >= $$3.getMinLevel(); --$$4) {
                if ($$0 < $$3.getMinCost($$4) || $$0 > $$3.getMaxCost($$4)) continue;
                $$3.add(new EnchantmentInstance((Holder<Enchantment>)$$2, $$4));
                break;
            }
        });
        return $$3;
    }

    public static void enchantItemFromProvider(ItemStack $$0, RegistryAccess $$1, ResourceKey<EnchantmentProvider> $$2, DifficultyInstance $$3, RandomSource $$42) {
        EnchantmentProvider $$5 = $$1.lookupOrThrow(Registries.ENCHANTMENT_PROVIDER).getValue($$2);
        if ($$5 != null) {
            EnchantmentHelper.updateEnchantments($$0, $$4 -> $$5.enchant($$0, (ItemEnchantments.Mutable)$$4, $$42, $$3));
        }
    }

    @FunctionalInterface
    static interface EnchantmentVisitor {
        public void accept(Holder<Enchantment> var1, int var2);
    }

    @FunctionalInterface
    static interface EnchantmentInSlotVisitor {
        public void accept(Holder<Enchantment> var1, int var2, EnchantedItemInUse var3);
    }
}


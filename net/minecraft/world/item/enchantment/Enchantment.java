/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  java.lang.MatchException
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;

public record Enchantment(Component description, EnchantmentDefinition definition, HolderSet<Enchantment> exclusiveSet, DataComponentMap effects) {
    public static final int MAX_LEVEL = 255;
    public static final Codec<Enchantment> DIRECT_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ComponentSerialization.CODEC.fieldOf("description").forGetter(Enchantment::description), (App)EnchantmentDefinition.CODEC.forGetter(Enchantment::definition), (App)RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("exclusive_set", HolderSet.a(new Holder[0])).forGetter(Enchantment::exclusiveSet), (App)EnchantmentEffectComponents.CODEC.optionalFieldOf("effects", (Object)DataComponentMap.EMPTY).forGetter(Enchantment::effects)).apply((Applicative)$$0, Enchantment::new));
    public static final Codec<Holder<Enchantment>> CODEC = RegistryFixedCodec.create(Registries.ENCHANTMENT);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Enchantment>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT);

    public static Cost constantCost(int $$0) {
        return new Cost($$0, 0);
    }

    public static Cost dynamicCost(int $$0, int $$1) {
        return new Cost($$0, $$1);
    }

    public static EnchantmentDefinition a(HolderSet<Item> $$0, HolderSet<Item> $$1, int $$2, int $$3, Cost $$4, Cost $$5, int $$6, EquipmentSlotGroup ... $$7) {
        return new EnchantmentDefinition($$0, Optional.of($$1), $$2, $$3, $$4, $$5, $$6, List.of((Object[])$$7));
    }

    public static EnchantmentDefinition a(HolderSet<Item> $$0, int $$1, int $$2, Cost $$3, Cost $$4, int $$5, EquipmentSlotGroup ... $$6) {
        return new EnchantmentDefinition($$0, Optional.empty(), $$1, $$2, $$3, $$4, $$5, List.of((Object[])$$6));
    }

    public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity $$0) {
        EnumMap<EquipmentSlot, ItemStack> $$1 = Maps.newEnumMap(EquipmentSlot.class);
        for (EquipmentSlot $$2 : EquipmentSlot.VALUES) {
            ItemStack $$3;
            if (!this.matchingSlot($$2) || ($$3 = $$0.getItemBySlot($$2)).isEmpty()) continue;
            $$1.put($$2, $$3);
        }
        return $$1;
    }

    public HolderSet<Item> getSupportedItems() {
        return this.definition.supportedItems();
    }

    public boolean matchingSlot(EquipmentSlot $$0) {
        return this.definition.slots().stream().anyMatch($$1 -> $$1.test($$0));
    }

    public boolean isPrimaryItem(ItemStack $$0) {
        return this.isSupportedItem($$0) && (this.definition.primaryItems.isEmpty() || $$0.is(this.definition.primaryItems.get()));
    }

    public boolean isSupportedItem(ItemStack $$0) {
        return $$0.is(this.definition.supportedItems);
    }

    public int getWeight() {
        return this.definition.weight();
    }

    public int getAnvilCost() {
        return this.definition.anvilCost();
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return this.definition.maxLevel();
    }

    public int getMinCost(int $$0) {
        return this.definition.minCost().calculate($$0);
    }

    public int getMaxCost(int $$0) {
        return this.definition.maxCost().calculate($$0);
    }

    public String toString() {
        return "Enchantment " + this.description.getString();
    }

    public static boolean areCompatible(Holder<Enchantment> $$0, Holder<Enchantment> $$1) {
        return !$$0.equals($$1) && !$$0.value().exclusiveSet.contains($$1) && !$$1.value().exclusiveSet.contains($$0);
    }

    public static Component getFullname(Holder<Enchantment> $$0, int $$1) {
        MutableComponent $$2 = $$0.value().description.copy();
        if ($$0.is(EnchantmentTags.CURSE)) {
            ComponentUtils.mergeStyles($$2, Style.EMPTY.withColor(ChatFormatting.RED));
        } else {
            ComponentUtils.mergeStyles($$2, Style.EMPTY.withColor(ChatFormatting.GRAY));
        }
        if ($$1 != 1 || $$0.value().getMaxLevel() != 1) {
            $$2.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + $$1));
        }
        return $$2;
    }

    public boolean canEnchant(ItemStack $$0) {
        return this.definition.supportedItems().contains($$0.getItemHolder());
    }

    public <T> List<T> getEffects(DataComponentType<List<T>> $$0) {
        return this.effects.getOrDefault($$0, List.of());
    }

    public boolean isImmuneToDamage(ServerLevel $$0, int $$1, Entity $$2, DamageSource $$3) {
        LootContext $$4 = Enchantment.damageContext($$0, $$1, $$2, $$3);
        for (ConditionalEffect $$5 : this.getEffects(EnchantmentEffectComponents.DAMAGE_IMMUNITY)) {
            if (!$$5.matches($$4)) continue;
            return true;
        }
        return false;
    }

    public void modifyDamageProtection(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, DamageSource $$4, MutableFloat $$5) {
        LootContext $$6 = Enchantment.damageContext($$0, $$1, $$3, $$4);
        for (ConditionalEffect $$7 : this.getEffects(EnchantmentEffectComponents.DAMAGE_PROTECTION)) {
            if (!$$7.matches($$6)) continue;
            $$5.setValue(((EnchantmentValueEffect)$$7.effect()).process($$1, $$3.getRandom(), $$5.floatValue()));
        }
    }

    public void modifyDurabilityChange(ServerLevel $$0, int $$1, ItemStack $$2, MutableFloat $$3) {
        this.modifyItemFilteredCount(EnchantmentEffectComponents.ITEM_DAMAGE, $$0, $$1, $$2, $$3);
    }

    public void modifyAmmoCount(ServerLevel $$0, int $$1, ItemStack $$2, MutableFloat $$3) {
        this.modifyItemFilteredCount(EnchantmentEffectComponents.AMMO_USE, $$0, $$1, $$2, $$3);
    }

    public void modifyPiercingCount(ServerLevel $$0, int $$1, ItemStack $$2, MutableFloat $$3) {
        this.modifyItemFilteredCount(EnchantmentEffectComponents.PROJECTILE_PIERCING, $$0, $$1, $$2, $$3);
    }

    public void modifyBlockExperience(ServerLevel $$0, int $$1, ItemStack $$2, MutableFloat $$3) {
        this.modifyItemFilteredCount(EnchantmentEffectComponents.BLOCK_EXPERIENCE, $$0, $$1, $$2, $$3);
    }

    public void modifyMobExperience(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, MutableFloat $$4) {
        this.modifyEntityFilteredValue(EnchantmentEffectComponents.MOB_EXPERIENCE, $$0, $$1, $$2, $$3, $$4);
    }

    public void modifyDurabilityToRepairFromXp(ServerLevel $$0, int $$1, ItemStack $$2, MutableFloat $$3) {
        this.modifyItemFilteredCount(EnchantmentEffectComponents.REPAIR_WITH_XP, $$0, $$1, $$2, $$3);
    }

    public void modifyTridentReturnToOwnerAcceleration(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, MutableFloat $$4) {
        this.modifyEntityFilteredValue(EnchantmentEffectComponents.TRIDENT_RETURN_ACCELERATION, $$0, $$1, $$2, $$3, $$4);
    }

    public void modifyTridentSpinAttackStrength(RandomSource $$0, int $$1, MutableFloat $$2) {
        this.modifyUnfilteredValue(EnchantmentEffectComponents.TRIDENT_SPIN_ATTACK_STRENGTH, $$0, $$1, $$2);
    }

    public void modifyFishingTimeReduction(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, MutableFloat $$4) {
        this.modifyEntityFilteredValue(EnchantmentEffectComponents.FISHING_TIME_REDUCTION, $$0, $$1, $$2, $$3, $$4);
    }

    public void modifyFishingLuckBonus(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, MutableFloat $$4) {
        this.modifyEntityFilteredValue(EnchantmentEffectComponents.FISHING_LUCK_BONUS, $$0, $$1, $$2, $$3, $$4);
    }

    public void modifyDamage(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, DamageSource $$4, MutableFloat $$5) {
        this.modifyDamageFilteredValue(EnchantmentEffectComponents.DAMAGE, $$0, $$1, $$2, $$3, $$4, $$5);
    }

    public void modifyFallBasedDamage(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, DamageSource $$4, MutableFloat $$5) {
        this.modifyDamageFilteredValue(EnchantmentEffectComponents.SMASH_DAMAGE_PER_FALLEN_BLOCK, $$0, $$1, $$2, $$3, $$4, $$5);
    }

    public void modifyKnockback(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, DamageSource $$4, MutableFloat $$5) {
        this.modifyDamageFilteredValue(EnchantmentEffectComponents.KNOCKBACK, $$0, $$1, $$2, $$3, $$4, $$5);
    }

    public void modifyArmorEffectivness(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, DamageSource $$4, MutableFloat $$5) {
        this.modifyDamageFilteredValue(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, $$0, $$1, $$2, $$3, $$4, $$5);
    }

    public void doPostAttack(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, EnchantmentTarget $$3, Entity $$4, DamageSource $$5) {
        for (TargetedConditionalEffect $$6 : this.getEffects(EnchantmentEffectComponents.POST_ATTACK)) {
            if ($$3 != $$6.enchanted()) continue;
            Enchantment.doPostAttack($$6, $$0, $$1, $$2, $$4, $$5);
        }
    }

    public static void doPostAttack(TargetedConditionalEffect<EnchantmentEntityEffect> $$0, ServerLevel $$1, int $$2, EnchantedItemInUse $$3, Entity $$4, DamageSource $$5) {
        if ($$0.matches(Enchantment.damageContext($$1, $$2, $$4, $$5))) {
            Entity $$6;
            switch ($$0.affected()) {
                default: {
                    throw new MatchException(null, null);
                }
                case ATTACKER: {
                    Entity entity = $$5.getEntity();
                    break;
                }
                case DAMAGING_ENTITY: {
                    Entity entity = $$5.getDirectEntity();
                    break;
                }
                case VICTIM: {
                    Entity entity = $$6 = $$4;
                }
            }
            if ($$6 != null) {
                $$0.effect().apply($$1, $$2, $$3, $$6, $$6.position());
            }
        }
    }

    public void modifyProjectileCount(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, MutableFloat $$4) {
        this.modifyEntityFilteredValue(EnchantmentEffectComponents.PROJECTILE_COUNT, $$0, $$1, $$2, $$3, $$4);
    }

    public void modifyProjectileSpread(ServerLevel $$0, int $$1, ItemStack $$2, Entity $$3, MutableFloat $$4) {
        this.modifyEntityFilteredValue(EnchantmentEffectComponents.PROJECTILE_SPREAD, $$0, $$1, $$2, $$3, $$4);
    }

    public void modifyCrossbowChargeTime(RandomSource $$0, int $$1, MutableFloat $$2) {
        this.modifyUnfilteredValue(EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME, $$0, $$1, $$2);
    }

    public void modifyUnfilteredValue(DataComponentType<EnchantmentValueEffect> $$0, RandomSource $$1, int $$2, MutableFloat $$3) {
        EnchantmentValueEffect $$4 = this.effects.get($$0);
        if ($$4 != null) {
            $$3.setValue($$4.process($$2, $$1, $$3.floatValue()));
        }
    }

    public void tick(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3) {
        Enchantment.applyEffects(this.getEffects(EnchantmentEffectComponents.TICK), Enchantment.entityContext($$0, $$1, $$3, $$3.position()), $$4 -> $$4.apply($$0, $$1, $$2, $$3, $$3.position()));
    }

    public void onProjectileSpawned(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3) {
        Enchantment.applyEffects(this.getEffects(EnchantmentEffectComponents.PROJECTILE_SPAWNED), Enchantment.entityContext($$0, $$1, $$3, $$3.position()), $$4 -> $$4.apply($$0, $$1, $$2, $$3, $$3.position()));
    }

    public void onHitBlock(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4, BlockState $$52) {
        Enchantment.applyEffects(this.getEffects(EnchantmentEffectComponents.HIT_BLOCK), Enchantment.blockHitContext($$0, $$1, $$3, $$4, $$52), $$5 -> $$5.apply($$0, $$1, $$2, $$3, $$4));
    }

    private void modifyItemFilteredCount(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> $$0, ServerLevel $$1, int $$2, ItemStack $$32, MutableFloat $$4) {
        Enchantment.applyEffects(this.getEffects($$0), Enchantment.itemContext($$1, $$2, $$32), $$3 -> $$4.setValue($$3.process($$2, $$1.getRandom(), $$4.getValue().floatValue())));
    }

    private void modifyEntityFilteredValue(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> $$0, ServerLevel $$1, int $$2, ItemStack $$32, Entity $$4, MutableFloat $$5) {
        Enchantment.applyEffects(this.getEffects($$0), Enchantment.entityContext($$1, $$2, $$4, $$4.position()), $$3 -> $$5.setValue($$3.process($$2, $$4.getRandom(), $$5.floatValue())));
    }

    private void modifyDamageFilteredValue(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> $$0, ServerLevel $$1, int $$2, ItemStack $$32, Entity $$4, DamageSource $$5, MutableFloat $$6) {
        Enchantment.applyEffects(this.getEffects($$0), Enchantment.damageContext($$1, $$2, $$4, $$5), $$3 -> $$6.setValue($$3.process($$2, $$4.getRandom(), $$6.floatValue())));
    }

    public static LootContext damageContext(ServerLevel $$0, int $$1, Entity $$2, DamageSource $$3) {
        LootParams $$4 = new LootParams.Builder($$0).withParameter(LootContextParams.THIS_ENTITY, $$2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, $$1).withParameter(LootContextParams.ORIGIN, $$2.position()).withParameter(LootContextParams.DAMAGE_SOURCE, $$3).withOptionalParameter(LootContextParams.ATTACKING_ENTITY, $$3.getEntity()).withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, $$3.getDirectEntity()).create(LootContextParamSets.ENCHANTED_DAMAGE);
        return new LootContext.Builder($$4).create(Optional.empty());
    }

    private static LootContext itemContext(ServerLevel $$0, int $$1, ItemStack $$2) {
        LootParams $$3 = new LootParams.Builder($$0).withParameter(LootContextParams.TOOL, $$2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, $$1).create(LootContextParamSets.ENCHANTED_ITEM);
        return new LootContext.Builder($$3).create(Optional.empty());
    }

    private static LootContext locationContext(ServerLevel $$0, int $$1, Entity $$2, boolean $$3) {
        LootParams $$4 = new LootParams.Builder($$0).withParameter(LootContextParams.THIS_ENTITY, $$2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, $$1).withParameter(LootContextParams.ORIGIN, $$2.position()).withParameter(LootContextParams.ENCHANTMENT_ACTIVE, $$3).create(LootContextParamSets.ENCHANTED_LOCATION);
        return new LootContext.Builder($$4).create(Optional.empty());
    }

    private static LootContext entityContext(ServerLevel $$0, int $$1, Entity $$2, Vec3 $$3) {
        LootParams $$4 = new LootParams.Builder($$0).withParameter(LootContextParams.THIS_ENTITY, $$2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, $$1).withParameter(LootContextParams.ORIGIN, $$3).create(LootContextParamSets.ENCHANTED_ENTITY);
        return new LootContext.Builder($$4).create(Optional.empty());
    }

    private static LootContext blockHitContext(ServerLevel $$0, int $$1, Entity $$2, Vec3 $$3, BlockState $$4) {
        LootParams $$5 = new LootParams.Builder($$0).withParameter(LootContextParams.THIS_ENTITY, $$2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, $$1).withParameter(LootContextParams.ORIGIN, $$3).withParameter(LootContextParams.BLOCK_STATE, $$4).create(LootContextParamSets.HIT_BLOCK);
        return new LootContext.Builder($$5).create(Optional.empty());
    }

    private static <T> void applyEffects(List<ConditionalEffect<T>> $$0, LootContext $$1, Consumer<T> $$2) {
        for (ConditionalEffect<T> $$3 : $$0) {
            if (!$$3.matches($$1)) continue;
            $$2.accept($$3.effect());
        }
    }

    public void runLocationChangedEffects(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, LivingEntity $$32) {
        EquipmentSlot $$4 = $$2.inSlot();
        if ($$4 == null) {
            return;
        }
        Map<Enchantment, Set<EnchantmentLocationBasedEffect>> $$5 = $$32.activeLocationDependentEnchantments($$4);
        if (!this.matchingSlot($$4)) {
            Set<EnchantmentLocationBasedEffect> $$6 = $$5.remove((Object)this);
            if ($$6 != null) {
                $$6.forEach($$3 -> $$3.onDeactivated($$2, $$32, $$32.position(), $$1));
            }
            return;
        }
        ObjectArraySet $$7 = $$5.get((Object)this);
        for (ConditionalEffect $$8 : this.getEffects(EnchantmentEffectComponents.LOCATION_CHANGED)) {
            boolean $$10;
            EnchantmentLocationBasedEffect $$9 = (EnchantmentLocationBasedEffect)$$8.effect();
            boolean bl = $$10 = $$7 != null && $$7.contains($$9);
            if ($$8.matches(Enchantment.locationContext($$0, $$1, $$32, $$10))) {
                if (!$$10) {
                    if ($$7 == null) {
                        $$7 = new ObjectArraySet();
                        $$5.put(this, (Set<EnchantmentLocationBasedEffect>)$$7);
                    }
                    $$7.add((EnchantmentLocationBasedEffect)$$9);
                }
                $$9.onChangedBlock($$0, $$1, $$2, $$32, $$32.position(), !$$10);
                continue;
            }
            if ($$7 == null || !$$7.remove($$9)) continue;
            $$9.onDeactivated($$2, $$32, $$32.position(), $$1);
        }
        if ($$7 != null && $$7.isEmpty()) {
            $$5.remove((Object)this);
        }
    }

    public void stopLocationBasedEffects(int $$0, EnchantedItemInUse $$1, LivingEntity $$2) {
        EquipmentSlot $$3 = $$1.inSlot();
        if ($$3 == null) {
            return;
        }
        Set<EnchantmentLocationBasedEffect> $$4 = $$2.activeLocationDependentEnchantments($$3).remove((Object)this);
        if ($$4 == null) {
            return;
        }
        for (EnchantmentLocationBasedEffect $$5 : $$4) {
            $$5.onDeactivated($$1, $$2, $$2.position(), $$0);
        }
    }

    public static Builder enchantment(EnchantmentDefinition $$0) {
        return new Builder($$0);
    }

    public static final class EnchantmentDefinition
    extends Record {
        final HolderSet<Item> supportedItems;
        final Optional<HolderSet<Item>> primaryItems;
        private final int weight;
        private final int maxLevel;
        private final Cost minCost;
        private final Cost maxCost;
        private final int anvilCost;
        private final List<EquipmentSlotGroup> slots;
        public static final MapCodec<EnchantmentDefinition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("supported_items").forGetter(EnchantmentDefinition::supportedItems), (App)RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("primary_items").forGetter(EnchantmentDefinition::primaryItems), (App)ExtraCodecs.intRange(1, 1024).fieldOf("weight").forGetter(EnchantmentDefinition::weight), (App)ExtraCodecs.intRange(1, 255).fieldOf("max_level").forGetter(EnchantmentDefinition::maxLevel), (App)Cost.CODEC.fieldOf("min_cost").forGetter(EnchantmentDefinition::minCost), (App)Cost.CODEC.fieldOf("max_cost").forGetter(EnchantmentDefinition::maxCost), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("anvil_cost").forGetter(EnchantmentDefinition::anvilCost), (App)EquipmentSlotGroup.CODEC.listOf().fieldOf("slots").forGetter(EnchantmentDefinition::slots)).apply((Applicative)$$0, EnchantmentDefinition::new));

        public EnchantmentDefinition(HolderSet<Item> $$0, Optional<HolderSet<Item>> $$1, int $$2, int $$3, Cost $$4, Cost $$5, int $$6, List<EquipmentSlotGroup> $$7) {
            this.supportedItems = $$0;
            this.primaryItems = $$1;
            this.weight = $$2;
            this.maxLevel = $$3;
            this.minCost = $$4;
            this.maxCost = $$5;
            this.anvilCost = $$6;
            this.slots = $$7;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{EnchantmentDefinition.class, "supportedItems;primaryItems;weight;maxLevel;minCost;maxCost;anvilCost;slots", "supportedItems", "primaryItems", "weight", "maxLevel", "minCost", "maxCost", "anvilCost", "slots"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EnchantmentDefinition.class, "supportedItems;primaryItems;weight;maxLevel;minCost;maxCost;anvilCost;slots", "supportedItems", "primaryItems", "weight", "maxLevel", "minCost", "maxCost", "anvilCost", "slots"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EnchantmentDefinition.class, "supportedItems;primaryItems;weight;maxLevel;minCost;maxCost;anvilCost;slots", "supportedItems", "primaryItems", "weight", "maxLevel", "minCost", "maxCost", "anvilCost", "slots"}, this, $$0);
        }

        public HolderSet<Item> supportedItems() {
            return this.supportedItems;
        }

        public Optional<HolderSet<Item>> primaryItems() {
            return this.primaryItems;
        }

        public int weight() {
            return this.weight;
        }

        public int maxLevel() {
            return this.maxLevel;
        }

        public Cost minCost() {
            return this.minCost;
        }

        public Cost maxCost() {
            return this.maxCost;
        }

        public int anvilCost() {
            return this.anvilCost;
        }

        public List<EquipmentSlotGroup> slots() {
            return this.slots;
        }
    }

    public record Cost(int base, int perLevelAboveFirst) {
        public static final Codec<Cost> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.INT.fieldOf("base").forGetter(Cost::base), (App)Codec.INT.fieldOf("per_level_above_first").forGetter(Cost::perLevelAboveFirst)).apply((Applicative)$$0, Cost::new));

        public int calculate(int $$0) {
            return this.base + this.perLevelAboveFirst * ($$0 - 1);
        }
    }

    public static class Builder {
        private final EnchantmentDefinition definition;
        private HolderSet<Enchantment> exclusiveSet = HolderSet.a(new Holder[0]);
        private final Map<DataComponentType<?>, List<?>> effectLists = new HashMap();
        private final DataComponentMap.Builder effectMapBuilder = DataComponentMap.builder();

        public Builder(EnchantmentDefinition $$0) {
            this.definition = $$0;
        }

        public Builder exclusiveWith(HolderSet<Enchantment> $$0) {
            this.exclusiveSet = $$0;
            return this;
        }

        public <E> Builder withEffect(DataComponentType<List<ConditionalEffect<E>>> $$0, E $$1, LootItemCondition.Builder $$2) {
            this.getEffectsList($$0).add(new ConditionalEffect<E>($$1, Optional.of($$2.build())));
            return this;
        }

        public <E> Builder withEffect(DataComponentType<List<ConditionalEffect<E>>> $$0, E $$1) {
            this.getEffectsList($$0).add(new ConditionalEffect<E>($$1, Optional.empty()));
            return this;
        }

        public <E> Builder withEffect(DataComponentType<List<TargetedConditionalEffect<E>>> $$0, EnchantmentTarget $$1, EnchantmentTarget $$2, E $$3, LootItemCondition.Builder $$4) {
            this.getEffectsList($$0).add(new TargetedConditionalEffect<E>($$1, $$2, $$3, Optional.of($$4.build())));
            return this;
        }

        public <E> Builder withEffect(DataComponentType<List<TargetedConditionalEffect<E>>> $$0, EnchantmentTarget $$1, EnchantmentTarget $$2, E $$3) {
            this.getEffectsList($$0).add(new TargetedConditionalEffect<E>($$1, $$2, $$3, Optional.empty()));
            return this;
        }

        public Builder withEffect(DataComponentType<List<EnchantmentAttributeEffect>> $$0, EnchantmentAttributeEffect $$1) {
            this.getEffectsList($$0).add($$1);
            return this;
        }

        public <E> Builder withSpecialEffect(DataComponentType<E> $$0, E $$1) {
            this.effectMapBuilder.set($$0, $$1);
            return this;
        }

        public Builder withEffect(DataComponentType<Unit> $$0) {
            this.effectMapBuilder.set($$0, Unit.INSTANCE);
            return this;
        }

        private <E> List<E> getEffectsList(DataComponentType<List<E>> $$0) {
            return this.effectLists.computeIfAbsent($$0, $$1 -> {
                ArrayList $$2 = new ArrayList();
                this.effectMapBuilder.set($$0, $$2);
                return $$2;
            });
        }

        public Enchantment build(ResourceLocation $$0) {
            return new Enchantment(Component.translatable(Util.makeDescriptionId("enchantment", $$0)), this.definition, this.exclusiveSet, this.effectMapBuilder.build());
        }
    }
}


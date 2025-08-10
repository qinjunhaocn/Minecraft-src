/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancements.critereon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer $$0, ItemStack $$1, Entity $$22) {
        LootContext $$3 = EntityPredicate.createContext($$0, $$22);
        this.trigger($$0, $$2 -> $$2.matches($$1, $$3));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity) implements SimpleCriterionTrigger.SimpleInstance
    {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item), (App)EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)).apply((Applicative)$$0, TriggerInstance::new));

        public static Criterion<TriggerInstance> itemUsedOnEntity(Optional<ContextAwarePredicate> $$0, ItemPredicate.Builder $$1, Optional<ContextAwarePredicate> $$2) {
            return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new TriggerInstance($$0, Optional.of($$1.build()), $$2));
        }

        public static Criterion<TriggerInstance> equipmentSheared(Optional<ContextAwarePredicate> $$0, ItemPredicate.Builder $$1, Optional<ContextAwarePredicate> $$2) {
            return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.createCriterion(new TriggerInstance($$0, Optional.of($$1.build()), $$2));
        }

        public static Criterion<TriggerInstance> equipmentSheared(ItemPredicate.Builder $$0, Optional<ContextAwarePredicate> $$1) {
            return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.createCriterion(new TriggerInstance(Optional.empty(), Optional.of($$0.build()), $$1));
        }

        public static Criterion<TriggerInstance> itemUsedOnEntity(ItemPredicate.Builder $$0, Optional<ContextAwarePredicate> $$1) {
            return TriggerInstance.itemUsedOnEntity(Optional.empty(), $$0, $$1);
        }

        public boolean matches(ItemStack $$0, LootContext $$1) {
            if (this.item.isPresent() && !this.item.get().test($$0)) {
                return false;
            }
            return this.entity.isEmpty() || this.entity.get().matches($$1);
        }

        @Override
        public void validate(CriterionValidator $$0) {
            SimpleCriterionTrigger.SimpleInstance.super.validate($$0);
            $$0.validateEntity(this.entity, "entity");
        }
    }
}


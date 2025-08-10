/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetStewEffectFunction
extends LootItemConditionalFunction {
    private static final Codec<List<EffectEntry>> EFFECTS_LIST = EffectEntry.CODEC.listOf().validate($$0 -> {
        ObjectOpenHashSet $$1 = new ObjectOpenHashSet();
        for (EffectEntry $$2 : $$0) {
            if ($$1.add($$2.effect())) continue;
            return DataResult.error(() -> "Encountered duplicate mob effect: '" + String.valueOf($$2.effect()) + "'");
        }
        return DataResult.success((Object)$$0);
    });
    public static final MapCodec<SetStewEffectFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetStewEffectFunction.commonFields($$02).and((App)EFFECTS_LIST.optionalFieldOf("effects", (Object)List.of()).forGetter($$0 -> $$0.effects)).apply((Applicative)$$02, SetStewEffectFunction::new));
    private final List<EffectEntry> effects;

    SetStewEffectFunction(List<LootItemCondition> $$0, List<EffectEntry> $$1) {
        super($$0);
        this.effects = $$1;
    }

    public LootItemFunctionType<SetStewEffectFunction> getType() {
        return LootItemFunctions.SET_STEW_EFFECT;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.effects.stream().flatMap($$0 -> $$0.duration().getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if (!$$0.is(Items.SUSPICIOUS_STEW) || this.effects.isEmpty()) {
            return $$0;
        }
        EffectEntry $$2 = Util.getRandom(this.effects, $$1.getRandom());
        Holder<MobEffect> $$3 = $$2.effect();
        int $$4 = $$2.duration().getInt($$1);
        if (!$$3.value().isInstantenous()) {
            $$4 *= 20;
        }
        SuspiciousStewEffects.Entry $$5 = new SuspiciousStewEffects.Entry($$3, $$4);
        $$0.update(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY, $$5, SuspiciousStewEffects::withEffectAdded);
        return $$0;
    }

    public static Builder stewEffect() {
        return new Builder();
    }

    record EffectEntry(Holder<MobEffect> effect, NumberProvider duration) {
        public static final Codec<EffectEntry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)MobEffect.CODEC.fieldOf("type").forGetter(EffectEntry::effect), (App)NumberProviders.CODEC.fieldOf("duration").forGetter(EffectEntry::duration)).apply((Applicative)$$0, EffectEntry::new));
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final ImmutableList.Builder<EffectEntry> effects = ImmutableList.builder();

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEffect(Holder<MobEffect> $$0, NumberProvider $$1) {
            this.effects.add((Object)new EffectEntry($$0, $$1));
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetStewEffectFunction(this.getConditions(), (List<EffectEntry>)((Object)this.effects.build()));
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}


/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.LambdaMetafactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetLoreFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetLoreFunction.commonFields($$02).and($$02.group((App)ComponentSerialization.CODEC.sizeLimitedListOf(256).fieldOf("lore").forGetter($$0 -> $$0.lore), (App)ListOperation.codec(256).forGetter($$0 -> $$0.mode), (App)LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter($$0 -> $$0.resolutionContext))).apply((Applicative)$$02, SetLoreFunction::new));
    private final List<Component> lore;
    private final ListOperation mode;
    private final Optional<LootContext.EntityTarget> resolutionContext;

    public SetLoreFunction(List<LootItemCondition> $$0, List<Component> $$1, ListOperation $$2, Optional<LootContext.EntityTarget> $$3) {
        super($$0);
        this.lore = List.copyOf($$1);
        this.mode = $$2;
        this.resolutionContext = $$3;
    }

    public LootItemFunctionType<SetLoreFunction> getType() {
        return LootItemFunctions.SET_LORE;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.resolutionContext.map($$0 -> Set.of($$0.getParam())).orElseGet((Supplier<Set>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, of(), ()Ljava/util/Set;)());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$12) {
        $$0.update(DataComponents.LORE, ItemLore.EMPTY, $$1 -> new ItemLore(this.updateLore((ItemLore)$$1, $$12)));
        return $$0;
    }

    private List<Component> updateLore(@Nullable ItemLore $$0, LootContext $$1) {
        if ($$0 == null && this.lore.isEmpty()) {
            return List.of();
        }
        UnaryOperator<Component> $$2 = SetNameFunction.createResolver($$1, this.resolutionContext.orElse(null));
        List $$3 = this.lore.stream().map($$2).toList();
        return this.mode.apply($$0.lines(), $$3, 256);
    }

    public static Builder setLore() {
        return new Builder();
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private Optional<LootContext.EntityTarget> resolutionContext = Optional.empty();
        private final ImmutableList.Builder<Component> lore = ImmutableList.builder();
        private ListOperation mode = ListOperation.Append.INSTANCE;

        public Builder setMode(ListOperation $$0) {
            this.mode = $$0;
            return this;
        }

        public Builder setResolutionContext(LootContext.EntityTarget $$0) {
            this.resolutionContext = Optional.of($$0);
            return this;
        }

        public Builder addLine(Component $$0) {
            this.lore.add((Object)$$0);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetLoreFunction(this.getConditions(), (List<Component>)((Object)this.lore.build()), this.mode, this.resolutionContext);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}


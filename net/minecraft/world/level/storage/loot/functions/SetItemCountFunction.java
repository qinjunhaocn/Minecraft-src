/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetItemCountFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetItemCountFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetItemCountFunction.commonFields($$02).and($$02.group((App)NumberProviders.CODEC.fieldOf("count").forGetter($$0 -> $$0.value), (App)Codec.BOOL.fieldOf("add").orElse((Object)false).forGetter($$0 -> $$0.add))).apply((Applicative)$$02, SetItemCountFunction::new));
    private final NumberProvider value;
    private final boolean add;

    private SetItemCountFunction(List<LootItemCondition> $$0, NumberProvider $$1, boolean $$2) {
        super($$0);
        this.value = $$1;
        this.add = $$2;
    }

    public LootItemFunctionType<SetItemCountFunction> getType() {
        return LootItemFunctions.SET_COUNT;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.value.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        int $$2 = this.add ? $$0.getCount() : 0;
        $$0.setCount($$2 + this.value.getInt($$1));
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider $$0) {
        return SetItemCountFunction.simpleBuilder($$1 -> new SetItemCountFunction((List<LootItemCondition>)$$1, $$0, false));
    }

    public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider $$0, boolean $$1) {
        return SetItemCountFunction.simpleBuilder($$2 -> new SetItemCountFunction((List<LootItemCondition>)$$2, $$0, $$1));
    }
}


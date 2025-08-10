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

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetComponentsFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetComponentsFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetComponentsFunction.commonFields($$02).and((App)DataComponentPatch.CODEC.fieldOf("components").forGetter($$0 -> $$0.components)).apply((Applicative)$$02, SetComponentsFunction::new));
    private final DataComponentPatch components;

    private SetComponentsFunction(List<LootItemCondition> $$0, DataComponentPatch $$1) {
        super($$0);
        this.components = $$1;
    }

    public LootItemFunctionType<SetComponentsFunction> getType() {
        return LootItemFunctions.SET_COMPONENTS;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.applyComponentsAndValidate(this.components);
        return $$0;
    }

    public static <T> LootItemConditionalFunction.Builder<?> setComponent(DataComponentType<T> $$0, T $$1) {
        return SetComponentsFunction.simpleBuilder($$2 -> new SetComponentsFunction((List<LootItemCondition>)$$2, DataComponentPatch.builder().set($$0, $$1).build()));
    }
}


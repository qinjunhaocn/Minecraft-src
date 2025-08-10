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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FilteredFunction
extends LootItemConditionalFunction {
    public static final MapCodec<FilteredFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> FilteredFunction.commonFields($$02).and($$02.group((App)ItemPredicate.CODEC.fieldOf("item_filter").forGetter($$0 -> $$0.filter), (App)LootItemFunctions.ROOT_CODEC.fieldOf("modifier").forGetter($$0 -> $$0.modifier))).apply((Applicative)$$02, FilteredFunction::new));
    private final ItemPredicate filter;
    private final LootItemFunction modifier;

    private FilteredFunction(List<LootItemCondition> $$0, ItemPredicate $$1, LootItemFunction $$2) {
        super($$0);
        this.filter = $$1;
        this.modifier = $$2;
    }

    public LootItemFunctionType<FilteredFunction> getType() {
        return LootItemFunctions.FILTERED;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if (this.filter.test($$0)) {
            return (ItemStack)this.modifier.apply($$0, $$1);
        }
        return $$0;
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        this.modifier.validate($$0.forChild(new ProblemReporter.FieldPathElement("modifier")));
    }
}


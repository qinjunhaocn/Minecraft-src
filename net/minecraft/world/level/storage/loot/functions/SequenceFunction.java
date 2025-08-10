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
import java.util.function.BiFunction;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

public class SequenceFunction
implements LootItemFunction {
    public static final MapCodec<SequenceFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)LootItemFunctions.TYPED_CODEC.listOf().fieldOf("functions").forGetter($$0 -> $$0.functions)).apply((Applicative)$$02, SequenceFunction::new));
    public static final Codec<SequenceFunction> INLINE_CODEC = LootItemFunctions.TYPED_CODEC.listOf().xmap(SequenceFunction::new, $$0 -> $$0.functions);
    private final List<LootItemFunction> functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

    private SequenceFunction(List<LootItemFunction> $$0) {
        this.functions = $$0;
        this.compositeFunction = LootItemFunctions.compose($$0);
    }

    public static SequenceFunction of(List<LootItemFunction> $$0) {
        return new SequenceFunction(List.copyOf($$0));
    }

    @Override
    public ItemStack apply(ItemStack $$0, LootContext $$1) {
        return this.compositeFunction.apply($$0, $$1);
    }

    @Override
    public void validate(ValidationContext $$0) {
        LootItemFunction.super.validate($$0);
        for (int $$1 = 0; $$1 < this.functions.size(); ++$$1) {
            this.functions.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("functions", $$1)));
        }
    }

    public LootItemFunctionType<SequenceFunction> getType() {
        return LootItemFunctions.SEQUENCE;
    }

    @Override
    public /* synthetic */ Object apply(Object object, Object object2) {
        return this.apply((ItemStack)object, (LootContext)object2);
    }
}


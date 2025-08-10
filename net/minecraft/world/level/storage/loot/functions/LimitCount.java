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
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount
extends LootItemConditionalFunction {
    public static final MapCodec<LimitCount> CODEC = RecordCodecBuilder.mapCodec($$02 -> LimitCount.commonFields($$02).and((App)IntRange.CODEC.fieldOf("limit").forGetter($$0 -> $$0.limiter)).apply((Applicative)$$02, LimitCount::new));
    private final IntRange limiter;

    private LimitCount(List<LootItemCondition> $$0, IntRange $$1) {
        super($$0);
        this.limiter = $$1;
    }

    public LootItemFunctionType<LimitCount> getType() {
        return LootItemFunctions.LIMIT_COUNT;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.limiter.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        int $$2 = this.limiter.clamp($$1, $$0.getCount());
        $$0.setCount($$2);
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> limitCount(IntRange $$0) {
        return LimitCount.simpleBuilder($$1 -> new LimitCount((List<LootItemCondition>)$$1, $$0));
    }
}


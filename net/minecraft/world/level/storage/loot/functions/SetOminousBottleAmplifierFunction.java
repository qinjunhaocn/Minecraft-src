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
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetOminousBottleAmplifierFunction
extends LootItemConditionalFunction {
    static final MapCodec<SetOminousBottleAmplifierFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetOminousBottleAmplifierFunction.commonFields($$02).and((App)NumberProviders.CODEC.fieldOf("amplifier").forGetter($$0 -> $$0.amplifierGenerator)).apply((Applicative)$$02, SetOminousBottleAmplifierFunction::new));
    private final NumberProvider amplifierGenerator;

    private SetOminousBottleAmplifierFunction(List<LootItemCondition> $$0, NumberProvider $$1) {
        super($$0);
        this.amplifierGenerator = $$1;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.amplifierGenerator.getReferencedContextParams();
    }

    public LootItemFunctionType<SetOminousBottleAmplifierFunction> getType() {
        return LootItemFunctions.SET_OMINOUS_BOTTLE_AMPLIFIER;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        int $$2 = Mth.clamp(this.amplifierGenerator.getInt($$1), 0, 4);
        $$0.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier($$2));
        return $$0;
    }

    public NumberProvider amplifier() {
        return this.amplifierGenerator;
    }

    public static LootItemConditionalFunction.Builder<?> setAmplifier(NumberProvider $$0) {
        return SetOminousBottleAmplifierFunction.simpleBuilder($$1 -> new SetOminousBottleAmplifierFunction((List<LootItemCondition>)$$1, $$0));
    }
}


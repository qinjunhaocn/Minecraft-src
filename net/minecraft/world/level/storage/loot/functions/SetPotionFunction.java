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
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetPotionFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetPotionFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetPotionFunction.commonFields($$02).and((App)Potion.CODEC.fieldOf("id").forGetter($$0 -> $$0.potion)).apply((Applicative)$$02, SetPotionFunction::new));
    private final Holder<Potion> potion;

    private SetPotionFunction(List<LootItemCondition> $$0, Holder<Potion> $$1) {
        super($$0);
        this.potion = $$1;
    }

    public LootItemFunctionType<SetPotionFunction> getType() {
        return LootItemFunctions.SET_POTION;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.update(DataComponents.POTION_CONTENTS, PotionContents.EMPTY, this.potion, PotionContents::withPotion);
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setPotion(Holder<Potion> $$0) {
        return SetPotionFunction.simpleBuilder($$1 -> new SetPotionFunction((List<LootItemCondition>)$$1, $$0));
    }
}


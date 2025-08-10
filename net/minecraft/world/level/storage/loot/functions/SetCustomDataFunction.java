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
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetCustomDataFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetCustomDataFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetCustomDataFunction.commonFields($$02).and((App)TagParser.LENIENT_CODEC.fieldOf("tag").forGetter($$0 -> $$0.tag)).apply((Applicative)$$02, SetCustomDataFunction::new));
    private final CompoundTag tag;

    private SetCustomDataFunction(List<LootItemCondition> $$0, CompoundTag $$1) {
        super($$0);
        this.tag = $$1;
    }

    public LootItemFunctionType<SetCustomDataFunction> getType() {
        return LootItemFunctions.SET_CUSTOM_DATA;
    }

    @Override
    public ItemStack run(ItemStack $$02, LootContext $$1) {
        CustomData.update(DataComponents.CUSTOM_DATA, $$02, $$0 -> $$0.merge(this.tag));
        return $$02;
    }

    @Deprecated
    public static LootItemConditionalFunction.Builder<?> setCustomData(CompoundTag $$0) {
        return SetCustomDataFunction.simpleBuilder($$1 -> new SetCustomDataFunction((List<LootItemCondition>)$$1, $$0));
    }
}

